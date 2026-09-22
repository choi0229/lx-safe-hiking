<template>
  <HeaderView style="z-index: 4;width: 1920px;"></HeaderView>

  <div class="dashboard">

    <aside class="sidebar">
      <div class="d-flex flex-column">
        <div>
          <div class="header">
            <button @click="goBack" class="back-button"><img src="/images/moveback_icon.png" alt="뒤로가기 아이콘" width="15"
                height="15"></button>


          </div>

          <div class="course-info">
            <div class="image-container">
              <img v-if="courseData?.courseImage" :src="courseData.courseImage" alt="Mountain Image"
                class="course-image" />
              <div class="image-overlay">
                <p class="image-course-name">{{ courseData?.courseName }}코스</p>
                <p class="image-course-location">{{ courseData?.courseLocation }}<span id="courseTitle">{{
                  courseData?.mountainName || '코스 정보' }}</span></p>
                <p class="difficulty-text"> 난이도: {{ averageDifficultyText }} · <i
                    class="bi bi-star-fill image-rating-star"></i> {{ averageRating }}({{ reviewCount }})</p>
              </div>
            </div>
            <div class="course-details">
              <p>{{ courseData?.courseContent }}</p>
            </div>
          </div>

          <div class="button-group">
            <div class="btn custom-btn primary-btn" @click="showCoursePreviewModal">
              <img src="/images/미리보기.png">
            <p type="button" @click="showCoursePreviewModal">코스 미리보기</p>
            </div>

            <div class="btn custom-btn secondary-btn" @click="goToCourse3DPage()">
              <img src="/images/산3D.png">
              <p type="button" >3D 보기</p>
            </div>
            
            <div class="btn custom-btn tertiary-btn">
              <img src="/images/비교하기.png">
              <p type="button">코스 비교하기</p>
            </div>
            
          </div>

          <div class="accident-summary">
            <div class="carousel-container">
              <div class="card-carousel" ref="accidentCarouselRef">
                <div class="card">
                  <div class="card-overlay">
                    <img src="/images/strumble.png" class="card-img-top"/>
                    <span class="accident-item-content1">실족 : {{ strumbleCount }}
                      <div class="image-grid">
                        <div v-for="(strumble, index) in strumbles" :key="strumble" class="image-item">
                          <img
                            :src="'/images/' + strumble"
                            class="clickable-image"
                            @click="openInfoWindowForImage(index, '실족')"
                          />
                        </div>
                      </div>
                    </span>
                  </div>
                </div>

                <div class="card">
                  <div class="card-overlay">
                    <img src="/images/distress.png" class="card-img-top" /> 
                    <span class="accident-item-content1">조난 : {{ distressCount }}
                      <div class="image-grid">
                        <div v-for="(distress, index) in distresses" :key="distress" class="image-item">
                          <img
                            :src="'/images/' + distress"
                            class="clickable-image"
                            @click="openInfoWindowForImage(index, '조난')"
                          />
                        </div>
                      </div>
                    </span>
                    <span class="accident-item-content2"></span>
                  </div>
                </div>

                <div class="card">
                  <div class="card-overlay">
                    <img src="/images/disease.png" class="card-img-top" />
                    <span class="accident-item-content1">질환 : {{ diseaseCount }}
                      <div class="image-grid">
                        <div v-for="(disease, index) in diseases" :key="disease" class="image-item">
                          <img
                            :src="'/images/' + disease"
                            class="clickable-image"
                            @click="openInfoWindowForImage(index, '질환')"
                          />
                        </div>
                      </div>
                    </span>
                    <span class="accident-item-content2"></span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="weather-forecast" v-if="dailyWeather.length > 0">

            <div class="date-selector">

              <div v-for="(day, index) in dailyWeather.slice(0, 6)" :key="index" class="date-button-wrapper">

                <div class="day-label">{{ formatDayOfWeek(day.date) }}</div>

                <button :class="['date-button', { active: selectedDateIndex === index }]" @click="selectDate(index)">
                  {{ formatDate(day.date) }}
                </button>

              </div>
            </div>

            <div class="forecast-details" v-if="selectedDay">
              <div class="d-flex">

                <div class="row" id="forecast1">

                  <div class="d-flex" style="margin-top: -1rem; margin-left: 0.3rem; width:200px; height:100px">
                    <p class="current-temperature">{{ Math.round(selectedDay.currentTemp - 273.15) }}°</p>
                    <p class="weather-icon" v-html="getWeatherEmoji(selectedDay.weather[0].main)"></p>
                  </div>


                  <div class="row">

                    <p class="description">{{ translateWeatherDescription(selectedDay.weather[0].id) }}</p>

                    <div class="temperature-range d-flex">
                      <p class="min-temp">최저: {{ Math.round(selectedDay.minTemp - 273.15) }}°</p>
                      <p class="max-temp">최고: {{ Math.round(selectedDay.maxTemp - 273.15) }}°</p>
                    </div>

                  </div>

                </div>




                <div class="rainSun-info">
                  <div class="rain-info d-flex">
                    <div class="rain-icon" :style="{ '--rain-level': selectedDay.pop * 100 + '%' }"></div>
                    <p class="rain-chance" style="margin-top: 1rem; margin-left: 1.5rem;">{{
                      Math.round(selectedDay.pop * 100) }}%</p>
                  </div>

                  <div class="sun-info">
                    <p id="sunrise"><img src="/images/sunrise.png" alt="sunrise" class="icon-small">일출: {{
                      sunriseTimes[selectedDateIndex] }}</p>
                    <p id="sunset"><img src="/images/sunset.png" alt="sunset" class="icon-small">일몰: {{
                      sunsetTimes[selectedDateIndex] }}</p>
                  </div>

                </div>

              </div>
            </div>
          </div>
        </div>

        <div class="summary-section">
          <div class="rating">
            <p class="average-rating-text">{{ averageRating }}</p>
            <div class="stars">
              <i v-for="star in fullStars" :class="'bi bi-star-fill'" :key="star"></i>
              <i v-if="halfStar" class="bi bi-star-half"></i>
              <i v-for="star in emptyStars" class="bi bi-star" :key="star"></i>
            </div>
            <p class="review-count-text">{{ reviewCount }}개의 리뷰</p>
          </div>

          <div class="rating-breakdown">
            <div v-for="(count, star) in ratingDistribution" :key="star" class="rating-bar">
              <span>{{ star }}점</span>
              <i class="bi bi-star-fill n-star"></i>
              <div class="progress-bar">
                <div class="progress" :style="{ width: (count / reviewCount) * 100 + '%' }"></div>
              </div>
            </div>
          </div>
        </div>


        <div v-if="showModal" class="overlay" @click.self="showModal = false">
          <div class="review-form">
            <button class="close-button" @click="showModal = false">X</button>
            <h2>리뷰 작성</h2>

            <label for="course" class="modal-label">코스 선택</label>
            <select v-model="selectedCourse" id="course" class="input-field">
              <option value="" disabled>코스를 선택하세요</option>
              <option v-for="course in courses" :key="course.courseId" :value="course.courseId">
                {{ course.courseName }}
              </option>
            </select>

            <div class="rating">
              <label class="modal-label">별점</label>
              <div class="stars">
                <i v-for="star in 5" :key="star"
                  :class="star <= rating ? 'bi bi-star-fill n-star' : 'bi bi-star n-star'" @click="setRating(star)">
                </i>
              </div>
            </div>

            <label for="difficulty" class="modal-label">난이도</label>
            <select v-model="difficulty" id="difficulty" class="input-field">
              <option value="" disabled>난이도를 선택하세요</option>
              <option value="쉬움">쉬움</option>
              <option value="보통">보통</option>
              <option value="어려움">어려움</option>
            </select>

            <label class="modal-label">리뷰 내용</label>
            <textarea v-model="reviewContent" placeholder="후기 내용을 입력하세요" class="input-field textarea"></textarea>

            <label for="photos" class="modal-label">사진 업로드</label>
            <input type="file" multiple @change="onFileChange" class="file-input" />

            <button @click="submitReview" class="submit-button">리뷰 작성</button>
          </div>
        </div>

        <div class="review-page">
          <div class="content-wrapper">
            <div class="review-search">
          
            </div>
            <div>
              <button class="review-button" @click="showModal = true"><img src="/images/write.png"
                  style="width: 32px; height:32px; justify-content: center; margin-bottom: 0.1rem;"></button>
            </div>

            <div class="reviews-list">
              <div v-for="review in visibleReviews" :key="review.reviewId" class="review-item">
                <div class="review-header">
                  <img class="profile-image" :src="review.profileImage || '/images/main1.jpg'" alt="Profile" />
                  <div class="user-info">
                    <h3>{{ review?.userId || 'Unknown User' }}</h3>
                    <div class="stars-inline" v-if="review?.rating !== undefined">
                      <i v-for="star in 5" :class="{
                        'bi bi-star-fill': review?.rating && star <= Math.floor(review.rating),
                        'bi bi-star-half': review?.rating && star === Math.ceil(review.rating) && review.rating % 1 >= 0.5,
                        'bi bi-star': !review?.rating || star > Math.ceil(review.rating)
                      }" :key="star"></i>
                    </div>

                    <span class="review-date">{{ formatDate(review?.date) }}</span>
                  </div>
                  <template v-if="review.userId === loggedInUserId">
                    <button @click="editReview(review)" class="edit-button">수정</button>
                    <button @click="confirmDelete(review.reviewId)" class="delete-button">X</button>
                  </template>
                </div>
                <p class="review-content">{{ review?.reviewContent || '내용 없음' }}</p>
                <div class="review-photos">
                  <img v-for="photo in review.photos" :src="photo" :key="photo" alt="리뷰 사진" class="review-photo" />
                </div>
                <span class="difficulty">난이도: {{ review?.difficulty || '정보 없음' }}</span>
              </div>
            </div>
            <button v-if="canShowMore" @click="loadMoreReviews" class="load-more-button">더 보기</button>
          </div>
        </div>

        <div v-if="editModal && currentReview" class="overlay" @click.self="editModal = false">
          <div class="review-form">
            <button class="close-button" @click="editModal = false">X</button>
            <h2>리뷰 수정</h2>

            <label class="modal-label">코스 선택</label>
            <select v-model="currentReview.courseId" class="input-field" disabled>
              <option :value="currentReview.courseId">{{ courseData2.courseName }} 코스</option>
              <option v-for="course in courses" :key="course.courseId" :value="course.courseId">
                {{ course.courseName }}
              </option>
            </select>

            <label class="modal-label">별점</label>
            <div class="stars">
              <i v-for="star in 5" :key="star"
                :class="star <= currentReview.rating ? 'bi bi-star-fill n-star' : 'bi bi-star n-star'"
                @click="currentReview.rating = star"></i>
            </div>

            <label class="modal-label">난이도</label>
            <select v-model="currentReview.difficulty" class="input-field">
              <option value="쉬움">쉬움</option>
              <option value="보통">보통</option>
              <option value="어려움">어려움</option>
            </select>

            <label class="modal-label">리뷰 내용</label>
            <textarea v-model="currentReview.reviewContent" placeholder="후기 내용을 입력하세요"
              class="input-field textarea"></textarea>

            <button @click="saveReview" class="submit-button">저장</button>
          </div>
        </div>



        <div v-if="deleteModal" class="overlay" @click.self="deleteModal = false">
          <div class="review-form">
            <button class="close-button" @click="deleteModal = false">X</button>
            <h2>리뷰 삭제</h2>
            <p>정말 삭제하시겠습니까?</p>
            <div class="modal-actions">
              <button @click="deleteReview" class="submit-button confirm-button">예</button>
              <button @click="deleteModal = false" class="submit-button cancel-button">아니오</button>
            </div>
          </div>
        </div>
      </div>
    </aside>

    <main class="main">
      <div class="toggle-switch-container2" style="z-index: 10;">
        <div class="toggle-switch2" @click="toggleMap('default')" :class="{ active: showMap === 'default' }">
          <img src="/images/기본.png" alt="" style="width: 20px; height: auto; margin-right: 5px;"> 기본
        </div>
        <div class="toggle-switch2" @click="toggleMap('photo')" :class="{ active: showMap === 'photo' }">
          <img src="/images/사진.png" alt="" style="width: 20px; height: auto; margin-right: 5px;"> 사진
        </div>
        <div class="toggle-switch2" @click="applyFilter('실족')" :class="{ active: selectedFilter === '실족' }">
          <img src="/images/strumble.png" alt="" style="width: 20px; height: auto; margin-right: 5px;"> 실족
        </div>
        <div class="toggle-switch2" @click="applyFilter('조난')" :class="{ active: selectedFilter === '조난' }">
          <img src="/images/distress.png" alt="" style="width: 20px; height: auto; margin-right: 5px;"> 조난
        </div>
        <div class="toggle-switch2" @click="applyFilter('질환')" :class="{ active: selectedFilter === '질환' }">
          <img src="/images/disease.png" alt="" style="width: 20px; height: auto; margin-right: 5px;"> 질환
        </div>
      </div>

  <div v-if="showMap === 'default'" id="map" ref="map-container" class="map-container">
        
      <button id="showHelpButton" @click="showHelp = true">?</button>
        <div class="map-legend ms-4">
          
          <div class="legend-item">
            <div class="legend-icon" style="background-color:#FF4500;"></div>
            <span>오르막</span>
          </div>
          
          <div class="legend-item">
            <div class="legend-icon" style="background-color:#32CD32;"></div>
            <span>평지</span>
          </div>
          
          <div class="legend-item">
            <div class="legend-icon" style="background-color:#1E90FF;"></div>
            <span>내리막</span>
          </div>
          
        </div>
      </div>
      <div v-else-if="showMap === 'photo'" id="photoMap" class="map-container"></div>

      <div id="grapth">
        <div class="chart1-section">
          <canvas id="elevationChart" style="height: 180px; width: 1000px;"></canvas>
        </div>
      </div>
    </main>
  </div>

 <div class="modal fade" id="kt_modal_course_preview" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered mw-1200px">
    <div class="modal-content rounded">

      <div class="modal-header compact-header">
        <h1 class="modal-title">코스 미리보기</h1>
        <button class="btn btn-sm btn-icon btn-active-color-primary" data-bs-dismiss="modal">
          <i class="bi bi-x-circle"></i>
        </button>
      </div>

      <div class="modal-body scroll-y px-10 px-lg-15 pt-0 pb-15">
        <div class="mb-13 text-center">
          <div class="text-muted fw-semibold fs-5">동영상과 경로를 동시에 미리 확인 할 수 있습니다.</div>
        </div>

        <div class="video-container">
          <div id="modalMap"></div>

          <video
            ref="videoRef"
            controls
            autoplay
            muted
            @timeupdate="syncCourseWithVideo"
          >
            <source src="/videos/HJcourse.mp4" type="video/mp4" />
            브라우저가 비디오 태그를 지원하지 않습니다.
          </video>
        </div>
      </div>
    </div>
  </div>
