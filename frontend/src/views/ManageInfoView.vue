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

              <div class="mb-3 mt-4">
                <h1 class="text-gray-900 fs-4 mb-3">
                  위험신고내용
                </h1>
                <div class="container table-container">
                  <div class="row">
                    <div class="col-2 table-header">
                      제목
                    </div>
                    <div class="col-10 table-cell">
                      {{ complaintone.complaintTitle }}
                    </div>
                  </div>
                  
                  <div class="row">
                    <div class="col-2 table-header">
                      신고일시
                    </div>
                    <div class="col-4 table-cell">
                      {{ complaintone.createdAt }}
                    </div>
                    <div class="col-2 table-header">
                      신고유형
                    </div>
                    <div class="col-4 table-cell">
                      {{ complaintone.complaintType }}
                    </div>
                  </div>
                  
                  <div class="row">
                    <div class="col-2 table-header">
                      산이름
                    </div>
                    <div class="col-10 table-cell">
                      {{ complaintone.mountainName }}
                    </div>
                  </div>
                  
                  <div class="row lastrow mb-2">
                    <div class="col-2 table-header">
                      내용
                    </div>
                    <div class="col-10 table-cell">
                      {{ complaintone.complaintContent }}
                    </div>
                  </div>
                </div>
              </div>

              <div class="mb-5 mt-5">
                <h1 class="text-gray-900 fs-4 mb-3">
                  처리결과
                </h1>
                <div class="container table-container">
                  <div class="row">
                    <div class="col-2 table-header">
                      처리내용
                    </div>
                    <div class="col-10 table-cell">
                      {{ processingone?.processingContent ? processingone.processingContent : '처리전입니다.' }}
                    </div>
                  </div>
                  
                  <div class="row lastrow mb-2">
                    <div class="col-2 table-header">
                      처리기관
                    </div>
                    <div class="col-10 table-cell">
                      {{ processingone?.processor ? processingone.processor : '처리전입니다.'}}
                    </div>
                  </div>
                </div>
              </div>

              <div class="d-flex justify-content-center align-items-center">
                <div class="col-5 image-container mb-2 d-flex justify-content-center position-relative">
                  <img
                    :src="complaintone.complaintImg"
                    alt="Complaint Image"
                    class="img-fluid"
                  />
                  <div class="triangle">
                    <span class="triangle-text">전</span>
                  </div>
                </div>
                <div class="col-1 ms-1">
                  <i class="bi bi-chevron-double-right fs-1 ms-4" style="color: gray;"></i>
                </div>
                <div class="col-5 image-container mb-2 d-flex justify-content-center">
                  <img
                    :src="processingone?.processingImg ? processingone.processingImg : '/images/normal.png'"
                    alt="Processing Image"
                    class="img-fluid"
                  />
                  <div class="triangle2">
                    <span class="triangle-text">후</span>
                  </div>
                </div>
              </div>
              
              <hr class="separator-line mt-5 mb-4" />

              <div class="mb-4">
                <button v-if="!processingone?.processingContent" class="styled-button2 me-2" @click="goToProcessing()">
                처리결과등록하기</button>
                <button v-else class="styled-button2 me-2" @click="showUpdateModal()">
                처리결과수정하기
                </button>
              </div>
              
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="modal fade" id="processing_update" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content rounded">
        <div class="modal-header pb-0 border-0 justify-content-end">
          <div class="" data-bs-dismiss="modal">
            <button class="btn btn-sm btn-success">닫기</button>
          </div>
        </div>
        <div class="modal-body scroll-y px-10 px-lg-15 pt-0 pb-15">
          <form id="kt_modal_new_target_form" class="form">
            <div class="mb-8 text-center">
              <h1 class="mb-4">처리결과 수정</h1>
              <div class="text-muted fs-6">내용, 결과, 이미지를 수정하세요.</div>
            </div>

            <div class="d-flex flex-column mb-8 fv-row mt-3">
              <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                <span class="required">처리내용</span>
              </label>
              <input type="text" class="form-control form-control-solid" name="target_content" v-model="contentInput" />
            </div>

            <div class="d-flex flex-column mb-8 fv-row">
              <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                <span class="required">처리기관</span>
              </label>
              <input type="text" class="form-control form-control-solid" name="target_processor" v-model="processorInput" />
            </div>

            <div class="d-flex flex-column mb-8 fv-row">
              <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                <span class="required">처리 후 파일 선택</span>
              </label>
              <input type="file" id="image" @change="onFileChange" ref="image" />
            </div>

            <div class="text-center mt-3">
              <button type="button" class="btn btn-success" @click.prevent="updateProcessing(processingone.processingComplaintNo, $event)">저장</button>
              <button type="reset" class="btn btn-light ms-3" @click="clearAll()">모두 지우기</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup>
