<template>
    <div id="app">
        <div class="app-main">
            <div id="map-container">
                <div id="map"></div>
            </div>
                
            <div class="review-search" style="top: 30px;" @click="openSearchModal">
                <div class="search-container">
                <input type="text" placeholder="검색" />
                <button class="search-button">
                    <img src="/images/돋보기white.png" alt="Search" />
                </button>
                </div>
            </div>


      <button id="back-button" @click="goToListPage">
      <img src="/images/뒤로가기.png" alt="뒤로가기">
      </button>
    </div>

    <div class="toggle-switch-container2" style="z-index: 10;">
        <div class="toggle-switch2">
          <img src="/images/파손.png" alt="" style="width: 15px; height: auto; margin-right: 5px;"> 파손
        </div>
        <div class="toggle-switch2" >
          <img src="/images/낙석.png" alt="" style="width: 15px; height: auto; margin-right: 5px;"> 낙석
        </div>
        <div class="toggle-switch2" style=" font-size: 12px; width: 100px;">
          <img src="/images/야생동물.png" alt="" style="width: 15px; height: auto; margin-right: 5px;"> 야생동물
        </div>
      </div>
            
    
        <MobileFooterView4></MobileFooterView4>

        <div v-if="isModalOpen" class="modal">
            <div class="modal-content">
                <span class="close" @click="closeSearchModal">&times;</span>
                <input v-model="keywordInput" placeholder="장소 검색" class="mt-5" />
                <button class="searchButton" @click="searchPlaces">검색하기</button>

                <ul v-if="searchResults.length > 0">
                    <li v-for="(place, index) in searchResults" :key="index" @click="selectPlace(place)">
                        {{ place.place_name }} ({{ place.address_name }})
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>
    
    
<script setup>
import MobileFooterView4 from "@/components/MobileFooterView4.vue";
import { ref, onMounted } from "vue";
import { useComplaintListStore, useComplaintStore } from "@/stores/complaint"
import router from "@/router/index.js";
import { storeToRefs } from 'pinia';

const complaintListStore = useComplaintListStore();

onMounted(async () => {
    await complaintListStore.fetchComplaintList();
    const hasRefreshed = sessionStorage.getItem('hasRefreshed');

    if (!hasRefreshed) {
    sessionStorage.setItem('hasRefreshed', 'true');
    window.location.reload();
    } else {
    sessionStorage.removeItem('hasRefreshed');
    }
    initializeMap();
});

const { complaintlist } = storeToRefs(complaintListStore);

function initializeMap() {
    const script = document.createElement('script');
    script.onload = () => {
        kakao.maps.load(() => {
            createMap();
            isKakaoMapLoaded.value = true;
        });
    };
    script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false&libraries=clusterer,services';
    document.head.appendChild(script);
}

let map;

function createMap() {
    const mapContainer = document.getElementById('map');
    const mapOption = {
        center: new kakao.maps.LatLng(37.660833, 126.993333),
        level: 9,
    };

    map = new kakao.maps.Map(mapContainer, mapOption);

    var clusterer = new kakao.maps.MarkerClusterer({
        map: map,
        averageCenter: true,
        minLevel: 6
    });

    const markerImages = {
        낙석: "/images/낙석.png",
        야생동물: "/images/야생동물.png",
        파손: "/images/파손.png",
    };

    const positions = complaintlist.value.map(complaint => ({
        title: complaint.mountainName,
        latlng: new kakao.maps.LatLng(complaint.latitude, complaint.longitude),
        type: complaint.complaintType,
        complaintNo: complaint.complaintNo
    }));

    function goToComplaintInfo(complaintNo) {
        sessionStorage.setItem("complaintNo", complaintNo);
        router.push({ path: "/complaintInfoMobile" });
    }

    const markers = positions.map((position) => {
        const imageSrc = markerImages[position.type] || markerImages['낙석'];

        const imageSize = new kakao.maps.Size(25, 30);
        const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);

        const marker = new kakao.maps.Marker({
            position: position.latlng,
            title: position.title,
            image: markerImage,
        });

        const infowindowContent = document.createElement('div');
        infowindowContent.style.padding = '2px';
        infowindowContent.style.cursor = 'pointer';
        infowindowContent.style.width = '180px';
        infowindowContent.style.textAlign = 'center';
        infowindowContent.innerHTML = `
            ${position.type} - ${position.title}
            <i class="bi bi-caret-right-fill" style="font-size: 1em; color: black; cursor: pointer; margin-left: 1px;"></i>
        `;
    
        infowindowContent.querySelector('.bi-caret-right-fill').addEventListener('click', () => {
            goToComplaintInfo(position.complaintNo);
        });


        const infowindow = new kakao.maps.InfoWindow({
            content: infowindowContent,
            zIndex: 10
        });


        let openInfowindow = null;

        kakao.maps.event.addListener(marker, 'click', function() {
            if (openInfowindow !== infowindow) {
                if (openInfowindow) {
                    openInfowindow.close();
                }
                infowindow.open(map, marker);
                openInfowindow = infowindow;
            } else {
                infowindow.close();
                openInfowindow = null;
            }
        });

        return marker;

    });

    clusterer.addMarkers(markers);

    kakao.maps.event.addListener(map, 'zoom_changed', function() {
        if (map.getLevel() <= 6) {
            clusterer.clear();
            markers.forEach(function(marker) {
                marker.setMap(map);
            });
        } else {
            markers.forEach(function(marker) {
                marker.setMap(null);
            });
            clusterer.addMarkers(markers);
        }
    });
}