</div>

</template>

<script setup>
import HeaderView from '@/components/HeaderView.vue';
import { onMounted, ref, computed } from 'vue';
import Chart from 'chart.js/auto';
import axios from "axios";
import { defineProps } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { nextTick } from 'vue';
import { Modal } from 'bootstrap';
import {
  resolveTrailId,
  SLOPE_WINDOW_METERS,
  getEstimatedSlopeColor,
  fetchTrailGeoJson,
  fetchSlopeSections,
} from '@/api/slopeSection.js';

const props = defineProps({
  course: {
    type: String,
    required: true
  }

});

const courseData = ref(JSON.parse(props.course));

console.log("Mountain Data:", courseData.value);


const map = ref(null);
const routeCoordinates = ref([]);
let chartInstance = null;
const currentMarker = ref(null);
const modalPolyline = ref(null);
const showMap = ref('default');
  let photoMap = ref(null);
const distressCount = ref(0);
const strumbleCount = ref(0);
const diseaseCount = ref(0);

const videoRef = ref(null);
const modalMap = ref(null);

onMounted(() => {
  const queryMapType = route.query.mapType || 'default';
    showMap.value = queryMapType

  initializeMap();
  
  fetchReviewsAndRatings();
  fetchSummary();

  fetchWeatherData(37.590870, 126.958077);
  requestCourse()

});

