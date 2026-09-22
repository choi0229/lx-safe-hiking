<template>
  <HeaderView></HeaderView>
  <div class="dashboard">
    <aside class="sidebar">
      <div class="header">
        <p @click="toggleExtraSidebar" class="header-title">자세히 보기</p>
      </div>
      <div class="content">
          <p id="contentTitle">코스 이용현황</p>
          <div class="stats-container">
            <div class="section">
              <h3 id="todayPeopleTOP5">금일 유동인구  TOP 5</h3>
              <ul class="station-list">
                <li>
                  <div class="rank">1</div>
                  <div class="station">마루</div>
                  <div class="number">47</div>
                </li>
                <li>
                  <div class="rank">2</div>
                  <div class="station">무악</div>
                  <div class="number">24</div>
                </li>
                <li>
                  <div class="rank">3</div>
                  <div class="station">홍제</div>
                  <div class="number">11</div>
                </li>
                <li>
                  <div class="rank">4</div>
                  <div class="station">부암</div>
                  <div class="number">9</div>
                </li>
              </ul>
            </div>

            <div class="section">
              <h3 id="CoursePeopleTOP5">누적 코스별 유동인구 TOP 5</h3>
              <ul class="station-list">
                <li>
                  <div class="rank">1</div>
                  <div class="station">무악</div>
                  <div class="number">67,969</div>
                </li>
                <li>
                  <div class="rank">2</div>
                  <div class="station">마루</div>
                  <div class="number">52,164</div>
                </li>
                <li>
                  <div class="rank">2</div>
                  <div class="station">홍제</div>
                  <div class="number">23,164</div>
                </li>
                <li>
                  <div class="rank">2</div>
                  <div class="station">부암</div>
                  <div class="number">7,164</div>
                </li>
              </ul>
            </div>
          </div>

          <div class="stat-section1">
            <div class="circle-chart">
                <svg viewBox="0 0 36 36" class="circular-chart green">
                  <path
                    class="circle-bg"
                    d="M18 2.0845
                      a 15.9155 15.9155 0 0 1 0 31.831
                      a 15.9155 15.9155 0 0 1 0 -31.831"
                  />
                  <path
                    class="circle"
                    stroke-dasharray="80, 100"
                    d="M18 2.0845
                      a 15.9155 15.9155 0 0 1 0 31.831
                      a 15.9155 15.9155 0 0 1 0 -31.831"
                  />
                  <text x="18" y="19" class="chart-value">52분</text>
                </svg>
                <p class="chart-title">평균 등산시간</p>
              </div>
              <div class="chart-info">
                <p>[전일대비] <span class="down">▼ -0.61%</span></p>
                <p>[전주대비] <span class="down">▼ -0.56%</span></p>
              </div>
            </div>
          <div class="stat-section2">
            <div class="circle-chart">
              <svg viewBox="0 0 36 36" class="circular-chart blue">
                <path
                  class="circle-bg"
                  d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <path
                  class="circle"
                  stroke-dasharray="65, 100"
                  d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <text x="18" y="19" class="chart-value">4.2km/h</text>
              </svg>
              <p class="chart-title">평균 걸음속도</p>
            </div>
            <div class="chart-info">
              <p>[전일대비] <span class="down">▼ -0.69%</span></p>
              <p>[전주대비] <span class="up">▲ +0.3%</span></p>
            </div>
          </div>
        </div>
        
    </aside>

    <transition name="slide" style="z-index: 10;">
      <aside v-if="isExtraSidebarVisible" class="extra-sidebar">
        <div class="header2">
          <button @click="toggleExtraSidebar" class="btn btn-secondary custom-btn">&lt;</button>
        </div>

        <div class="content">
          <p id="contentTitle">코스 이용현황</p>
          <div class="stats-container">
            <div class="section">
              <h3 id="todayPeopleTOP5">금일 유동인구  TOP 5</h3>
              <ul class="station-list">
                <li>
                  <div class="rank">1</div>
                  <div class="station">마루</div>
                  <div class="number">47</div>
                </li>
                <li>
                  <div class="rank">2</div>
                  <div class="station">무악</div>
                  <div class="number">24</div>
                </li>
                <li>
                  <div class="rank">3</div>
                  <div class="station">홍제</div>
                  <div class="number">11</div>
                </li>
                <li>
                  <div class="rank">4</div>
                  <div class="station">부암</div>
                  <div class="number">9</div>
                </li>
              </ul>
            </div>

            <div class="section">
              <h3 id="CoursePeopleTOP5">누적 코스별 유동인구 TOP 5</h3>
              <ul class="station-list">
                <li>
                  <div class="rank">1</div>
                  <div class="station">무악</div>
                  <div class="number">67,969</div>
                </li>
                <li>
                  <div class="rank">2</div>
                  <div class="station">마루</div>
                  <div class="number">52,164</div>
                </li>
                <li>
                  <div class="rank">2</div>
                  <div class="station">홍제</div>
                  <div class="number">23,164</div>
                </li>
                <li>
                  <div class="rank">2</div>
                  <div class="station">부암</div>
                  <div class="number">7,164</div>
                </li>
              </ul>
            </div>
          </div>

          <div class="stat-section1">
            <div class="circle-chart">
                <svg viewBox="0 0 36 36" class="circular-chart green">
                  <path
                    class="circle-bg"
                    d="M18 2.0845
                      a 15.9155 15.9155 0 0 1 0 31.831
                      a 15.9155 15.9155 0 0 1 0 -31.831"
                  />
                  <path
                    class="circle"
                    stroke-dasharray="80, 100"
                    d="M18 2.0845
                      a 15.9155 15.9155 0 0 1 0 31.831
                      a 15.9155 15.9155 0 0 1 0 -31.831"
                  />
                  <text x="18" y="19" class="chart-value">52분</text>
                </svg>
                <p class="chart-title">평균 등산시간</p>
              </div>
              <div class="chart-info">
                <p>[전일대비] <span class="down">▼ -0.61%</span></p>
                <p>[전주대비] <span class="down">▼ -0.56%</span></p>
              </div>
            </div>
          <div class="stat-section2">
            <div class="circle-chart">
              <svg viewBox="0 0 36 36" class="circular-chart blue">
                <path
                  class="circle-bg"
                  d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <path
                  class="circle"
                  stroke-dasharray="65, 100"
                  d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <text x="18" y="19" class="chart-value">4.2km/h</text>
              </svg>
              <p class="chart-title">평균 걸음속도</p>
            </div>
            <div class="chart-info">
              <p>[전일대비] <span class="down">▼ -0.69%</span></p>
              <p>[전주대비] <span class="up">▲ +0.3%</span></p>
            </div>
          </div>
        </div>
        
      </aside>
    </transition>

    <main class="main">
      <div id="map" class="map-container"></div>

        <div class="RealTimeCard">
          <p class="RealTime">{{ currentTime }}</p>
        </div>

        <div class="card peopleRealTimeCard">
          <div class="d-flex">
            <p class="people">실시간 인원 : </p><p class="peopleCount"> 37 <span>명</span></p>
          </div>
        </div>

    </main>
  </div>
