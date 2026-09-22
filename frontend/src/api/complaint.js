import { apiInstance } from "./index.js";
const api = apiInstance();
import router from "@/router/index.js";
import axios from 'axios';

export async function getComplaintList() {
  try { 
    const response = await axios.get('/api/complaint/list');
    console.log("you can use getComplaintList", response.data);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function getRecentComplaintList() {
  try { 
    const response = await axios.get('/api/complaint/listRecent');
    console.log("you can use getRecentComplaintList", response.data);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function getComplaintByUserId(userId) {
  try { 
    const response = await axios.get(`/api/complaint/mine/${userId}`);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function getComplaintListByInst(institution) {
  try { 
    const response = await axios.get(`/api/complaint/myList/${institution}`);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function getPathList() {
  try { 
    const response = await axios.get('/api/pathList');
    console.log("you can use getPathList", response.data);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function insertComplaint(data) {
  try {
    const response = await axios.post('/api/complaint/insert', data);
    console.log('응답 상태 코드:', response.status);
    console.log('민원글 작성에 대한 응답 데이터:', response.data); 
    return response.data;
  } catch (err) {
    console.error('API 요청 중 오류 발생:', err);
    throw err;
  }
}

export async function getComplaintByNo(complaintNo) {
  try {
    const response = await axios.get(`/api/complaint/one/${complaintNo}`);
    console.log("API response:", response);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function getUserById(userId) {
  try {
    const response = await axios.get(`/api/userInfo/${userId}`);
    console.log("API response:", response);
    return response.data;
  } catch (err) {
    console.error(err);
  }
  
}

export async function getCourseById(courseId) {
  try {
    const response = await axios.get(`/api/course/${courseId}`);
    console.log("API response:", response);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}


export async function deleteComplaintByNo(complaintNo) {
  try {
    const response = await axios.delete(`/api/complaint/delete/${complaintNo}`);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function updateComplaintByNo(complaintNo, data) {
  try {
    const response = await axios.patch(`/api/complaint/update/${complaintNo}`, data);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function getUserPostList(userNo) {
  try { 
    const response = await api.get(`/auth/userpostlist/${userNo}`);
    console.log(userNo);
    console.log("you can use getUserPostList", response.data);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function insertPostAndKeyword(postData, keywordData) {
  const formData = new FormData();
  formData.append('post', new Blob([JSON.stringify(postData)], { type: 'application/json' }));
  formData.append('postkeyword', new Blob([JSON.stringify(keywordData)], { type: 'application/json' }));

  try {
    const response = await api.post('/auth/insert', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  } catch (err) {
    console.error(err);
    if (err.response && err.response.status === 401) {
      alert("로그인 후 이용하실 수 있습니다.")
      router.push({ path: '/login' });
    }
    throw err;
  }
} 

export async function saveImage(formData) {
  try {
    const response = await api.post('/auth/upload-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    console.log('서버 응답:', response.data);
    return response.data;
  } catch (err) {
    console.error('이미지 업로드 중 오류 발생:', err);
  }
}

export async function getProcessingByNo(processingComplaintNo) {
  try {
    const response = await axios.get(`/api/complaint/processing/${processingComplaintNo}`);
    console.log("API response:", response);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}

export async function insertProcessing(data) {
  try {
    const response = await axios.post('/api/complaint/insertProcessing', data);
    console.log('응답 상태 코드:', response.status);
    console.log('민원처리정보 작성에 대한 응답 데이터:', response.data); 
    return response.data;
  } catch (err) {
    console.error('API 요청 중 오류 발생:', err);
    throw err;
  }
}

export async function updateProcessingByNo(processingComplaintNo, data) {
  try {
    const response = await axios.patch(`/api/complaint/updateProcessing/${processingComplaintNo}`, data);
    return response.data;
  } catch (err) {
    console.error(err);
  }
}