function showCoursePreviewModal() {
  console.log('showCoursePreviewModal 호출됨');
  const elem = document.querySelector('#kt_modal_course_preview');
  const coursePreviewModal = new Modal(elem);
  coursePreviewModal.show();

  coursePreviewModal._element.addEventListener('shown.bs.modal', () => {
    initializeModalMap();
    kakao.maps.event.trigger(modalMap.value, 'resize');
    modalMap.value.setCenter(new kakao.maps.LatLng(37.66433293993584, 127.01160029114365));
    console.log('모달 지도 리사이즈 완료');
  });
}

function initializeModalMap() {
  const modalMapElement = document.getElementById('modalMap');
  if (!modalMapElement) {
    console.error('modalMap 요소를 찾을 수 없습니다.');
    return;
  }

  modalMap.value = new kakao.maps.Map(modalMapElement, {
    center: new kakao.maps.LatLng(37.66433293993584, 127.01160029114365),
    level: 5,
  });

  loadGeoJSONFromServer(modalMap.value);

  if (routeCoordinates.value.length > 0) {
    addRouteLayer(routeCoordinates.value, modalMap.value);
  }
}

const selectedFilter = ref('전체');

let mapMarkers = []

 function applyFilter(filter) {
    selectedFilter.value = filter;
    
    if (mapMarkers.length) {
      mapMarkers.forEach(marker => marker.setMap(null));
      mapMarkers = [];
    }
    
    if (selectedFilter.value === '전체') {
      loadMarkers("/data/헬기장spot.geojson", '/images/helipad.png');
      loadMarkers("/data/화장실.geojson", '/images/toilets.png');
      loadDangerMarkers("/data/2023산악사고_인왕산2.geojson", '/images/danger.png');
    } else {
      loadDangerMarkers("/data/2023산악사고_인왕산2.geojson", '/images/danger.png');
    }
  }

  async function loadMarkers(url, imageSrc) {
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const markerData = await response.json();

    const filteredMarkers = markerData.features.filter((feature) => {
      const courseName = feature.properties.MNTN_NM2;
      return (
        courseName === courseData.value.courseName);
    });

    filteredMarkers.forEach((spot) => {
      const lat = spot.geometry.coordinates[1];
      const lng = spot.geometry.coordinates[0];
      const markerPosition = new kakao.maps.LatLng(lat, lng);

      const imageSize = new kakao.maps.Size(35, 45);
      const imageOption = { offset: new kakao.maps.Point(12, 35) };
      const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);

      const marker = new kakao.maps.Marker({
        position: markerPosition,
        map: map.value,
        title: spot.properties.MNTN_NM,
        image: markerImage,
      });

      mapMarkers.push(marker);

      const infowindow = new kakao.maps.InfoWindow({
        content: `
          <div style="display: flex; flex-direction: column; align-items: center; width: 150px; height: auto; 
          box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); font-family: Arial, sans-serif; overflow: hidden; 
          border: 1px solid #ddd; border-radius: 12px; z-index: 500;">
            <!-- 사진 영역 -->
            <div style="display: flex; justify-content: center; align-items: center; width: 100%; height: 150px; 
            overflow: hidden; border-bottom: 2px solid #ced4da;">
              <img src="/images/${spot.properties.image}" alt="사진" 
              style="width: 130px; height: 120px; object-fit: cover; border: 1px solid black; border-radius: 10px;" />
            </div>
            <!-- 텍스트 영역 -->
            <div style="text-align: center; padding: 10px;">
              <p style="font-size: 14px; font-weight: bold; margin: 0; color: #333;">
                <img src="/images/window산.png" style="width: 16px; height: 16px;" /> ${spot.properties.MNTN_NM}
              </p>
              <p style="font-size: 12px; color: #666; margin: 5px 0;">- ${spot.properties.SAFE_SPOT2} -</p>
            </div>
          </div>
        `,
        disableAutoPan: true,
      });

      kakao.maps.event.addListener(marker, 'click', () => {
        if (infowindow.getMap()) {
          infowindow.close();
        } else {
          infowindow.open(map.value, marker);
        }
      });
    });
  } catch (error) {
    console.error(`${url} 파일 로드 중 에러 발생:`, error);
  }
}

const distresses = ref([]);
const strumbles = ref([]);
const diseases = ref([]);


function openInfoWindowForImage(index, type = '실족') {
  const target = markers.value.find(
    (item) => item.type === type && item.image === (type === '실족' ? strumbles.value[index] :
                                                   type === '조난' ? distresses.value[index] :
                                                   diseases.value[index])
  );

  if (target) {
    if (currentMarker.value && currentMarker.value.infowindow) {
      currentMarker.value.infowindow.close();
    }

    target.infowindow.open(map.value, target.marker);

    currentMarker.value = target;
  } else {
    console.error('해당 이미지를 찾을 수 없습니다.');
  }
}


const markers = ref([]);

async function loadDangerMarkers(url, imageSrc) {
  try {
    if(selectedFilter.value == '전체'){
      strumbles.value = [];
      distresses.value = [];
      diseases.value = [];
      strumbleCount.value = 0;
      distressCount.value = 0;
      diseaseCount.value = 0;
    } else if(selectedFilter.value == '실족'){
      strumbles.value = [];
      strumbleCount.value = 0;
    } else if(selectedFilter.value == '조난'){
      distresses.value = [];
      distressCount.value = 0;
    } else if(selectedFilter.value == '질환'){
      diseases.value = [];
      diseaseCount.value = 0;
    }    


    const response = await fetch(url);
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const markerData = await response.json();

    const filteredMarkers = markerData.features.filter((feature) => {
      const markerType = feature.properties.type;
      const courseName = feature.properties.MNTN_NM2;
      return courseName === courseData.value.courseName &&
             (selectedFilter.value === '전체' || markerType === selectedFilter.value || !markerType);
    });

    filteredMarkers.forEach((spot) => {
      const lat = spot.geometry.coordinates[1];
      const lng = spot.geometry.coordinates[0];
      const markerPosition = new kakao.maps.LatLng(lat, lng);

      if (spot.properties.type === '실족') {
        strumbleCount.value += 1;
        strumbles.value.push(spot.properties.image);
      } else if (spot.properties.type === '조난') {
        distressCount.value += 1;
        distresses.value.push(spot.properties.image);
      } else if (spot.properties.type === '질환') {
        diseaseCount.value += 1;
        diseases.value.push(spot.properties.image);
      }

      switch (spot.properties.type) {
          case '실족':
            imageSrc = '/images/strumble.png';
            break;
          case '조난':
            imageSrc = '/images/distress.png';
            break;
          case '질환':
            imageSrc = '/images/disease.png';
            break;
        }

      const imageSize = new kakao.maps.Size(35, 45);
      const imageOption = { offset: new kakao.maps.Point(12, 35) };
      const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);

      const marker = new kakao.maps.Marker({
        position: markerPosition,
        map: map.value,
        title: spot.properties.MNTN_NM,
        image: markerImage,
      });

      const infowindow = new kakao.maps.InfoWindow({
          content: `
    <div style="display: flex; flex-direction: column; align-items: center; width: 150px; height: auto; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); font-family: Arial, sans-serif; overflow: hidden; border: 1px solid #ddd; border-radius: 12px; z-index: 500;">
      <!-- 사진 영역 -->
      <div style="display: flex; justify-content: center; align-items: center; width: 100%; height: 150px; overflow: hidden; border-bottom: 2px solid #ced4da;">
        <img src="/images/${spot.properties.image}" alt="사진" 
          style="
            width: 130px; 
            height: 120px; 
            object-fit: cover; 
            border: 1px solid black; 
            border-radius: 10px;
            box-sizing: border-box; /* 여백과 크기 조정 방지 */
            position: relative;
          ">
      </div>
      <!-- 텍스트 영역 -->
      <div style="text-align: center; padding: 10px; margin-top: -10px;">
        <p style="font-size: 14px; font-weight: bold; margin: 0; color: #333;">
          <img src="/images/window산.png" style="width: 16px; height: 16px;"/> ${spot.properties.MNTN_NM}
        </p>
        <p style="font-size: 12px; color: #666; margin: 5px 0 0;">- ${spot.properties.SAFE_SPOT2} -</p>
      </div>
    </div>
  `,
          disableAutoPan: true,
});

      markers.value.push({
        type: spot.properties.type,
        image: spot.properties.image,
        marker,
        infowindow,
      });

      kakao.maps.event.addListener(marker, 'click', () => {
        infowindow.open(map.value, marker);
      });
      mapMarkers.push(marker);
    });
  } catch (error) {
    console.error(`${url} 파일 로드 중 에러 발생:`, error);
  }
}


