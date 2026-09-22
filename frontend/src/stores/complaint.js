import { ref } from "vue";
import { defineStore } from "pinia";
import { getComplaintList, getUserPostList, getComplaintListByInst, getComplaintByNo, getRecentComplaintList, getPathList, getComplaintByUserId } from '@/api/complaint';

export const useComplaintListStore = defineStore('complaintlist', () => {
  const complaintlist = ref([]);
  async function fetchComplaintList() {
    console.log("Fetching complaintlist ===");
    complaintlist.value = await getComplaintList();
    console.log("postlist--",complaintlist.value);
  }

  return { complaintlist, fetchComplaintList }
})

export const useRecentComplaintListStore = defineStore('recentcomplaintlist', () => {
  const recentcomplaintlist = ref([]);
  async function fetchRecentComplaintList() {
    recentcomplaintlist.value = await getRecentComplaintList();
  }

  return { recentcomplaintlist, fetchRecentComplaintList }
})

export const useMyComplaintListStore = defineStore('mycomplaintlist', () => {
  const mycomplaintlist = ref([]);
  async function fetchMyComplaintList(userId) {
    mycomplaintlist.value = await getComplaintByUserId(userId);
  }

  return { mycomplaintlist, fetchMyComplaintList }
})

export const useManagingComplaintListStore = defineStore('managingcomplaintlist', () => {
  const managingcomplaintlist = ref([]);
  async function fetchManagingComplaintList(institution) {
    managingcomplaintlist.value = await getComplaintListByInst(institution);
    console.log("담당업무--",managingcomplaintlist.value);
  }

  return { managingcomplaintlist, fetchManagingComplaintList }
})

export const usePathList = defineStore('pathList', () => {
  const pathList = ref([]);
  async function fetchPathList() {
    pathList.value = await getPathList();
  }

  return { pathList, fetchPathList }
})

export const useComplaintStore = defineStore('complaintByNo', () => {
  const complaintone = ref({});
  async function fetchComplaintone(postNo) {
    try {
      complaintone.value = await getComplaintByNo(postNo);
    } catch (err) {
      console.error('Error fetching post:', err);
    }
  }
  return { complaintone, fetchComplaintone };
});

export const useUserPostListStore = defineStore('userpostlist', () => {
  const userpostlist = ref([]);
  async function fetchUserPost() {
    const userNo = sessionStorage.getItem('userNo');
    console.log(userNo);
    if (!userNo) {
      console.error('userNo is undefined or null');
      return;
    }
    try {
      console.log("userpostlist -- 유저 포스트 리스트");
      userpostlist.value = await getUserPostList(userNo);
      console.log("userpostlist--", userpostlist.value);
    } catch (err) {
      console.error('Error fetching userpostlist:', err);
    }
  }

  return { userpostlist, fetchUserPost }
})
