<template>
    <MobileHeaderView class="header" />
    <div class="btn-group-container">
        <div class="btn-group" role="group" aria-label="Basic radio toggle button group">
            <input type="radio" class="btn-check" name="btnradio" id="btnradio1" autocomplete="off" checked>
            <label class="btn btn-outline-success" for="btnradio1">운동</label>
            <input type="radio" class="btn-check" name="btnradio" id="btnradio2" autocomplete="off" @click="goToRecordImg()">
            <label class="btn btn-outline-success" for="btnradio2">기록</label>
            <input type="radio" class="btn-check" name="btnradio" id="btnradio3" autocomplete="off" @click="goToMyCom()">
            <label class="btn btn-outline-success" for="btnradio3">나의 커뮤니티</label>
        </div>
    </div>
    <MobileHeaderView class="header" />
        <div class="app-main">
            <div class="map-container">
                <div id="map"></div>
                <div class="timer-display">
                    <span>{{ formattedHours }}</span>:<span>{{ formattedMins }}</span>:<span>{{ formattedSecs }}</span>
                </div>
                <div class="centered-button">
                    <button @click="toggleTracking" class="start-button">
                    {{ isTracking ? "저장" : "시작" }}
                    </button>
                </div>

                <button class="goal-button" @click="stopTracking">
                <img src="/images/정지4.png" alt="" style="width: 20px; height: 20px;">
                </button>
            <button class="TTS"><img src="/images/음성.png" style="width: 32px; height: auto;"></button>
            </div>
        </div>
        <MobileFooterView3 class="footer"></MobileFooterView3>

<div class="modal" id="saveModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title"><img src="/images/저장.png" alt="" style="width: 20px; height: 20px; margin-right: 3px;">기록 저장</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <p style="border-bottom: solid 1px #333;">등산 기록을 저장하시겠습니까?</p>
        <div class="d-flex flex-column mb-3 fv-row">
            <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
            <span class="required">사진 등록</span>
            </label>
            <input
            type="file"
            id="image"
            @change="onFileChange"
            ref="image"
            accept="image/*"
            />
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="delteRecord()">삭제</button>
        <button type="button" class="btn btn-primary" @click="saveRecord()">저장</button>
      </div>
    </div>
  </div>
</div>


</template>

<script setup>
import MobileFooterView3 from "@/components/MobileFooterView3.vue";
import MobileHeaderView from "@/components/MobileHeaderView.vue";
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue';
import axios from 'axios';
import router from '@/router';
import { Modal } from "bootstrap";
import { fetchTrailGeoJson, resolveAllTrailIds } from '@/api/slopeSection.js';
import { fetchNearbyAccidents } from '@/api/accidentCandidates.js';

const latitude = ref(37.641774041520812);
const longitude = ref(126.98750152017325);
const altitude = ref(null);
const speed = ref(null);
const bearing = ref(null);
const time = ref("");
let polyline, map, redDot, whiteDot, BigDot;

let hours = ref(0);
let mins = ref(0); 
let secs = ref(0);
let interval;
let path = reactive([]);
const isTracking = ref(false);
const isTrackingComplete = ref(false);

const message = ref('30m 앞에 위험 구간입니다. 주의하세요.');
const triggeredAccidentIds = new Set();
const isVoiceLoaded = ref(false);

// 값은 현재 둘 다 30m이지만 의미가 다르다 -- 값이 같다고 하나로 합치지 않는다.
// ACCIDENT_CANDIDATE_DISTANCE_METERS: Trail ↔ AccidentPoint (PostGIS 후보 선별 반경, Backend)
// ACCIDENT_ALERT_DISTANCE_METERS: User ↔ AccidentPoint (실시간 알림 판정 반경, Frontend)
const ACCIDENT_CANDIDATE_DISTANCE_METERS = 30;
const ACCIDENT_ALERT_DISTANCE_METERS = 30;

