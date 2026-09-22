<template>
  <div class="container">
    <div class="card">
      <div class="card-body p-lg-5 pb-lg-0">
        <div class="d-flex flex-column flex-md-row">
          <div class="flex-md-grow-1 me-md-4">
            <div class="mb-2">
              <div class="d-flex flex-wrap mb-3 align-items-center">
                <button class="styled-button" @click="goToMainPage()">뒤로가기</button>
              </div>

              <div class="mb-3">
                <h1 class="text-gray-900 fs-3 fw-bold mb-2">
                  {{ complaintone.complaintTitle }}
                </h1>
                <hr class="separator-line2" />
                <span class="fw-bold text-muted fs-6 ps-1">
                  신고자 : {{ complaintone.userId }}
                </span>
                <hr class="separator-line" />
                <span class="fw-bold text-muted fs-6 ps-1">
                  신고 날짜 : {{ complaintone.createdAt }}
                </span>
                <hr class="separator-line" />
                <span class="fw-bold text-muted fs-6 ps-1">
                  신고 유형 : {{ complaintone.complaintType }}
                </span>
                <hr class="separator-line" />
                <span class="fw-bold text-muted fs-6 ps-1">
                  산이름 : {{ complaintone.mountainName }}
                </span>
              </div>

              <hr class="separator-line" />
              <div class="image-container mb-2 d-flex justify-content-center">
                <img
                  :src="complaintone.complaintImg"
                  alt="Complaint Image"
                  class="img-fluid"
                />
              </div>

              <hr class="separator-line" />
              <p class="fw-bold text-muted fs-6 ps-1">신고 내용</p>
              <div class="card mb-2" style="height: 150px">
                <div class="card-body p-2">
                  <p class="fs-6 text-gray-600">
                    {{ complaintone.complaintContent }}
                  </p>
                </div>
              </div>
              <hr class="separator-line mt-3" />
              <button class="styled-button2 me-2" @click="showUpdateModal()">수정</button>
              <button
                class="styled-button2"
                @click="deleteComplaint(complaintone.complaintNo)"
              >
                삭제
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="modal fade" id="complaint_update" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog mw-1000px" style="width: 800px">
      <div class="modal-content rounded">
        <div class="modal-header pb-0 border-0 justify-content-end">
          <div class="" data-bs-dismiss="modal">
            <button class="btn btn-sm btn-success">닫기</button>
          </div>
        </div>
        <div class="modal-body scroll-y px-10 px-lg-15 pt-0 pb-15">
          <form id="kt_modal_new_target_form" class="form">
            <div class="mb-8 text-center">
              <h1 class="mb-4">신고 글 수정</h1>
              <div class="text-muted fs-6">제목, 이미지, 위치, 내용을 수정하세요.</div>
            </div>

            <div class="d-flex flex-column mb-8 fv-row mt-3">
              <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                <span class="required">제목</span>
              </label>
              <input
                type="text"
                class="form-control form-control-solid"
                name="target_title"
                v-model="titleInput"
              />
            </div>

            <label for="category" class="col-form-label fw-bold">구분</label>
            <div class="form-group mb-2">
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

            <div class="d-flex flex-column mb-8 fv-row">
              <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                <span class="required">산이름</span>
              </label>
              <input
                type="text"
                class="form-control form-control-solid"
                name="target_title"
                v-model="mountainNameInput"
              />
            </div>

            <div class="mb-3 mt-3">
              <div id="map" class="map-container"></div>
              <p><em>지도를 클릭해주세요!</em></p>
              <div id="clickLatlng"></div>
            </div>

            <div class="d-flex flex-column mb-8 fv-row">
              <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                <span class="required">파일 선택</span>
              </label>
              <input type="file" id="image" @change="onFileChange" ref="image" />
            </div>
            <div class="d-flex flex-column mb-8 fv-row mt-2">
              <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                <span class="required">내용</span>
                <span
                  class="ms-1"
                  data-bs-toggle="tooltip"
                  title="내용을 입력하세요. 필수랍니다~"
                >
                  <i class="ki-duotone ki-information-5 text-gray-500 fs-6">
                    <span class="path1"></span>
                    <span class="path2"></span>
                    <span class="path3"></span>
                  </i>
                </span>
              </label>
              <textarea
                class="form-control form-control-solid"
                name="target_content"
                v-model="contentInput"
                rows="5"
              ></textarea>
            </div>
            <div class="text-center mt-3">
              <button
                type="button"
                class="btn btn-success"
                @click="updateComplaint(complaintone.complaintNo)"
              >
                저장
              </button>
              <button type="reset" class="btn btn-light ms-3" @click="clearAll()">
                모두 지우기
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useComplaintStore } from "@/stores/complaint";
import { storeToRefs } from "pinia";
import router from "@/router/index.js";
import { deleteComplaintByNo, updateComplaintByNo } from "@/api/complaint";
import { Modal } from "bootstrap";
import axios from "axios";

