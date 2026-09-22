<template>
  <div id="mobileFooter">
    <div class="footer-category" @click="gotoMobileCourse()" style="margin-right: 10px;">
      <img 
        :src="isCoursePage ? '/images/코스.gif' : '/images/코스2.png'" 
        :style="isCoursePage ? gifStyle : defaultStyle"/>
      <p style="font-size: 13px; margin-top: 1rem;">코스</p>
    </div>

    <div class="footer-category" @click="gotoMobileRecoding()" style="margin-right: 10px;">
      <img 
        :src="isFitnessPage ? '/images/HikingIcon.gif' : '/images/HikingIcon.png'" 
        :style="isFitnessPage ? gifStyle : defaultStyle" />
      <p style="font-size: 13px; margin-top: 1rem;">등산</p>
    </div>

    <div class="footer-category home" @click="gotoHome()" style="">
      <img 
        :src="isHomePage ? '/images/홈.gif' : '/images/홈.png'" 
        :style="isHomePage ? gifStyle : defaultStyle"
      />
      <p style="font-size: 13px; margin-top: -5px;">홈</p>
    </div>

    <div class="footer-category" @click="gotoMobileComplaint()" style="margin-left: 10px;">
      <img 
        :src="isComplaintPage ? '/images/위험신고.gif' : '/images/위험신고.png'" 
        :style="isComplaintPage ? gifStyle : defaultStyle"
      />
      <p style="font-size: 13px; margin-top: 1rem;">민원신고</p>
    </div>

    <div class="footer-category" @click="gotoMobileCommunity()" style="margin-left: 10px;">
      <img 
        :src="isCommunityPage ? '/images/커뮤니티.gif' : '/images/커뮤니티.png'" 
        :style="isCommunityPage ? gifStyle : defaultStyle"
      />
      <p style="font-size: 13px; margin-top: 1rem;">커뮤니티</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

const isCoursePage = ref(false);
const isFitnessPage = ref(false);
const isHomePage = ref(false);
const isComplaintPage = ref(false);
const isCommunityPage = ref(false);

onMounted(() => {
  isCoursePage.value = route.path === '/MobileCourseView';
  isFitnessPage.value = route.path === '/MobileFitnessView';
  isHomePage.value = route.path === '/mobilemainview';
  isComplaintPage.value = route.path === '/complaintListMobile';
  isCommunityPage.value = route.path === '/communityListMobile';
});

function gotoHome() {
  window.location.href = '/mobilemainview';
}

function gotoMobileCourse() {
  window.location.href = '/mobilecourseview';
}

function gotoMobileCommunity() {
  window.location.href = '/mobilecommunity';
}

function gotoMobileComplaint() {
  window.location.href = '/complaintListMobile';
}

function gotoMobileRecoding() {
  window.location.href = '/record';
}

const defaultStyle = {
  width: '30px',
  height: '30px',
  filter: 'grayscale(100%) brightness(0%)',
  marginBottom: '-0.7rem'
};

const gifStyle = {
  width: '40px',
  height: '40px',
  filter: 'hue-rotate(280deg)'
};
</script>

<style scoped>
@font-face {
  font-family: 'TheJamsil5Bold';
  src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_2302_01@1.0/TheJamsil5Bold.woff2') format('woff2');
  font-weight: 700;
  font-style: normal;
}

h1,
h2,
h3,
div,
.btn-group .btn,
p {
  font-family: "TheJamsil5Bold", sans-serif !important;
}

#mobileFooter {
  position: fixed;
  height: 70px;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #ffffff;
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding: 0 5px;
}

.footer-category {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  font-family: 'TheJamsil4Bold';
  font-weight: 500;
  cursor: pointer;
  flex: 1;
}

.footer-category img {
  width: 33px;
  height: 33px;
  margin-bottom: 8px;
}

.footer-category p {
  margin: 0;
  font-size: 13px;
  line-height: 1.2;
  position: relative;
  top: 4px;
}

.home p {
  top: 0;
}

</style>
