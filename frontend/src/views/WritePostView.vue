<template>
  <div style="text-align: left;">
    <button class="back-button ms-2 btn btn-success btn-sm mb-3 mt-3 ms-3 mx-2 custom-btn" @click="goBack">뒤로가기</button>
  </div>
  <div class="container">
    <div class="d-flex justify-content-center">
      <h2>게시글 작성</h2>
    </div>
    <div class="form-check form-switch d-flex justify-content-end">
      <input class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckDefaultPublic"
        v-model="isPublic">
      <label class="form-check-label ms-2" for="flexSwitchCheckDefaultPublic">공개 여부</label>
    </div>

    <div class="form-check form-switch d-flex justify-content-end">
      <input class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckDefaultSafe" v-model="isSafe">
      <label class="form-check-label ms-2" for="flexSwitchCheckDefaultSafe">안전 게시글</label>
    </div>

    <div class="mb-3">
      <div class="text-start">
        <label for="title" class="form-label">제목</label>
      </div>

      <input type="text" id="title" v-model="title" class="form-control" required />
    </div>

    <div class="mb-2">
      <div id="map" class="map-container"></div>
      <div class="d-flex justify-content-center">
        <p style="margin: 0;"><em>지도를 클릭해주세요!</em></p>
      </div>
      <div id="clickLatlng"></div>
    </div>

    <div class="mb-3">
      <div class="text-start">
        <label for="courseName" class="form-label">코스 이름</label>
      </div>
      <input type="text" id="courseName" v-model="courseName" class="form-control" required />
    </div>

    <div class="mb-3 text-start">
      <label for="content" class="form-label">내용</label>
      <textarea id="content" v-model="content" class="form-control" rows="5" required></textarea>
    </div>

    <div class="d-flex flex-column mb-3 fv-row text-start">
      <label for="image" class="form-label">이미지 첨부</label>
      <input type="file" id="image" class="form-control" @change="onImageChange" ref="image" accept="image/*" />
    </div>

    <button type="submit" class="btn btn-success" @click="submitPost()">작성하기</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

let userId = localStorage.getItem('userId');
let userNickName = localStorage.getItem('userNickName');
const router = useRouter();

const title = ref('');
const content = ref('');
const courseName = ref('');

const isPublic = ref(true);
let isPublicState = 0;

const isSafe = ref(true);
let isSafeState = 0;

let latitudeInput = 0;
let longitudeInput = 0;
let isKakaoMapLoaded = ref(false);

let marker = null;

const selectedFile = ref(null);

const onImageChange = (event) => {
  selectedFile.value = event.target.files[0];
};

const uploadImage = async () => {
  if (!selectedFile.value) {
    return null;
  }
  const formData = new FormData();
  formData.append('file', selectedFile.value);

  try {
    const response = await axios.post('/api/community/upload', formData, {
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

const submitPost = async () => {


  if (!userId) {
    console.error('유저 정보가 없습니다.');
    alert('로그인 정보가 없습니다. 다시 로그인 해주세요.');
    return;
  }

  if (isPublic.value == true) {
    isPublicState = 1;
  } else {
    isPublicState = 0;
  }

  if (isSafe.value == true) {
    isSafeState = 1;
  } else {
    isSafeState = 0;
  }


  const imagePath = await uploadImage();
  const postData = {
    communityTitle: title.value,
    communityBody: content.value,
    communityUrl: imagePath,
    userNickName: userNickName,
    userId: userId,
    longitude: longitudeInput,
    latitude: latitudeInput,
    publicState: isPublicState,
    safeState: isSafeState,
    courseName: courseName.value
  };

  try {
    await axios.post('/api/community/add', postData, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    router.push('/mobilecommunity');
  } catch (error) {
    console.error('게시글 작성 실패:', error);
    alert('게시글 작성에 실패했습니다. 다시 시도해주세요.');
  }
};


function initializeMap() {
  const script = document.createElement('script');
  script.onload = () => {
    kakao.maps.load(() => {
      createMap();
      isKakaoMapLoaded.value = true;
    });
  };
  script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false&libraries=services';
  document.head.appendChild(script);
}

let map;

function createMap() {
  const mapContainer = document.getElementById("map");
  const mapOption = {
    center: new kakao.maps.LatLng(37.578633866659999, 126.959531894056894),
    level: 4,
  };

  map = new kakao.maps.Map(mapContainer, mapOption);

  marker = new kakao.maps.Marker({
    position: map.getCenter(),
  });
  marker.setMap(map);

  kakao.maps.event.addListener(map, "click", function (mouseEvent) {
    const latlng = mouseEvent.latLng;
    marker.setPosition(latlng);
    latitudeInput = latlng.getLat();
    longitudeInput = latlng.getLng();
    document.getElementById("clickLatlng").innerHTML = `위도: ${latlng.getLat()}, 경도: ${latlng.getLng()}`;
  });
}



onMounted(() => {
  initializeMap();
})

const goBack = () => {
  router.push({ name: 'mobilecommunity' });
};
</script>

<style scoped>
@font-face {
  font-family: 'TheJamsil5Bold';
  src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_2302_01@1.0/TheJamsil5Bold.woff2') format('woff2');
  font-weight: 500;
  font-style: normal;
}

body,
h1,
h2,
h3,
p,
label,
.form-label,
.form-control,
.form-check-label,
.btn,
textarea,
input {
  font-family: 'TheJamsil5Bold', sans-serif !important;
}

h2 {
  font-weight: bold;
}

button.back-button {
  font-family: 'TheJamsil5Bold', sans-serif !important;
  font-weight: bold;
  font-size: 16px;
  padding: 10px 20px;
  border-radius: 5px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
}

button.back-button:hover {
  background-color: #4CAF50;
}

.form-check-label {
  font-family: 'TheJamsil5Bold', sans-serif !important;
}

.form-control {
  font-family: 'TheJamsil5Bold', sans-serif !important;
  font-size: 14px;
}

textarea.form-control {
  font-family: 'TheJamsil5Bold', sans-serif !important;
  font-size: 14px;
}

button[type="submit"] {
  font-family: 'TheJamsil5Bold', sans-serif !important;
  font-weight: bold;
  font-size: 16px;
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
}

button[type="submit"]:hover {
  background-color: #45a049;
}

.map-container {
  width: 100%;
  height: 150px;
}

#clickLatlng {
  font-size: 12px;
  color: #555;
}

.container {
  font-family: 'TheJamsil5Bold', sans-serif !important;
  padding: 20px;
}

.form-label {
  font-family: 'TheJamsil5Bold', sans-serif !important;
}

.form-check-input {
  font-family: 'TheJamsil5Bold', sans-serif !important;
}
</style>