function initializeMap() {
    const script = document.createElement('script');
    script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false&libraries=clusterer,services';
    document.head.appendChild(script);

    script.onload = () => {
        if (window.kakao && window.kakao.maps) {
            window.kakao.maps.load(() => {
                const mapContainer = document.getElementById('map');
                const mapOption = {
                    center: new kakao.maps.LatLng(latitude.value, longitude.value),
                    level: 3
                };
                map = new kakao.maps.Map(mapContainer, mapOption);

                initOverlays();
                initPolyline();
                loadAccidentCandidateMarkers();
                if (latitude.value && longitude.value) {
                    console.log('실행');
                    updateMapPosition();
                }
            });
        }
    };
}

function calculateDistanceUsingPolyline(userPosition, dangerPosition) {
  const linePath = [
    userPosition,
    dangerPosition
  ];
  const polyline = new kakao.maps.Polyline({ path: linePath });
  return polyline.getLength();
}

function checkProximityUsingPolyline(userPosition) {
  console.log("Checking proximity for position:", userPosition);
  dangerMarkers.value.forEach((dangerMarker) => {
    const accidentId = dangerMarker.id; // accident_point.id (좌표 문자열 대신)
    if (triggeredAccidentIds.has(accidentId)) return;

    const markerPosition = dangerMarker.getPosition();
    const distance = calculateDistanceUsingPolyline(userPosition, markerPosition);
    if (distance <= ACCIDENT_ALERT_DISTANCE_METERS) {
      console.log('위험 구역 접근:', dangerMarker.getTitle());
      speakMessage();
      triggeredAccidentIds.add(accidentId);
    }
  });
}

function loadResponsiveVoice() {
  return new Promise((resolve, reject) => {
    const script = document.createElement('script');
    script.src = 'https://code.responsivevoice.org/responsivevoice.js?key=VGYcmCsP';
    script.onload = () => {
      isVoiceLoaded.value = true;
      resolve();
    };
    script.onerror = () => reject(new Error('ResponsiveVoice.js 로드 실패'));
    document.head.appendChild(script);
  });
}

function speakMessage() {
  if (isVoiceLoaded.value && window.responsiveVoice) {
    const text = message.value;
    const lang = "Korean Female";
    window.responsiveVoice.speak(text, lang);
  } else {
    alert('음성 합성 라이브러리가 로드되지 않았습니다.');
  }
}

const dangerMarkers = ref([]);

// 실제 PostGIS accident_point(42건) 기반 위험 마커.
// 이 화면은 별도 코스 선택 UI가 없어 현재 코스를 식별할 방법이 없으므로
// MountainDetailView2.vue와 동일한 패턴으로 Trail GeoJSON에 실제
// 존재하는 trailId 전체를 순회한다 -- 코스명을 임의로 추정하거나 하드코딩하지 않는다.
// Trail ↔ AccidentPoint 공간관계(후보 선별)는 Backend/PostGIS가 계산하고, 이 함수는 그
// 결과를 지도에 그리기만 한다 -- 실시간 사용자 접근 판정은 여전히 checkProximityUsingPolyline
// (Frontend/Kakao 거리 계산)이 담당한다.
async function loadAccidentCandidateMarkers() {
  try {
    const trailGeoJson = await fetchTrailGeoJson();
    const trailIds = resolveAllTrailIds(trailGeoJson);

    const imageSize = new kakao.maps.Size(35, 45);
    const imageOpation = { offset: new kakao.maps.Point(12, 35) };
    const markerImage = new kakao.maps.MarkerImage('/images/danger.png', imageSize, imageOpation);

    const candidatesByAccidentId = new Map();
    for (const trailId of trailIds) {
      try {
        const candidateGeoJson = await fetchNearbyAccidents(trailId, ACCIDENT_CANDIDATE_DISTANCE_METERS);
        candidateGeoJson.features.forEach((feature) => {
          candidatesByAccidentId.set(feature.properties.accidentId, feature);
        });
      } catch (error) {
        console.error(`사고지점 후보 조회 중 에러 발생 (trailId=${trailId}):`, error);
      }
    }

    candidatesByAccidentId.forEach((feature) => {
      const [lng, lat] = feature.geometry.coordinates;
      const { accidentId, accidentType, locationName, dispatchDate, distanceToTrailMeters } = feature.properties;
      const markerPosition = new kakao.maps.LatLng(lat, lng);

      const marker = new kakao.maps.Marker({
        position: markerPosition,
        map: map,
        title: accidentType,
        image: markerImage,
      });
      marker.id = accidentId; // accident_point.id -- 중복 알림 방지 식별자
      dangerMarkers.value.push(marker);

      const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;">${accidentType}<br>${locationName} (${dispatchDate})<br>등산로에서 약 ${distanceToTrailMeters.toFixed(1)}m</div>`,
      });
      kakao.maps.event.addListener(marker, 'mouseover', () => infowindow.open(map, marker));
      kakao.maps.event.addListener(marker, 'mouseout', () => infowindow.close());
    });
  } catch (error) {
    console.error('사고지점 후보 마커 로드 중 에러 발생:', error);
  }
}

