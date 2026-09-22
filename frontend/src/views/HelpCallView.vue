<template>
  <div id="notification">
    <p v-for="msg in filteredMessages" :key="msg.id">{{ msg.text }}</p>

    <div class="review-search" style="top: 30px;">
        <div class="search-container">
          <input type="text" placeholder="검색" />
          <button class="search-button">
            <img src="/images/돋보기white.png" alt="Search" />
          </button>
        </div>
      </div>
    <div ref="mapContainer" class="map-container">
      <button id="back-button" @click="back_site">
      <img src="/images/뒤로가기.png" alt="뒤로가기">
      </button>

      <button id="zoom-button" @click="Zoom">
      <img src="/images/내위치2.png" alt="Zoom">
    </button>
    </div>

    <div class="toggle-switch-container2" style="z-index: 10;">
        <div class="toggle-switch2">
          <img src="/images/내위치.png" alt="" style="width: 13px; height: auto; margin-right: 5px;"> 내 위치
        </div>
        <div class="toggle-switch2" >
          <img src="/images/SOS.png" alt="" style="width: 13px; height: auto; margin-right: 5px;"> SOS
        </div>
        <div class="toggle-switch2" style=" font-size: 12px; width: 100px;">
          <img src="/images/국가지점번호.png" alt="" style="width: 13px; height: auto; margin-right: 5px;"> 국가지점번호
        </div>
      </div>
   
    <div v-if="!isSOSActive" class="info-panel">
      <div  id="overline"></div>
      <h3 style="position:relative; text-align: center; top: 25px; z-index: 45;">긴급 도움 요청</h3>
      <div style="position: relative; text-align: center;">
      
        <div style="position: relative; top: 130px; font-size: 12px; color: white;">현재 위치가 주변 사람들( 300M 이내 )에게 알림으로 전송됩니다.</div>
      </div>
      <div id="wrap">
          <button v-if="!isSOSActive" class="overlay-button" @click="handleSendNotification">SOS</button>
          <div v-if="!isSOSActive" class="wave -one"></div>
          <div v-if="!isSOSActive" class="wave -two"></div>
          <div v-if="!isSOSActive" class="wave -three"></div>
      </div>
    </div>

    <div v-else class="info-panel">
      <h3 style="position:relative; text-align: center; top: 25px">긴급 도움 요청</h3>
      <div style="font-size: 20px; font-weight: bold; margin-top: 25px; text-align: center; color: white;">SOS 알림  <span style="color: #38b64a;">전송 완료</span>(300M)</div>

        <div class="button-container">
        <div class="button-text-container">
          <button class="action-button" @click="reSendSOS">
            <img src="/images/리콜.png" alt="다시 보내기">
          </button>
          <div class="description-text" style="font-size: 10px; margin-top: 5px; color: white;">알림 보내기</div>
        </div>
        <div class="button-text-container">
          <button class="action-button" @click="callEmergency">
            <img src="/images/119.png" alt="119">
          </button>
          <div class="description-text" style="font-size: 10px; margin-top: 5px; color: white;">119 전화</div>
        </div>
        <div class="button-text-container">
          <button class="action-button" @click="findNearestPoint">
            <img src="/images/국가표지판.png" alt="국가지점번호">
          </button>
          <div class="description-text" style="font-size: 10px; margin-top: 5px; color: white;">
            국가지점번호
          </div>
        </div>
         <div v-if="isPointInfoVisible" class="info-display">
          <h3>가장 가까운 국가지점번호</h3>
          <p>지점번호: {{ nearestPoint?.label || '알 수 없음' }}</p>
          <p>위도: {{ nearestPoint?.latitude }}</p>
          <p>경도: {{ nearestPoint?.longitude }}</p>
          <p>거리: {{ nearestPoint?.distance?.toFixed(2) || 0 }} km</p>
          <p>방향: {{ nearestPoint?.direction || '알 수 없음' }}</p>
          <button @click="closePointInfo" class="close-button">닫기</button>
        </div>
      </div>
    </div>
    <MobileFooterView4 class="footer"></MobileFooterView4>
  </div>

