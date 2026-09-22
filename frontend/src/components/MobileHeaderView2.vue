<template>
  <header class="d-flex justify-content-between align-items-center p-2 MobileHeader" style="background-color: white;">
    <div class="d-flex align-items-center" @click="gotoHome()" style="margin-left: 0.5rem;">
      <img src="/images/icon2.png" alt="산 로고" class="" width="25" height="15" />
      <span class="brand-name">안전하산</span>
    </div>

    <div class="dropdown">
      <a role="button" @click="toggleDropdown" data-bs-toggle="dropdown" aria-expanded="false" style="margin-right: -0.4rem;">
        <img src="/images/hamburgerIcon.png" alt="돋보기 아이콘" width="50" height="50">
      </a>
      <ul class="dropdown-menu">
        <li><a class="dropdown-item" href="#">로그인</a></li>
        <li><a class="dropdown-item" href="#">커뮤니티</a></li>
        <li><a class="dropdown-item" href="#">멘토신청</a></li>
      </ul>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue';

const showDropdown = ref(false);

function toggleDropdown() {
  showDropdown.value = !showDropdown.value;
}

function gotoHome() {
  window.location.href = '/mobilemainview';
}
</script>

<style scoped>
@font-face {
  font-family: 'TheJamsil5Bold';
  src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_2302_01@1.0/TheJamsil5Bold.woff2') format('woff2');
  font-weight: 700;
  font-style: normal;
}

.MobileHeader {
  box-shadow: 0 1px 10px rgba(0, 0, 0, 0.3);
}

.brand-name {
  font-family: 'TheJamsil3Bold';
  font-weight: 700;
  font-size: 17px;
}

.dropdown-menu {
  opacity: 0;
  transform: translateY(-10px);
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.dropdown-menu.show {
  opacity: 1;
  transform: translateY(0);
}

.description {
  font-weight: 300;
  font-size: 13px;
}
</style>
