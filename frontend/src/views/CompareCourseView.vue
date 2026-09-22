<template>
  <div id="app">
      <MobileHeaderView></MobileHeaderView>
      <div class="app-main">
          <div class="map-container">
          <div class="button-group">
              <button class="button-item" @click="goBack" style="border-radius: 50%; height: 40px; width: 40px;"><img src="/images/뒤로가기.png"  alt="뒤로가기 아이콘" width="24" height="24"></button>
          </div>
          <div class="button-group2">
              <button class="button-item" @click="handleClick()" style="border-radius: 50%; height: 40px; width: 40px;">
                  <i class="bi bi-eye-fill"></i>
              </button>
          </div>
          <div class="button-group3">
              <button class="button-item" style="border-radius: 50%; height: 40px; width: 40px;" data-bs-toggle="modal" data-bs-target="#exampleModal">
                <i class="bi bi-question-lg"></i>
              </button>
          </div>
          <div class="map-legend ms-4">
            <div class="legend-item">
              <div class="legend-icon" style="background-color:#FF4500;"></div>
              <span>오르막</span>
            </div>
            
            <div class="legend-item">
              <div class="legend-icon" style="background-color:#32CD32;"></div>
              <span>평지</span>
            </div>
            
            <div class="legend-item">
              <div class="legend-icon" style="background-color:#1E90FF;"></div>
              <span>내리막</span>
            </div>
          </div>
          
              <div id="map">
                  <div class="card-container">
                    <div 
                      v-for="(course, index) in courseData" 
                      :key="index" 
                      class="card" 
                      :class="{ clicked: isClicked[index] }" 
                      @click="handleClickCard(index)"
                    >
                      <span>{{ course.mountainName }}</span>
                      <h4 style="font-weight: 700;" class="m-0">{{ course.courseName }}코스</h4>
                      <span style="font-size: small;">{{ course.distance }} | {{ course.duration }}</span>
                      
                      <span class="mt-1" v-if="incidentInfo[index]?.typeCounts" style="font-size: small;">
                        <span class="m-0" v-for="(count, type) in incidentInfo[index].typeCounts" :key="type" style="display: block;  margin-bottom: 5px;">
                          <span style="display: inline-flex; align-items: center;">
                            {{ type }} : {{ count }} 
                            <div
                              class="bar"
                              :style="{ width: (count * 20) + 'px', 
                              backgroundColor: getBarColor(count, type) }">
                            </div>
                          </span>
                        </span>
                      </span>
                    </div>
                  </div>

              </div>
          </div>
      </div>
      <MobileFooterView></MobileFooterView>
  </div>

<div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
<div class="modal-dialog modal-dialog-centered">
  <div class="modal-content p-0">
    <div class="modal-body pt-3 pb-3 ps-2 pe-2">
      <table class="comparison-table">
          <thead>
              <tr>
                  <th>코스</th>
                  <th>{{ courseData[0]?.courseName }}코스</th>
                  <th>{{ courseData[1]?.courseName }}코스</th>
              </tr>
          </thead>
          <tbody>
              <tr>
                <th>산 이름</th>
                <td v-for="(course, index) in courseData" :key="'mountainName-' + index">
                    {{ course.mountainName }}
                </td>
              </tr>
              <tr>
                <th>총 거리</th>
                <td v-for="(course, index) in courseData" :key="'distance-' + index">
                    {{ course.distance }}
                </td>
              </tr>
              <tr>
                <th>난이도</th>
                <td v-for="(course, index) in courseData" :key="'distance-' + index"  :style="{color: getLevel(course.courseLevel)}">
                    {{ course.courseLevel }}
                </td>
              </tr>
              <tr>
                <th>후기</th>
                  <td v-for="(course,index) in courseStars" :key="index">
                    <div class="stars">
                      <i v-for="n in course.fullStars" :class="'bi bi-star-fill'" :key="'full-' + n + '-' + index"></i>
                      <i v-if="course.halfStar" class="bi bi-star-half"></i>
                      <i v-for="n in course.emptyStars" class="bi bi-star" :key="'empty-' + n + '-' + index"></i>
                    </div>
                  </td>
              </tr>
              <tr>
                <th>최고 고도</th>
                  <td v-for="(elev, index) in maxElev" :key="'elevation-' + index">
                    {{ elev }}m
                  </td>
              </tr>
              <tr>
                <th>예상 소요 시간</th>
                <td v-for="(course, index) in courseData" :key="'estimatedTime-' + index">
                    {{ course.duration }}
                </td>
              </tr>
              <tr v-for="(type) in typeList" :key="type">
                <th>{{ type }}</th>
                  <td v-for="(course, index) in incidentInfo" :key="index" :style="{
                    color: (course.typeCounts[type] === undefined ? 0 : course.typeCounts[type]) === getMinCount(type) ? 'black' : 'red'
                  }">
                  {{ course.typeCounts[type] || 0 }}건
                  </td>
              </tr>
          </tbody>
      </table>
    </div>
  </div>