</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed} from 'vue';
import axios from 'axios';
import MobileFooterView4 from '@/components/MobileFooterView4.vue';

const messages = ref([]);
const userId = ref(localStorage.getItem('userId') || '');
let socket = null;
let reconnectInterval = null;
const connectionError = ref('');
const successMessage = ref('');
let isConnecting = false;
const isModalOpenForSOS = ref(false);
const isSOSActive = ref(false);

const isPointInfoVisible = ref(false);
const nearestPoint = ref(null);

const userLocation = ref({ latitude: null, longitude: null });

const sosNotificationLocation = ref({ latitude: null, longitude: null });

const mapContainer = ref(null);
let map = null;
let userMarkers = {};
let sosMarkers = [];

let autoCenterEnabled = true;

const filteredMessages = computed(() => messages.value.filter(msg => msg.text !== userId.value));

const calculateDirection = (lat1, lon1, lat2, lon2) => {
  const dLat = lat2 - lat1;
  const dLon = lon2 - lon1;

  if (Math.abs(dLat) > Math.abs(dLon)) {
    return dLat > 0 ? "남쪽" : "북쪽";
  } else {
    return dLon > 0 ? "서쪽" : "동쪽";
  }
};

const calculateDistance = (lat1, lon1, lat2, lon2) => {
  const R = 6371;
  const dLat = (lat2 - lat1) * (Math.PI / 180);
  const dLon = (lon2 - lon1) * (Math.PI / 180);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(lat1 * (Math.PI / 180)) *
      Math.cos(lat2 * (Math.PI / 180)) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
};

const findNearestPoint = async () => {
  if (!userLocation.value.latitude || !userLocation.value.longitude) {
    console.warn("사용자의 위치가 유효하지 않습니다:", userLocation.value);
    alert("현재 위치를 찾을 수 없습니다.");
    return;
  }

    const response = await axios.get(`${window.location.origin}/data/국가지점번호2.geojson`);
    const geojsonData = response.data;

    if (!geojsonData || !geojsonData.features || geojsonData.features.length === 0) {
      alert("국가지점번호 데이터를 불러올 수 없습니다.");
      return;
    }

    let closestPoint = null;
    let minDistance = Infinity;

    geojsonData.features.forEach((feature) => {
      const pointLatitude = feature.properties.Y좌표;
      const pointLongitude = feature.properties.X좌표;
      const pointLabel = feature.properties.지점번호;

      const distance = calculateDistance(
        userLocation.value.latitude,
        userLocation.value.longitude,
        pointLatitude,
        pointLongitude
      );

      console.log(
        `지점: ${pointLabel}, 거리: ${distance.toFixed(2)} km, 현재 최소 거리: ${minDistance}`
      );

      if (distance < minDistance) {
        minDistance = distance;
        closestPoint = {
          label: pointLabel,
          latitude: pointLatitude,
          longitude: pointLongitude,
          distance,
          direction: calculateDirection(
            userLocation.value.latitude,
            userLocation.value.longitude,
            pointLatitude,
            pointLongitude
          ),
        };
      }
    });

    if (closestPoint) {
    nearestPoint.value = closestPoint;
    console.log("가장 가까운 지점 정보:", nearestPoint.value);
    isPointInfoVisible.value = true;
    console.log("isPointInfoVisible 상태:", isPointInfoVisible.value);
  } else {
    alert("가까운 국가지점번호를 찾을 수 없습니다.");
  }
};

const closePointInfo = () => {
  isPointInfoVisible.value = false;
};

const handleSendNotification = async () => {
  isSOSActive.value = true;
  isModalOpenForSOS.value = true;

  sosNotificationLocation.value.latitude = userLocation.value.latitude;
  sosNotificationLocation.value.longitude = userLocation.value.longitude;
  
  try {
    await axios.post("/api/sendNotificationToAll", {
      userId: localStorage.getItem("userId"),
      latitude: sosNotificationLocation.value.latitude,
      longitude: sosNotificationLocation.value.longitude,
    });

    if (map && userLocation.value.latitude && userLocation.value.longitude) {
      const currentPosition = new window.kakao.maps.LatLng(
        userLocation.value.latitude,
        userLocation.value.longitude
      );
      map.setCenter(currentPosition);
      map.panTo(currentPosition);
    }
    updateUserMarker();
  } catch (error) {
    console.error("모든 사용자에게 SOS 알림 전송 실패:", error);
  }

  sendNotification();
};