async function fetchLocation() {
    try {
        const response = await axios.get('/api/recordlocation');
        console.log("Received response:", response.data);
        latitude.value = response.data.latitude;
        longitude.value = response.data.longitude;
        altitude.value = response.data.altitude;
        speed.value = response.data.speed;
        bearing.value = response.data.bearing;
        time.value = response.data.time;

        updateMapPosition();
    } catch (error) {
        console.error("Error fetching location:", error);
    }
}

function updateMapPosition() {
    
    if (!map) return;

    const newLatLng = new kakao.maps.LatLng(latitude.value, longitude.value);

    if (isTracking.value) {
        path.push({
            latitude: latitude.value,
            longitude: longitude.value,
            altitude: altitude.value,
            speed: speed.value,
            bearing: bearing.value,
            time: time.value
        });
        polyline.setPath(path.map(point => new kakao.maps.LatLng(point.latitude, point.longitude)));

    }

    if (redDot && whiteDot && BigDot) {
        redDot.setPosition(newLatLng);
        whiteDot.setPosition(newLatLng);
        BigDot.setPosition(newLatLng);
    }

    map.panTo(newLatLng);

    checkProximityUsingPolyline(newLatLng);
}

function initOverlays() {
    const position = new kakao.maps.LatLng(latitude.value, longitude.value);
    BigDot = new kakao.maps.CustomOverlay({
        position,
        content: '<div style="width: 24px; height: 24px; border-radius: 50%; background-color: rgba(255, 0, 0, 0.2);"></div>',
        yAnchor: 0.5
    });
    BigDot.setMap(map);

    whiteDot = new kakao.maps.CustomOverlay({
        position,
        content: '<div style="width: 14px; height: 14px; border-radius: 50%; background-color: #FFFFFF;"></div>',
        yAnchor: 0.5
    });
    whiteDot.setMap(map);

    redDot = new kakao.maps.CustomOverlay({
        position,
        content: '<div style="width: 10px; height: 10px; background-color: #FF0000; border-radius: 50%;"></div>',
        yAnchor: 0.5
    });
    redDot.setMap(map);
}

function initPolyline() {
    polyline = new kakao.maps.Polyline({
        map: map,
        path: path,
        strokeWeight: 5,
        strokeColor: '#0000FF',
        strokeOpacity: 0.7,
        strokeStyle: 'solid'
    });
}

function startTimer() {
    secs.value++;
    if (secs.value >= 60) {
        secs.value = 0;
        mins.value++;
    }
    if (mins.value >= 60) {
        mins.value = 0;
        hours.value++;
    }
}

function toggleTracking() {
    if (isTracking.value) {
        resetTracking();
        isTrackingComplete.value = true;
    } else {
        startTracking();
        isTrackingComplete.value = false;
    }
}

function startTracking() {
    if (!isTracking.value) {
        isTracking.value = true;
        interval = setInterval(startTimer, 1000);
    }
}

function stopTracking() {
    clearInterval(interval);
    isTracking.value = false;
}

function resetTracking() {
    clearInterval(interval);
    isTracking.value = false;
    showSaveModal();
}

const formattedHours = computed(() => hours.value.toString().padStart(2, '0'));
const formattedMins = computed(() => mins.value.toString().padStart(2, '0'));
const formattedSecs = computed(() => secs.value.toString().padStart(2, '0'));

const totalTime = computed(() => `${formattedHours.value}:${formattedMins.value}:${formattedSecs.value}`);

let saveModal;

