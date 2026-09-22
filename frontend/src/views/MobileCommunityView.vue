<template>
  <div class="container">
    <MobileHeaderView class="header" />
    <div class="switch-container">
      <label class="switch">
        <input type="checkbox" v-model="communityState" @change="toggleCommunityState" />
        <span class="slider">
          <span class="switch-text">{{ communityState ? "안전" : "게시판" }}</span>
        </span>
      </label>
    </div>

    <div class="content">
      <div class="col-md-12">
        <div class="write-button-container fixed-button-container">
          <button class="btn btn-lg custom-btn2" @click="goToWritePost()">
            <img src="/images/글쓰기.png" width="35px" height="auto" />
          </button>
        </div>

        <div v-for="community in communityList" :key="community.communityPk" class="card shadow-sm">
          <div class="scrollable-content">
            <div class="community-item">
              <div class="d-flex align-items-center" style="padding: 6px;">
                <img src="/images/profile.png" alt="프로필 사진" class="profile-image" />
                <p class="text-muted" id="userNickName" style="margin: 2px;">{{ community.userNickName }}</p>
              </div>

              <div class="image-map-container">
                <img
                  v-if="community.communityUrl"
                  :src="community.communityUrl"
                  alt="게시글 이미지"
                  class="img-fluid"
                  style="width: 500px; height: 225px; object-fit: cover;"
                  @click="goToCommunityDetail(community)"
                />
                <div
                  v-if="community.showMap"
                  :id="'map-' + community.communityPk"
                  class="map-overlay"
                  @click="goToMapDetail(community.courseName)"
                  style="cursor: pointer;"
                ></div>
              </div>

              <div class="d-flex align-items-center" style="border-bottom: solid 1px black;">
                <div class="d-flex justify-content-between align-items-center" id="my-container">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="19"
                    height="19"
                    fill="currentColor"
                    style="margin-left: 15px;"
                    class="bi bi-heart like-icon"
                    @click="Like(community.communityPk)"
                    :class="{ liked: community.liked }"
                    viewBox="0 0 16 16"
                  >
                    <path
                      d="m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 
                      6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 
                      2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15"
                      :class="{ 'text-danger': community.liked }"
                    />
                  </svg>
                  <span class="like-count ms-2" style="margin-right: 5px;">{{ community.likes }}</span>
                  <span class="comment-icon ms-2">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="19"
                      height="19"
                      fill="currentColor"
                      class="bi bi-chat-left-dots"
                      viewBox="0 0 16 16"
                    >
                      <path
                        d="M14 1a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H4.414A2 2 0 0 0 3 11.586l-2 2V2a1 1 0 0 1 1-1zM2 0a2 2 0 0 0-2 2v12.793a.5.5 0 0 0 .854.353l2.853-2.853A1 1 0 0 1 4.414 12H14a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2z"
                      />
                      <path
                        d="M5 6a1 1 0 1 1-2 0 1 1 0 0 1 2 0m4 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m4 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0"
                      />
                    </svg>
                  </span>
                  <span class="comment-count ms-2">{{ community.commentCount }}</span>
                  <span
                    ><img
                      src="/images/공유.png"
                      style="width: 18px; height: 18px; margin-left: 10px; margin-bottom: 1px"
                  /></span>
                  <span class="date-overlay" id="communityRegDate">{{ community.communityRegDate }}</span>
                </div>
              </div>

              <div style="margin-bottom: 15px; padding: 5px;">
                <span id="userNickName2"><img src="/images/등산객.png" width="16px;">{{ community.userNickName }}</span>
                <div>
                  <div class="card-text" :class="{ expanded: community.expanded }" id="cardContent">
                    {{ community.communityBody }}
                  </div>
                  <button
                    v-if="!community.expanded && community.communityBody.length > 50"
                    @click="toggleExpand(community)"
                    class="show-more-btn"
                  >
                    더 보기
                  </button>
                  <button
                    v-if="community.expanded"
                    @click="toggleExpand(community)"
                    class="show-more-btn"
                  >
                    닫기
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <MobileFooterView5 class="footer"></MobileFooterView5>
  </div>
</template>


<script setup>
import MobileHeaderView from '@/components/MobileHeaderView.vue';
import MobileFooterView5 from "@/components/MobileFooterView5.vue";
import { ref, computed, onMounted, nextTick } from 'vue';
import axios from 'axios';
import router from '@/router/index.js';