onMounted(() => {
  loadKakaoMapScript(() => {
    initializeMap();
  });

  startLocationTracking();
  connectWebSocket();
});

const loadKakaoMapScript = (callback) => {
  if (window.kakao && window.kakao.maps) {
    callback();
  } else {
    const script = document.createElement('script');
    script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false&libraries=clusterer,services';
    script.onload = () => {
      window.kakao.maps.load(callback);
    };
    script.onerror = () => {
      console.error("카카오 지도 API 로드 실패");
    };
    document.head.appendChild(script);
  }
};

const initializeMap = async () => {
  if (mapContainer.value) {
    map = new window.kakao.maps.Map(mapContainer.value, {
      center: new window.kakao.maps.LatLng(37.5665, 126.9780),
      level: 5,
    });

    try {
      const response = await axios.get(`${window.location.origin}/data/국가지점번호2.geojson`);
      const geojsonData = response.data;

      if (!geojsonData.features || geojsonData.features.length === 0) {
        console.error("GeoJSON 데이터에 features가 없습니다.");
        return;
      }

      const customMarkers = geojsonData.features.map((feature) => {
        const lat = feature.properties.Y좌표;
        const lng = feature.properties.X좌표;

        const customMarkerContent = document.createElement("div");
        customMarkerContent.className = "custom-marker";
        customMarkerContent.innerHTML = `
          <div class="marker-icon2">
            <img src="/images/국가지점번호.png" alt="Custom Marker" />
            <div class="marker-label">${feature.properties.지점번호 || "정보 없음"}</div>
          </div>
        `;

        const customOverlay = new window.kakao.maps.CustomOverlay({
          position: new window.kakao.maps.LatLng(lat, lng),
          content: customMarkerContent,
          xAnchor: -0.2,
          yAnchor: -0.1,
        });

        customMarkerContent.addEventListener("click", () => {
          const infoWindow = new window.kakao.maps.InfoWindow({
            content: `<div style="padding:5px;">${feature.properties.지점번호 || "위치 정보"}</div>`,
          });
          infoWindow.open(map, customOverlay);
        });

        return customOverlay;
      });

      const clusterMarkers = [];
      const clusterRadiusBase = 100;

      const createClusters = () => {
        clusterMarkers.forEach((cluster) => cluster.setMap(null));
        clusterMarkers.length = 0;

        const clusters = [];
        const clusterRadius = getClusterRadius(map.getLevel());

        customMarkers.forEach((marker) => {
          let addedToCluster = false;

          for (const cluster of clusters) {
            const distance = getPixelDistance(map, cluster.center, marker.getPosition());
            if (distance <= clusterRadius) {
              cluster.markers.push(marker);
              addedToCluster = true;
              break;
            }
          }

          if (!addedToCluster) {
            clusters.push({
              center: marker.getPosition(),
              markers: [marker],
            });
          }
        });

        clusters.forEach((cluster) => {
          if (cluster.markers.length > 1) {
            const clusterContent = document.createElement("div");
            clusterContent.className = "cluster-icon";
            clusterContent.textContent = `${cluster.markers.length}`;

            const clusterOverlay = new window.kakao.maps.CustomOverlay({
              position: cluster.center,
              content: clusterContent,
              xAnchor: 0.5,
              yAnchor: 0.5,
            });

            clusterMarkers.push(clusterOverlay);
            clusterOverlay.setMap(map);
          } else {
            cluster.markers[0].setMap(map);
          }
        });
      };

      const updateMarkersVisibility = () => {
        const zoomLevel = map.getLevel();
        if (zoomLevel > 5) {
          customMarkers.forEach((marker) => marker.setMap(null));
          createClusters();
        } else {
          clusterMarkers.forEach((cluster) => cluster.setMap(null));
          customMarkers.forEach((marker) => marker.setMap(map));
        }
      };

      const getPixelDistance = (map, pos1, pos2) => {
        const proj = map.getProjection();
        const p1 = proj.pointFromCoords(pos1);
        const p2 = proj.pointFromCoords(pos2);
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
      };

      const getClusterRadius = (zoomLevel) => {
        return clusterRadiusBase * Math.pow(1, 6 - zoomLevel);
      };

      window.kakao.maps.event.addListener(map, "zoom_changed", () => {
        updateMarkersVisibility();
      });

      updateMarkersVisibility();
    } catch (error) {
      console.error("GeoJSON 데이터를 로드할 수 없습니다:", error);
    }
  } else {
    console.error("Map container가 존재하지 않습니다.");
  }
};