function showSaveModal(){
    const elem = document.querySelector('#saveModal');
    saveModal = new Modal(elem);
    saveModal.show();
}

function delteRecord() {
    hours.value = 0;
    mins.value = 0;
    secs.value = 0;
    path.splice(0, path.length);
    polyline.setPath(path);
}

async function saveRecord() {
    console.log("경로:", path, "타입:", typeof path);

    const trackingPathList = path.map(point => ({
        latitude: point.latitude,
        longitude: point.longitude,
        altitude: point.altitude,
        speed: point.speed,
        bearing: point.bearing,
        time: point.time
    }));

    console.log("바꾼 경로 : ",trackingPathList);

    const id = await requestInsertPathInfo();
    console.log("id = " + id);

    if (id) {
        await requestInsertTrackingPath(trackingPathList, id);
    } else {
        console.error("Path ID is not valid.");
        return;
    }
    
    saveModal.hide();
    delteRecord();
    router.push({path:'/recordImg'});
}

async function requestInsertPathInfo() {
    const imagePath = await uploadImage();
    const data = {
        userId: localStorage.getItem("userId"),
        pathImg: imagePath || '',
        totalTime: totalTime.value
    }
    try {
        const response = await axios.post('/api/savePathInfo', data);
        console.log('등산 기록 코스에 대한 응답 데이터:', response.data);
        return response.data;
    } catch (error) {
        console.error("Error fetching location:", error);
    }
}

async function requestInsertTrackingPath(trackingPathList, pathId) {
    for (const tracking of trackingPathList) {
        const data = {
            pathId: pathId,
            latitude: tracking.latitude,
            longitude: tracking.longitude,
            altitude: tracking.altitude,
            speed: tracking.speed,
            bearing: tracking.bearing,
            time: tracking.time
        };
        
        try {
            const response = await axios.post('/api/saveTrackingPath', data);
            console.log('코스 경로 정보에 대한 응답 데이터:', response.data);
        } catch (error) {
            console.error("Error saving tracking path:", error);
        }
    }
}


function goToRecordImg(){
    router.push({path:'/recordImg'});
}

function goToMyCom(){
    router.push({path:'/myCommunity'});
}

onMounted(async() => {
    try{
        await loadResponsiveVoice();
        
        initializeMap();
        fetchLocation();
        setInterval(fetchLocation, 2000);
    }catch(error){
        console.log(error);
    }
});

onBeforeUnmount(() => {
    clearInterval(fetchLocation);
});

const selectedFile = ref(null);

const onFileChange = (event) => {
  selectedFile.value = event.target.files[0];
};

const uploadImage = async () => {
  if (!selectedFile.value) {
    return null;
  }
  const formData = new FormData();
  formData.append('file', selectedFile.value);

  try {
    const response = await axios.post('/api/complaint/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error uploading image', error);
    return null;
  }
};


</script>



<style scoped>
h1,
h2,
h3,
.btn-group .btn,
.start-button,
p {
  font-family: "TheJamsil5Bold", sans-serif !important;
}

.map-container {
    width: 100%;
    height: 670px;
    overflow: hidden;
    position: relative;
}

#map {
    width: 100%;
    height: 100%;
}

.time {
    position: absolute;
    bottom: 90px;
    left: 53%;
    transform: translateX(-50%);
    background-color: rgba(255, 255, 255, 0.8);
    padding: 10px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    width: 90%;
    justify-content: space-between;
    z-index: 10;
}

.time p {
    margin: 0;
}

.time button {
    margin-left: 5px;
}


.icon-button img {
    width: 24px;
    height: 24px;
}

.icon-button:hover {
    background-color: #848484;
}

.icon-button:active {
    background-color: #ff4d4d;
}

.btn-group-container {
  position: fixed;
  top: 13px;
  right: 10px;
  z-index: 50;
  background-color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px;
}

.btn-group .btn {
  font-size: 12px;
  padding: 4px 8px;
  color: black;
  border: 1px solid #4caf50;
  border-radius: 4px;
  margin: 0;
  white-space: nowrap;
}

.btn-group .btn:hover {
  background-color: #4caf50;
  color: white;
}

.btn-group .btn-check:checked + .btn {
  background-color: #4caf50;
  color: white;
}

