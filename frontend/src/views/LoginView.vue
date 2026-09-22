<template>
  <MobileHeaderView class="header" />
    <div id="form-container" :class="isApp ? 'app' : 'web'">
      <div id="form-inner-container">
        <div id="sign-up-container">
          <h3 style="font-size: 30px;"><img src="/images/icon2.png" style="width: 45px; height: auto;">안전등산</h3>
          <div>
            <label for="id" style="font-weight: bold;">아이디</label>
            <input type="text" v-model="loginId" id="id" name="id" placeholder="아이디">
  
            <label for="password" style="font-weight: bold;">비밀번호</label>
            <input type="password" v-model="loginPw" name="password" id="password" placeholder="&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;">
  
            <div id="form-controls">
              <button type="mainButton" id="toggleSignIn" @click="requestLogin">로그인</button>
            </div>
            <div>
              <span style="font-size: 14px; border-bottom: solid 1px gray; width: 12px;">안전등산 회원이 아니신가요? </span>
              <button class="link-button" @click="moveNewLoginPage"> 회원가입하기</button>
            </div>
            <div id="form-controls2">
              <button type="naverButton">
                <img src="/images/naver.png" alt="네이버 아이콘" style="width: 24px; height: auto;">
                네이버 로그인
              </button>
            </div>

            <div id="form-controls2">
              <button type="googleButton">
                <img src="/images/google.png" alt="구글 아이콘">
                Google 로그인
              </button>
            </div>

            <div id="form-controls2">
              <button type="kakaoButton">
                <img src="/images/kakao.png" alt="카카오 아이콘" style="width: 24px; height: auto;">
                카카오 로그인
              </button>
            </div>

            <input type="checkbox" name="terms" id="terms" style="display: none;">
          </div>
        </div>
      </div>
    </div>
    
    <MobileFooterView></MobileFooterView>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import axios from 'axios';
  import MobileHeaderView from '@/components/MobileHeaderView.vue';
  import MobileFooterView from '@/components/MobileFooterView.vue';
  
  let loginId = ref('');
  let loginPw = ref('');
  
  const isApp = window.navigator.userAgent.includes('Android') || window.navigator.userAgent.includes('iPhone');
  
  function moveNewLoginPage() {
    window.location.href = '/signup';
  }
  
  const requestLogin = async () => {
    console.log("로그인 요청: ", loginId.value, loginPw.value);

    if (!loginId.value || loginId.value.trim() === '') {
      alert("아이디를 입력해주세요.");
      return;
    }
    if (!loginPw.value || loginPw.value.trim() === '') {
      alert("비밀번호를 입력해주세요.");
      return;
    }

    try {
      const response = await axios.post('/api/login', {
        userId: loginId.value,
        userPw: loginPw.value,
      }, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      console.log(response.data);

      localStorage.setItem('userNickName', response.data.userNickName);
      localStorage.setItem('userGender', response.data.userGender);
      localStorage.setItem('userId', response.data.userId);
      localStorage.setItem('userInstitution', response.data.userInstitution);

      if (isApp && window.Android && window.Android.sendUserIdToAndroid) {
        window.Android.sendUserIdToAndroid(response.data.userId);
        console.log("Android WebView로 userId 전송: ", response.data.userId);
      }

      window.location.href = '/mobilemainview';

      if (!response.data) {
        alert('아이디 또는 비밀번호가 다릅니다.');
      }
    } catch (err) {
      alert('아이디 또는 비밀번호가 일치하지 않습니다.');
      console.log(err);
    }
  };

</script>

<style>
#form-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  box-sizing: border-box;
}

#form-inner-container {
  background-color: white;
  width: 100%;
  max-width: 350px;
  padding: 30px 20px;
  border-radius: 10px;
  text-align: center;
  box-sizing: border-box;
}

#sign-up-container h3 {
  color: #327C2B;
  font-weight: bold;
  font-size: xx-large;
  margin-bottom: 20px;
}

#sign-up-container label {
  color: #666666;
  font-size: 14px;
  display: block;
  text-align: left;
  margin-left: 5%;
}

#sign-up-container input {
  width: 90%;
  margin: 8px;
  padding: 12px;
  border: 1px solid #dadada;
  background-color: #ffffff;
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
}

#sign-up-container input:focus {
  border-color: #327C2B;
  outline: none;
}

#form-controls {
  margin-top: 20px;
}

#form-controls button[type="mainButton"] {
  width: 90%;
  padding: 12px;
  background-color: #327C2B;
  color: white;
  font-size: 16px;
  font-weight: bold;
  border: none;
  cursor: pointer;
}

#form-controls2 {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

#form-controls2 button[type="naverButton"] {
  width: 90%;
  padding: 12px;
  background-color: #03c75a;
  color: white;
  font-size: 16px;
  font-weight: bold;
  border: none;
  cursor: pointer;
  position: relative;
  text-align: center;
}

#form-controls2 button[type="naverButton"]:hover {
  background-color: #02b753;
}

#form-controls2 button[type="googleButton"] {
  width: 90%;
  padding: 12px;
  background-color: white;
  color: black;
  font-size: 16px;
  font-weight: bold;
  border: 1px solid #dadada;
  cursor: pointer;
  position: relative;
  text-align: center;
}

#form-controls2 button[type="googleButton"]:hover {
  background-color: #f5f5f5;
}

#form-controls2 button[type="kakaoButton"] {
  width: 90%;
  padding: 12px;
  background-color: #fee500;
  color: black;
  font-size: 16px;
  font-weight: bold;
  border: none;
  cursor: pointer;
  position: relative;
  text-align: center;
}

#form-controls2 button[type="kakaoButton"]:hover {
  background-color: #ffd700;
}

#form-controls2 img {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
}


.link-button {
  display: inline-block;
  font-size: 14px;
  font-weight: bold;
  color: #327C2B;
  text-decoration: none;
  border: none;
  background: none;
  cursor: pointer;
  position: relative;
  margin-top: 20px;
}

.link-button:hover {
  text-decoration: underline;
}


@media (max-width: 480px) {
  #form-container {
    padding: 10px 0;
  }

  #form-inner-container {
    width: 95%;
    padding: 20px;
  }

  #sign-up-container h3 {
    font-size: 18px;
  }

  #form-controls button {
    font-size: 14px;
  }
}
</style>