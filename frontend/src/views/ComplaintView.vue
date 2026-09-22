<template>
  <div id="kt_app_content_container" class="app-container container-xxl" style="margin-top: 10px; margin-bottom: 10px;">
    <div class="card">
      <div class="card-body p-lg-20 pb-lg-0">
          <div class="flex-lg-row-fluid me-xl-15">
            <div class="mb-17">
              <div class="d-flex flex-wrap mb-2">
                <div>
                  <button class="styled-button" @click="goToMainPage()">
                    뒤로가기
                  </button>
                </div>
              </div>

              <div class="card p-3 mt-4 mb-3" style="background-color: #21252908;">
                <div class="row align-items-center mb-3 mt-3">
                  <div class="col-md-1 text-center">
                    <label class="col-form-label fw-bold">제목</label>
                  </div>
                  <div class="col-md-11">
                    <input class="form-control form-control-lg" type="text" v-model="titleInput"/>
                  </div>
                </div>

                <div class="row align-items-center">
                  <div class="col-md-1 text-center">
                    <label for="category" class="col-form-label fw-bold">구분</label>  
                  </div>
                  <div class="col-auto">
                    <div class="form-group">
                      <select
                        v-model="typeInput"
                        id="number"
                        name="number"
                        class="form-control"
                        style="cursor: pointer"
                      >
                      <option value="" disabled>유형 선택</option>
                      <option value="파손">파손</option>
                      <option value="낙석">낙석</option>
                      <option value="화재">화재</option>
                      </select>
                    </div>
                  </div>
                </div>

                <div class="row align-items-center mb-3 mt-3">
                  <div class="col-md-1 text-center">
                    <label class="col-form-label fw-bold">산 이름</label>
                  </div>
                  <div class="col-auto">
                    <input type="text" id="groupName" class="form-control" v-model="mountainNameInput"/>
                  </div>
                </div>
            
                <div class="">
                  <div id="map" style="width: 100%; height: 350px"></div>
                  <p><em>지도를 클릭해주세요!</em></p>
                  <div id="clickLatlng"></div>
                </div>
            
                <div class="d-flex flex-column fv-row">
                  <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                    <span class="required">사진 등록</span>
                  </label>
                  <input
                    type="file"
                    id="image"
                    @change="onFileChange"
                    ref="image"
                  />
                </div>

                <div class="mb-3 mt-3">
                  <label for="exampleFormControlTextarea1" class="form-label">위치, 시간, 내용을 자세히 입력해주세요.</label>
                  <textarea
                    class="form-control"
                    id="exampleFormControlTextarea1"
                    rows="7"
                    v-model="contentInput"></textarea>
                </div>
                <div class="d-flex justify-content-end">
                  <button
                    id="updateButton"
                    @click="insertComplaintButton()"
                    class="btn btn-success fs-6 p-2"
                  >
                  등록하기
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import router from "@/router/index.js";
import { insertComplaint } from "@/api/complaint.js";
import axios from 'axios';

const titleInput = ref("");
let userIdInput = localStorage.getItem("userNickName");
const mountainNameInput = ref("");
const contentInput = ref("");
const typeInput = ref("");
let latitudeInput = 0;
let longitudeInput = 0;


onMounted(() => {
initializeMap();
});

function initializeMap() {
const script = document.createElement('script');
script.onload = () => kakao.maps.load(createMap);
script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false&libraries=services';
document.head.appendChild(script);

}

function createMap() {
const mapContainer = document.getElementById("map");
const mapOption = {
    center: new kakao.maps.LatLng(37.660833, 126.993333),
    level: 8,
};

const map = new kakao.maps.Map(mapContainer, mapOption);

var marker = new kakao.maps.Marker({
    position: map.getCenter(),
});
marker.setMap(map);

kakao.maps.event.addListener(map, "click", function (mouseEvent) {
    var latlng = mouseEvent.latLng;

    marker.setPosition(latlng);

    var message = "클릭한 위치의 위도는 " + latlng.getLat() + " 이고, ";
    message += "경도는 " + latlng.getLng() + " 입니다";

    latitudeInput = latlng.getLat();
    longitudeInput = latlng.getLng();

    var resultDiv = document.getElementById("clickLatlng");
    resultDiv.innerHTML = message;
});

}

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
const response = await axios.post(
  '/api/complaint/upload',
  formData,
  {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  }
);
return response.data;
} catch (error) {
console.error('Error uploading image', error);
return null;
}
};

async function insertComplaintButton() {
const imagePath = await uploadImage();

const complaintData = {
complaintTitle: titleInput.value,
complaintContent: contentInput.value,
complaintType: typeInput.value,
complaintImg: imagePath || '',
userId: userIdInput,
mountainName: mountainNameInput.value,
latitude: latitudeInput,
longitude: longitudeInput
};

try {
const response = await insertComplaint(complaintData);
console.log('서버 응답: ', response);

goToMainPage();
} catch (error) {
console.error(error);
}
}


function goToMainPage() {
router.replace({ path: "/complaintList" });
}
</script>

<style scoped>
.styled-button {
background: linear-gradient(#d3d3d3, #a9a9a9);
color: white;
padding: 10px 20px;
border: none;
border-radius: 25px;
font-size: 16px;
font-weight: bold;
cursor: pointer;
box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
transition: all 0.3s ease;
}
</style>