.header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 30;
    background-color: white;
    border-bottom: 1px solid #ddd;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 15px;
}

.timer-display{
    position: absolute;
    bottom: 8%; 
    left: 50%;
    transform: translate(-50%, 50%);
    z-index: 12;
    font-size: 30px;
    font-weight: bold;
    color: #333333;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    border-bottom: solid 1px #4caf50;
    padding-left: 4px;
    padding-right: 4px;
}

.centered-button {
  position: absolute;
  bottom: 20%; 
  left: 50%;
  transform: translate(-50%, 50%);
  z-index: 10;
}

.goal-button {
    width: 50px; 
    height: 50px;
    position: absolute;
    bottom: 16%;
    left: 25%;
    transform: translateX(-50%);
    background-color: #ffffff;
    border: 1px solid #cccccc;
    border-radius: 50%;
    font-size: 14px;
    color: #333333;
    cursor: pointer;
    z-index: 10;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
    transition: all 0.3s ease;
}

.goal-button:hover {
    background-color: #f8f8f8;
    border-color: #bdbdbd;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.3);
}

.goal-button:active {
    background-color: #e0e0e0;
    transform: translateX(-100%) scale(0.95);
}

.start-button {
    width: 130px;
    height: 130px;
    position: relative;
    bottom: 0px;
    border: none;
    border-radius: 50%;
    background-color: #51b951;
    font-size: 22px;
    font-weight: bold;
    color: white;
    cursor: pointer;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
    transition: all 0.3s ease;
}

.start-button:hover {
    background-color: #a6ec02;
    color: black;
}

.start-button:active {
    background-color: #ffa500;
    transform: scale(0.95);
}

.TTS {
    position: absolute;
    bottom: 16%;
    left: 75%;
    transform: translateX(-50%);
    background-color: #ffffff;
    border: 1px solid #cccccc;
    border-radius: 50%;
    width: 50px;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #333333;
    cursor: pointer;
    z-index: 10;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
    transition: all 0.3s ease;
}

.TTS:hover {
    background-color: #f8f8f8;
    border-color: #bdbdbd;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.3);
}

.TTS:active {
    background-color: #e0e0e0;
    transform: scale(0.95);
}

.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60px;
    z-index: 1000;
    background-color: white;
    box-shadow: 0px -1px 5px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .start-button {
    width: 100px;
    height: 100px;
    font-size: 25x;
  }

  .goal-button {
    font-size: 12px;
  }
}

.modal-content {
  border-radius: 15px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.2);
  background: linear-gradient(145deg, #f9f9f9, #ffffff);
  padding: 20px;
}

.modal-header {
  border-bottom: none;
  text-align: center;
  padding-bottom: 10px;
}

.modal-title {
  font-family: "TheJamsil5Bold", sans-serif;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.btn-close {
  background-color: #f0f0f0;
  border-radius: 50%;
  padding: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-close:hover {
  background-color: #e0e0e0;
}

.modal-body {
  font-family: "TheJamsil5Bold", sans-serif;
  color: #666;
  line-height: 1.6;
  font-size: 14px;
  text-align: center;
}

.modal-body p {
  font-size: 16px;
  margin-bottom: 20px;
  font-weight: 600;
  color: #444;
}

input[type="file"] {
  display: block;
  margin: 10px auto;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  width: 80%;
  transition: all 0.3s ease;
}

input[type="file"]:hover {
  background-color: #f9f9f9;
  border-color: #bbb;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: none;
}

.modal-footer button {
  font-family: "TheJamsil5Bold", sans-serif;
  font-size: 14px;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.modal-footer .btn-secondary {
  background-color: #e0e0e0;
  color: #333;
  border: none;
}

.modal-footer .btn-secondary:hover {
  background-color: #d6d6d6;
}

.modal-footer .btn-primary {
  background-color: #4caf50;
  color: white;
  border: none;
}

.modal-footer .btn-primary:hover {
  background-color: #45a047;
}

.modal-footer .btn-primary::before {
  content: "✔ ";
  font-weight: bold;
}

.modal-footer .btn-secondary::before {
  content: "✖ ";
  font-weight: bold;
}
</style>