const communityList = ref([]);

const communityMap = ref({});

const searchQuery = ref('');

const courseData = ref({});

const course = ref([]);

const communityState = ref(false);

const showMap = ref('photo');

const toggleCommunityState = () => {
  if (communityState.value) {
    getSafeList();
  } else {
    getAllList();
  }
};

const getAllList = async () => {
  try {
    await getList();
    communityState.value = false;

    await nextTick();
    communityList.value.forEach(community => {
      drawCourseOnMap(community.courseName, community.communityPk, community.lat, community.lng);
    });
  } catch (error) {
    console.error(error);
  }
};

const getSafeList = async () => {
  try {
    const response = await axios.post('/api/safeList');
    communityList.value = response.data.map(community => ({
      ...community,
      showMap: true,
      likes: community.likes || 0,
      commentCount: community.commentCount || 0,
      courseName: community.courseName,
      lat: community.lat || community.latitude,
      lng: community.lng || community.longitude
    }));

    await nextTick();
    communityList.value.forEach(community => {
      drawCourseOnMap(community.courseName, community.communityPk, community.lat, community.lng);
    });
    communityState.value = true;
  } catch (error) {
    console.error("Error during safe list loading:", error);
  }
};

const filteredCommunityList = computed(() => {
  return communityList.value.filter(community => {
    return community.communityBody.includes(searchQuery.value) ||
      community.userNickName.includes(searchQuery.value);
  });
});


const goToWritePost = () => {
  router.push({ path: '/writePost' });
};

const goToCommunityDetail = (community) => {
  router.push({ path: `/communityDetailView`, query: { community: JSON.stringify(community) } });
};


function createMap(containerId, startLat, startLng) {
  const container = document.getElementById(containerId);
  if (!container) {
    console.error(`Element with ID ${containerId} not found`);
    return;
  }

  const options = {
    center: new kakao.maps.LatLng(startLat, startLng),
    level: 6,
  };

  const map = new kakao.maps.Map(container, options);

  communityMap.value[containerId] = map;

  return map;
}

const goToMapDetail = async (courseName) => {
  try {
    const response = await axios.post('/api/course/byName', {
      courseName: courseName
    });

    course.value = response.data;

    router.push({
      name: 'mobilemountaindetailview',
      query: {
        course: JSON.stringify(course.value),
        mapType: showMap.value,
      },
    });
  } catch (error) {
    console.error("Error during like or navigation:", error);
  }
};


const loadCourseData = async () => {
  try {
    const response = await axios.get('/api/spatial/trails/geojson');
    const geoJsonData = response.data;

    geoJsonData.features.forEach(feature => {
      const courseName = feature.properties.PMNTN_NM;
      const coordinates = feature.geometry.coordinates
        .reduce((acc, lineString) => acc.concat(lineString.map(coord => ({ lat: coord[1], lng: coord[0] }))), []);

      if (!courseData.value[courseName]) {
        courseData.value[courseName] = [];
      }
      courseData.value[courseName].push(...coordinates);
    });

    console.log("Course Data Loaded:", courseData.value);
  } catch (error) {
    console.error("GeoJSON 데이터를 불러오는 중 오류 발생:", error);
  }
};

const drawCourseOnMap = (courseName, communityPk, lat, lng) => {
  const mapId = `map-${communityPk}`;
  resetMap(mapId);

  const matchingCourseKey = Object.keys(courseData.value).find(key =>
    key.includes(courseName)
  );

  if (!matchingCourseKey) {
    console.error(`Coordinates not found for course: ${courseName}`);
    return;
  }

  const coordinates = courseData.value[matchingCourseKey];
  const linePath = coordinates.map(coord => new kakao.maps.LatLng(coord.lat, coord.lng));

  let map = communityMap.value[mapId];
  if (!map) {
    map = createMap(mapId, lat, lng);
  }

  if (map) {
    const polyline = new kakao.maps.Polyline({
      path: linePath,
      strokeWeight: 5,
      strokeColor: '#00FF00',
      strokeOpacity: 0.8,
      strokeStyle: 'solid',
    });
    polyline.setMap(map);

    console.log(`Polyline set for course: ${matchingCourseKey}`, polyline.getPath());
  } else {
    console.error(`Map not found for map ID: ${mapId}`);
  }
};