</template>

<script setup>
import HeaderView from '@/components/HeaderView.vue';
import { ref, onMounted, onUnmounted } from 'vue';

const map = ref(null);


function initializeMap() {
  const script = document.createElement('script');
  script.onload = () => kakao.maps.load(() => {
    map.value = new kakao.maps.Map(document.getElementById('map'), {
      center: new kakao.maps.LatLng(37.618262, 126.964524),
      level: 5,
      mapTypeId: kakao.maps.MapTypeId.SKYVIEW
    });
    loadGeoJSONFromServer('/api/spatial/trails/geojson');
  });
  script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false';
  document.head.appendChild(script);
}

async function loadGeoJSONFromServer(url) {
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const geojsonData = await response.json();
    processGeoJSON(geojsonData);
  } catch (error) {
    console.error('GeoJSON 파일 로드 중 에러 발생:', error);
  }
}

function processGeoJSON(geojsonData) {
  let allCoordinates = [];
  
  geojsonData.features.forEach((feature) => {
    if (feature.properties.PMNTN_NM && feature.properties.PMNTN_NM.includes('마루')) {
      let coordinates = [];

      feature.geometry.coordinates.forEach(line => {
            coordinates = coordinates.concat(line.map((coord) => ({
              lng: coord[0],
              lat: coord[1],
              elevation: feature.properties.DN || 0
            })));
          });
          allCoordinates = allCoordinates.concat(coordinates);
          }
      });
      addRouteLayer(allCoordinates)

  addMarkersOnPath(allCoordinates);

}

