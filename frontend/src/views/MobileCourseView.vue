<template>
  <div id="app" >
    <div class="shadow-container">
    <header class="d-flex justify-content-between align-items-center p-2 MobileHeader" style="background-color: white; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);">
    <div class="d-flex align-items-center" @click="gotoHome()" >
      <img src="/images/BrandIcon.png" alt="산 로고" class="" width="45" height="45" />
    </div>
      <div class="review-search" style="position: absolute; top: 12px;">
        <div class="search-container">
          <input type="text" placeholder="검색" v-model="searchQuery" />
          <button class="search-button">
            <img src="/images/돋보기white.png" alt="Search" />
          </button>
        </div>
      </div>
    
      <div class="dropdown">
        <a role="button" data-bs-toggle="dropdown" aria-expanded="false" style="margin-right: -0.4rem;"><img src="/images/hamburgerIcon.png" alt="돋보기 아이콘" width="50" height="50" >
          </a>
        <ul class="dropdown-menu">
          <li><a class="dropdown-item" href="login">로그인</a></li>
          <li><a class="dropdown-item" href="#">커뮤니티</a></li>
          <li><a class="dropdown-item" href="#">멘토신청</a></li>
        </ul>
      </div>
    </header>
  </div>

    <div class="app-main">
    <div class="tag-buttons">
      <button class="btn btn-secondary btn-sm btn-custom">#서울</button>
      <button class="btn btn-secondary btn-sm btn-custom">#산린이</button>
      <button class="btn btn-secondary btn-sm btn-custom">#부산</button>
      <button class="btn btn-secondary btn-sm btn-custom">#인천</button>
      <button class="btn btn-secondary btn-sm btn-custom" style="width: 45px;">···</button>
    </div>
    
      <div class="card-container">
          <div v-for="course in filteredCourses" :key="course.courseId" class="card" @click="goToMountainDetail(course)">
              <div class="card-image">
                  <img :src="''+course.courseImage" alt="Mountain Image" />
              </div>
              <div class="card-content mb-3">
                <div class="text-group">
                  <p id="mountainName">{{ course.mountainName }} - {{ course.courseName }}</p>
                  <p id="courseLocation">( 주소 - {{ course.courseLocation }} )</p>
                </div>
                <div class="card-info" style="font-weight: bold;">
                  <span>⭐ 3.5 · Nomarl </span>
                    <span style="margin-left: 10px;">😊 · 7.56KM</span>
                     <span style="margin-left: 10px;">🏃 · 50명</span>
                </div>
                <div id="mountain-button">
                  <button @click="goToMountainDetail(course)">등산 코스</button>
                  <button @click="goToCourseInfoViewPage(courseData)">등산 정보</button>
                </div>
              </div>
          </div>
      </div>
    </div>
 <MobileFooterView2></MobileFooterView2>
  </div>
</template>

<script setup>

import MobileFooterView2 from '@/components/MobileFooterView2.vue';
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';


const router = useRouter();
const courses = ref([]);
const filteredCourses = ref([]);

onMounted(() => {
  fetchCourses();
});

const fetchCourses = async () => {
try {
  const response = await axios.get('/api/course');
  courses.value = response.data;
  filteredCourses.value = response.data;
} catch (error) {
  console.error('산 목록을 불러오는 중 오류 발생:', error);
}
};

const goToMountainDetail = (course) => {
router.push({
  name: 'mobilemountaindetailview',
  query: {
    course: JSON.stringify(course), 
  },
});
};

</script>

<style scoped>
#app {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 0;
}

.header-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  z-index: 1000;
  background-color: white;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

.app-main {
  flex: 1;
  margin-top:3px;
  margin-bottom: 70px;
  overflow-y: auto;
  background-color: #ffffff;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.app-main::-webkit-scrollbar {
  display: none;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  z-index: 1000;
  background-color: white;
  box-shadow: 0px -4px 6px rgba(0, 0, 0, 0.1);
}

.tag-buttons {
  display: flex;
  gap: 10px;
  padding:5px;
  justify-content: center;
  background-color: #f0f7f1;
}

.btn-custom {
  background-color: #ffffff;
  border-radius: 10px;
  border-color: transparent;
  border: solid 1px #327C2B;
  color: black;
  font-size: 14px;
  padding: 1px 8px;
}

.btn-custom:hover {
  transform: scale(1.05);
  background-color: #327C2B;
  color: white;
  cursor: pointer;
}

.card-container {
  max-width: auto;
  display: flex;
  flex-direction: column;
  
}

.card {
  background-color: white;
  box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.card:hover {
  transform: scale(1.02);
}

.card-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  margin-bottom: 1rem;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

#mountainName {
  font-family: "TheJamsil5Bold";
  font-weight: 700;
  font-size: 20px;
  margin-bottom: 7px;
}

#courseLocation {
  margin: 0;
  font-family: "TheJamsil4Bold";
  font-weight: 500;
  font-size: 12px; 
  color: #555; 
  white-space: nowrap; 
  font-weight: bold;
  margin-left: 10px;
  margin-top: 6px;
}

.card-info {
  margin-left: 10px;
  font-size: 0.9em;
  color: #777;
}

.review-search input {
  width: 20px;
  padding: 5px;
  border-radius: 10px;
  border: 1px solid #ddd;
}

.search-container {
  display: flex;
  align-items: center;
  border: 1px solid #327C2B; 
  border-radius: 5px;
  overflow: hidden;
  width: 240px; 
  height: 40px; 
}

.search-container input {
  flex: 1; 
  border: none;
  padding: 0 10px;
  font-size: 16px;
  color: #333;
  outline: none; 
}

.search-button {
  width: 50px;
  height: 50px;
  background-color: #327C2B;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.search-button img {
  width: 30px;
  height: 30px;
  filter: brightness(0) invert(1); 
}

.search-container input::placeholder {
  color: #aaa;
  font-size: 16px;
}

.text-group{
  display: flex;
  font-size: 8px;
  margin-left: 10px;
}

#mountain-button {
  display: flex;  
  margin-top:  10px;
  z-index: 555;
  border-bottom: solid 1px #2b801e;
}

#mountain-button button {
  width: 200px;
  background-color: white; 
  color: black; 
  border: solid 1px #327C2B; 
  border-radius: 5px; 
  padding: 5px 10px; 
  font-size: 14px; 
  cursor: pointer; 
  transition: transform 0.3s, background-color 0.3s;
  margin: 3px;
  font-weight: bold;
}

#mountain-button button:hover {
  transform: scale(1.05);
  background-color: #327C2B;
  color: white;
  border-color: #265e21;
  box-shadow: 0px 4px 6px rgba(50, 124, 43, 0.3);
}

</style>