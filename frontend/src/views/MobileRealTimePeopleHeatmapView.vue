<template>
  <div id="infomain">
      <div class="row" style="margin: 0px; padding: 0px;">

          <div class="btn-group" role="group" aria-label="Basic radio toggle button group"
              style="position: fixed; top: -15px;  z-index: 100;">
              <div class="d-flex">
                  <button class="button-item ms-2 mt-1" @click="goBack"
                      style="border-radius: 50%; height: 37px; width: 37px; z-index: 10;"><img src="/images/뒤로가기.png" 
                          alt="돋보기 아이콘" width="17" height="17"></button>

                  <div class="review-search" style="top: 30px; z-index: 2;">
                      <div class="search-container">
                          <input type="text" placeholder="검색" v-model="searchQuery" />
                          <button class="search-button">
                              <img src="/images/돋보기white.png" alt="Search" />
                          </button>
                      </div>
                  </div>

                  <div class="dropdown">
                      <a role="button" data-bs-toggle="dropdown">
                          <img src="/images/hamburgerIcon.png" id="hambugermenu" alt="돋보기 아이콘" width="37" height="37">
                      </a>
                      <ul class="dropdown-menu">
                          <li><a class="dropdown-item delay-1" @click="goToCourseInfoViewPage(courseData)">정보</a></li>
                          <li><a class="dropdown-item delay-2" @click="goToCoursePreviewPage()">영상</a></li>
                          <li><a class="dropdown-item delay-3" @click="goToCourse3DPage()"
                                  style="margin-left: 0.009rem;">3D</a></li>
                      </ul>
                  </div>
              </div>
          </div>
          <div id="map" ref="mapContainer" class="map-container"></div>

          <div class="RealTimeCard" style="margin-top: 25rem;">
              <p class="RealTime">{{ currentTime }}</p>
          </div>
      </div>
  </div>

  <div class="drag-panel" :style="{ top: panelTop + 'px' }">
      <div class="drag-handle" @click="togglePanel"></div>
      <div class="content">
          <p id="contentTitle">실시간 코스 이용 현황</p>

          <div class="section-wrapper">
              <div class="section">
                  <div id="todayPeopleTOP5" class="d-flex justify-content-center" style="text-align: center">
                      <h3 id="todayPeopleTOP5-title" style="margin-bottom:0px; color: white;">금일 유동 인구 TOP 3</h3>
                  </div>
                  <ul class="station-list">
                      <li>
                          <div class="rank">1</div>
                          <div class="station">마루</div>
                          <div class="number">47 명</div>
                      </li>
                      <li>
                          <div class="rank">2</div>
                          <div class="station">홍제</div>
                          <div class="number">24 명</div>
                      </li>
                      <li>
                          <div class="rank">3</div>
                          <div class="station">무악</div>
                          <div class="number">11 명</div>
                      </li>
                  </ul>
              </div>

              <div class="section me-3">
                  <div id="CoursePeopleTOP5" class="d-flex justify-content-center" style="text-align: center">
                      <h3 id="CoursePeopleTOP5-title" style="margin-bottom:0px; color: white;">누적 유동 인구 TOP 3</h3>
                  </div>
                  <ul class="station-list">
                      <li>
                          <div class="rank">1</div>
                          <div class="station">무악</div>
                          <div class="number">67,969 명</div>
                      </li>
                      <li>
                          <div class="rank">2</div>
                          <div class="station">마루</div>
                          <div class="number">52,164 명</div>
                      </li>
                      <li>
                          <div class="rank">3</div>
                          <div class="station">부암</div>
                          <div class="number">23,164 명</div>
                      </li>
                  </ul>
              </div>
          </div>
      </div>


      <div class="divider" style="height: 1px; background-color: #dcdcdc; margin: 25px 0;"></div>

      <div class="circle-chart-container">
          <p class="chart-title">평균 등산 시간</p>
          <div class="circle-chart">
              <svg viewBox="0 0 36 36" class="circular-chart green">
                  <path class="circle-bg" d="M18 2.0845
          a 15.9155 15.9155 0 0 1 0 31.831
          a 15.9155 15.9155 0 0 1 0 -31.831" />
                  <path class="circle" stroke-dasharray="80, 100" d="M18 2.0845
          a 15.9155 15.9155 0 0 1 0 31.831
          a 15.9155 15.9155 0 0 1 0 -31.831" />
                  <text x="18" y="19" class="chart-value">52분</text>
              </svg>

          </div>
          <div class="chart-info">
              <p>[전일 대비] <span class="down">▼ -0.61%</span></p>
              <p>[전주 대비] <span class="down">▼ -0.56%</span></p>
          </div>
      </div>

      <div class="divider" style="height: 1px; background-color: #dcdcdc; margin: 25px 0;"></div>

      <div class="circle-chart-container">
          <p class="chart-title">평균 걸음 속도</p>
          <div class="circle-chart">
              <svg viewBox="0 0 36 36" class="circular-chart blue">
                  <path class="circle-bg" d="M18 2.0845
          a 15.9155 15.9155 0 0 1 0 31.831
          a 15.9155 15.9155 0 0 1 0 -31.831" />
                  <path class="circle" stroke-dasharray="65, 100" d="M18 2.0845
          a 15.9155 15.9155 0 0 1 0 31.831
          a 15.9155 15.9155 0 0 1 0 -31.831" />
                  <text x="18" y="19" class="chart-value">4.2km/h</text>
              </svg>

          </div>
          <div class="chart-info">
              <p>[전일 대비] <span class="down">▼ -0.69%</span></p>
              <p>[전주 대비] <span class="up">▲ +0.3%</span></p>
          </div>
      </div>
  </div>
  <MobileFooterView5 class="footer"></MobileFooterView5>
