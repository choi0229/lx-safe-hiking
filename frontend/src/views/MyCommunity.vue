<template>
  <div class="container">
    <MobileHeaderView class="header" />
     <div class="btn-group-container">
        <div class="btn-group" role="group" aria-label="Basic radio toggle button group">
          <input type="radio" class="btn-check" name="btnradio" id="btnradio1" autocomplete="off" @click="goToRecord()"/>
          <label class="btn btn-outline-success" for="btnradio1">운동</label>

          <input type="radio" class="btn-check" name="btnradio" id="btnradio2" autocomplete="off" @click="goToRecordImg()"/>
          <label class="btn btn-outline-success" for="btnradio2">기록</label>

          <input type="radio" class="btn-check" name="btnradio" id="btnradio3" autocomplete="off" checked />
          <label class="btn btn-outline-success" for="btnradio3">나의 커뮤니티</label>
        </div>
      </div>

      <div class="record-summary" style="margin-top: 60px; margin-bottom: 70px; margin-left: 7px;">
        <div class="image-container-wrapper">
          <div v-for="community in communityList" :key="community.communityPk" class="image-container2">
            <div class="scrollable-content">
              <div class="community-item">
                <span class="title-container">
                  <span class="title-text" >
                    <img src="/images/메모.png" alt="메모 아이콘" class="memo-icon" style="width: 16px; height: 16px; margin-left: 1px; margin-right: 2px;"/>
                    {{ community.communityTitle }}
                  </span>
                  <button @click="toggleMap(community)" class="custom-btn">
                    {{ community.showInfo ? "기록" : "경로" }}
                  </button>
                </span>
                <p class="location-text"> <img src="/images/기본.png" alt="" style="width: 13px; height: 13px;">서울 종로구 무악동</p>

                <div class="image-map-container">
                  <img
                    v-if="community.communityUrl"
                    :src="community.communityUrl"
                    alt="게시글 이미지"
                    class="img-fluid"
                    style="width: 500px; height: 225px; object-fit: cover"/>

                  <div
                    v-if="community.showMap"
                    :id="'map-' + community.communityPk"
                    class="map-overlay"
                    @click="goToMapDetail(community.lat, community.lng)"
                    style="cursor: pointer"
                  ></div>

                  <div v-if="community.showInfo" class="info-overlay">
                    <div class="overlay-content">
                      <span class="overlay-title">Time 33:00</span>
                      <span class="overlay-title">Distance 3.40km</span>
                      <span class="overlay-title">Pace 8'37"</span>
                    </div>
                  </div>
                </div>

                <div class="d-flex justify-content-between " style="border-bottom: solid 1px black; margin-bottom: 3px;">
                  <div class="d-flex ">
                    <div class="d-flex align-items-center me-2">
                      <span
                        class="like-icon"
                        @click="toggleLike(community.communityPk)"
                        :class="{ liked: community.liked }"
                        >{{ community.liked ? "💚" : "🤍" }}</span
                      >
                      <span class="like-count ms-1">{{ community.likeCount }}</span>
                    </div>
                    
                    <div class="d-flex align-items-center me-2">
                      <span class="comment-icon">💬</span>
                      <span class="comment-count ms-1">{{ community.commentCount }}</span>
                    </div>
                  </div>
                  <span class="community-date" style="margin-top: 3px; font-size: 14px;">{{ community.communityRegDate }}</span>
                </div>
                <div class="d-flex align-items-center ms-2" style="margin-bottom: 10px; margin-top: 5px;">
                  <img
                    v-if="community.profileImageUrl"
                    :src="community.profileImageUrl"
                    alt="프로필 사진"
                    class="profile-image me-2"/>
                  <span class="text-muted me-2" id="userNickName">{{community.userNickName}}</span
                  ><br />
                  <span class="card-text" id="cardContent">{{community.communityBody}}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
    </div>
  </div>
  <MobileFooterView3 class="footer"></MobileFooterView3>
</template>


<script setup>
import MobileHeaderView from "@/components/MobileHeaderView.vue";
import MobileFooterView3 from "@/components/MobileFooterView3.vue";
import { ref, onMounted, nextTick } from "vue";
import axios from "axios";
import router from "@/router/index.js";

const goToMapDetail = (lat, lng) => {
  router.push({ path: "/mapdetail", query: { lat, lng } });
};

function createMap(containerId, lat, lng) {
  const container = document.getElementById(containerId);
  if (!container) {
    console.error(`Element with ID ${containerId} not found`);
    return;
  }
  const options = {
    center: new kakao.maps.LatLng(lat, lng),
    level: 5,
  };
  const map = new kakao.maps.Map(container, options);

  const markerPosition = new kakao.maps.LatLng(lat, lng);
  const marker = new kakao.maps.Marker({
    position: markerPosition,
  });

  marker.setMap(map);
}

const communityList = ref([]);

