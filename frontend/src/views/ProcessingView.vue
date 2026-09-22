<template>
  <div id="kt_app_content_container" class="app-container container-xxl" style="margin-top: 10px; margin-bottom: 10px;">
    <div class="card">
      <div class="card-body p-lg-20 pb-lg-0">
          <div class="flex-lg-row-fluid me-xl-15">
            <div class="mb-17">
              <div class="d-flex flex-wrap mb-2">
                <div>
                  <button class="styled-button" @click="goToManageInfoPage()">
                    뒤로가기
                  </button>
                </div>
              </div>

              <div class="card p-3 mt-4 mb-3" style="background-color: #21252908;">
                <div class="row align-items-center mb-3 mt-3">
                  <div class="col-md-1 text-center">
                    <label class="col-form-label fw-bold">처리내용</label>
                  </div>
                  <div class="col-md-11">
                    <input class="form-control form-control-lg" type="text" v-model="contentInput"/>
                  </div>
                </div>

                <div class="row align-items-center mb-3 mt-3">
                  <div class="col-md-1 text-center">
                    <label class="col-form-label fw-bold">처리기관</label>
                  </div>
                  <div class="col-auto">
                    <input type="text" id="groupName" class="form-control" v-model="processorInput"/>
                  </div>
                </div>
            
                <div class="d-flex flex-column fv-row">
                  <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                    <span class="required">처리 후 사진 등록</span>
                  </label>
                  <input
                    type="file"
                    id="image"
                    @change="onFileChange"
                    ref="image"
                  />
                </div>

                <div class="d-flex justify-content-end">
                  <button
                    id="updateButton"
                    @click.prevent="insertProcessingBtn()"
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
import { insertProcessing } from "@/api/complaint.js";
import axios from 'axios';

const contentInput = ref("");
const processorInput = ref(localStorage.getItem('userInstitution'));

onMounted(() => {
  window.scroll({
    top: 0,
    behavior: 'smooth'
  });
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

async function insertProcessingButton() {
  const imagePath = await uploadImage();
  console.log("Image Path:", imagePath);
  if (!imagePath) {
    console.error("Image upload failed");
    return;
  }

  const processingData = {
    processingContent: contentInput.value,
    processor: processorInput.value,
    processingComplaintNo: sessionStorage.getItem("complaintNo"),
    processingImg: imagePath || ''
  };
  try {
    const response = await insertProcessing(processingData);

    console.log("Full Response Object:", response);

  } catch (error) {
    console.error("Error in insertProcessingButton:", error);
  }
}

function insertProcessingBtn() {
  insertProcessingButton();
  goToMainPage();

}

// eslint-disable-next-line no-unused-vars
function goToMainPage() {
  console.log("Navigating to /manageList");
  router.push({path: '/manageList'});
}

function goToManageInfoPage() {
  router.replace({path: '/manageInfo'});
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