function resetMap(containerId) {
  const map = communityMap.value[containerId];
  if (map) {
    map.removeOverlayMapTypeId(kakao.maps.MapTypeId.POLYLINE);
    communityMap.value[containerId] = null;
  }
}


onMounted(async () => {
  const script = document.createElement('script');
  script.onload = async () => {
    kakao.maps.load(async () => {
      await loadCourseData();
      await getList();
      await nextTick();

      communityList.value.forEach(community => {
        drawCourseOnMap(community.courseName, community.communityPk, community.lat, community.lng);
      });
    });
  };
  script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false';
  document.head.appendChild(script);
});

const getList = async () => {
  try {
    const response = await axios.get('/api/communityList');
    communityList.value = response.data.map(community => ({
      ...community,
      showMap: true,
      likes: community.likes || 0,
      commentCount: community.commentCount || 0,
      courseName: community.courseName,
      lat: community.lat || community.latitude,
      lng: community.lng || community.longitude
    }));

    filteredCommunityList.value = communityList.value;

    communityList.value.sort((a, b) => new Date(b.communityRegDate) - new Date(a.communityRegDate));

    await nextTick();

    communityList.value.forEach(community => {
      const mapId = `map-${community.communityPk}`;
      createMap(mapId, community.lat, community.lng);
    });
    console.log(communityList.value);
  } catch (err) {
    console.error(err);
  }
};


const Like = async (communityPk) => {
  try {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      console.log("User not logged in");
      return;
    }

    const response = await axios.post('/api/like', {
      userId: userId,
      communityPk: communityPk
    });

    communityList.value = response.data.map(community => ({
      ...community,
      showMap: true,
      likes: community.likes || 0,
      commentCount: community.commentCount || 0,
      lat: community.lat || community.latitude,
      lng: community.lng || community.longitude
    }));


  } catch (error) {
    console.log("Error liking the post:", error);
  }
};
</script>


<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: white;
  border-bottom: 1px solid #ddd;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
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

.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: visible;
  margin: 0;
  padding: 0;
}


.content {
  flex: 1;
  overflow-y: auto;
  margin-top: 65px;
  padding-bottom: 70px;
}

.card {
  margin: 2px auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  overflow: hidden;
}

.card-body {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.scrollable-content {
  max-height: 300%;
  overflow-y: auto;
  background: transparent;
  position: relative;
  z-index: 10;

}

.separator-line {
  border: none;
  border-top: 1px solid white;
  margin: 20px 0;
}

.scrollable-content::-webkit-scrollbar {
  width: 0;
}

.scrollable-content {
  scrollbar-width: none;
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
}

.map-overlay {
  position: absolute;
  bottom: 15px;
  right: 5px;
  width: 80px;
  height: 80px;
  border: 2px solid #dedede;
  border-radius: 15px;
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


.custom-btn {
  padding: 4px 7px;
  font-size: 10px;
  border-radius: 20px;
}

.profile-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid black;
  margin-right: 10px;
}

.back-button-container {
  text-align: left;
  margin-top: 10px;
}

.location-text {
  font-size: 0.70em;
  font-weight: 100;
  margin: 0;
  padding: 0;
  text-decoration: underline;
  color: #555;
  text-align: left;
  margin-left: 2px;
}

.fixed-button-container {
  position: fixed;
  bottom: 100px;
  right: 20px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.fixed-button-container .btn {
  background-color: #ffffff;
  border: solid 1px #4caf50;
  border-radius: 20px;
  color: #4caf50;
  font-size: 12px;
  padding: 5px 10px;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.2);
  transition: background-color 0.2s ease-in-out, color 0.2s ease-in-out;
}

.fixed-button-container .btn:hover {
  background-color: #4caf50;
  color: #ffffff;
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

.content {
  flex: 1;
  overflow-y: auto;
  padding-top: 0px;
  padding-bottom: 70px;
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

.switch-container {
  position: fixed;
  top: 25px;
  left: 160px;
  z-index: 1100;
  display: flex;
  align-items: center;
  border: solid 1px black;
  border-radius: 50px;
}

.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  border-radius: 50px;
  transition: background-color 0.4s;
  display: flex;
  justify-content: center;
  align-items: center;
}

.switch-text {
  font-size: 12px;
  font-weight: bold;
  color: black;
  text-transform: uppercase;
  transition: color 0.4s;
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