</template>


<script setup>
import MobileFooterView5 from "@/components/MobileFooterView5.vue";
import { ref, onMounted, nextTick, onUnmounted } from 'vue';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import 'leaflet.heat';
import { useRouter } from 'vue-router';

const router = useRouter();

const currentTime = ref('');

const updateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleTimeString('ko-KR', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
  });
};

const mapContainer = ref(null);
let leafletMap = null;
let heatmapLayer = null;

onMounted(async () => {
  await nextTick();
  initializeMap();
  const geojsonData = await loadGeoJSON();
  const getjsonData2 = await loadGeoJSON2();
  initializeHeatmap(geojsonData);
  drawRoutesFromGeoJSON(getjsonData2);
  simulateRealTimeHeatmap(geojsonData);
  updateTime();
  const timer = setInterval(updateTime, 1000);
  onUnmounted(() => {
      clearInterval(timer);
  });
});

function initializeMap() {
  leafletMap = L.map(mapContainer.value, {
      zoomControl: false
  }).setView([37.622955, 126.977834], 13);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
  }).addTo(leafletMap);

  const zoomControl = L.control.zoom({
      position: 'topleft'
  }).addTo(leafletMap);

  const zoomElement = zoomControl.getContainer();

  zoomElement.style.position = 'absolute';
  zoomElement.style.top = '100%';
  zoomElement.style.left = '80%';
  zoomElement.style.transform = 'translate(0%, 570%)';
}
async function loadGeoJSON2() {
  try {
      const response = await fetch('/api/spatial/trails/geojson');
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      return await response.json();
  } catch (error) {
      console.error('GeoJSON 파일 로드 실패:', error);
  }
}

async function loadGeoJSON() {
  try {
      const response = await fetch('/api/spatial/trails/geojson');
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      return await response.json();
  } catch (error) {
      console.error('GeoJSON 파일 로드 실패:', error);
  }
}

function initializeHeatmap(geojsonData) {
  const heatmapData = extractHeatmapDataFromGeoJSON(geojsonData);
  heatmapLayer = L.heatLayer(heatmapData, {
      radius: 25,
      blur: 15,
      maxZoom: 17,
  }).addTo(leafletMap);
}

function extractHeatmapDataFromGeoJSON(geojsonData) {
  const heatmapData = [];
  geojsonData.features.forEach((feature) => {
      const { type, coordinates } = feature.geometry;
      if (type === 'LineString') {
          coordinates.forEach(([lng, lat]) => {
              heatmapData.push([lat, lng, 0.5]);
          });
      } else if (type === 'MultiLineString') {
          coordinates.flat().forEach(([lng, lat]) => {
              heatmapData.push([lat, lng, 0.5]);
          });
      }
  });
  return heatmapData;
}

function drawRoutesFromGeoJSON(geojsonData) {
  geojsonData.features.forEach((feature) => {
      const { type, coordinates } = feature.geometry;

      if (type === 'LineString') {
          const latLngs = coordinates.map(([lng, lat]) => [lat, lng]);
          L.polyline(latLngs, {
              color: 'blue',
              weight: 4,
              opacity: 0.8,
          }).addTo(leafletMap);
      } else if (type === 'MultiLineString') {
          coordinates.forEach((line) => {
              const latLngs = line.map(([lng, lat]) => [lat, lng]);
              L.polyline(latLngs, {
                  color: 'blue',
                  weight: 4,
                  opacity: 0.8,
              }).addTo(leafletMap);
          });
      }
  });
}