function syncCourseWithVideo(event) {
  if (!modalMap.value || !routeCoordinates.value.length) return;

  const progress = event.target.currentTime / event.target.duration;
  const index = Math.floor(progress * routeCoordinates.value.length);

  if (index >= 0 && index < routeCoordinates.value.length) {
    const point = routeCoordinates.value[index];

    if (currentMarker.value) currentMarker.value.setMap(null);

    currentMarker.value = new kakao.maps.Marker({
      position: new kakao.maps.LatLng(point.lat, point.lng),
      map: modalMap.value,
    });

    modalMap.value.panTo(new kakao.maps.LatLng(point.lat, point.lng));
  }
}

function initializeMap() {
    if (showMap.value === 'default') {
        const script = document.createElement('script');
        script.onload = () => kakao.maps.load(() => {
          map.value = new kakao.maps.Map(document.getElementById('map'), {
            center: new kakao.maps.LatLng(37.66433293993584, 127.01160029114365),
            level: 5,
          });
          if (map.value) {
            loadGeoJSONFromServer(map.value);
            loadMarkers("/data/헬기장spot.geojson", '/images/helipad.png');
            loadMarkers("/data/화장실.geojson", '/images/toilets.png');
            loadDangerMarkers("/data/2023산악사고_인왕산2.geojson", '/images/danger.png');
          }
        });
        script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false';
        document.head.appendChild(script);
    } else if (showMap.value === 'photo') {
        const script = document.createElement('script');
        script.onload = () => kakao.maps.load(() => {
          photoMap.value = new kakao.maps.Map(document.getElementById('photoMap'), {
          center: new kakao.maps.LatLng(37.66433293993584, 127.01160029114365),
          level: 5,
          });
          if (photoMap.value) {
            loadGeoJSONFromServer(photoMap.value);
            loadMarkersToPhotoMap();
          }
        });
        script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=333bda7da18df138fb4d9b3e5cf351c4&autoload=false';
        document.head.appendChild(script);
      }
  }

   const toggleMap = (type) => {
    showMap.value = type;
    initializeMap();
  };

// 정적 GeoJSON 대신 Trail GeoJSON API(DB 기반 TrailFeature)에서 경로를 불러온다
async function loadGeoJSONFromServer(targetMap = map.value) {
    try {
      const geojsonData = await fetchTrailGeoJson();
      console.log('Trail GeoJSON data loaded:', geojsonData);
      processGeoJSON(geojsonData, targetMap);
    } catch (error) {
      console.error('Trail GeoJSON API 로드 중 에러 발생:', error);
    }
  }

  let comCourse = ref([]);

const requestCourse = async () => {
  try{
    console.log(courseData.value.courseName)
    const response = await axios.post('/api/comCourse',{
      courseName: courseData.value.courseName
    });
    comCourse.value = response.data;
    console.log(comCourse.value)
  }catch(error){
    console.log('에러발생! '+error)
  }
}


  function loadMarkersToPhotoMap() {
    if (!photoMap.value) {
      console.error("photoMap이 초기화되지 않았습니다.");
      return;
    }
  
    comCourse.value.forEach(course => {
      const lat = course.latitude;
      const lng = course.longitude;
  
      const markerPosition = new kakao.maps.LatLng(lat, lng);
  
      const imageSrc = '/images/danger-icon.png';
      const imageSize = new kakao.maps.Size(35, 45);
      const imageOption = { offset: new kakao.maps.Point(12, 35) };
      const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);

      const marker = new kakao.maps.Marker({
        position: markerPosition,
        map: photoMap.value,
        title:  course.courseName,
        image: markerImage,
      });
  
      const infowindowPhotoMap = new kakao.maps.InfoWindow({
          content: `
    <div style="display: flex; flex-direction: column; align-items: center; width: 150px; height: auto; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); font-family: Arial, sans-serif; overflow: hidden; border: 1px solid #ddd; border-radius: 12px; z-index: 500;">
      <!-- 사진 영역 -->
      <div style="display: flex; justify-content: center; align-items: center; width: 100%; height: 150px; overflow: hidden; border-bottom: 2px solid #ced4da;">
        <img src="${course.communityUrl}" alt="사진" 
          style="
            width: 130px; 
            height: 120px; 
            object-fit: cover; 
            border: 1px solid black; 
            border-radius: 10px;
            box-sizing: border-box; /* 여백과 크기 조정 방지 */
            position: relative;
          ">
      </div>
      <!-- 텍스트 영역 -->
      <div style="text-align: center; padding: 10px; margin-top: -10px;">
        <p style="font-size: 14px; font-weight: bold; margin: 0; color: #333;">
          <img src="/images/window산.png" style="width: 16px; height: 16px;"/> ${course.communityTitle}
        </p>
        <p style="font-size: 12px; color: #666; margin: 5px 0 0;">- ${course.courseName} -</p>
      </div>
    </div>
  `,
          disableAutoPan: true,
        });
  
      kakao.maps.event.addListener(marker, 'click', () => {
      if (infowindowPhotoMap.getMap()) {
        infowindowPhotoMap.close();
      } else {
        infowindowPhotoMap.open(photoMap.value, marker);
      }
    });
    });
  }



  function processGeoJSON(geojsonData, targetMap) {
    let allCoordinates = [];

    geojsonData.features.forEach((feature) => {
      if (feature.properties.PMNTN_NM && feature.properties.PMNTN_NM.includes(courseData.value.courseName)) {
        let coordinates = [];

        if (feature.geometry.type === 'MultiLineString') {
          feature.geometry.coordinates.forEach(line => {
            coordinates = coordinates.concat(line.map((coord) => ({
              lng: coord[0],
              lat: coord[1],
              elevation: feature.properties.DN || 0
            })));
          });
        } else if (feature.geometry.type === 'LineString') {
          coordinates = feature.geometry.coordinates.map((coord) => ({
            lng: coord[0],
            lat: coord[1],
            elevation: feature.properties.DN || 0
          }));
        }

        routeCoordinates.value = allCoordinates;
        allCoordinates = allCoordinates.concat(coordinates);
      }
    });

    if (allCoordinates.length > 0) {
      addRouteLayer(allCoordinates, targetMap);
      drawElevationChart(allCoordinates);
      if (targetMap === map.value) {
        renderSlopeOverlay(targetMap, geojsonData);
      }
    } else {
      console.log('유효한 구간 데이터가 없습니다.');
    }
  }

// 경로(Base Trail) 레이어 추가 함수 -- 경사 색상은 이 함수가 아니라
// renderSlopeOverlay()가 별도 Overlay로 그린다 (Base + Overlay 구조).
function addRouteLayer(coordinates, targetMap) {
    if (!targetMap) {
      console.error("targetMap이 초기화되지 않았습니다.");
      return;
    }

    const linePath = coordinates.map((coord) => new kakao.maps.LatLng(coord.lat, coord.lng));

    if (targetMap === map.value) {
      drawBaseRoute(coordinates);

    } else if (targetMap === photoMap.value || targetMap === modalMap.value) {
      modalPolyline.value = new kakao.maps.Polyline({
        path: linePath,
        strokeWeight: 5,
        strokeColor: '#00FF00',
        strokeOpacity: 0.8,
        strokeStyle: 'solid',
      });

      modalPolyline.value.setMap(targetMap);
    }

    const bounds = new kakao.maps.LatLngBounds();
    coordinates.forEach(coord => bounds.extend(new kakao.maps.LatLng(coord.lat, coord.lng)));
    targetMap.setBounds(bounds);
  }

// 20m SlopeSection Backend API 기반 경사 Overlay -- Base Trail(drawBaseRoute) 위에
// 덧그려진다. course 변경 시 이전 Overlay를 반드시 제거한 뒤 새로 그린다(중복 방지).
// SlopeSection이 존재하지 않는 구간(coverage 한계)은 Base Trail만 계속
// 보이며, 이 함수는 그 구간에 임의로 slope=0을 채우지 않는다 -- Feature를 아예 그리지 않는다.
let slopeOverlayPolylines = [];