</div>
</div>

</template>

<script setup>
import MobileFooterView from "@/components/MobileFooterView2.vue";
import MobileHeaderView from "@/components/MobileHeaderView.vue";
import { ref, onMounted} from "vue";
import { useRouter } from 'vue-router';
import { getCourseById } from "@/api/complaint";
import {
  resolveTrailId,
  SLOPE_WINDOW_METERS,
  getEstimatedSlopeColor,
  fetchTrailGeoJson,
  fetchSlopeSections,
} from '@/api/slopeSection.js';

const router = useRouter();

const goBack = () => router.back();

const addCustomOverlay = () => {
routeCoordinates.value.forEach(courseData => {
  if (map.value) {
    const minLat = Math.min(...courseData.coordinates.map(coord => coord.lat));
    const minLon = Math.min(...courseData.coordinates.map(coord => coord.lng));

    maxElev.value.push(Math.max(...courseData.coordinates.map(coord => coord.elevation)));

    const content = `
      <div class="overlay-container" style="display: inline-block;">
        <div class="label" style="background-color: white; padding: 4px 8px; border-radius: 8px; box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.15); color: black; font-size: 14px; text-align: center;">
          <span class="center">${courseData.courseName}</span>
        </div>
      </div>
    `;

    const position = new kakao.maps.LatLng(minLat + 0.0015, minLon);

    const customOverlay = new kakao.maps.CustomOverlay({
      position: position,
      content: content,
      zIndex: 9999
    });

    customOverlay.setMap(map.value);
    console.log("오버레이 성공함");
  } else {
    console.log("오버레이 실패함");
  }
});

}

function handleClick() {
if (map.value) {
  setMapBoundsToAllCourse(map.value);
} else {
  console.log("Map is not ready yet.");
}
}

const isClicked = ref([true, true]);

const handleClickCard = (index) => {
isClicked.value[index] = !isClicked.value[index];

if (isClicked.value[index]) {
    handleClick();
    console.log("클릭 해제");
  } else {
    setMapBoundsToCourse(routeCoordinates.value[index]);
}
};

const map = ref(null);
const routeCoordinates = ref([]);
let courseData = ref([]);
let incidentInfo = ref([]);
let typeList = ref([]);

let maxElev = ref([]);

function setMapBoundsToCourse(courseRoute) {
  const bounds = new kakao.maps.LatLngBounds();

  courseRoute.coordinates.forEach(coord => {
  bounds.extend(new kakao.maps.LatLng(coord.lat, coord.lng));
  });

  map.value.setBounds(bounds);
}

onMounted(async () => {
  initializeMap();
  await init();
});

async function init(){
  const selectedId = JSON.parse(sessionStorage.getItem("selectedCourseIds"));

const courses = await Promise.all(
  selectedId.map(async (id) => {
    return await getCourseById(id);
  })
);

courseData.value = courses;
console.log("코스데이터:", JSON.stringify(courseData.value[0], null, 2));
courseStars.value = courseData.value.map((course) => {
  return {
    fullStars: Math.floor(course.courseRate),
    halfStar: course.courseRate % 1 >= 0.5,
    emptyStars: 5 - Math.floor(course.courseRate) - (course.courseRate % 1 >= 0.5 ? 1 : 0)
  };
});

}