function simulateRealTimeHeatmap(geojsonData) {
  const allCoordinates = extractHeatmapDataFromGeoJSON(geojsonData).map(([lat, lng]) => ({
      lat,
      lng,
  }));

  const movingObjects = createMovingObjects(allCoordinates, 200);

  function updateHeatmap() {
      const heatmapData = movingObjects.map((obj) => {
          obj.updatePosition();
          return [obj.currentPosition.lat, obj.currentPosition.lng, 0.7];
      });

      if (heatmapLayer) {
          heatmapLayer.setLatLngs(heatmapData);
      }
  }

  setInterval(updateHeatmap, 100);
}

function createMovingObjects(coordinates, count) {
  return Array.from({ length: count }, () => {
      const startIndex = Math.floor(Math.random() * coordinates.length);
      const isReverse = Math.random() > 0.5;

      return {
          currentPosition: coordinates[startIndex],
          currentIndex: startIndex,
          isReverse,
          updatePosition() {
              this.currentIndex += this.isReverse ? -1 : 1;
              if (this.currentIndex < 0) {
                  this.currentIndex = coordinates.length - 1;
              } else if (this.currentIndex >= coordinates.length) {
                  this.currentIndex = 0;
              }
              this.currentPosition = coordinates[this.currentIndex];
          },
      };
  });
}

const panelTop = ref(window.innerHeight - 289);
const isPanelOpen = ref(false);

const togglePanel = () => {
  if (isPanelOpen.value) {
      panelTop.value = window.innerHeight - 650;
  } else {
      panelTop.value = 0;
  }
  isPanelOpen.value = !isPanelOpen.value;
};

const goBack = () => {
  console.log('뒤로가기')
  router.back();
};
</script>

<style scoped>
@font-face {
  font-family: 'TheJamsil5Bold';
  src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_2302_01@1.0/TheJamsil5Bold.woff2') format('woff2');
  font-weight: 700;
  font-style: normal;
}

.section-wrapper {
  display: flex;

}

#infomain {
  position: relative;
  background-color: #f5f5f5;
  font-family: Arial, sans-serif;
  height: 100vh;
}

.btn-group {
  flex-direction: row;
  align-items: left;
  margin: 0%;
  padding: 0%;
  margin-top: 12%;
}

.button-item {
  background-color: #ffffff;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
}

.button-item img {
  width: 30px;
  height: 30px;
}

.button-item:hover {
  background-color: #e03e00;
}

.dropdown {
  align-items: right;
  position: fixed;
  top: 0;
  right: 0;
  margin-top: 2rem;
  margin-right: 2.8rem;
}

#hambugermenu {
  background-color: white;
  border-radius: 50%;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
  position: absolute;
}

.dropdown-menu {
  background-color: #fff;
  border: 1px solid #ddd;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-20px);
  transition: opacity 0.3s, visibility 0s 0.3s, transform 0.3s ease;
  margin-top: 10px;
}

.dropdown-menu.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
  transition: opacity 0.3s, visibility 0s 0s, transform 0.3s ease;
}

.dropdown-item {
  padding: 10px 15px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.dropdown-item:hover {
  background-color: #f0f0f0;
}

#map {
  width: 100%;
  height: 100vh;
  position: relative;
  z-index: 10;
}

.map-legend {
  position: absolute;
  top: 15%;
  right: 30px;
  background-color: rgba(255, 255, 255, 0.85);
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 200;
  font-size: 14px;
  font-family: 'TheJamsil4Bold';
  color: #333;
}

.legend-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.legend-icon {
  width: 15px;
  height: 15px;
  margin-right: 10px;
  border-radius: 50%;
}

.legend-item span {
  font-size: 14px;
  color: #333;
}

.drag-panel {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #ffffff;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  transition: top 0.3s ease-in-out;
  z-index: 3000;
  overflow-y: auto;
  max-height: calc(100vh - 70px);
  box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.3);
}

.drag-panel::-webkit-scrollbar {
  display: none;
}

.drag-handle {
  width: 40px;
  height: 4px;
  background-color: #ddd;
  border-radius: 2px;
  margin: 10px auto;
  cursor: pointer;
}

.content {
  margin-top: 20px;
}