async function renderSlopeOverlay(targetMap, trailGeoJson) {
  slopeOverlayPolylines.forEach((polyline) => polyline.setMap(null));
  slopeOverlayPolylines = [];

  try {
    const trailId = resolveTrailId(courseData.value.courseName, trailGeoJson);
    const slopeGeoJson = await fetchSlopeSections(trailId, SLOPE_WINDOW_METERS);
    slopeGeoJson.features.forEach((feature) => {
      const color = getEstimatedSlopeColor(feature.properties.estimatedSlopePercent);
      if (color == null) {
        // invalid/missing slope (not the same thing as a measured ~0% slope) -- skip this
        // Overlay Feature entirely; the Base Trail Layer already shows this geometry.
        return;
      }
      const path = feature.geometry.coordinates.map(([lng, lat]) => new kakao.maps.LatLng(lat, lng));
      const polyline = new kakao.maps.Polyline({
        path,
        strokeWeight: 5,
        strokeColor: color,
        strokeOpacity: 0.8,
        strokeStyle: 'solid',
      });
      polyline.setMap(targetMap);
      slopeOverlayPolylines.push(polyline);
    });
  } catch (error) {
    // SlopeSection API 실패는 경사 Overlay만 생략시킨다 -- 이미 그려진 Base Trail은 유지된다.
    console.error('SlopeSection API 로드 중 에러 발생 (Base Trail은 계속 표시됩니다):', error);
  }
}

  function drawBaseRoute(coordinates) {
    const linePath = coordinates.map(coord => new kakao.maps.LatLng(coord.lat, coord.lng));
  
    const basePolyline = new kakao.maps.Polyline({
      path: linePath,
      strokeWeight: 5,
      strokeColor: '#32CD32',
      strokeOpacity: 1,
      strokeStyle: 'solid'
    });
  
    basePolyline.setMap(map.value);
  }

function calculateDistance(index, coord, coordinates) {
  if (index === 0) return 0;

  const prevCoord = coordinates[index - 1];

  const R = 6371;
  const dLat = deg2rad(coord[1] - prevCoord[1]);
  const dLng = deg2rad(coord[0] - prevCoord[0]);

  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(deg2rad(prevCoord[1])) * Math.cos(deg2rad(coord[1])) *
    Math.sin(dLng / 2) * Math.sin(dLng / 2);

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

function deg2rad(deg) {
  return deg * (Math.PI / 180);
}

function calculateCumulativeDistances(coordinates) {
    let cumulativeDistances = [];
    let totalDistance = 0;
  
    for (let i = 0; i < coordinates.length; i++) {
      if (i > 0) {
        totalDistance += calculateDistance(i, coordinates[i], coordinates);
      }
      cumulativeDistances.push(totalDistance);
    }
  
    return cumulativeDistances;
  }

// 경사(slope) 계산은 더 이상 Frontend 책임이 아니다 -- 기존
// getColorBySlope()/calculateHaversineDistance()/calculateSlope()는 제거했다. 색상 매핑은
// src/api/slopeSection.js의 getEstimatedSlopeColor()가, 경사 계산 자체는 Backend
// SlopeSectionService가 담당한다.

function drawElevationChart(data) {
    const ctx = document.getElementById('elevationChart').getContext('2d');
  
    const distances = calculateCumulativeDistances(data.map(point => [point.lng, point.lat]));
    const totalDistance = distances[distances.length - 1];
    const scaleFactor = 645 / totalDistance;
  
    const scaledDistances = distances.map(distance => distance * scaleFactor);
    const elevations = data.map(point => point.elevation);
  
  const backgroundImagePlugin = {
      id: 'backgroundImagePlugin',
      beforeDraw: (chart) => {
        if (chart.config.options.backgroundImage) {
          const ctx = chart.ctx;
          const chartArea = chart.chartArea;
          const backgroundImage = new Image();
          backgroundImage.src = chart.config.options.backgroundImage;
  
          backgroundImage.onload = function () {
            ctx.save();
            ctx.globalAlpha = 0.4;
            ctx.drawImage(backgroundImage, chartArea.left, chartArea.top, chartArea.right - chartArea.left, chartArea.bottom - chartArea.top);
            ctx.restore();
          };
        }
      },
    };
    if(chartInstance){
      return 
    }
  
    chartInstance = new Chart(ctx, {
      type: 'line',
      data: {
        labels: scaledDistances,
        datasets: [
          {
            label: '고도 (m)',
            data: elevations,
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 2,
            fill: false,
          },
        ],
      },
      options: {
        backgroundImage: '/images/mountainbackground.png',
        scales: {
          x: {
            title: {
              display: true,
              text: '거리 (m)',
            },
            grid: {
              display : false,
            },
            ticks: {
              autoSkip: true,
              maxTicksLimit: 10,
              maxRotation: 0,
              minRotation: 0,
              callback: function(value) {
                return value.toFixed(2) + ' m';
              }
            }
          },
          y: {
            title: {
              display: true,
              text: '고도 (m)',
            },
            grid: {
              display : false,
            }
          },
        },
        interaction: {
          mode: 'index',
          intersect: false,
        },
        plugins: {
          tooltip: {
            enabled:false,
          },
          legend: {
            display: false,
          }
        },
        onClick: (event, elements) => {
          if (elements.length > 0) {
            const index = elements[0].index;
            highlightRouteOnMap(index);
          }
        },
        onHover: (event, elements) => {
          if (elements.length > 0) {
            const index = elements[0].index;
            highlightRouteOnMap(index);
          }
        },
      },
      plugins: [backgroundImagePlugin],
    });
  }

function highlightRouteOnMap(index, targetMap = map.value) {
    if (!targetMap) {
      console.error("지도 객체가 초기화되지 않았습니다. targetMap이 null입니다.");
      return;
    }
  
    if (index >= 0 && index < routeCoordinates.value.length) {
      const point = routeCoordinates.value[index];
  
      if (currentMarker.value) {
        currentMarker.value.setMap(null);
      }
  
      const imageSrc = '/images/running.png';
      const imagesSize = new kakao.maps.Size(35,45);
      const imageOption = { offset: new kakao.maps.Point(17,45) };
      const markerImage = new kakao.maps.MarkerImage(imageSrc,imagesSize,imageOption);
  
      currentMarker.value = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(point.lat, point.lng),
        image: markerImage,
      });
  
      currentMarker.value.setMap(targetMap);
      targetMap.panTo(new kakao.maps.LatLng(point.lat, point.lng));
    }
  }

const dailyWeather = ref([]);
const sunriseTimes = ref([]);
const sunsetTimes = ref([]);
const selectedDateIndex = ref(0);

const selectedDay = computed(() => dailyWeather.value[selectedDateIndex.value]);

const selectDate = (index) => {
  selectedDateIndex.value = index;
};


const fetchWeatherData = async (latitude, longitude) => {
  try {
    const apiKey = "5c48577c775896e979e7bcc3b225b730";
    const response = await axios.get(
      `https://api.openweathermap.org/data/2.5/forecast?lat=${latitude}&lon=${longitude}&appid=${apiKey}`
    );

    console.log(response.data);

    const groupedByDay = {};
    response.data.list.forEach(item => {
      const date = item.dt_txt.split(" ")[0];
      if (!groupedByDay[date]) groupedByDay[date] = [];
      groupedByDay[date].push(item);
    });

    const selectedWeather = [];
    Object.keys(groupedByDay).forEach(date => {
      const { currentTemp, minTemp, maxTemp } = calculateMinMaxTemp(groupedByDay[date]);
      const daySummary = {
        date,
        currentTemp,
        minTemp,
        maxTemp,
        ...groupedByDay[date][0],
      };
      selectedWeather.push(daySummary);
    });

    dailyWeather.value = selectedWeather;

    const sunriseSunsetPromises = dailyWeather.value.map(day =>
      axios.get(
        `https://api.sunrise-sunset.org/json?lat=${latitude}&lng=${longitude}&date=${day.date}&formatted=0`
      )
    );

    const sunriseSunsetResults = await Promise.all(sunriseSunsetPromises);
    sunriseTimes.value = sunriseSunsetResults.map(result => formatTime(result.data.results.sunrise));
    sunsetTimes.value = sunriseSunsetResults.map(result => formatTime(result.data.results.sunset));

  } catch (error) {
    console.error("Error fetching weather data:", error);
  }
};

const formatDayOfWeek = (date) => {
  const formattedDate = new Date(date);
  return formattedDate.toLocaleDateString("ko-KR", { weekday: "short" });
};

const formatDate = (date) => {
  const formattedDate = new Date(date);
  return `${formattedDate.getDate()}`;
};

