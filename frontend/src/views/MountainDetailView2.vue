<template>
  <div id="app">
    <HeaderView style="z-index: 4;width: 1920px;"></HeaderView>

    <div class="d-flex">    
      <div class="row" style="margin: 0px; padding: 0px;">
        <div id="map" style="width: 100%; height: 680px; margin: 0px;"></div>
        <canvas id="elevationChart" width="400px" height="100px"></canvas>
      </div>
    </div>

  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import Chart from 'chart.js/auto';
import axios from "axios";
import {
  resolveAllTrailIds,
  SLOPE_WINDOW_METERS,
  getEstimatedSlopeColor,
  fetchTrailGeoJson,
  fetchSlopeSections,
} from '@/api/slopeSection.js';

const map = ref(null);
const routeCoordinates = ref([]);
let chartInstance = null;
const currentMarker = ref(null);

onMounted(() => {
  initializeMap();
  fetchWeatherData(37.66433293993584, 127.01160029114365);
});


function initializeMap() {
  const script = document.createElement('script');
  script.onload = () => kakao.maps.load(() => {
    map.value = new kakao.maps.Map(document.getElementById('map'), {
      center: new kakao.maps.LatLng(37.66433293993584, 127.01160029114365),
      level: 5
    });

    loadGeoJSONFromServer();

    loadMarkers("/data/헬기장spot.geojson" , '/images/helipad.png');
    loadMarkers("/data/벤치spot.geojson", '/images/bench.png');
    loadMarkers("/data/2023산악사고_인왕산.geojson", '/images/danger.png');
  });
  script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false';
  document.head.appendChild(script);
}

// 정적 GeoJSON 대신 Trail GeoJSON API(DB 기반 TrailFeature)에서 경로를 불러온다
async function loadGeoJSONFromServer() {
  try {
    const geojsonData = await fetchTrailGeoJson();
    processGeoJSON(geojsonData);
  } catch (error) {
    console.error('Trail GeoJSON API 로드 중 에러 발생:', error);
  }
}

async function loadMarkers(url, imageSrc) {
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const markerData = await response.json();
    console.log(`${url} 마커 데이터 로드 성공:`, markerData);

    const filteredMarkers = markerData.features.filter((feature) => {
      mountainName.value = feature.properties.MNTN_NM;

      return mountainName.value == '북한산_백운대';
    });

    const imageSize = new kakao.maps.Size(35,45);
    const imageOpation = { offset: new kakao.maps.Point(12, 35)};
    const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOpation);

    filteredMarkers.forEach((spot) => {
      const lat = spot.geometry.coordinates[1];
      const lng = spot.geometry.coordinates[0];
      const markerPosition = new kakao.maps.LatLng(lat, lng);

      const marker = new kakao.maps.Marker({
        position: markerPosition,
        map: map.value,
        title: spot.properties.MNTN_NM,
        image: markerImage,
      });

      const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;">${spot.properties.MNTN_NM}<br>${spot.properties.SAFE_SPOT2}</div>`,
      });

      kakao.maps.event.addListener(marker, 'mouseover', () => infowindow.open(map.value, marker));
      kakao.maps.event.addListener(marker, 'mouseout', () => infowindow.close());
    });
  } catch (error) {
    console.error(`${url} 파일 로드 중 에러 발생:`, error);
  }
}

function processGeoJSON(geojsonData) {
  let allCoordinates = [];

  geojsonData.features.forEach((feature) => {
    if (feature.properties.PMNTN_NM && feature.properties.PMNTN_NM.includes('')) {
      let coordinates = [];

      if (feature.geometry.type === 'MultiLineString') {
        feature.geometry.coordinates.forEach(line => {
          coordinates = coordinates.concat(line.map((coord) => ({
            lng: coord[0],
            lat: coord[1],
            elevation: feature.properties.DN || 0
          })));
        });
      } else if (feature.geometry.type === 'LineString') {
        coordinates = feature.geometry.coordinates.map((coord) => ({
          lng: coord[0],
          lat: coord[1],
          elevation: feature.properties.DN || 0
        }));
      }
      routeCoordinates.value = allCoordinates;
      allCoordinates = allCoordinates.concat(coordinates);
    }
  });
  
  if (allCoordinates.length > 0) {
    addRouteLayer(allCoordinates);
    drawElevationChart(allCoordinates);
    renderSlopeOverlay(geojsonData);
  } else {
    console.log('유효한 구간 데이터가 없습니다.');
  }
}


// 경로(Base Trail) 레이어 추가 함수 -- 경사 색상은 이 함수가 아니라
// renderSlopeOverlay()가 별도 Overlay로 그린다 (Base + Overlay 구조).
function addRouteLayer(coordinates) {
  drawBaseRoute(coordinates);

  const bounds = new kakao.maps.LatLngBounds();
  coordinates.forEach(coord => bounds.extend(new kakao.maps.LatLng(coord.lat, coord.lng)));
  map.value.setBounds(bounds);
}