import { ref, onMounted } from "vue";
import router from "@/router/index.js";
import { getComplaintByNo, getProcessingByNo, updateProcessingByNo } from "@/api/complaint";
import { Modal } from 'bootstrap';
import axios from 'axios';

let complaintNo = 0;

onMounted(async () => {
  complaintNo = sessionStorage.getItem("complaintNo");
  await init(complaintNo);
});

let complaintone = ref({});
let processingone = ref({});
let userInstitution = '';

async function init(complaintNo) {
  try {
    const response = await getComplaintByNo(complaintNo);
    complaintone.value = response;
    console.log("민원 가져온거", complaintone.value);
    const response2 = await getProcessingByNo(complaintNo);
    processingone.value = response2;
    console.log("민원처리정보 가져온거", processingone.value);
    userInstitution = localStorage.getItem('userInstitution');
  } catch (error) {
    console.error("민원글을 가져오는 중 오류 발생:", error);
  }
}

let updateProcessingModal;

const processorInput = ref("");
const contentInput = ref("");

function showUpdateModal() {

  window.scrollTo({
    top: 0,
    behavior: 'smooth'
    });

  contentInput.value = processingone.value.processingContent;
  processorInput.value = processingone.value.processor;

  const elem = document.querySelector('#processing_update');
  updateProcessingModal = new Modal(elem);
  updateProcessingModal.show();
}


async function updateProcessing(processingComplaintNo, event) {

  event.preventDefault();
  console.log("Update processing triggered");

  let imagePath = await uploadImage();
  if(imagePath == null) {
    imagePath = processingone.value.processingImg;
  }

  const data = {
    processingContent: contentInput.value,
    processor: processorInput.value,
    processingComplaintNo: processingComplaintNo,
    processingImg: imagePath || ''
  }
  processingone.value.processingContent = contentInput.value;
  processingone.value.processor = processorInput.value;
  processingone.value.processingImg = imagePath;

  updateProcessingByNo(processingComplaintNo, data);
  updateProcessingModal.hide();

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


function goToMainPage() {
  router.replace({ path: "/manageList" });
}

function goToProcessing() {
  console.log('실행')
  if(userInstitution.trim().toLowerCase() !== complaintone.value.institution.trim().toLowerCase()) {
    alert('담당 업무만 처리가능합니다!');
    return;
  }
  router.replace({ path: "/processing"});
}



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

.image-container {
  width: 500px;
  height: 350px;
  position: relative;
  overflow: hidden;
}

.fixed-size-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}


img {
  display: block;
  width: 100%;
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

.table-container {
  border-collapse: collapse;
  border-top: 3px solid black;
}

.table-header {
  background-color: #f0f0f0;
  padding: 10px;
  padding-left: 20px;
  text-align: left;
}

.table-cell {
  padding: 10px;
  padding-left: 20px;
}

.container .row {
  border-bottom: 1px solid lightgray;
}

.row:last-child {
  border-bottom: 1px solid black;
}

.triangle {
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 0;
  border-right: 55px solid transparent;
  border-top: 55px solid gray;
  z-index: 1;
}

.triangle2 {
  position: absolute;
  top: 0;
  left: 0;
  width: 0;
  height: 0;
  border-right: 55px solid transparent;
  border-top: 55px solid blue;
  z-index: 1;
}

.triangle-text {
  position: absolute;
  bottom: 26px;
  left: 10px;
  color: white;
  font-size: 14px;
  font-weight: bold;
  z-index: 2;
}
</style>