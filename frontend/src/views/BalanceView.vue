<template>
    <div id="app">
        <MobileHeaderView></MobileHeaderView>
        <div class="app-main">
            <button @click="goBack(courseData)" class="back-button"><img src="/images/뒤로가기.png" alt="돋보기 아이콘" width="25"
                height="25"></button>
            <div class="comparison-container mb-5">
									<div class="card">
										<div class="card-header border-0 pt-6">
											<div class="card-title">
												<div class="d-flex align-items-center position-relative my-1">
													<i class="ki-duotone ki-magnifier fs-3 position-absolute ms-5">
														<span class="path1"></span>
														<span class="path2"></span>
													</i>
													<input type="text" data-kt-user-table-filter="search" class="form-control form-control-solid w-250px ps-13" placeholder="등산코스 검색하기" />
												</div>
											</div>
                                            <div class="d-flex justify-content-end">
                                                <button class="btn btn-success btn-sm" @click="goToCompare()">비교하기</button>
                                            </div>
										</div>
										<div class="card-body py-4">
											<table class="table align-middle table-row-dashed fs-6 gy-5" id="kt_table_users">
												<thead>
													<tr class="text-start text-muted fw-bold fs-7 text-uppercase gs-0">
														<th class="w-10px pe-2">
															<div class="form-check form-check-sm form-check-custom form-check-solid me-3">
																<input class="form-check-input" type="checkbox" v-model="selectAll" @click="toggleSelectAll" />
															</div>
														</th>
														<th class="min-w-125px">등산코스</th>
													</tr>
												</thead>
												<tbody class="text-gray-600 fw-semibold">
													<tr v-for="(item, index) in courses" :key="item.courseId">
                                                        <td>
                                                            <div class="form-check form-check-sm form-check-custom form-check-solid">
                                                                <input class="form-check-input" type="checkbox" v-model="checkedItems[index]" @change="handleCheckboxChange(item.courseId, index)" />
                                                            </div>
                                                        </td>
                                                        <td class="d-flex align-items-center">
                                                            <div class="d-flex">
                                                                <span class="d-flex justify-content-start me-4">{{ item.mountainName }}</span>
                                                                <span>{{ item.courseName }}코스</span>
                                                            </div>
                                                        </td>
                                                    </tr>
												</tbody>
											</table>
										</div>
									</div>
            </div>
        </div>
        <MobileFooterView></MobileFooterView>
    </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import MobileFooterView from "@/components/MobileFooterView2.vue";
import MobileHeaderView from "@/components/MobileHeaderView.vue";
import axios from 'axios';
import { useRouter, useRoute } from 'vue-router';


const courses = ref([]);
const selectedCourseIds = ref([]);

const router = useRouter();
const route = useRoute();

const courseData = ref(JSON.parse(route.query.course));

const selectAll = ref(false);
const checkedItems = reactive([]);

onMounted(() => {
  fetchCourses();
});

const fetchCourses = async () => {
try {
  const response = await axios.get('/api/course');
  courses.value = response.data;
} catch (error) {
  console.error('산 목록을 불러오는 중 오류 발생:', error);
}
};

function goToCompare() {
    router.push({path: '/compareCourse'});
}

function toggleSelectAll() {
    if (selectAll.value) {
        courses.value.forEach((_, index) => {
            checkedItems[index] = false;
        });
        selectedCourseIds.value = [];
    }
    selectAll.value = false;
    updateSelectedCourseIds();
}

function handleCheckboxChange(courseId, index) {
    if (checkedItems[index]) {
        if (selectedCourseIds.value.length < 2) {
            selectedCourseIds.value.push(courseId);
        } else {
            checkedItems[index] = false;
            alert('최대 2개까지만 선택할 수 있습니다.');
            return;
        }
    } else {
        selectedCourseIds.value = selectedCourseIds.value.filter(id => id !== courseId);
    }

    selectAll.value = selectedCourseIds.value.length === 2;

    updateSelectedCourseIds();
}

function updateSelectedCourseIds() {
    sessionStorage.setItem('selectedCourseIds', JSON.stringify(selectedCourseIds.value));
}

const goBack = (course) => {
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
    height: 100vh;
    width: 100vw;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.app-main {
    flex: 1;
    display: flex;
    justify-content: center;
    padding: 1rem;
}

.comparison-container {
    width: 100%;
    max-width: 600px;
    text-align: center;
    overflow-y: auto;
    max-height: 80vh;
}

.comparison-container::-webkit-scrollbar,
.comparison-container::-webkit-scrollbar-thumb,
.comparison-container::-webkit-scrollbar-track {
    background-color: transparent;
    width: 8px;
}

.map-container {
    display: flex;
    justify-content: space-around;
    margin-bottom: 1rem;
    height: 400px;
    align-items: center;
}

.map {
    width: 45%;
    height: 400px;
    background-color: #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid #ccc;
}

.map-card {
    position: relative;
    width: 48%;
    height: 400px;
    background-color: #f0f0f0;
    display: flex;
    flex-direction: column;
    align-items: center;
    border: 1px solid #ccc;
    box-sizing: border-box;
    overflow: hidden;
}

.map-card-title {
    width: 100%;
    text-align: center;
    background-color: rgba(17, 134, 13, 0.384);
    color: white;
    padding: 4px 8px;
    font-size: 14px;
    font-weight: bold;
}

.map-card img {
    max-width: 100%;
    height: auto;
    object-fit: cover;
    flex: 1;
}

.comparison-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 1rem;
}

.comparison-table th,
.comparison-table td {
    border: 1px solid #ddd;
    padding: 0.5rem;
    text-align: center;
}

.comparison-table th {
    background-color: #f5f5f5;
    font-weight: bold;
}

.comparison-table td:first-child {
    font-weight: bold;
    background-color: #fafafa;
}

.back-button {
  position: absolute;
  top: 140px;
  left: 30px;
  background-color: transparent;
  color: rgb(12, 12, 12);
  border: none;
  padding: 6px;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  z-index: 10;
  cursor: pointer;
}
</style>