const getWeatherEmoji = (weatherMain) => {
  const weatherIcons = {
    Clear: "☀️",
    Clouds: "☁️",
    Rain: "🌧️",
    Snow: "🌨️",
    Fog: "🌁",
    Thunderstorm: "⛈️",
    Drizzle: "🌦️",
    Mist: "🌫️",
    breeze: "༄",
    "Few Clouds": "🌥️",
    "freezing rain": "🌧️➜❄️",
  };
  return weatherIcons[weatherMain] || "🌥️";
};

const translateWeatherDescription = (weatherId) => {
  const weatherDescriptions = {
    200: "가벼운 비를 동반한 천둥구름",
    201: "비를 동반한 천둥구름",
    202: "폭우를 동반한 천둥구름",
    210: "약한 천둥구름",
    211: "천둥구름",
    212: "강한 천둥구름",
    221: "불규칙적 천둥구름",
    230: "약한 연무를 동반한 천둥구름",
    231: "연무를 동반한 천둥구름",
    232: "강한 안개비를 동반한 천둥구름",
    300: "가벼운 안개비",
    301: "안개비",
    302: "강한 안개비",
    310: "가벼운 적은비",
    311: "적은비",
    312: "강한 적은비",
    313: "소나기와 안개비",
    314: "강한 소나기와 안개비",
    321: "소나기",
    500: "약한 비",
    501: "중간 비",
    502: "강한 비",
    503: "매우 강한 비",
    504: "극심한 비",
    511: "우박",
    520: "약한 소나기 비",
    521: "소나기 비",
    522: "강한 소나기 비",
    531: "불규칙적 소나기 비",
    600: "가벼운 눈",
    601: "눈",
    602: "강한 눈",
    611: "진눈깨비",
    612: "소나기 진눈깨비",
    615: "약한 비와 눈",
    616: "비와 눈",
    620: "약한 소나기 눈",
    621: "소나기 눈",
    622: "강한 소나기 눈",
    701: "박무",
    711: "연기",
    721: "연무",
    731: "모래 먼지",
    741: "안개",
    751: "모래",
    761: "먼지",
    762: "화산재",
    771: "돌풍",
    781: "토네이도",
    800: "구름 한 점 없는 맑은 하늘",
    801: "약간의 구름이 낀 하늘",
    802: "드문드문 구름이 낀 하늘",
    803: "구름이 거의 없는 하늘",
    804: "구름으로 뒤덮인 흐린 하늘",
    900: "토네이도",
    901: "태풍",
    902: "허리케인",
    903: "한랭",
    904: "고온",
    905: "바람부는",
    906: "우박",
    951: "바람이 거의 없는",
    952: "약한 바람",
    953: "부드러운 바람",
    954: "중간 세기 바람",
    955: "신선한 바람",
    956: "센 바람",
    957: "돌풍에 가까운 센 바람",
    958: "돌풍",
    959: "심각한 돌풍",
    960: "폭풍",
    961: "강한 폭풍",
    962: "허리케인"
  };
  return weatherDescriptions[weatherId] || "알 수 없는 날씨";
};

const calculateMinMaxTemp = (dayData) => {
  const temps = dayData.map(item => item.main.temp);
  const currentTemp = dayData[0].main.temp;
  const minTemp = Math.min(...temps);
  const maxTemp = Math.max(...temps);
  return { currentTemp, minTemp, maxTemp };
};

const formatTime = (time) => {
  const date = new Date(time);
  return date.toLocaleTimeString("ko-KR", { hour: "2-digit", minute: "2-digit", timeZone: "Asia/Seoul" });
};


const router = useRouter();
const route = useRoute();
const goBack = () => router.back();

const showModal = ref(false);
const editModal = ref(false);
const deleteModal = ref(false);

const currentReview = ref({
  courseId: null,
  reviewId: null,
  userId: '',
  rating: 0,
  difficulty: '',
  reviewContent: ''
});
const selectedReviewId = ref(null);
const loggedInUserId = ref(localStorage.getItem('userId') || '');

const courseData2 = ref({});
const selectedCourse = ref('');
const rating = ref(0);
const difficulty = ref('');
const reviewContent = ref('');
const photos = ref([]);
const courses = ref([]);

const editReview = async (review) => {
  console.log("리뷰 수정 모달 열기:", review);
  currentReview.value = { ...review };
  await nextTick();
  editModal.value = true;
  console.log("currentReview 상태:", currentReview.value);
  console.log("editModal 상태:", editModal.value);
};


const confirmDelete = (reviewId) => {
  console.log("리뷰 삭제 요청됨, ID:", reviewId);
  if (reviewId && reviewId !== 0) {
    selectedReviewId.value = reviewId;
    console.log("선택된 리뷰 ID:", selectedReviewId.value);
    deleteModal.value = true;
  } else {
    console.warn("유효하지 않은 리뷰 ID입니다:", reviewId);
  }
};


const saveReview = async () => {
  console.log("리뷰 저장 실행, currentReview:", currentReview.value);
  if (!currentReview.value || !currentReview.value.reviewId) {
    console.error("유효하지 않은 리뷰 ID");
    return;
  }
  try {
    const reviewData = {
      reviewId: currentReview.value.reviewId,
      userId: loggedInUserId.value,
      rating: currentReview.value.rating,
      difficulty: currentReview.value.difficulty,
      reviewContent: currentReview.value.reviewContent,
    };

    await axios.put(`/api/reviews/edit/${currentReview.value.reviewId}`, reviewData);

    alert('리뷰가 성공적으로 수정되었습니다.');
    editModal.value = false;
    fetchReviewsAndRatings();
  } catch (error) {
    console.error('리뷰 수정 오류:', error);
    alert("리뷰 수정에 실패했습니다.");
  }
};



const deleteReview = async () => {
  try {
    await axios.delete(`/api/reviews/delete/${selectedReviewId.value}`, {
      params: { userId: localStorage.getItem('userId') },
    });
    alert('리뷰가 삭제되었습니다.');
    deleteModal.value = false;
    fetchReviewsAndRatings();
  } catch (error) {
    console.error('리뷰 삭제 오류:', error);
  }
};

try {
  courseData.value = route.query.course ? JSON.parse(route.query.course) : {};
} catch (e) {
  console.error("Failed to parse course data:", e);
  courseData.value = {};
}

console.log("Loaded course data:", courseData.value);


const difficultyMapping = {
  "쉬움": 1,
  "보통": 2,
  "어려움": 3,
};

const averageDifficultyText = computed(() => {
  if (reviews.value.length === 0) return "정보 없음";
  const totalDifficulty = reviews.value.reduce((sum, review) => {
    return sum + (difficultyMapping[review.difficulty] || 0);
  }, 0);
  const averageDifficulty = totalDifficulty / reviews.value.length;

  if (averageDifficulty <= 1.5) {
    return "쉬움";
  } else if (averageDifficulty <= 2.5) {
    return "보통";
  } else {
    return "어려움";
  }
});


const courseId = computed(() => courseData.value.courseId);

const averageRating = ref(0);
const reviewSummary = ref('');
const reviews = ref([]);
const ratingDistribution = ref({});
const reviewCount = ref(0);
const searchQuery = ref('');
const reviewsToShow = ref(3);

const setRating = (star) => {
  rating.value = star;
};

const onFileChange = (event) => {
  photos.value = Array.from(event.target.files);
};

