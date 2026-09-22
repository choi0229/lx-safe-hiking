<template>
  <HeaderView style="position: fixed; z-index: 4;width: 1920px;"></HeaderView>

  <div id="main-nav">
    <p id="mainTitle1" class="main-title">Let ' s find , 마루</p>
    <p id="mainTitle2">적당한 설명</p>
    <img id="mainImage1" class="main-image" src="/images/main2.jpg">
  </div>

  <div id="mainInput1" class="input-group">
    <input type="text" class="form-control rounded-pill" placeholder="🔭 산 이름이나 위치를 검색하세요..." aria-label="Recipient's username" aria-describedby="button-addon2" @focus="expandContainer($event)"
      v-model="searchQuery" @input="filterMountains">
  </div>

  <div id="inforTitle" class="d-flex flex-wrap custom-gab2">
    <div>
      <p id="helpTitle">등산 도우미</p>
      <p id="helpTitleContent1">등산에 도움이 되는 서비스를</p>
      <p id="helpTitleContent2">한곳에 모아봤어요.</p>
    </div>

    <div class="card" style="width: 10em; justify-content: center; align-items: center;">
      <img src="/images/메인4.jpg" style="object-fit: cover; width: 100%; height: 100%;" alt="">
      <p style="position: absolute; color: white; font-weight: bold; margin-top: 6.5em; margin-right: 3.5em;">대중교통</p>
    </div>
  </div>

  <div style="margin-top: 10em; margin-left: 18.2em;">
    <span style="font-size: 25px; font-weight: lighter;">근처의 지역 인기 등산로</span><span style="font-size: 25px;"> 서울</span>
  </div>

  <div class="d-flex row row-cols-1 row-cols-md-6 custom-gab" ref="cardsContainer">
    <div class="col card-wrapper" v-for="course in filteredCourses" :key="course.courseId">
      <div class="card mountainCourseCard" @click="goToMountainDetail(course)">
        <img :src="''+course.courseImage" id="mountainImage" class="card-img-top" alt="Mountain Image">
        <div class="card-body">
          <p id="mountain_name">{{ course.mountainName }}</p>
          <p id="mountain_name">{{ course.courseName }} 코스</p>
          <p id="mountain_location">{{ course.courseLocation }}</p>
          <p class="card-text" id="mountain_content">{{ course.courseContent }}</p>
        </div>
      </div>
    </div>
  </div>

  <div id="subTitle1">
    <p></p>
  </div>
</template>

<script setup>
import HeaderView from '@/components/HeaderView.vue';
import { ref, nextTick, onMounted, onUnmounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';


const router = useRouter();

const courses = ref([]);
const searchQuery = ref("");
const filteredCourses = ref([]);

const fetchCourses = async () => {
  try {
    const response = await axios.get('/api/course');
    courses.value = response.data;
    filteredCourses.value = response.data;
  } catch (error) {
    console.error('산 목록을 불러오는 중 오류 발생:', error);
  }
};


const filterMountains = () => {
  const query = searchQuery.value.toLowerCase();
  filteredCourses.value = courses.value.filter((course) => {
    return (
      course.courseName.toLowerCase().includes(query) ||
      course.courseLocation.toLowerCase().includes(query)
    );
  });
};

const goToMountainDetail = (course) => {
  router.push({
    name: 'mountaindetail',
    query: {
      course: JSON.stringify(course), 
    },
  });
};

const cardsContainer = ref(null);

const expandContainer = async (event) => {
  event.stopPropagation();

  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });

  setTimeout(async () => {
    document.body.style.overflow = 'hidden';

    const mainTitle = document.getElementById('mainTitle1');
    const mainNav = document.getElementById('main-nav');
    const mainTitle2 = document.getElementById('mainTitle2');

    mainTitle.style.opacity = 0;

    mainNav.style.height = '800px';

    mainTitle2.style.opacity = 1;

    await nextTick();

    const cards = cardsContainer.value?.querySelectorAll('.card-wrapper');
    if (cards) {
      cards.forEach((card, index) => {
        setTimeout(() => {
          card.style.opacity = 0;
        }, index * 1);
      });
    }
  }, 100);
};

const resetContainer = async (event) => {
  const inputElement = document.querySelector('#mainInput1 input');
  const imageElement = document.getElementById('mainImage1');
  if (event.target === inputElement || event.target === imageElement) {
    return;
  }

  document.body.style.overflow = 'auto';

  const mainTitle = document.getElementById('mainTitle1');
  const mainNav = document.getElementById('main-nav');
  const mainTitle2 = document.getElementById('mainTitle2');

  mainTitle.style.opacity = 1;

  mainNav.style.height = '630px';

  mainTitle2.style.opacity = 0;

  await nextTick();

  const cards = cardsContainer.value?.querySelectorAll('.card-wrapper');
  if (cards) {
    cards.forEach((card, index) => {
      setTimeout(() => {
        card.style.opacity = 1;
      }, index * 230);
    });
  }
};

onMounted(() => {
  fetchCourses();
  document.addEventListener('click', resetContainer);
});

onUnmounted(() => {
  document.removeEventListener('click', resetContainer);
});
</script>

<style scoped>
</style>