const startLocationTracking = () => {
  setInterval(async () => {
    try {
      const response = await axios.get(`/api/location`);
      const location = response.data[userId.value];

      if (location?.latitude && location?.longitude) {
        userLocation.value.latitude = location.latitude;
        userLocation.value.longitude = location.longitude;

        console.log("사용자 위치 업데이트:", userLocation.value);
        updateUserMarker(userId.value, location.latitude, location.longitude);
      } else {
        console.warn("위치 정보를 찾을 수 없습니다:", location);
      }
    } catch (error) {
      console.error("위치 정보 갱신 실패:", error);
    }
  }, 1000);
};



const updateUserMarker = (userId, latitude, longitude = false ) => {
  if (!map || !latitude || !longitude) return;

  const position = new window.kakao.maps.LatLng(latitude, longitude);

  const markerContent = `
    <div class="marker-icon">
       <img src="${isSOSActive.value ? '/images/SOS.png' : '/images/내위치.png'}">
        <div class="wave2 ${isSOSActive.value ? '-sos-one' : '-one'}"></div>
        <div class="wave2 ${isSOSActive.value ? '-sos-two' : '-two'}"></div>
        <div class="wave2 ${isSOSActive.value ? '-sos-three' : '-three'}"></div>
    </div>`;

  if (!userMarkers[userId]) {
    userMarkers[userId] = new window.kakao.maps.CustomOverlay({
      position,
      content: markerContent,
      xAnchor: 0.5,
      yAnchor: 1,
    });
    userMarkers[userId].setMap(map);
  } else {
    userMarkers[userId].setPosition(position);
    userMarkers[userId].setContent(markerContent);
  }
   if (userId === localStorage.getItem('userId') && autoCenterEnabled) {
    map.setCenter(position);
    autoCenterEnabled = false;
  }
};

const updateSOSMarkers = (latitude, longitude) => {
    if (!latitude || !longitude) {
        console.error("유효하지 않은 SOS 위치 데이터입니다.");
        return;
    }

    const sosPosition = new window.kakao.maps.LatLng(latitude, longitude);

    sosMarkers.forEach((marker) => marker.setMap(null));
    sosMarkers = [];

    const sosMarkerContent = `
        <div class="marker-icon">
            <img src="/images/SOS.png">
            <div class="wave2 -sos-one"></div>
            <div class="wave2 -sos-two"></div>
            <div class="wave2 -sos-three"></div>
        </div>`;
    const sosMarker = new window.kakao.maps.CustomOverlay({
        position: sosPosition,
        content: sosMarkerContent,
        xAnchor: 0.5,
        yAnchor: 1,
    });

    sosMarker.setMap(map);
    sosMarkers.push(sosMarker);

    map.panTo(sosPosition);
};

const handleNotificationClick = (latitude, longitude) => {
  if (!latitude || !longitude) {
    console.error("유효하지 않은 SOS 위치 데이터입니다.");
    return;
  }

  const sosPosition = new window.kakao.maps.LatLng(latitude, longitude);

  sosMarkers = sosMarkers.filter((marker) => {
    const isSamePosition = marker.getPosition().equals(sosPosition);
    if (isSamePosition) {
      marker.setMap(null);
    }
    return !isSamePosition;
  });

  const sosMarkerContent = `
    <div class="marker-icon">
      <img src="/images/SOS.png">
      <div class="wave2 -sos-one"></div>
      <div class="wave2 -sos-two"></div>
      <div class="wave2 -sos-three"></div>
    </div>`;
  const sosMarker = new window.kakao.maps.CustomOverlay({
    position: sosPosition,
    content: sosMarkerContent,
    xAnchor: 0.5,
    yAnchor: 1,
    zIndex: 100,
  });

  sosMarker.setMap(map);
  sosMarkers.push(sosMarker);

  map.panTo(sosPosition);
};