function addRouteLayer(allCoordinates){
  const linePath = allCoordinates.map(coord => new kakao.maps.LatLng(coord.lat, coord.lng));
  
    const polyline = new kakao.maps.Polyline({
      path: linePath,
      strokeWeight: 5,
      strokeColor: '#32CD32',
      strokeOpacity: 1,
      strokeStyle: 'solid'
    });
  
    polyline.setMap(map.value);
}

function addMarkersOnPath(coordinates) {
  if (!coordinates.length) return;

  const densePath = generateDensePath(coordinates.map(coord => ({
    lat: coord.lat,
    lng: coord.lng
  })));

  

  for (let i = 0; i < 37; i++) {
    const randomStartIndex = getWeightedRandomIndex(densePath.length);
    const isReverse = Math.random() > 0.5;
    addMarkerWithAnimation(densePath, randomStartIndex, isReverse);
  }
}

function getWeightedRandomIndex(length) {
  const weights = Array.from({ length }, (_, i) => Math.max(length - i, 1));
  const totalWeight = weights.reduce((acc, weight) => acc + weight, 0);
  const randomWeight = Math.random() * totalWeight;

  let cumulativeWeight = 0;
  for (let i = 0; i < length; i++) {
    cumulativeWeight += weights[i];
    if (randomWeight <= cumulativeWeight) {
      return i;
    }
  }

  return 0;
}


function generateDensePath(points) {
  const densePath = [];
  for (let i = 0; i < points.length - 1; i++) {
    const start = points[i];
    const end = points[i + 1];
    const steps = 50;
    for (let j = 0; j <= steps; j++) {
      const lat = start.lat + ((end.lat - start.lat) * j) / steps;
      const lng = start.lng + ((end.lng - start.lng) * j) / steps;
      densePath.push({ lat, lng });
    }
  }
  return densePath;
}

function addMarkerWithAnimation(path, startIndex, isReverse) {
  const marker = new kakao.maps.Marker({
    position: new kakao.maps.LatLng(path[startIndex].lat, path[startIndex].lng),
    map: map.value,
    image: new kakao.maps.MarkerImage(
      'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png',
      new kakao.maps.Size(30, 30)
    )
  });

  let currentIndex = startIndex;

  function moveMarker() {
    if (isReverse) {
      currentIndex--;
      if (currentIndex < 0) {
        currentIndex = path.length - 1;
      }
    } else {
      currentIndex++;
      if (currentIndex >= path.length) {
        currentIndex = 0;
      }
    }

    const nextPosition = new kakao.maps.LatLng(path[currentIndex].lat, path[currentIndex].lng);
    marker.setPosition(nextPosition);

    requestAnimationFrame(moveMarker);
  }

  moveMarker();
}

const currentTime = ref('');

const updateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
};


onMounted(() => {
  initializeMap();
  updateTime();
  const timer = setInterval(updateTime, 1000);
    onUnmounted(() => {
    clearInterval(timer);
  });
});


const isExtraSidebarVisible = ref(false);

function toggleExtraSidebar() {
  isExtraSidebarVisible.value = !isExtraSidebarVisible.value;
}
</script>

<style scoped>

.dashboard {
  display: grid;
  grid-template-columns: 410px 1fr;
  height: 100%;
}

.sidebar {
  background-color: #ffffff;
  width: 410px;
  max-height: 100%;
  border-right: 1px solid #ddd;
}

.sidebar .header {
  padding: 10px;
  background-color: #fff;
  border-bottom: 1px solid #ddd;
  margin-top: 0.9rem;
}

.header2{
  padding: 10px;
  display: flex;
  background-color: #fff;
  border-bottom: 1px solid #ddd;
  margin-top: 5rem;
}

.custom-btn{
  border: none ;
  color: none;
  
}

.sidebar .header-title {
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.sidebar .content {
  padding: 10px;
}

.content{
  height: 85vh;
  overflow-y: auto;
}

.extra-sidebar {
  position: absolute;
  top: 0;
  left: 410px;
  width: 400px;
  height: 100%;
  background-color: #ffffff;
  border-left: 1px solid #ddd;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}



.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease-in-out, opacity 0.3s ease-in-out;
}
.slide-enter-from {
  transform: translateX(-100%);
  opacity: 0;
}
.slide-enter-to {
  transform: translateX(0);
  opacity: 1;
}
.slide-leave-from {
  transform: translateX(0);
  opacity: 1;
}
.slide-leave-to {
  transform: translateX(-100%);
  opacity: 0;
}

.main {
  background-color: #fff;
}

.map-container {
  height: 854px;
  width: 100%;
}

.stats-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 50px;
}