onMounted(() => {
  const script = document.createElement("script");
  script.onload = () => {
    kakao.maps.load(() => {
      getList();
    });
  };
  script.src =
    "https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false&libraries=clusterer,services";
  document.head.appendChild(script);
});

const getList = async () => {
  console.log("get 실행");
  try {
    const response = await axios.get("/api/communityList");
    communityList.value = response.data.map((community) => ({
      ...community,
      showMap: true,
      showInfo: false,
      likeCount: community.likeCount || 0,
      commentCount: community.commentCount || 0,
      lat: community.lat || community.latitude,
      lng: community.lng || community.longitude,
    }));
    console.log(response.data);
    await nextTick();
    communityList.value.forEach((community) => {
      const mapId = `map-${community.communityPk}`;
      createMap(mapId, community.lat, community.lng);
    });
  } catch (err) {
    console.log(err);
  }
};

const toggleMap = async (community) => {
  community.showInfo = !community.showInfo;
  community.showMap = !community.showMap;

  if (community.showMap) {
    await nextTick();
    const mapId = `map-${community.communityPk}`;
    createMap(mapId, community.lat, community.lng);
  }
};

const toggleLike = (communityPk) => {
  const post = communityList.value.find((c) => c.communityPk === communityPk);
  if (post) {
    post.liked = !post.liked;

    post.liked ? post.likeCount++ : post.likeCount--;

  }
};

function goToRecord() {
  router.push({ path: "/record" });
}

function goToRecordImg() {
  router.push({ path: "/recordImg" });
}
</script>

<style scoped>
.info-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
  z-index: 2;
}

.styled-button {
  border: 2px solid #4caf50;
  border-radius: 5px;
  padding: 8px 16px;
  transition: background-color 0.3s, transform 0.2s;
}

#app {
  background-color: white;
  min-height: 100vh;
}

#map {
  height: 400px;
  width: 100%;
}

.app-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.record-summary {
  flex: 1;
  margin-top: 60px;
  margin-bottom: 70px;
  overflow-y: auto;
  box-sizing: border-box;
}

.record-summary::-webkit-scrollbar {
    width: 8px;
    background-color: transparent;
}

.record-summary::-webkit-scrollbar-thumb {
    background-color: transparent;
}

.image-container-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
}


.image-container2 {
  position: relative;
  width: 100%;
  background-color: white;
  margin: 0 auto;
  overflow: hidden;
}


.card-body {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  width: 100%;
}

.image-container2 img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}


.separator-line {
  border: none;
  border-top: 1px solid white;
  margin: 20px 0;
}

.scrollable-content::-webkit-scrollbar {
  width: 0;
}



.map-overlay {
  position: absolute;
  bottom: 20px;
  right: 10px;
  width: 70px;
  height: 70px;
  border: 2px solid white;
  border-radius: 10px;
  background-color: #4caf50;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  cursor: pointer;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
}

.date-overlay {
  position: absolute;
  top: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  bottom: 10px;
  left: 10px;
  color: white;
  background-color: rgba(0, 0, 0, 0);
  padding: 5px;
  border-radius: 5px;
}

.separator-line {
  border: none;
  border-top: 1px solid white;
  margin: 20px 0;
}

@font-face {
  font-family: "TheJamsil5Bold";
  src: url("https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_2302_01@1.0/TheJamsil5Bold.woff2")
    format("woff2");
  font-weight: 700;
  font-style: normal;
}

h1,
h2,
h3,
.btn-group .btn,
p {
  font-family: "TheJamsil5Bold", sans-serif !important;
}

#cardContent {
  font-family: "TheJamsil4Bold";
  font-weight: 500;
  font-size: 14px;
}

#userNickName {
  font-family: "TheJamsil5Bold";
  font-weight: 600;
  font-size: 14px;
  margin-top: 0.2rem;
}

#communityRegDate {
  font-family: "TheJamsil4Bold";
  font-weight: 700;
  font-size: 15px;
}

.info-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
  padding: 10px;
  background: rgba(255, 255, 255, 0);
  z-index: 2;
}

.overlay-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  gap: 20px;
}

.overlay-title {
  margin: 0px;
  font-size: 17px;
  color: white;
  font-weight: bold;
}

.card-group {
  display: flex;
  width: 100%;
}

.info-card {
  width: 150px;
  height: auto;
  margin: 0 5px;
  color: white;
  background-color: rgba(255, 255, 255, 0);
}

.card {
  background-color: #fff;
  margin-bottom: 10px;
  border-radius: 10px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.profile-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
}

.location-text {
  font-size: 0.7em;
  font-weight: 100;
  margin: 0;
  color: #555;
  margin-bottom: 3px;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: white;
  border-bottom: 1px solid #ddd;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70px;
  background-color: white;
  border-top: 1px solid #ddd;
  z-index: 1000;
}

.info-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
  z-index: 2;
}

