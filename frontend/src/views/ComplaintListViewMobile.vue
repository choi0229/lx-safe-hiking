
<template>
  <div id="app">
    <div class="custom-header">
      <button class="back-button" @click="goBack">
        <img src="/images/뒤로가기.png" alt="뒤로가기" />
      </button>
      <span class="header-title">민원신고</span>
      <button class="done-button" @click="submitPost()">완료</button>
    </div>
  <div class="app-main">
    <div class="button-group-container" style="border-bottom: solid 1px #45a049">
      <div class="button-group" >
        <button :class="{ active: isRecent }" @click="getRecentList()"  ><img src="/images/나의민원.png" width="16" height="auto" style="margin-right: 7px; margin-bottom: 4px;">{{ btnname }}</button>
        <button :class="{ active: !isRecent && currentPage === 2 }" @click="goToMap()"><img src="/images/지도.png" width="16" height="auto" style="margin-right: 7px; margin-bottom: 4px;">지도보기</button>
        <button :class="{ active: currentPage === 3 }" @click="goToComplaintPage()"><img src="/images/신고.png" width="16" height="auto" style="margin-right: 7px; margin-bottom: 4px;">신고하기</button>
      </div>
    </div>
    <div class="card-container d-flex align-items-center justify-content-center mt-3 mb-4">
      <div class="col-12 col-md-9">
          <div class="card-body" id="kt_contacts_list_body"  style="height: 520px;">
            <div class="container">
              <div class="row" style="flex-wrap: nowrap;">
                <div class="col-2 col-md-2 text-center">
                  <span class="text-muted fs-9" style="border-bottom: solid 1px #444; padding-bottom: 3px; display: inline-block;">번호</span>
                </div>
                <div class="col-5 text-center" style="margin-left: 0px;">
                  <span class="text-muted fs-9" style="border-bottom: solid 1px #444; padding-bottom: 3px; display: inline-block;">제목</span>
                </div>
              </div>
              <hr class="separator-line" />
            </div>
  
            <div v-for="(item, index) in paginatedList" :key="item.complaintNo" class="post-container">
              <div class="post-content" @click="getComplaintByNo(item.complaintNo)">
                  <div class="post-header">
                    <span class="post-number">{{ (currentPage - 1) * itemsPerPage + index + 1 }}</span>
                    <span class="separator"></span>
                    <span class="post-title">{{ item.complaintTitle }}</span>
                  </div>
                  <div class="post-meta">
                    <span class="author">{{ userNickNames[item.userId] || '...' }}</span>
                    <span class="separator">|</span>
                    <span class="time">{{ formatDate(item.createdAt) }} | {{ formatTime(item.createdAt) }}</span>
                  </div>
                </div>

            </div>

          </div>
          
      </div>
    </div>
  </div>
   <div class="pagination-container">
  <nav aria-label="Pagination">
    <ul class="pagination">
      <li
        class="page-item"
        v-for="page in totalPages"
        :key="page"
        :class="{ active: page === currentPage, hover: page === currentPage }">
        <a class="page-link" @click.prevent="changePage(page)" >{{ page }} </a>
      </li>
    </ul>
  </nav>
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
  let btnname = ref('나의 민원');
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
      btnname = '나의 민원';
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

  function goBack() {
    window.location.href=`/mobilemainview`
  }

  onMounted(() => {
  currentPage.value = 1; 
});
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
  flex: 1;
  margin-top: 60px;
  margin-bottom: 60px;
  overflow-y: auto;
  background-color: #ffffff;

}

.card-item {
    display: flex;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #ddd;
    cursor: pointer;
    transition: background-color 0.3s;
}

.card-item:hover {
    background-color: #f9f9f9;
}

.card-item .card-number,
.card-item .card-title,
.card-item .card-author,
.card-item .card-date {
    font-size: 0.9rem;
    color: #333;
    text-align: left;
}

.card-item .card-number {
    flex: 1;
    text-align: center;
}

.card-item .card-title {
    flex: 4;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.card-item .card-author {
    flex: 2;
    text-align: center;
}

.card-item .card-date {
    flex: 3;
    text-align: center;
}

@media (max-width: 480px) {
    .header-title {
        font-size: 1rem;
    }

    .card-item {
        flex-direction: column;
        align-items: flex-start;
    }

    .card-item .card-number,
    .card-item .card-title,
    .card-item .card-author,
    .card-item .card-date {
        text-align: left;
        width: 100%;
    }
    .custom-header,
  .footer {
    height: 50px;
  }
  .app-main {
    margin-top: 50px;
    margin-bottom: 50px;
  }
}

.card-body {
  overflow-y: auto;
  padding: 10px;
  background-color: #f7f7f7;
  border: 1px solid #000000;
  top:-20px;
  position: relative;
}


.card-footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  background-color: transparent;
  border: none; 
  padding: 10px 0; 
}


