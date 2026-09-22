<template>
  <div class="container">
    <HeaderView class="header" />
    <div class="row content">
      <div v-if="communityState">
        <button class="btn btn-success btn-sm mb-3 mt-3 ms-3" @click="getAllList()">모든 게시물 보기</button>
      </div>
      <div v-else>
        <button class="btn btn-success btn-sm mb-3 mt-3 ms-3" @click="getSafeList()">안전 게시물 보기</button>
      </div>

      <div class="col-md-12">
        <div class="write-button-container fixed-button-container mt-3 mx-4">
          <button class="btn btn-lg custom-btn2" @click="goToWritePost()">
            <svg xmlns="http://www.w3.org/2000/svg" width="35" height="35" fill="currentColor"
              class="bi bi-plus-circle me-2" viewBox="0 0 16 16" style="margin-left: -12px; margin-top: -5px;">
              <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16" />
              <path
                d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4" />
            </svg>
          </button>
        </div>

        <div v-for="community in communityList" :key="community.communityPk" class="card shadow-sm">
          <div class="scrollable-content">
            <div class="community-item">
              <div class="d-flex align-items-center">
                <img src="/images/profile.png" alt="프로필 사진" class="profile-image me-2" />
                <p class="text-muted mt-4" id="userNickName">{{ community.userNickName }}</p>
              </div>

              <div class="image-map-container mb-2">
                <img v-if="community.communityUrl" :src="community.communityUrl" alt="게시글 이미지" class="img-fluid"
                  style="width: 500px; height: 225px; object-fit: cover;" @click="goToCommunityDetail(community)" />
                <div class="date-overlay" id="communityRegDate">{{ community.communityRegDate }}</div>


                <div v-if="community.showMap" :id="'map-' + community.communityPk" class="map-overlay"
                  @click="goToMapDetail(community.courseName)" style="cursor: pointer;"></div>
              </div>

              <div class="d-flex justify-content-between align-items-center ms-2" id="my-container">
                <div class="d-flex align-items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" width="19" height="19" fill="currentColor"
                    class="bi bi-heart like-icon" @click="Like(community.communityPk)"
                    :class="{ liked: community.liked }" viewBox="0 0 16 16">
                    <path
                      d="m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 
                      6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 
                      2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15"
                      :class="{ 'text-danger': community.liked }" />
                  </svg>

                  <span class="like-count ms-1">{{ community.likes }}</span>

                  <span class="comment-icon ms-2">
                    <svg xmlns="http://www.w3.org/2000/svg" width="19" height="19" fill="currentColor"
                      class="bi bi-chat-left-dots" viewBox="0 0 16 16">
                      <path
                        d="M14 1a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H4.414A2 2 0 0 0 3 11.586l-2 2V2a1 1 0 0 1 1-1zM2 0a2 2 0 0 0-2 2v12.793a.5.5 0 0 0 .854.353l2.853-2.853A1 1 0 0 1 4.414 12H14a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2z" />
                      <path
                        d="M5 6a1 1 0 1 1-2 0 1 1 0 0 1 2 0m4 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m4 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0" />
                    </svg>
                  </span>
                  <span class="comment-count ms-2">{{ community.commentCount }}</span>
                </div>
              </div>

              <div class="d-flex ms-2">
                <span class="text-muted me-2" id="userNickName">{{ community.userNickName }}</span>
                <span class="card-text" id="cardContent">{{ community.communityBody }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      
    </div>
    
  </div>

  
</template>

<script setup>
import HeaderView from '@/components/HeaderView.vue';
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

body {
  background: linear-gradient(to bottom, #fafafa, #ffffff);
}

.container {
  background: #ffffff;
  max-width: 900px;
  margin: auto;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  border-radius: 10px;
  padding: 20px;
}

.main-content {
  width: 70%;
  padding-right: 20px;
}

.sidebar {
  width: 25%;
  background-color: #fff;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  font-family: 'Roboto', sans-serif;
}

.sidebar-item {
  margin-bottom: 20px;
}

.sidebar-item h5 {
  font-size: 18px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 10px;
}

.sidebar-item ul {
  list-style-type: none;
  padding: 0;
}

.sidebar-item ul li {
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}

.sidebar-item ul li:hover {
  text-decoration: underline;
  cursor: pointer;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding-top: 70px;
  padding-bottom: 70px;
  margin-top: 25%;
}

.card {
  transition: transform 0.2s;
  background-color: white;
  min-height: 350px;
  border-radius: 20px;
  border: none;
  max-width: 700px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  word-wrap: break-word;
  overflow: visible;
}

.card-body {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}


.like-icon:hover {
  color: #FF5252;
  transform: scale(1.2);
  transition: 0.2s ease-in-out;
}

.comment-icon:hover {
  color: #007BFF;
  transform: scale(1.2);
  transition: 0.2s ease-in-out;
}



.scrollable-content {
  max-height: 300%;
  overflow-y: auto;
  padding: 10px;
  background: transparent;
  position: relative;
  z-index: 10;
  margin: 13px;
  border-radius: 15px;
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
  height: 100%;
  object-fit: cover;
  border-radius: 15px;
}

.map-overlay {
  position: absolute;
  bottom: 0;
  margin-bottom: 2.5%;
  margin-right: 2.5%;
  right: 0;
  width: 80px;
  height: 80px;
  border: 2px solid white;
  border-radius: 15px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.date-overlay {
  position: absolute;
  top: 2px;
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
}

#userNickName {
  font-family: 'TheJamsil5Bold';
  font-weight: 600;
  font-size: 14px;
  margin-left: 2;
  text-align: left;
  width: 500px;
  min-width: 50px;
}

#communityRegDate {
  font-family: 'TheJamsil4Bold';
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
  z-index: 9999;
}

.custom-btn2 {
  background-color: #FF5E57;
  color: white;
  border: none;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.5rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s;
}

.custom-btn2:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

#my-container .d-flex.align-items-center {
  height: 30px;
  padding: 0;
  margin: 0;
}

#my-container {
  padding: 0;
  height: 35px;
}

#userNickName {
  flex: 0 0 15%;
  font-size: 16px;
  font-weight: bold;
  word-wrap: break-word;
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
</style>