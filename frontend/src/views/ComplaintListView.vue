<template>
  <div id="app">
  
  <div class="app-main">
    <div class="container" style="height: 100px;">
        <div class="row" style="height: 65px;">
          <div class="col-8 d-flex justify-content-center">
            <span class="fs-1 m-2 fw-bold">위험신고 목록</span>
          </div>
          <div class="col-4 d-flex justify-content-center">
            <button class="styled-button" @click="goToMap()">지도보기</button>
          </div>
        </div> 
        <div class="row" style="height: 45px;">
          <div class="col-6 d-flex justify-content-center">
            <button class="styled-button" @click="getRecentList()">{{btnname}}</button>
          </div>
          <div class="col-6 d-flex justify-content-center">
            <button class="styled-button" @click="goToComplaintPage()">신고하기</button>
          </div>
        </div> 
    </div>
    <div class="card-container d-flex align-items-center justify-content-center mt-3 mb-4">
      <div class="col-12 col-md-9">
        <div class="card card-flush" id="kt_contacts_list">
          <div class="card-body pt-3 pb-4" id="kt_contacts_list_body"  style="height: 500px;">
            <div class="container">
              <div class="row" style="flex-wrap: nowrap;">
                <div class="col-2 col-md-2 text-center">
                  <span class="text-muted fs-9">번호</span>
                </div>
                <div class="col-5 col-md-5 text-center">
                  <span style="white-space: nowrap" class="text-muted fs-9">제목</span>
                </div>
                <div class="col-2 col-md-2 text-center">
                  <span class="text-muted fs-9">글쓴이</span>
                </div>
                <div class="col-3 col-md-3 text-center">
                  <span class="text-muted fs-9">작성시간</span>
                </div>
              </div>
              <hr class="separator-line" />
            </div>
  
            <div v-for="(item, index) in paginatedList" :key="item.complaintNo" class="container mb-2">
              <div class="row" style="flex-wrap: nowrap;" @click="getComplaintByNo(item.complaintNo)" >
                <div class="col-2 col-md-2 text-center">
                  <span class="text-muted smaller-text">{{ (currentPage - 1) * itemsPerPage + index + 1 }}</span>
                </div>
                <div class="col-5 col-md-5">
                  <span class="smaller-text" style="white-space: nowrap">{{ item.complaintTitle }}</span>
                </div>
                <div class="col-2 col-md-2 text-center">
                  <span class="text-muted smaller-text">{{ userNickNames[item.userId] || '...' }}</span>
                </div>
                <div class="col-3 col-md-3 text-center">
                  <span class="text-muted smaller-text">
                    <span>{{ formatDate(item.createdAt) }}</span>
                    <br />
                    <span>{{ formatTime(item.createdAt) }}</span>
                  </span>
                </div>
              </div>
              <hr class="separator-line" />
            </div>
          </div>
          <div class="card-footer d-flex justify-content-center pb-3">
            <nav aria-label="Pagination">
              <ul class="pagination">
                <li
                  class="page-item"
                  v-for="page in totalPages"
                  :key="page"
                  :class="{ active: page === currentPage }"
                >
                  <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <MobileFooterView4></MobileFooterView4>
  </div>
  </template>
  
  
  <script setup>
  import MobileFooterView4 from "@/components/MobileFooterView4.vue";
  import { ref, onMounted, computed } from "vue";
  import { useComplaintListStore, useComplaintStore, useRecentComplaintListStore, useMyComplaintListStore } from "@/stores/complaint";
  import { getUserById } from "@/api/complaint";
  import router from "@/router/index.js";
  import { storeToRefs } from 'pinia';
  
  const complaintListStore = useComplaintListStore();
  const recentcomplaintListStore = useRecentComplaintListStore();
  const myComplaintListStore = useMyComplaintListStore();
  
  const isRecent = ref(false);
  let btnname = ref('나의글');
  let loginUserId = localStorage.getItem("userId");

  const userNickNames = ref({});
  const currentPage = ref(1);
  const itemsPerPage = 7;

  onMounted(async () => {

    await complaintListStore.fetchComplaintList();
    await recentcomplaintListStore.fetchRecentComplaintList();
    await myComplaintListStore.fetchMyComplaintList(loginUserId);
    const hasRefreshed = sessionStorage.getItem('hasRefreshed');
  
    if (!hasRefreshed) {
      sessionStorage.setItem('hasRefreshed', 'true');
      window.location.reload();
    } else {
      sessionStorage.removeItem('hasRefreshed');
    }

    const uniqueUserIds = [...new Set(displayedList.value.map(item => item.userId))];
    await Promise.all(uniqueUserIds.map(async (userId) => {
      const userInfo = await getUserById(userId);
      userNickNames.value[userId] = userInfo.userNickName;
    }));

  });
 
  // eslint-disable-next-line no-unused-vars
  const { complaintlist } = storeToRefs(complaintListStore);
  const { recentcomplaintlist } = storeToRefs(recentcomplaintListStore);
  const { mycomplaintlist } = storeToRefs(myComplaintListStore);
  
  const displayedList = computed(() => isRecent.value ? mycomplaintlist.value : recentcomplaintlist.value);

  const paginatedList = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    return displayedList.value.slice(start, end);
  });

  const totalPages = computed(() => Math.ceil(displayedList.value.length / itemsPerPage));

  function changePage(page) {
    if (page >= 1 && page <= totalPages.value) {
      currentPage.value = page;
    }
  }

  function getRecentList() {
    isRecent.value = !isRecent.value;
    if(isRecent.value) {
      btnname = '전체글';
    }else{
      btnname = '나의글';
    }
  }
  function formatDate(datetime) {
    return datetime.split(' ')[0];
  }
  
  function formatTime(datetime) {
    return datetime.split(' ')[1];
  }
  
  const complaintStore = useComplaintStore();
  // eslint-disable-next-line no-unused-vars
  const complaintone = storeToRefs(complaintStore);
  
  async function getComplaintByNo(key) {
    await complaintStore.fetchComplaintone(key);
    sessionStorage.setItem("complaintNo", key);
    router.replace({path: '/complaintInfoMobile'});
  }
 
  function goToComplaintPage() {
    router.replace({ path: "/complaintMobile" });
  }

  function goToMap() {
    router.replace({ path: "/clMapMobile"});
  }

  </script>
  
  <style scoped>
  #app {
      height: 100vh;
      width: 100vw;
      overflow: hidden;
    }
  
  
   .app-main {
    height: 100vh;
    width: 100vw;
      overflow-y: scroll;
      background-color: #f5f5f5;
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

  .styled-button {
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
  

  @media (max-width: 360px) {
    .smaller-text {
      font-size: 0.65rem;
      white-space: normal;
    }
  
    .card-title {
      font-size: 1.2rem;
    }
  
    .btn {
      padding: 5px 10px;
      font-size: 0.8rem;
    }
  
    .container {
      padding-left: 5px;
      padding-right: 5px;
    }
  
    #map {
      height: 200px;
    }
  
    .card-body {
      padding: 10px;
    }
  
    hr.separator-line {
      margin: 5px 0;
    }
  
    .col-5, .col-2, .col-3 {
      text-align: left;
    }
  
    .card-container {
      padding-left: 5px;
      padding-right: 5px;
    }
  }

  .pagination .page-link {
    color: black;
  }

  .pagination .page-item.active .page-link {
    background-color: #04663f;
    border-color: #04663f;
    color: white;
  }
  </style>
  