const submitReview = async () => {
  if (!validateForm()) {
    return;
  }

  const review = {
    courseId: selectedCourse.value,
    userId: localStorage.getItem('userId'),
    rating: rating.value,
    difficulty: difficulty.value,
    reviewContent: reviewContent.value,
  };

  const formData = new FormData();
  formData.append('review', new Blob([JSON.stringify(review)], { type: 'application/json' }));

  photos.value.forEach((photo, index) => {
    formData.append(`photos[${index}]`, photo);
  });

  try {
    await axios.post('/api/reviews/create', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    alert('리뷰가 성공적으로 작성되었습니다!');
    showModal.value = false;

    fetchReviewsAndRatings();
  } catch (error) {
    console.error('리뷰 작성 오류:', error);
  }
};


const validateForm = () => {
  if (!selectedCourse.value) {
    alert('코스를 선택하세요.');
    return false;
  }
  if (rating.value === 0) {
    alert('별점을 선택하세요.');
    return false;
  }
  if (!difficulty.value) {
    alert('난이도를 선택하세요.');
    return false;
  }
  if (!reviewContent.value) {
    alert('리뷰 내용을 입력하세요.');
    return false;
  }
  return true;
};

const fetchReviewsAndRatings = async () => {
  if (!courseId.value) return;
  try {
    const response = await axios.get(`/api/course/${courseId.value}/reviews`);
    console.log("로드된 리뷰 데이터:", response.data.reviews);
    reviews.value = response.data.reviews.map(review => ({
      ...review,
      reviewId: review.reviewId || review.id || null
    }));

    ratingDistribution.value = response.data.ratingDistribution || {};
    reviewCount.value = response.data.reviewCount || 0;

    if (response.data.averageRating) {
      averageRating.value = response.data.averageRating.toFixed(1);
    }
  } catch (error) {
    console.error('리뷰 데이터를 가져오는 데 실패했습니다:', error);
  }
};



const fetchSummary = async () => {
  if (!courseId.value) return;
  try {
    const response = await axios.get(`/api/course/${courseId.value}/summarize`);
    reviewSummary.value = response.data.summary || '요약된 내용 없음';
  } catch (error) {
    console.error('리뷰 요약 데이터를 가져오는 데 실패했습니다:', error);
  }
};

const fullStars = computed(() => Math.floor(averageRating.value));
const halfStar = computed(() => averageRating.value % 1 >= 0.5);
const emptyStars = computed(() => 5 - fullStars.value - (halfStar.value ? 1 : 0));

const filteredReviews = computed(() => {
  return reviews.value.map(review => ({
    ...review,
    rating: review.rating ?? 0,
  })).filter(review => {
    const reviewContent = review?.reviewContent || '';
    const userId = review?.userId || '';
    return reviewContent.toLowerCase().includes(searchQuery.value.toLowerCase()) || userId.toLowerCase().includes(searchQuery.value.toLowerCase());
  });
});


const visibleReviews = computed(() => {
  return (reviews.value || [])
    .map(review => ({
      ...review,
      rating: review?.rating ?? 0,
      difficulty: review?.difficulty ?? '정보 없음',
      reviewContent: review?.reviewContent ?? '내용 없음'
    }))
    .filter(review => {
      const reviewContent = review?.reviewContent || '';
      const userId = review?.userId || '';
      return reviewContent.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
        userId.toLowerCase().includes(searchQuery.value.toLowerCase());
    })
    .slice(0, reviewsToShow.value);
});

const loadMoreReviews = () => {
  if (reviewsToShow.value < filteredReviews.value.length) {
    reviewsToShow.value += 3;
  }
};

const canShowMore = computed(() => {
  return reviewsToShow.value < filteredReviews.value.length;
});

function goToCourse3DPage() {
    router.push({ name: '3d', query: { course: JSON.stringify(courseData.value) }});
  }


</script>

<style scoped>
*{
  overflow: hidden;
}

  .btn-group-vertical {
    position: absolute;
    font-size: 0.5rem !important;
    top: 100px;
    right: 0;
    z-index: 2;
    width: 85px;
    margin-right: 2rem;
    margin-top: 3rem;
  }

.dashboard {
  display: grid;
  background-color: white;
  grid-template-columns: 410px 1fr;
  grid-template-rows: auto 1fr auto;
  height: 100vh;
  gap: 10px;
  overflow: hidden;
}

.sidebar {
  background-color: white;
  justify-content: center;
  width: 410px;
  max-height: 100vh;
  overflow: auto;
  overflow-x: hidden;
}

.main {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.map-container {
  height: 60vh;
  width: 100%;
  border-radius: 10px;
}

#grapth {
  padding: 10px;
  background-color: #f1f1f1;
  width: 100%;
}

.chart1-section {
  text-align: center;
}


.weather-forecast {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 100%;
  margin-top: 5rem;
  margin-bottom: 3rem;
  }

.date-selector {
  display: flex;
  justify-content: space-around;
  width: 100%;
  margin-bottom: 10px;
}

.date-button-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .day-label {
    font-family: 'TheJamsil4Bold';
    font-size: 12px;
    color: #333;
    margin-bottom: 5px;
  }

  .date-button {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    background-color: #ffffff;
    border: none;
    font-size: 14px;
    font-family: 'TheJamsil4Bold';
    transition: background-color 0.3s;
  }

.forecast-header {
  text-align: center;
  margin-bottom: 20px;
}

.forecast-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 5px;
  width: 100%;
}

.forecast-item {
  text-align: center;
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 10px;
  background-color: #f7f7f7;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
}

.forecast-info {
  margin-top: 10px;
}

.current-temperature {
    font-family: 'TheJamsil4Bold';
    font-weight: 500;
    font-size: 70px;
    font-weight: bold;
    color: #333;
  }

  .weather-icon {
    font-size: 50px;
    margin-top: 1rem;
  }

  .description {
    font-family: 'TheJamsil4Bold';
  font-size: 14px;
  font-weight: 400;
  color: #333;
  text-align: left;
  width: 180px;
  margin-left: 0.5rem;
  }

  .temperature-range {
  display: flex;
  margin-left: 0.5rem;
  margin-top: -0.6rem;
  }

  .min-temp{
    font-family: 'TheJamsil4Bold';
    width: 80px;
    height: 50px;
    font-weight: 300;
    font-size: 14px;
    color:rgb(80, 80, 80);
    margin-right: -1rem;

  }

  .max-temp {
    font-family: 'TheJamsil4Bold';
    width: 80px;
    height: 50px;
    font-weight: 300;
    font-size: 14px;
    color:rgb(80, 80, 80);
  }

  .sun-info{
    font-family: 'TheJamsil3Bold';
  font-weight: 400;
  font-size: 16px;
  color: rgb(130, 130, 130);
  margin-top: 0.5rem;
  text-align: center;
  }

  .icon-small {
  width: 15px;
  height: 15px;
  margin-right: 5px;
  opacity: 0.7;
}

  .rain-info {
    display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.4rem;
  }

  .rain-icon {
  width: 20px;
  height: 20px;
  background-color: rgba(173, 216, 230, 0.5);
  border-radius: 50%;
  position: relative;
  overflow: hidden;
  }

  .rain-icon::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: var(--rain-level, 0);
  background-color: rgb(78, 130, 182);
  }

  .rain-chance {
    font-family: 'TheJamsil5Bold';
    font-weight: 400;
    font-size: 14px;
  }

  #sunrise, #sunset {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: rgb(80, 80, 80);
  margin: 0.5rem 0;
}

    .date-button.active {
    background-color: #415d37;
    color: #fff;
  }

.course-preview-page {
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  gap: 10px;
}
.back-button {
  background-color: #ffffff;
  color: #a52d2d;
  border: none;
  padding: 8px;
  width: 50px;
  height: 50px;
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin-bottom: 10px;
}
.review-button {
  background-color: #28a745;
  color: white;
  border: none;
  font-weight: bold;
  border-radius: 50%;
  cursor: pointer;
  width: 40px;
  height: 40px;
  transition: background-color 0.3s ease;
  box-shadow: 0 3px 7px rgba(0, 0, 0, 0.5);
  margin-top: 2rem;
  margin-left: 19.9rem;
}
.review-button:hover {
  background-color: #36672f;
}
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex !important;
  visibility: visible !important;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.review-form {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  position: relative;
  text-align: center;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #333;
}

.close-button:hover {
  color: #999;
}

.modal-label {
  font-size: 1rem;
  font-weight: bold;
  margin-top: 15px;
  display: block;
  text-align: left;
  color: #444;
}

.input-field {
  width: 100%;
  padding: 10px;
  margin-top: 10px;
  border-radius: 5px;
  border: 1px solid #ddd;
  font-size: 14px;
}

.textarea {
  height: 100px;
  resize: none;
}

.file-input {
  width: 100%;
  margin-top: 10px;
  padding: 10px;
  font-size: 14px;
}

.rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 10px;
}

.stars {
  display: flex;
  gap: 5px;
  color: #ffc107;
}

.n-star {
  font-size: 1.5rem;
  cursor: pointer;
}

.submit-button {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 15px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 25px;
  cursor: pointer;
  width: 100%;
  margin-top: 20px;
  transition: background-color 0.3s ease;
}

.submit-button:hover {
  background-color: #218838;
}