.container.mb-2 {
  background-color: #f9f9f9;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
  padding: 15px 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
}

.container.mb-2:hover {
  background-color: #fefefe;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.15);
}

.row {
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.row > div {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.row .col-2 {
  text-align: center;
  font-size: 14px;
  font-weight: bold;
}

.row .col-5 {
  flex: 3;
  padding-left: 10px;
  font-size: 14px;
  color: #333;
  display: flex;
  align-items: center;
}

.row .col-5 span {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.row .col-2.text-center {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.row .col-3 {
  flex: 2;
  text-align: center;
  font-size: 12px;
  color: #888;
}

.row span {
  font-size: 14px;
  color: #666;
}

.row .text-muted {
  font-size: 14px;
  font-weight: bold;
  color: #444;
}

hr.separator-line {
  margin: 8px 0;
  border: none;
  border-bottom: 1px solid #eee;
}

.button-group-container {
  margin-bottom: 0;
}

.button-group {
  display: flex;
  justify-content: space-around;
  background-color: transparent;
  padding-top: 9px;
}

.button-group button {
  flex: 1;
  font-size: 15px;
  font-weight: bold;
  color: #444;
  background-color: transparent;
  border: solid 1px #dddcdc;
  border-bottom: solid 1px #45a049;
  padding: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.button-group button:hover {
  background-color: #f9f9f9;
}


@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.pagination-container .active {
  background-color: transparent;
  color: #04663f;
  font-weight: bold;
}

.custom-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: white;
  padding: 0 10px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  z-index: 1000;
  font-family: 'TheJamsil5Bold', sans-serif !important;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background-color: white;
  border-top: 1px solid #ddd;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.1);
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 10px;
  background-color: white;
  border: solid 1px #45a049;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 5px;
  border-bottom: solid 1px black;
}

.header-title {
  flex: 1;
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #333;
  font-family: 'TheJamsil5Bold', sans-serif !important;
}

button.back-button {
  border: none;
  cursor: pointer;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%; 
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.done-button {
  font-size: 14px;
  font-weight: bold;
  color: #45a049;
  background: none;
  border: none;
  cursor: pointer;
  margin-right: 10px;
}

.post-container {
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border: 1px solid #04663f;
  border-radius: 8px;
  margin-bottom: 10px;
  padding: 15px;
  cursor: pointer;
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
}

.post-container:hover {
  background-color: #f9f9f9;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
  position: relative;
}

.post-number {
  font-weight: bold;
  font-size: 20px;
  color: #666;
  margin-right: 8px;
  position: relative;
  top: 10px;
}

.separator {
  width: 2px;
  height: 100%;
  background-color: #aaa;
  margin: 0 8px;
}

.post-title {
  flex: 1;
  font-weight: bold;
  font-size: 16px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 24px;
}


.post-meta {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
  margin-left: 37px;
}

.post-meta .author {
  font-weight: bold;
  color: #555;
}

.post-meta .separator {
  color: #aaa;
}

.post-meta .time {
  font-size: 13px;
  color: #888;
}

.pagination-container {
  position: fixed;
  bottom: 60px;
  left: 0;
  width: 100%;
  background-color: white;
  text-align: center;
  z-index: 1000;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.1);
}

.pagination {
  display: flex;
  justify-content: center;
}

.pagination .page-item {
  list-style: none;
  margin: 0 5px;
}

.pagination .page-link {
  background-color: transparent;
  border: none;
  color: black;
  font-weight: bold;
  font-size: 14px;
  padding: 6px 12px;
  cursor: pointer;
  text-decoration: none; 
  border-bottom: 2px solid rgb(160, 177, 156); 
  transition: all 0.3s ease; 
  border-radius: 0; 
}

.pagination .page-item.active .page-link,
.pagination .page-item.hover .page-link {
  color: #04663f;
  border-bottom: 2px solid #04663f;
  font-size: 16px;
  font-weight: bold;
}

.pagination .page-link:hover {
  color: #04663f;
  border-bottom: 2px solid #04663f;
  font-size: 16px;
}


</style>