let courseStars = ref([]);

function initializeMap() {
  const script = document.createElement('script');
  script.onload = () => {
      kakao.maps.load(() => {
          createMap();
      });
  };
  script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false&libraries=clusterer,services';
  document.head.appendChild(script);
}

async function createMap() {
  map.value = new kakao.maps.Map(document.getElementById('map'), {
    center: new kakao.maps.LatLng(37.66433293993584, 127.01160029114365),
    level: 5,
  });
  await loadGeoJSONFromServer();
  await loadMarkers("/data/헬기장spot.geojson", '/images/helipad.png');
  await loadMarkers("/data/화장실.geojson", '/images/toilets.png');
  await loadMarkers("/data/2023산악사고_인왕산2.geojson", '/images/danger.png');
  incidentInfo.value = await countIncidentTypes("/data/2023산악사고_인왕산2.geojson",courseData);
  
}

// 정적 GeoJSON 대신 Trail GeoJSON API(DB 기반 TrailFeature)에서 경로를 불러온다
async function loadGeoJSONFromServer(targetMap = map.value) {
try {
  const geojsonData = await fetchTrailGeoJson();
  console.log('Trail GeoJSON data loaded:', geojsonData);
  processGeoJSON(geojsonData, targetMap);
  addCustomOverlay();
} catch (error) {
  console.error('Trail GeoJSON API 로드 중 에러 발생:', error);
}
}

async function countIncidentTypes(url, courseData) {
try {
  const response = await fetch(url);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  
  const geojsonData = await response.json();
  if (!geojsonData || !geojsonData.features) {
    console.error("Invalid GeoJSON data:", geojsonData);
    return {};
  }

  const courseTypeCounts = courseData.value.map(course => ({
    courseName: course.courseName,
    mountainName: course.mountainName,
    typeCounts: {}
  }));

  geojsonData.features.forEach(feature => {
    const { MNTN_NM, MNTN_NM2, type } = feature.properties;

    courseData.value.forEach((course, index) => {
      if (course.mountainName === MNTN_NM && course.courseName === MNTN_NM2 && type) {
        courseTypeCounts[index].typeCounts[type] = 
          (courseTypeCounts[index].typeCounts[type] || 0) + 1;
      }
    });
  });

  courseTypeCounts.forEach(course => {
    const sortedTypeCounts = {};
    const sortedKeys = Object.keys(course.typeCounts).sort();
    typeList.value = [...new Set([...typeList.value, ...sortedKeys])].sort();
    sortedKeys.forEach(key => {
      sortedTypeCounts[key] = course.typeCounts[key];
    });
    course.typeCounts = sortedTypeCounts;
  });

  console.log("Incident Type Counts by Course:", courseTypeCounts);
  console.log("typeList;", typeList.value);
  return courseTypeCounts;
  

} catch (error) {
  console.error('Error loading GeoJSON file:', error);
  return [];
}
}

function getLevel(level) {
if(level == '쉬움') {
  return 'green'
} else if(level == '보통') {
  return 'orange'
} else {
  return 'red'
}
}

function getMinCount(type) {
let minCount = Infinity;

if (incidentInfo.value && Array.isArray(incidentInfo.value)) {
  incidentInfo.value.forEach(incident => {
    const count = incident.typeCounts && incident.typeCounts[type] !== undefined 
                  ? incident.typeCounts[type] 
                  : 0;
    minCount = Math.min(minCount, count);
  });
}
return minCount;
}

function getBarColor(count, type) {
let color = 'orange';
let maxCount = 0;

if (incidentInfo.value && Array.isArray(incidentInfo.value)) {
  incidentInfo.value.forEach(incident => {
    if (incident.typeCounts && incident.typeCounts[type]) {
      maxCount = Math.max(maxCount, incident.typeCounts[type]);
    }
  });
}

if(incidentInfo.value[0].typeCounts[type]!=incidentInfo.value[1].typeCounts[type]){
    if (maxCount === count) {
    color = 'red';
  }
}


return color;
}