window.showLocationOnMap = (latitude, longitude) => {
  if (latitude && longitude) {
    handleNotificationClick(parseFloat(latitude), parseFloat(longitude));
  }
};

function Zoom() {
  if (map && userLocation.value.latitude && userLocation.value.longitude) {
    const currentPosition = new window.kakao.maps.LatLng(
      userLocation.value.latitude,
      userLocation.value.longitude
    );
    map.setCenter(currentPosition);
    autoCenterEnabled = false;
  } else {
    console.error("사용자 위치 정보를 찾을 수 없습니다.");
  }
}

const reSendSOS = () => {
  isSOSActive.value = true;
  handleSendNotification(); 
};

const callEmergency = () => {
  alert("119에 연결 중입니다. 잠시만 기다려 주세요."); 
};

const connectWebSocket = () => {
  if (isConnecting || (socket && socket.readyState === WebSocket.OPEN)) return;
  isConnecting = true;
  const wsUrl = `ws://${window.location.hostname}:3000/ws`;

  socket = new WebSocket(wsUrl);

  socket.onopen = () => {
    console.log('WebSocket 연결 성공!');
    clearInterval(reconnectInterval);
    connectionError.value = '';
    isConnecting = false;
    if (userId.value) sendUserId(userId.value);
  };

  socket.onmessage = (event) => {
  try {
    const message = JSON.parse(event.data);

    if (message.type === "location_update") {
      const { userId, latitude, longitude } = message;

      if (userId === localStorage.getItem('userId')) {
        updateUserMarker(userId, latitude, longitude);
      }
    }

    if (message.type === "sos_notification") {
      const { userId, latitude, longitude } = message;

      handleNotificationClick(userId, latitude, longitude);
       updateSOSMarkers(latitude, longitude);
    }
  } catch (error) {
    console.error("WebSocket 메시지 처리 오류:", error);
  }
};


  socket.onerror = (error) => {
    console.error('WebSocket 에러:', error);
    isConnecting = false;
  };

  socket.onclose = () => {
    console.log('WebSocket 연결이 종료되었습니다. 재연결 시도 중...');
    connectionError.value = 'WebSocket 연결이 종료되었습니다. 재연결 중...';
    isConnecting = false;
    attemptReconnect();
  };
};

const attemptReconnect = () => {
  if (!reconnectInterval) {
    reconnectInterval = setInterval(() => {
      connectWebSocket();
    }, 2000);
  }
};

const sendUserId = async (userId) => {
  try {
    await axios.post('/api/saveNotificationData', { userId });
  } catch (error) {
    console.error("userId 전송 실패:", error);
  }
};

const sendNotification = async (isToAll = false) => {
  isModalOpenForSOS.value = true;
  try {
    const endpoint = '/api/sendNotification';
    const payload = {
      title: '비상 알림',
      body: '비상 알림을 보냈습니다.',
      latitude: userLocation.value.latitude,
      longitude: userLocation.value.longitude,
      notificationType:'send'
    };

    const userId = localStorage.getItem('userId');
    if (!userId) throw new Error("사용자 ID를 찾을 수 없습니다.");
    payload.userId = userId;

    await axios.post(endpoint, payload, { timeout: 5000 });

    successMessage.value = isToAll ? '모든 사용자에게 알림 전송 성공!' : '알림 전송 성공!';
  } catch (error) {
    console.error('알림 전송 실패:', error);
    connectionError.value = '알림 전송 실패: ' + error.message;
  }
};


onBeforeUnmount(() => {
  if (socket) socket.close();
  clearInterval(reconnectInterval);
});