.section {
  background: #f9f9f9;
  border: 1px solid #ddd;
  padding: 30px;
  border-radius: 10px;
}

h3 {
  margin: 0 0 10px;
  font-size: 18px;
  font-weight: bold;
}

.station-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.station-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
  border-bottom: 1px solid #ddd;
}

.station-list li:last-child {
  border-bottom: none;
}

.rank {
  font-weight: bold;
  color: #333;
}

.station {
  flex: 1;
  text-align: left;
  margin-left: 10px;
}

.number {
  color: #666;
  font-size: 14px;
}

.stats {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.stat {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.change {
  font-size: 14px;
  color: #999;
}

.circle-chart {
  position: relative;
  width: 300px;
  height: 300px;
}

.circular-chart {
  display: block;
  margin: 0 auto;
  max-width: 100%;
  max-height: 250px;
}

.circle-bg {
  fill: none;
  stroke: #eeeeee;
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

.stat-section1 {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 3rem;
}

.stat-section2 {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 5rem;
  margin-bottom: 3rem;
}

#contentTitle{
  font-family: 'TheJamsil';
  font-weight: 400;
  font-size: 30px;
  text-align: center;
  margin-top: 3rem;
}

#todayPeopleTOP5{
  font-family: 'TheJamsil';
  font-weight: 300;
  font-size: 18px;
  text-align: center;
  margin-bottom: 1.5rem;
}

#CoursePeopleTOP5{
  font-family: 'TheJamsil';
  font-weight: 300;
  font-size: 18px;
  text-align: center;
  margin-bottom: 1.5rem;
}


.accident-summary {
  text-align: center;
  background: #f9f9f9;
  padding: 19px;
  margin-bottom: 20px;
}

.carousel-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-direction: column;
}

.card-carousel {
  display: row;
  gap: 1.4rem;
  overflow-x: auto;
  overflow-y: hidden;
  scroll-behavior: smooth;
  width: 100%;
}

.card-carousel::-webkit-scrollbar {
  display: none;
}

.card {
  position: relative;
  width: 351px;
  height: 70px;
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  margin-top: 1rem;
}

.card-img-top {
  position: absolute;
  top: 0;
  left: 0;
  width: 100px;
  height: 100px;
  margin-top: -1.2rem;
}

.card-overlay {
  position: absolute;
  left: 0;
  width: 100%;
  padding: 1.4rem;
  text-align: left;
}

.accident-item-content1 {
  color: #000;
  font-family: 'TheJamsil';
  font-size: 14px;
  font-weight: 300;
  margin-left: 5rem;
}

.accident-item-content2 {
  color: #000000;
  font-weight: 100;
  font-size: 16px;
  margin-top: -1rem;
}

#DangerImage1, #DangerImage2, #DangerImage3 {
  position: absolute;
  top: 0;
  right: 0;
  width: 50px;
  height: 50px;
  margin-top: 0.6rem;
  margin-right: 1rem;
  object-fit: cover;
  border-radius: 5px;
}

#DangerImage2 {
  gap: 10rem;
  display: flex !important;
}

.RealTimeCard{
  position: fixed;
  top: 0;
  right: 0;
  border: none;
  color: white;
  margin-top: 8rem;
  margin-right: 3rem;
  z-index: 100;
  height: 100px;
}

.RealTime{
  font-family: 'TheJamsil';
  font-size: 30px;
  font-weight: 500;
}

.peopleRealTimeCard{
  position: fixed;
  top: 0;
  right: 0;
  margin-top: 12rem;
  margin-right: 2.5rem;
  z-index: 100;
  width: 260px;
  height: 65px;
  padding: 10px;
  box-shadow: 0px 1px 10px rgba(0, 0, 0, 0.9);
}

.people{
  font-family: 'TheJamsil';
  font-size: 30px;
  font-weight: 500;
}

.peopleCount{
  font-family: 'TheJamsil';
  font-size: 30px;
  font-weight: 500;
}

</style>