function processGeoJSON(geojsonData, targetMap) {
  routeCoordinates.value = courseData.value.map(course => ({
      courseName: course.courseName,
      coordinates: []
  }));

  geojsonData.features.forEach((feature) => {
      courseData.value.forEach((course, index) => {
          if (feature.properties.PMNTN_NM && feature.properties.PMNTN_NM.includes(course.courseName)) {
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

              routeCoordinates.value[index].coordinates.push(...coordinates);
          }
      });
  });

  routeCoordinates.value.forEach((courseRoute) => {
      if (courseRoute.coordinates.length > 0) {
          addRouteLayer(courseRoute.coordinates, targetMap);
          renderSlopeOverlay(courseRoute.courseName, targetMap, geojsonData);
      }
  });

  if (routeCoordinates.value.every(courseRoute => courseRoute.coordinates.length === 0)) {
      console.log('유효한 구간 데이터가 없습니다.');
  }
  console.log("좌표나눈거"+JSON.stringify(routeCoordinates));
}

let allCoordinates = [];

// 경로(Base Trail) 레이어 추가 함수 -- 경사 색상은 이 함수가 아니라
// renderSlopeOverlay()가 코스별로 별도 Overlay로 그린다 (Base + Overlay 구조).
function addRouteLayer(coordinates, targetMap) {
  allCoordinates.push(...coordinates);
  drawBaseRoute(coordinates);
  setMapBoundsToAllCourse(targetMap);
}

// 20m SlopeSection Backend API 기반 경사 Overlay. 이 화면은 여러 코스를 동시에
// 비교하므로 코스별로 독립적으로 호출/렌더링된다 -- 코스별 Overlay가 서로 섞이지 않도록
// slopeOverlayPolylinesByCourse에 course 이름별로 분리해서 추적하고, course 재렌더링 시
// 그 코스의 이전 Overlay만 제거한다.
const slopeOverlayPolylinesByCourse = {};

async function renderSlopeOverlay(courseName, targetMap, trailGeoJson) {
  if (slopeOverlayPolylinesByCourse[courseName]) {
    slopeOverlayPolylinesByCourse[courseName].forEach((polyline) => polyline.setMap(null));
  }
  slopeOverlayPolylinesByCourse[courseName] = [];

  try {
    const trailId = resolveTrailId(courseName, trailGeoJson);
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
      polyline.setMap(targetMap);
      slopeOverlayPolylinesByCourse[courseName].push(polyline);
    });
  } catch (error) {
    // SlopeSection API 실패는 해당 코스의 경사 Overlay만 생략시킨다 -- Base Trail은 유지된다.
    console.error(`SlopeSection API 로드 중 에러 발생 (${courseName}, Base Trail은 계속 표시됩니다):`, error);
  }
}

function setMapBoundsToAllCourse(targetMap) {
  const bounds = new kakao.maps.LatLngBounds();
  allCoordinates.forEach(coord => bounds.extend(new kakao.maps.LatLng(coord.lat, coord.lng)));
  targetMap.setBounds(bounds);
  setTimeout(() => {
  targetMap.panBy(0, 80);
  }, 200);
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

// 경사(slope) 계산은 더 이상 Frontend 책임이 아니다 -- 기존
// calculateHaversineDistance()/calculateSlope()/deg2rad()/getColorBySlope()는 제거했다.
// 색상 매핑은 src/api/slopeSection.js의 getEstimatedSlopeColor()가, 경사 계산 자체는
// Backend SlopeSectionService가 담당한다.

async function loadMarkers(url, imageSrc) {
  try {
      const response = await fetch(url);
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const markerData = await response.json();

      if (markerData && markerData.features) {
          const imageSize = new kakao.maps.Size(35, 45);
          const imageOpation = { offset: new kakao.maps.Point(12, 35) };
          const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOpation);

          markerData.features.forEach((spot) => {
              const isSelectedCourse = courseData.value.some(course => course.courseName === spot.properties.MNTN_NM2);
              
              if (isSelectedCourse) {
                  const lat = spot.geometry.coordinates[1];
                  const lng = spot.geometry.coordinates[0];
                  const markerPosition = new kakao.maps.LatLng(lat, lng);

                  const marker = new kakao.maps.Marker({
                      position: markerPosition,
                      map: map.value,
                      title: spot.properties.MNTN_NM2,
                      image: markerImage,
                  });

                  const infowindow = new kakao.maps.InfoWindow({
                      content: `<img src="/images/${spot.properties.image}" alt="사진" style="width: 150px; height: 100px;"/><br><div style="padding:5px;">${spot.properties.MNTN_NM2}<br>${spot.properties.SAFE_SPOT2}</div>`,
                  });

                  kakao.maps.event.addListener(marker, 'click', () => {
                      if (infowindow.getMap()) {
                          infowindow.close();
                      } else {
                          infowindow.open(map.value, marker);
                      }
                  });
              }
          });
      } else {
          throw new Error("Invalid GeoJSON structure.");
      }
  } catch (error) {
      console.error(`${url} 파일 로드 중 에러 발생:`, error);
  }
}