const complaintStore = useComplaintStore();
// eslint-disable-next-line no-unused-vars
const complaintone = storeToRefs(complaintStore);

const isModalOpen = ref(false);
const keywordInput = ref("");
const searchResults = ref([]);

let isKakaoMapLoaded = ref(false);

function openSearchModal() {
    isModalOpen.value = true; 
    console.log("모달열림" + isModalOpen.value);
}

function closeSearchModal() {
    isModalOpen.value = false;
}

function searchPlaces() {
    const keyword = keywordInput.value.trim();
    if (!keyword) {
        alert('키워드를 입력해주세요!');
        return;
    }

    if (!isKakaoMapLoaded.value) {
        alert('Kakao Maps가 아직 로드되지 않았습니다.');
        return;
    }

    const ps = new kakao.maps.services.Places();
    ps.keywordSearch(keyword, placesSearchCB);
}

function placesSearchCB(data, status) {
    if (status === kakao.maps.services.Status.OK) {
        searchResults.value = data; 
    } else {
        alert('검색 결과가 없습니다.');
    }
}

function selectPlace(place) {

    const centerPosition = new kakao.maps.LatLng(place.y, place.x);
    map.setCenter(centerPosition);
    map.setLevel(7);
    closeSearchModal();
}


function goToListPage() {
    router.replace({ path: "/complaintListMobile" });
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
    position: relative;
    height: 100vh;
    width: 100vw;
    background-color: #f5f5f5;
    overflow: hidden;
}

#map-container {
    position: relative;
    width: 100%;
    height: 100%;
}

#map {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
}


.fs-9 {
    font-size: 0.6rem !important;
}

hr.separator-line {
    border: 1px solid #ccc;
    margin: 10px 0;
    width: 100%;
}

.row {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
    justify-content: space-between;
}

.smaller-text {
    font-size: 0.75rem;
    white-space: nowrap;
}

.card-container {
    padding-left: 10px;
    padding-right: 10px;
}

.search-modal {
    position: absolute;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    width: 300px;
    background-color: #fff;
    padding: 10px;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    z-index: 10;
    display: flex;
    align-items: center;
    margin-left: 25px;
}

.search-modal input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 5px;
    font-size: 14px;
}


.carousel-inner {
  display: flex;
  width: 300px;
  align-items: center;
  margin-top: 28em;
  overflow: hidden;
}

.carousel-image {
    display: block; 
  width: 100%;
  height: auto; 
  max-width: 200px; 
  object-fit: cover; 
  margin: 0 5px;
  border-radius: 8px; 
}

.map-legend {
    position: absolute;
    bottom: 20px;
    left: 20px;
    background-color: rgba(255, 255, 255, 0.8);
    padding: 10px;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    z-index: 10;
}

.legend-item {
    display: flex;
    align-items: center;
    margin-bottom: 5px;
}

.legend-icon {
    width: 20px;
    height: 20px;
    margin-right: 5px;
}


button.back-button {
    position: absolute;
    top: 20px;
    left: 20px;
    z-index: 10;
    background-color: white;
    border-radius: 5px;
    padding: 10px;
    border: none;
    cursor: pointer;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

button.back-button img {
    width: 20px;
    height: 20px;
}

.modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 20;
}

.modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 10px;
    width: 80%;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.modal-content input {
    width: 100%;
    padding: 10px;
    margin-bottom: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
}

.searchButton {
    display: block;
    width: 100%;
    background-color: #04663f;
    color: white;
    padding: 10px;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
}

.searchButton:hover {
    background-color: #034f2c;
}

.close {
    position: absolute;
    top: 10px;
    right: 10px;
    font-size: 18px;
    cursor: pointer;
    color: #333;
}

@media (max-width: 480px) {
    .search-modal {
        width: 80%;
    }

    .map-legend {
        bottom: 10px;
        left: 10px;
        font-size: 12px;
    }

    .legend-icon {
        width: 15px;
        height: 15px;
    }

    button.back-button {
        top: 30px;
        left: 10px;
    }
}
</style>  