// 20m SlopeSection Backend API 기반 경사 Overlay. 이 화면은 특정 코스를
// 선택하지 않고 전체 Trail을 한 번에 보여주므로(PMNTN_NM 필터가 사실상 no-op), Trail
// GeoJSON 응답에 실제로 존재하는 trailId 전체를 그대로 읽어 그 Trail들의 SlopeSection을
// 모두 같은 지도에 겹쳐 그린다 -- trail.id는 surrogate PK이므로 하드코딩된 목록을 쓰지 않는다.
let slopeOverlayPolylines = [];

async function renderSlopeOverlay(trailGeoJson) {
  slopeOverlayPolylines.forEach((polyline) => polyline.setMap(null));
  slopeOverlayPolylines = [];

  for (const trailId of resolveAllTrailIds(trailGeoJson)) {
    try {
      const slopeGeoJson = await fetchSlopeSections(trailId, SLOPE_WINDOW_METERS);
      slopeGeoJson.features.forEach((feature) => {
        const color = getEstimatedSlopeColor(feature.properties.estimatedSlopePercent);
        if (color == null) {
          // invalid/missing slope (not the same thing as a measured ~0% slope) -- skip this
          // Overlay Feature entirely; the Base Trail Layer already shows this geometry.
          return;
        }
        const path = feature.geometry.coordinates.map(([lng, lat]) => new kakao.maps.LatLng(lat, lng));
        const polyline = new kakao.maps.Polyline({
          path,
          strokeWeight: 5,
          strokeColor: color,
          strokeOpacity: 0.8,
          strokeStyle: 'solid',
        });
        polyline.setMap(map.value);
        slopeOverlayPolylines.push(polyline);
      });
    } catch (error) {
      // SlopeSection API 실패는 해당 Trail의 경사 Overlay만 생략시킨다 -- Base Trail은 유지된다.
      console.error(`SlopeSection API 로드 중 에러 발생 (trailId=${trailId}, Base Trail은 계속 표시됩니다):`, error);
    }
  }
}

function drawBaseRoute(coordinates) {
  const linePath = coordinates.map(coord => new kakao.maps.LatLng(coord.lat, coord.lng));

  const basePolyline = new kakao.maps.Polyline({
    path: linePath,
    strokeWeight: 5,
    strokeColor: '#32CD32',
    strokeOpacity: 1,
    strokeStyle: 'solid'
  });

  basePolyline.setMap(map.value);
}

function calculateDistance(index, coord, coordinates) {
  if (index === 0) return 0;

  const prevCoord = coordinates[index - 1];

  const R = 6371;
  const dLat = deg2rad(coord[1] - prevCoord[1]);
  const dLng = deg2rad(coord[0] - prevCoord[0]);

  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(deg2rad(prevCoord[1])) * Math.cos(deg2rad(coord[1])) *
    Math.sin(dLng / 2) * Math.sin(dLng / 2);

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

// 경사(slope) 계산은 더 이상 Frontend 책임이 아니다 -- 기존
// calculateHaversineDistance()/calculateSlope()는 제거했다. 색상 매핑은
// src/api/slopeSection.js의 getEstimatedSlopeColor()가, 경사 계산 자체는 Backend
// SlopeSectionService가 담당한다. deg2rad()는
// drawElevationChart()의 거리 계산에서 계속 쓰이므로 유지한다.

function deg2rad(deg) {
  return deg * (Math.PI / 180);
}

function drawElevationChart(data) {
  const ctx = document.getElementById('elevationChart').getContext('2d');

  const distances = calculateCumulativeDistances(data.map(point => [point.lng, point.lat]));
  const totalDistance = distances[distances.length - 1];
  const scaleFactor = 645 / totalDistance;

  const scaledDistances = distances.map(distance => distance * scaleFactor);
  const elevations = data.map(point => point.elevation);

  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }

  chartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: scaledDistances,
      datasets: [
        {
          label: '고도 (m)',
          data: elevations,
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 2,
          fill: false,
        },
      ],
    },
    options: {
      scales: {
        x: {
          title: {
            display: true,
            text: '거리 (m)',
          },
          ticks: {
            autoSkip: true,
            maxTicksLimit: 10,
            maxRotation: 0,
            minRotation: 0,
            callback: function(value) {
              return value.toFixed(2) + ' m';
            }
          }
        },
        y: {
          title: {
            display: true,
            text: '고도 (m)',
          },
        },
      },
      interaction: {
        mode: 'index',
        intersect: false,
      },
      plugins: {
        tooltip: {
          enabled:false,
        },
      },
      onClick: (event, elements) => {
        if (elements.length > 0) {
          const index = elements[0].index;
          highlightRouteOnMap(index);
        }
      },
      onHover: (event, elements) => {
        if (elements.length > 0) {
          const index = elements[0].index;
          highlightRouteOnMap(index);
        }
      },
    },
  });
}