</script>

<style scoped>
#app {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.app-main {
  flex: 1;
  display: flex;
  justify-content: center;
}

.map-container {
  position: relative;
  flex: 1;
  width: 100%;
  height: 100%;
}

#map {
  width: 100%;
  height: 100%;
  position: relative;
}

.overlay-container {
display: inline-block;
}

.label {
background-color: white;
padding: 8px 16px;
border-radius: 8px;
box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.15);
color: black;
font-size: 14px;
text-align: center;
}

.button-group {
position: absolute;
top: 10px;
left: 10px;
z-index: 2;
}

.button-group2 {
position: absolute;
top: 10px;
right: 10px;
z-index: 2;
}

.button-group3{
position: absolute;
bottom: 245px;
right: 10px;
z-index: 2;
}

.map-legend {
position: absolute;
top: 10px;
right: 50px;
margin-right: 1.25em;
background-color: rgba(255, 255, 255, 0.8); 
border-radius: 10px;
font-size: 12px;
z-index: 10;
font-family: 'TheJamsil4Bold';
font-weight: 400;
font-size: 16px;
box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
display: flex; 
flex-direction: row; 
align-items: center; 
}

.legend-item {
display: flex;
align-items: center;
margin-bottom: 3px;
margin-top: 2px;
margin-right: 0.5rem;
font-family: 'TheJamsil4Bold';
font-weight: 400;
font-size: 14px;
}

.legend-icon {
width: 10px;
height: 10px;
margin-right: 5px;
margin-left: 5px;
border-radius: 10px;
}

.button-item {
  background-color: #ffffff;
  color: #000000;
  border: none;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
}

.button-item i {
font-size: 18px;
color: gray;
}

.card-container {
  display: flex;
  justify-content: space-between;
  position: absolute;
  bottom: 80px; 
  width: 100%;
  padding: 0 20px;
  z-index: 10;
}

.card {
  width: 155px;
  height: 150px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: start;
  transition: transform 0.3s ease, box-shadow 0.3s ease, background-color 0.3s ease, color 0.3s ease;
  cursor: pointer;
}

.card:hover:not(.clicked) {
transform: translateY(-10px);
box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
background-color: #04663f;
color: white;
}


.comparison-container::-webkit-scrollbar,
.comparison-container::-webkit-scrollbar-thumb,
.comparison-container::-webkit-scrollbar-track {
  background-color: transparent;
  width: 8px;
}

.bar {
height: 10px;
background-color: #04663f;
border-radius: 5px;
transition: width 0.3s ease-in-out;
margin-left: 10px;
}

div.modal-backdrop {
background-color: rgba(0, 0, 0, 0.041);
}

.comparison-table {
  width: 100%;
  border-collapse: collapse;
}

.comparison-table th,
.comparison-table td {
  border: 1px solid #ddd;
  padding: 0.5rem;
  text-align: center;
}

.comparison-table th {
  background-color: #f5f5f5;
  font-weight: bold;
}

.comparison-table td:first-child {
  font-weight: bold;
  background-color: #fafafa;
} 

.stars {
display: flex;
gap: 5px;
color: #28a745;
}

</style>