function back_site() {
  window.location.href='/mobilemainview';
}
</script>



<style>
#notification {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.map-container {
  position: relative;
  width: 100%;
  height: 65%;
  overflow: hidden;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.15);
  background-color: #e0e0e0;
  border: solid 1px rgb(189, 189, 189);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#wrap {
  position: absolute;
  bottom: 45px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 20;
}

.overlay-button {
  position: relative;
  width: 90px;
  height: 90px;
  background-color: #d32f2f;
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 1.5em;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 33;
  cursor: pointer;
  box-shadow: 0px 8px 12px rgba(0, 0, 0, 0.25);
  transition: transform 0.2s;
}

.overlay-button:hover {
  transform: scale(1.1);
}

.wave {
  position: absolute;
  border: 2.5px solid #d32f2f;
  border-radius: 50%;
  width: 80px;
  height: 80px;
  top: 50%;
  left: 50%;
  transform-origin: center;
  transform: translate(-50%, -50%) scale(0);
  animation: drift 1.8s infinite ease-out;
  z-index: 20;
  opacity: 0.8;
}

.wave.-two {
  border-color: rgba(211, 47, 47, 0.6);
  animation-duration: 3.5s;
}

.wave.-three {
  border-color: rgba(211, 47, 47, 0.4);
  animation-duration: 4s;
}

@keyframes drift {
  from {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }
  to {
    transform: translate(-50%, -50%) scale(3.5);
    opacity: 0;
  }
}

.info-panel {
  position: absolute;
  left: 50%;
  bottom: 55px;
  transform: translate(-50%, 0);
  width: 100%;
  height: 208px;
  background-color: rgba(63, 63, 62, 0.95);
  text-align: center;
  border-radius: 12px;
  box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.3);
  z-index: 10;
  border: solid 0.5px rgb(230, 230, 230);
}

.info-panel h3 {
  color:white;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 10px;
  position: relative;
  display: inline-block;
}

.info-panel h3::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: -5px;
  width: 100%;
  height: 2px;
  background-color: #d32f2f;
  animation: blink 1s infinite;
}

.info-panel div {
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
}


@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}

.info-display {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  background-color: rgba(255, 255, 255, 0.95);
  border: 1px solid #ddd;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  padding: 20px;
  z-index: 100;
  text-align: center;
}

.info-display h3 {
  margin-bottom: 10px;
  color: #d32f2f;
  font-size: 18px;
  font-weight: bold;
}

.info-display p {
  margin: 5px 0;
  color: #333;
  font-size: 14px;
}

.close-button {
  margin-top: 15px;
  padding: 10px 20px;
  background-color: #d32f2f;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.close-button:hover {
  background-color: #a50000;
}


.custom-marker {
  transform: translate(-50%, -50%); 
  z-index: 50; 
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.15);
}

.sos-marker {
  transform: translate(-50%, -50%); 
  z-index: 50;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.15);
}

.marker-icon {
  width: 16px;
  height: auto;
  border-radius: 50%;
  overflow: hidden;
  z-index: 51; 
  margin-bottom: 1px;
}

.marker-icon img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.wave2 {
  position: absolute;
  border-radius: 50%;
  opacity: 0.5;
  width: 30px;
  height: 30px;
  top: 50%;
  left: 50%;
  transform-origin: center;
  transform: translate(-50%, -50%) scale(0);
  animation: drift2 3s infinite ease-out;
  z-index: 52; 
}

.wave2.-one {
  border: 1px solid #27a4f2;
  width: 20px;
  height: 20px;
}

.wave2.-two {
  border: 1px solid #bbe5ff;
  width: 20px;
  height: 20px;
  animation-duration: 4s;
}

.wave2.-three {
  border: 1px solid #0ca1fd;
  width: 20px;
  height: 20px;
  animation-duration: 5s;
}

.wave2.-sos-one {
  position: absolute;
  border: 2px solid #d32f2f;
  border-radius: 50%;
  opacity: 0.5;
  width: 210px;
  height: 210px;
  top: 50%;
  left: 50%;
  transform-origin: center;
  transform: translate(-50%, -50%) scale(0);
  animation: sosWave1 2s infinite ease-out;
  z-index: 52;
}