const titleInput = ref("");
const mountainNameInput = ref("");
const contentInput = ref("");
const typeInput = ref("");
let latitudeInput = 0;
let longitudeInput = 0;

const complaintStore = useComplaintStore();
const { complaintone } = storeToRefs(complaintStore);

function goToMainPage() {
  router.replace({ path: "/complaintList" });
}

function deleteComplaint(complaintNo) {
  if (confirm("정말 삭제하시겠습니까??") == true) {
    deleteComplaintByNo(complaintNo);
    goToMainPage();
  } else {
    return false;
  }
}

let updateComplaintModal;

function showUpdateModal() {
    window.scrollTo({
    top: 0,
    behavior: 'smooth'
    });

  titleInput.value = complaintone.value.complaintTitle;
  mountainNameInput.value = complaintone.value.mountainName;
  contentInput.value = complaintone.value.complaintContent;
  typeInput.value = complaintone.value.complaintType;

  const elem = document.querySelector("#complaint_update");
  updateComplaintModal = new Modal(elem);
  updateComplaintModal.show();
  setTimeout(() => {
    initializeMap();
  }, 500);
}

function initializeMap() {
  const script = document.createElement("script");
  script.onload = () => kakao.maps.load(createMap);
  script.src =
    "https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false&libraries=services";
  document.head.appendChild(script);
}

function createMap() {
  const mapContainer = document.getElementById("map");
  const mapOption = {
    center: new kakao.maps.LatLng(
      complaintone.value.latitude,
      complaintone.value.longitude
    ),
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

async function updateComplaint(complaintNo) {
  let imagePath = await uploadImage();
  if (imagePath == null) {
    imagePath = complaintone.value.complaintImg;
  }

  console.log("수정할 글번호 : ", complaintNo);

  const existingLatitude = complaintone.value.latitude;
  const existingLongitude = complaintone.value.longitude;

  const latitude = latitudeInput !== 0 ? latitudeInput : existingLatitude;
  const longitude = longitudeInput !== 0 ? longitudeInput : existingLongitude;

  const data = {
    complaintNo: complaintNo,
    complaintTitle: titleInput.value,
    complaintContent: contentInput.value,
    complaintType: typeInput.value,
    mountainName: mountainNameInput.value,
    latitude: latitude,
    longitude: longitude,
    complaintImg: imagePath || "",
  };
  complaintone.value.complaintTitle = titleInput.value;
  complaintone.value.complaintContent = contentInput.value;
  complaintone.value.complaintType = typeInput.value;
  complaintone.value.mountainName = mountainNameInput.value;
  complaintone.value.latitude = latitudeInput;
  complaintone.value.longitude = longitudeInput;
  complaintone.value.complaintImg = imagePath;

  updateComplaintByNo(complaintNo, data);
  updateComplaintModal.hide();
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
  formData.append("file", selectedFile.value);

  try {
    const response = await axios.post("/api/complaint/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error uploading image", error);
    return null;
  }
};
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 300px;
  border: 1px solid #ccc;
}

hr.separator-line {
  border: 1px solid #ccc;
  margin: 10px 0;
  width: 100%;
}

.card {
  cursor: pointer;
  margin-top: 10px;
  margin-bottom: 10px;
}

.image-container img {
  width: 700px;
  height: auto;
}

.styled-button {
  background: linear-gradient(#d3d3d3, #a9a9a9);
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.styled-button2 {
  background: linear-gradient(#55995e, #3d7935);
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.styled-button:hover {
  background: linear-gradient(#a9a9a9, #808080);
}
</style>