.load-more-button {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: bold;
  border-radius: 25px;
  cursor: pointer;
  display: block;
  margin: 20px auto;
  transition: background-color 0.3s ease;
}
.load-more-button:hover {
  background-color: #218838;
}
.course-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
  text-align: center;
}
.course-image {
  width: 100%;
  max-width: 600px;
  height: auto;
  object-fit: cover;
}
.course-details {
  font-family: 'TheJamsil4Bold';
  font-size: 15px;
  font-weight:300;
  color: #555;
  padding-top: 15px;
  line-height: 1.7;
  margin-top: 0.5rem;
  margin: 0 1.5rem;
}

.accident-summary {
  text-align: center;
  background: #f9f9f9;
  padding: 20px;
  margin-bottom: 20px;
}

#reviewTitle{
  font-family: 'TheJamsil4Bold';
  font-size: 20px;
  font-weight: 700;
}

.rating-number {
  font-size: 2rem;
  color: #4caf50;
}

.review-page {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 10px;
  
}

.review-search input {
  width: 100%;
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #ddd;
}

.reviews-list {
  margin-top: 20px;
}

.review-item {
position: relative;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.review-header {
  display: flex;
  gap: 10px;
  align-items: center;
}

.profile-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.user-info h3 {
  font-size: 1rem;
  margin: 0;
}

.stars-inline i {
  color: #4caf50;
}

.review-photos img {
  width: 100%;
  height: auto;
  max-height: 200px;
  margin-top: 10px;
  border-radius: 5px;
  object-fit: cover;
}

.summary-section {
  background-color: #ffffff;
  padding: 20px;
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 20px;
}

.rating h1 {
  font-size: 3rem;
  color: #4caf50;
  margin-bottom: 10px;
}

.stars {
  display: flex;
  gap: 5px;
  color: #4caf50;
}

.review-count-text {
  font-size: 1rem;
  color: #666;
  margin-top: 10px;
}

.rating-breakdown {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-bar span {
  width: 30px;
  text-align: right;
  font-weight: bold;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background-color: #ddd;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.progress {
  height: 100%;
  background-color: #4caf50;
  border-radius: 4px;
}
.image-container {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}


.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: flex-start;
  padding: 10px;
  background: rgba(0, 0, 0, 0.1);

}

#courseTitle{
  font-family: 'TheJamsil4Bold';
  font-size: 13px;
  font-weight: 400;
  margin-top: 0.4rem;
  margin-left: 0.3rem;
}

.image-course-name{
    font-family: 'TheJamsil';
    font-weight: 500;
    color: white;
    font-size: 30px;
    margin: 5px 3px -6px;
}


.image-course-location{
  color: white;
  font-family: 'TheJamsil4Bold';
  font-size: 13px;
  font-weight: 400;
  margin: 5px 3px -5px;
}

.difficulty-text{
  color: white;
  font-family: 'TheJamsil4Bold';
  font-size: 13px;
  font-weight: 400;
  margin: 5px 3px 1px;
}
.image-rating-star {
 color: #50df4b;
}
.n-star{
    color: #4caf50; 
}
.edit-button {
  position: absolute;
  right: 10px;
  bottom: 10px;
  background: none;
  border: none;
  color: #4caf50;
  cursor: pointer;
  font-size: 0.9rem;
}

.delete-button {
  position: absolute;
  right: 10px;
  top: 10px;
  background: none;
  border: none;
  color: #4caf50;
  cursor: pointer;
  font-size: 0.9rem;
}

.delete-confirm-modal {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 300px;
  text-align: center;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
}

.confirm-button {
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 10px 20px;
  margin-right: 10px;
  cursor: pointer;
}

.cancel-button {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
}

.review-content{
  margin-top: 0.5rem;
  font-family: 'TheJamsil4Bold';
  font-size: 15px;
  font-weight:300;
  line-height:1.7;
}

.average-rating-text{
  font-family: 'TheJamsil5Bold';
  font-size: 50px;
  font-weight:700;
  color: #4caf50;
}

.carousel-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-direction: column;
}

.card-carousel::-webkit-scrollbar {
  display: none;
}

.card-carousel {
  display: row;
  gap: 1.4rem;
  overflow-x: auto;
  overflow-y: hidden;
  scroll-behavior: smooth;
  width: 100%;
}

.card {
  position: relative;
  width:353px;
  height: 70px;
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  flex-shrink: 0;
  margin-top: 1rem;
}

.card-img-top {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border: none;
  margin-right: 15px;
  margin-left: 10px;
}

.card-overlay {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
}

.accident-item-content1 {
  display: flex;
  align-items: center;
  font-family: 'TheJamsil5Bold', sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-left: 2rem;
  gap: 10px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.5px;
  transition: color 0.3s ease, transform 0.3s ease;
}

.accident-item-content1:hover {
  color: #4CAF50;
  transform: scale(1.02);
}

.toggle-switch-container2 {
  position: fixed;
  z-index: 10;
  display: flex;
  justify-content: start;
  gap: 10px;
  margin-top: 3rem;
  margin-left: 5rem;
  width: 50%;
  height: 50px;
}

.toggle-switch2 {
  width: 100px;
  height: 45px;
  background-color: #f5f5f5;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-family: 'TheJamsil4Bold';
  font-size: 16px;
  font-weight: 700;
  color: #555;
  transition: all 0.3s ease;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  border: 1px solid #ddd;
}

.toggle-switch2 img {
  width: 40px;
  height: auto;
  margin-right: 8px;
  transition: transform 0.3s ease;
}

.toggle-switch2:hover {
  background-color: #e0e0e0;
  color: #333;
}

.toggle-switch2.active {
  background-color: #4CAF50;
  color: #ffffff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.toggle-switch2.active img {
  transform: scale(1.1);
}

.toggle-switch-container2 {
  flex-wrap: wrap;
  gap: 15px;
}

  .forecast-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  width: 100%;
  padding: 10px;
}

  #forecast1{
    width: 60%;
  }

  .rainSun-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 40%;
  text-align: center;
  margin-top: 0.5rem;
  margin-left: 3rem;
}

.DangerImage {
  display: flex;
  gap: 5px;
}

#img-storage {
  display: flex;
  align-items: center;
  justify-content: center;
}

#DangerImage1,
#DangerImage2,
#DangerImage3 {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 5px;
}

.button-group {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
}

.btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  width: 100px;
  height: 120px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.btn img {
  width: 50px;
  height: 50px;
  margin-bottom: 10px;
  object-fit: contain;
}

.btn p {
  margin: 0;
  font-size: 12px;
}

.btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
  color:white;
  background-color:#218838
}
.custom-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.custom-btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.image-grid {
  display: flex;
  flex-wrap: nowrap; 
  gap: 10px;
  overflow-x: auto;
  overflow: hidden;
  padding: 5px 0;
  justify-content: start;
}

.image-item {
  width: 60px;
  height: 60px;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.image-item:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.clickable-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  border-radius: 8px;
}

.map-legend {
    position: absolute;
    top: 0;
    right: 0;
    margin-top: 35.5em;
    margin-right: 2em;
    background-color: #faf7f0;
    padding: 5px;
    border-radius: 10px;
    font-size: 12px;
    z-index: 10;
  }

  .legend-item {
    display: flex;
    align-items: center;
    margin-bottom: 2px;
  }

  .legend-icon {
    width: 10px;
    height: 10px;
    margin-right: 5px;
    margin-left: 5px;
    border-radius: 10px;
  }

.modal-dialog {
  max-width: 1400px;
}

.modal-content {
  background: #f9f9f9;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.modal-header {
  background: #28a745;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 50px;
}

.modal-header .modal-title {
  font-size: 2rem;
  font-weight: bold;
  color: white;
  margin: 0;
}

.modal-header .btn {
  width: 30px;
  height: 30px;
  color: white;
  background: transparent;
  border: none;
}

.modal-header .btn:hover {
  background: rgba(0, 0, 0, 0.1);
}

.modal-header .btn i {
  font-size: 2rem;
  color: white; 
}

.modal-body {
  padding: 20px;
  background: #ffffff;
  border-radius: 16px;
}

.video-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

#modalMap {
  width: 60%;
  height: 700px;
  border-radius: 8px;
  border: 1px solid #ddd;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.video-container video {
  width: 38%;
  height: 700px;
  border: 1px solid #ddd;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>