function calculateCumulativeDistances(coordinates) {
  let cumulativeDistances = [];
  let totalDistance = 0;

  for (let i = 0; i < coordinates.length; i++) {
    if (i > 0) {
      totalDistance += calculateDistance(i, coordinates[i], coordinates);
    }
    cumulativeDistances.push(totalDistance);
  }

  return cumulativeDistances;
}


function highlightRouteOnMap(index) {
  if (index >= 0 && index < routeCoordinates.value.length) {
    const point = routeCoordinates.value[index];

    console.log("마우스 오버 인덱스:", index);
    console.log("해당 좌표:", point);

    if (currentMarker.value) {
      currentMarker.value.setMap(null);
    }

    currentMarker.value = new kakao.maps.Marker({
      position: new kakao.maps.LatLng(point.lat, point.lng),
    });

    currentMarker.value.setMap(map.value);

    map.value.panTo(new kakao.maps.LatLng(point.lat, point.lng));
  } else {
    console.error(`Invalid index: ${index}`);
  }
}



const mountainName = ref("");
const dailyWeather = ref([]);
const sunriseTimes = ref([]);
const sunsetTimes = ref([]);


const fetchWeatherData = async (latitude, longitude) => {
  try {
    const apiKey = "5c48577c775896e979e7bcc3b225b730";
    const response = await axios.get(
      `https://api.openweathermap.org/data/2.5/forecast?lat=${latitude}&lon=${longitude}&appid=${apiKey}`
    );

    console.log(response.data);

    const groupedByDay = {};
    response.data.list.forEach(item => {
      const date = item.dt_txt.split(" ")[0];
      if (!groupedByDay[date]) groupedByDay[date] = [];
      groupedByDay[date].push(item);
    });

    const selectedWeather = [];
    Object.keys(groupedByDay).forEach(date => {
      const { currentTemp, minTemp, maxTemp } = calculateMinMaxTemp(groupedByDay[date]);
      const daySummary = {
        date,
        currentTemp,
        minTemp,
        maxTemp,
        ...groupedByDay[date][0],
      };
      selectedWeather.push(daySummary);
    });

    dailyWeather.value = selectedWeather;

    const sunriseSunsetPromises = dailyWeather.value.map(day =>
      axios.get(
        `https://api.sunrise-sunset.org/json?lat=${latitude}&lng=${longitude}&date=${day.date}&formatted=0`
      )
    );

    const sunriseSunsetResults = await Promise.all(sunriseSunsetPromises);
    sunriseTimes.value = sunriseSunsetResults.map(result => formatTime(result.data.results.sunrise));
    sunsetTimes.value = sunriseSunsetResults.map(result => formatTime(result.data.results.sunset));

  } catch (error) {
    console.error("Error fetching weather data:", error);
  }
};


const calculateMinMaxTemp = (dayData) => {
  const temps = dayData.map(item => item.main.temp);
  const currentTemp = dayData[0].main.temp;
  const minTemp = Math.min(...temps);
  const maxTemp = Math.max(...temps);
  return { currentTemp, minTemp, maxTemp };
};

const formatTime = (time) => {
  const date = new Date(time);
  return date.toLocaleTimeString("ko-KR", { hour: "2-digit", minute: "2-digit", timeZone: "Asia/Seoul" });
};

</script>

<style scoped>
#map {
  height: 600px;
  width: 100%;
  position: relative; 
}

canvas {
  height: 200px;
  width: 100%;
}
.weather-forecast {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 100%;
}

.forecast-header {
  text-align: center;
  margin-bottom: 20px;
}

.forecast-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 5px;
  width: 100%;
}

.forecast-item {
  text-align: center;
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 10px;
  background-color: #f7f7f7;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
}

.forecast-date {
  font-weight: bold;
  margin-bottom: 10px;
}

.forecast-info {
  margin-top: 10px;
}

.current-temperature {
  font-size: 30px;
  font-weight: bold;
  color: #333;
}

.weather-icon {
  font-size: 30px;
  margin-top: 10px;
}

.description {
  font-size: 16px;
  color: #555;
  margin-top: 5px;
}

.temperature-range {
  margin-top: 5px;
}

.min-temp, .max-temp {
  font-size: 16px;
  color: #555;
}

.sun-info {
  margin-top: 10px;
}

.icon-small {
  width: 16px;
  height: 16px; 
  vertical-align: middle;
  margin-right: 5px;
}

.rain-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.rain-icon {
  width: 20px;
  height: 30px;
  background-color: rgba(173, 216, 230, 0.5);
  border-radius: 50%;
  position: relative;
  overflow: hidden;
}

.rain-icon::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: var(--rain-level, 0);
  background-color: rgba(30, 144, 255, 1);
}

.rain-chance {
  font-size: 14px;
  font-weight: bold;
}

</style>
