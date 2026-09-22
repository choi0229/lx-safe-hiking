<template>
  <div id="app">
    <router-view />
    <div v-if="isLoading" class="loading-overlay">
      <img src="/images/로딩.gif" alt="로딩 중..." class="loading-gif" />
      <p>페이지 로딩 중...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const isLoading = ref(false);
const router = useRouter();

onMounted(() => {
  router.beforeEach((to, from, next) => {
    isLoading.value = true;
    next();
  });

  router.afterEach(() => {
    setTimeout(() => {
      isLoading.value = false;
    }, 500);
  });
});
</script>

<style scoped>

.v-application {
  font-family: 'TheJamsil', sans-serif !important;
}
@font-face {
  font-family:'TheJamsil';
  src: url('assets/font/TheJamsil1Thin.ttf') format('truetype');
  font-weight: 100;
}
@font-face {
  font-family:'TheJamsil';
  src: url('assets/font/TheJamsil2Light.ttf') format('truetype');
  font-weight: 200;
}
@font-face {
  font-family:'TheJamsil';
  src: url('assets/font/TheJamsil3Regular.ttf') format('truetype');
  font-weight: 300;
}
@font-face {
  font-family:'TheJamsil';
  src: url('assets/font/TheJamsil4Medium.ttf') format('truetype');
  font-weight: 400;
}
@font-face {
  font-family:'TheJamsil';
  src: url('assets/font/TheJamsil5Bold.ttf') format('truetype');
  font-weight: 500;
}
@font-face {
  font-family:'TheJamsil';
  src: url('assets/font/TheJamsil6ExtraBold.ttf') format('truetype');
  font-weight: 600;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(255, 255, 255, 1);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.loading-gif {
  width: 80px;
  height: 80px;
}

.loading-overlay p {
  font-family: 'TheJamsil4Bold', sans-serif;
  font-size: 14px;
  font-weight: 400;
  color: #333;
  margin-top: 10px;
}
</style>
