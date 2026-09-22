<template>
  <div id="app">
    <div id="map" style="width: 100%; height: 500px;"></div>
    <canvas id="elevationChart" width="400" height="200"></canvas>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import * as toGeoJSON from '@mapbox/togeojson';
import Chart from 'chart.js/auto';

const map = ref(null);
let elevationData = [];

onMounted(() => {
  initializeMap();
});

function initializeMap() {
  const script = document.createElement('script');
  script.onload = () => kakao.maps.load(createMap);
  script.src =
    'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false&libraries=services';
  document.head.appendChild(script);
}

function createMap() {
  const container = document.getElementById('map');
  const options = {
    center: new kakao.maps.LatLng(37.60255081802746, 126.946665543971804),
    level: 5,
  };
  map.value = new kakao.maps.Map(container, options);

  map.value.setMapTypeId(kakao.maps.MapTypeId.SKYVIEW);

  loadGPXFromServer('/gpx/북한산.gpx');
}

async function loadGPXFromServer(url) {
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const gpxText = await response.text();
    const parser = new DOMParser();
    const gpxDOM = parser.parseFromString(gpxText, 'application/xml');

    const geojson = toGeoJSON.gpx(gpxDOM);

    drawGPXPath(geojson);
    drawElevationChart();
  } catch (error) {
    console.error('GPX 파일 로드 중 에러 발생:', error);
  }
}

function drawGPXPath(geojson) {
  geojson.features.forEach((feature) => {
    if (feature.geometry && feature.geometry.coordinates) {
      const path = feature.geometry.coordinates.map((coord) => {
        elevationData.push(coord[2] || 0);
        return new kakao.maps.LatLng(coord[1], coord[0]);
      });

      const polyline = new kakao.maps.Polyline({
        path: path,
        strokeWeight: 5,
        strokeColor: '#FF0033',
        strokeOpacity: 0.85,
        strokeStyle: 'solid',
      });

      polyline.setMap(map.value);
    }
  });
}

function drawElevationChart() {
  const ctx = document.getElementById('elevationChart').getContext('2d');
  const labels = elevationData.map((_, index) => `${index + 1} km`);
  
  new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [
        {
          label: '고도 (m)',
          data: elevationData,
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 2,
          fill: false,
        },
      ],
    },
    options: {
      responsive: true,
      scales: {
        x: {
          title: {
            display: true,
            text: '거리 (km)',
          },
        },
        y: {
          title: {
            display: true,
            text: '고도 (m)',
          },
        },
      },
    },
  });
}
</script>

<style scoped>
#map {
  height: 100%;
}

canvas {
  margin-top: 20px;
}
</style>