.stat-section1 p {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.stats-container {
  display: flex;
  justify-content: space-between;
}

.section {
  flex: 1;
  text-align: center;
  border: 1px solid #ccc;
  padding: 5px;
  margin-left: 4%;
  border-radius: 5px;
  background-color: #f9f9f9;
}

h3 {
  font-size: 16px;
  color: #444;
  margin-bottom: 15px;
}

ul.station-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.station-list li {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.rank {
  font-size: 16px;
  font-weight: bold;
  color: #FF4500;
  width: 30px;
}

.station {
  font-size: 16px;
  color: #333;
  flex-grow: 1;
  text-align: left;
}

.station-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.number {
  font-size: 16px;
  color: #888;
  text-align: right;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  opacity: 0;
  animation: fadeIn 0.8s forwards;
}

.popup-content {
  background-color: #fff;
  padding: 2em;
  border-radius: 20px;
  max-width: 500px;
  max-height: 1000px;
  overflow-y: auto;
  width: 90%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  transform: scale(0.8);
  animation: scaleIn 0.8s forwards;
}

#popupTitle {
  margin-top: 2em;
  margin-bottom: 2.5em;
  text-align: center;
  font-family: 'TheJamsil4Bold';
  font-weight: 500;
  font-size: 18px;
}

@keyframes fadeIn {
  to {
      opacity: 1;
  }
}

@keyframes scaleIn {
  to {
      transform: scale(1);
  }
}

body.no-scroll {
  overflow: hidden;
}

@font-face {
  font-family: 'TheJamsil3Regular';
  src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_2302_01@1.0/TheJamsil5Bold.woff2') format('woff2');
  font-weight: 600;
  font-style: normal;
}

h1,
h2,
h3,
p {
  font-family: 'TheJamsil4Medium', sans-serif;
  font-weight: 700;
}

#contentTitle {
  font-family: 'TheJamsil5Bold', sans-serif;
  font-size: 20px;
  margin-left: 25%;
}

.search-bar {
  position: fixed;
  top: 76px;
  left: 55%;
  transform: translateX(-50%);
  z-index: 200;
  padding: 0 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 65%;
  max-width: 500px;
}

.search-bar input {
  width: 100%;
  height: 34.8px;
  padding: 12px 20px;
  border-radius: 25px;
  border: 1px solid #ffffff;
  font-size: 16px;
  box-sizing: border-box;
  transition: all 0.3s ease;
  background-color: #ffffff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

.search-bar input:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 10px rgba(76, 175, 80, 0.5);
  background-color: #fff;
}

#CoursePeopleTOP5 {
  background-color: #ff6c1e;
  padding: 5px;
  border-radius: 5px;
  color: white !important;
}

#todayPeopleTOP5 {
  background-color: #ff6c1e;
  padding: 5px;
  border-radius: 5px;
  color: white !important;
}


.circle-chart-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: 10px;
}

.circle-chart {
  position: relative;
  width: 200px;
  height: 200px;
  margin-bottom: 10px;
}

.circle-bg {
  fill: none;
  stroke: #e6e6e6;
  stroke-width: 3.8;
}

.circle {
  fill: none;
  stroke-width: 2.8;
  stroke-linecap: round;
  transform: rotate(-90deg);
  transform-origin: center;
  transition: stroke-dasharray 0.5s;
}

.circular-chart.green .circle {
  stroke: #4caf50;
}

.circular-chart.blue .circle {
  stroke: #2196f3;
}

.chart-value {
  font-family: 'TheJamsil';
  font-weight: 500;
  font-size: 0.35em;
  fill: #333333;
  text-anchor: middle;
  dominant-baseline: middle;

}

.chart-title {
  font-family: 'TheJamsil';
  font-size: 20px;
  font-weight: 400;
  margin-top: 1.3rem;
  text-align: center;
}

.chart-info {
  font-family: 'TheJamsil';
  font-size: 16px;
  font-weight: 300;
  text-align: center;
  margin-top: 1rem;
}

.chart-info p {
  margin: 5px 0;
  font-size: 14px;
}

.down {
  color: red;
}

.up {
  color: blue;
}

.RealTimeCard {
  position: fixed;
  top: 10;
  border: none;
  color: rgb(0, 0, 0);
  margin-top: 21.5rem;
  margin-left: 11rem;
  z-index: 90;
  height: 100px;
}

.RealTime {
  font-family: 'TheJamsil';
  font-size: 23px;
  font-weight: 500;
  color: #ffffff;
  text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.8);
}

.review-search input {
  width: 20px;
  padding: 5px;
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
  border: 1px solid #ddd;
}

.search-container {
  display: flex;
  align-items: center;
  border: none;
  background-color: transparent;
  width: 100%;
  height: 40px;
  justify-content: center;
  z-index: 100;
}

.search-container input {
  flex: 1;
  max-width: 190px;
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.08);
  padding: 0 10px;
  font-size: 16px;
  color: #333;
  outline: none;
  height: 40px;
  border: solid 1px #868e96;
  z-index: 100;
}

.search-button {
  width: 45px;
  height: 40px;
  background-color: #327C2B;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
}

.search-button img {
  width: 30px;
  height: 30px;
  filter: brightness(0) invert(1);
}

.search-container input::placeholder {
  color: #aaa;
  font-size: 16px;
}

.review-search {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  position: fixed;
  top: 20px;
  z-index: 20;
}
</style>