.styled-button {
  border: 2px solid #4CAF50;
  border-radius: 5px;
  padding: 8px 16px;
  transition: background-color 0.3s, transform 0.2s;
}

#map {
  height: 400px;
  width: 100%;
}

.content {
  flex: 1;
  margin-top: 60px;
  margin-bottom: 70px;
  overflow-y: auto;
  padding: 10px;
  box-sizing: border-box;
}

.content::-webkit-scrollbar {
  width: 8px;
}

.content::-webkit-scrollbar-thumb {
  background-color: #ccc;
}

.content::-webkit-scrollbar-track {
  background-color: #f0f0f0;
}

.scrollable-content {
  max-width: 100%;
  padding: 0;
}

.separator-line {
  border: none;
  border-top: 1px solid white;
  margin: 20px 0;
}

.scrollable-content::-webkit-scrollbar {
  width: 0;
}

.image-map-container {
  position: relative;
  width: 100%;

}

.image-map-container img {
  width: 100%;
  height: auto;
  object-fit: cover; 
  margin-bottom: 10px; 
  border: solid 1px #dedede; 
  border-radius: 10px;
}

.community-item {
  padding: 5px; 
  border-bottom: solid 1px #9e9e9e;
  width: 100%; 
  box-sizing: border-box; 
}

.separator-line {
  border: none;
  border-top: 1px solid white;
  margin: 20px 0;
}

@font-face {
  font-family: 'TheJamsil5Bold';
  src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_2302_01@1.0/TheJamsil5Bold.woff2') format('woff2');
  font-weight: 700;
  font-style: normal;
}

h1,
h2,
h3,
.title-container,
p {
  font-family: 'TheJamsil5Bold', sans-serif !important;
}

#cardContent {
  font-family: 'TheJamsil4Bold';
  font-weight: 500;
  font-size: 14px;
  padding: 2px;
}

#userNickName {
  font-family: 'TheJamsil5Bold';
  font-weight: 600;
  font-size: 14px;
  margin-left: 2;
  text-align: left;
  width: 500px;
  min-width: 50px;
  flex: 0 0 15%;
  font-size: 16px;
  font-weight: bold;
  word-wrap: break-word;
}

#userNickName2 {
  font-family: 'TheJamsil5Bold';
  margin-left: 2;
  flex: 0 0 15%;
  font-size: 14px;
  font-weight: bold;
  margin-left: 5px;
}

.info-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
  padding: 10px;
  background: rgba(255, 255, 255, 0);
  z-index: 2;
}

.overlay-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  gap: 20px;
}

.overlay-title {
  margin: 0px;
  font-size: 17px;
  color: white;
  font-weight: bold;
}


.info-card {
  width: 150px;
  height: auto;
  margin: 0 5px;
  color: white;
  background-color: rgba(255, 255, 255, 0);
}

.title-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.title-text {
  display: inline-block;
  font-size: 18px;
  padding-right: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: calc(100% - 120px);
  vertical-align: middle;
}

.custom-btn {
  width: 100px;
  font-size: 14px;
  border-radius: 5px;
  border: solid 2px #3b8f3e;
  background-color: #ffffff;
  cursor: pointer;
  position: relative;
}

.custom-btn:hover {
  background-color: #3b8f3e;
  color: white;
}

.back-button-container {
  text-align: left;
  margin-top: 10px;
}

.btn-group-container {
  position: fixed;
  top: 13px;
  right: 10px;
  z-index: 1100;
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

.custom-btn2 {
  background-color: #4caf50;
  color: white;
  border: solid 1px #4caf50;
  border-radius: 50%;
  width: 55px;
  height: 55px;
  padding: 10px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  cursor: pointer;
}

.custom-btn2:hover {
  transform: scale(1.1);
  box-shadow: 0px 6px 8px rgba(0, 0, 0, 0.5);
}

.custom-btn2 img {
  width: 40px;
  height: auto;
  left: 1px;
}

#cardContent {
  flex: 1;
  font-size: 14px;
  color: #333;
  word-wrap: break-word;
  text-align: left;
}



#my-container{
  margin-bottom: 5px;
  margin-right: 15px;
}


.card-text {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: normal;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.card-text.expanded {
  display: block;
  white-space: normal;
  overflow: visible;
}

.show-more-btn {
  background: none;
  font-weight: bold;
  border: none;
  color: #9e9e9e;
  cursor: pointer;
  font-size: 12px;
  margin-top: 2px;
  margin-left: 3px;
}

.show-more-btn:hover {
  text-decoration: underline;
}

.date-overlay{
  margin-left:140px;
  font-family: 'TheJamsil4Bold';
  font-size: 14px;
}

input:checked + .slider {
  background-color: #4caf50;
}

input:checked + .slider .switch-text {
  color: white;
}

input:not(:checked) + .slider {
  background-color: #fefefe;
}

input:not(:checked) + .slider .switch-text {
  color: black;
}
</style>