.wave2.-sos-two {
  border: 2px solid rgba(211, 47, 47, 0.6);
  width: 230px;
  height: 230px;
  opacity: 0.8;
  animation: sosWave2 4s infinite ease-out;
}

.wave2.-sos-three {
  border: 2px solid rgba(211, 47, 47, 0.3);
  width: 250px;
  height: 250px;
  opacity: 0.9;
  animation: sosWave3 5s infinite ease-out;
}

@keyframes sosWave1 {
  from {
    transform: translate(-50%, -50%) scale(0.5);
    opacity: 0.6;
  }
  to {
    transform: translate(-50%, -50%) scale(3);
    opacity: 0;
  }
}

@keyframes sosWave2 {
  from {
    transform: translate(-50%, -50%) scale(0.5);
    opacity: 0.5;
  }
  to {
    transform: translate(-50%, -50%) scale(3.5);
    opacity: 0;
  }
}

@keyframes sosWave3 {
  from {
    transform: translate(-50%, -50%) scale(0.5);
    opacity: 0.4;
  }
  to {
    transform: translate(-50%, -50%) scale(4);
    opacity: 0;
  }
}

@keyframes drift2 {
  from {
    transform: translate(-50%, -50%) scale(0);
    opacity: 1;
  }
  to {
    transform: translate(-50%, -50%) scale(3);
    opacity: 0;
  }
}

#back-button {
  position: fixed;
  top: 30px;
  left: 10px;
  z-index: 100;
  border: none;
  cursor: pointer;
  background-color: rgba(255, 255, 255, 0.9);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%; 
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.3); 
  transition: transform 0.2s ease, background-color 0.2s ease;
}

#back-button:hover {
  transform: scale(1.1); 
  background-color: rgba(255, 255, 255, 1); 
}

#back-button img {
  width: 20px;
  height: 20px;
}

#zoom-button {
  position: fixed;
  bottom: 280px;
  right: 20px;
  z-index: 100;
  border: none;
  cursor: pointer;
  background-color: rgba(255, 255, 255, 0.9); 
  width: 40px; 
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%; 
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.3);
  transition: transform 0.2s ease, background-color 0.2s ease;
}

#zoom-button:hover {
  background-color: rgba(255, 255, 255, 1); 
}

#zoom-button img {
  width: 20px;
  height: 20px;
}

.button-container {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-top: 10px;
  padding: 0 10px;
}

.action-button {
  background-color: rgba(255, 255, 255, 0.9); 
  border: none;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.3); 
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.action-button:hover {
  transform: scale(1.1); 
  background-color: rgba(255, 255, 255, 1); 
}

.action-button img {
  width: 30px;
  height: 30px;
}


.button-text-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-top: 5px;
}

#overline {
  position: absolute;
  border-top: solid 3px #b3b3b3;
  width: 30px;
  left: 50%;
  transform: translateX(-50%);
  margin-top: 10px;
  opacity: 0.8;
  border-radius: 25%;
}

.cluster-icon {
  width: 50px;
  height: 50px;
  background-color: rgba(34, 197, 94, 0.7);
  border-radius: 50%;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.marker-label {
  position: absolute;
  top: 15px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(255, 255, 255, 0.7);
  padding: 5px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  color: #333;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.2);
  white-space: nowrap;
  z-index: 10;
  pointer-events: none;
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

.toggle-switch-container2 {
    position: fixed;
    z-index: 21;
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 2.4rem;
    width: 100%;
    top: 40px;
  }
  
  .toggle-switch2 {
    width: 70px;
    height: 25px;
    background-color: #ffffff;
    border-radius: 15px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-family: 'TheJamsil4Bold';
    font-weight: 400;
    font-size: 13px;
    color: #000;
    transition: background-color 0.3s, color 0.3s;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.5);
    z-index: 15;
    border: solid 1px #868e96;
  }
  
  .toggle-switch2.active {
    background-color: #327C2B;
    color: #fff;
  }
  
  .marker-icon2{
    width: 14px;
    height: auto;
  }
</style>
