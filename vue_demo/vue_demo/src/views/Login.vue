<template>
  <div class="login-container">
    <div class="note">
      <h3>页面功能说明</h3>
      <ul>
        <li>系统登录入口页面</li>
        <li>包含用户名、密码输入框</li>
        <li>验证码输入和图片验证</li>
        <li>记住密码功能</li>
        <li>注册页面跳转链接</li>
        <li>支持背景图片展示</li>
      </ul>
    </div>

    <div class="wireframe">
      <div class="title">地铁隧道巡线大数据仿真和分析平台</div>
      
      <div class="form-item" data-label="用户名">
        <div class="input-with-icon">
          <div class="icon"></div>
          <input 
            class="input-text" 
            type="text" 
            v-model="username" 
            placeholder="请输入账号"
          />
        </div>
      </div>
      
      <div class="form-item" data-label="密码">
        <div class="input-with-icon">
          <div class="icon"></div>
          <input 
            class="input-text" 
            type="password" 
            v-model="password" 
            placeholder="请输入密码"
          />
        </div>
      </div>
      
      <div class="form-item" data-label="验证码">
        <div class="captcha-container">
          <div class="captcha-input">
            <div class="input-with-icon">
              <div class="icon"></div>
              <input 
                class="input-text" 
                type="text" 
                v-model="captcha" 
                placeholder="请输入验证码"
              />
            </div>
          </div>
          <div class="captcha-image" @click="refreshCaptcha">
            {{ captchaText }}
          </div>
        </div>
      </div>
      
      <div class="checkbox">
        <input type="checkbox" v-model="rememberPassword"> 记住密码
      </div>
      
      <button class="login-btn" @click="handleLogin">登 录</button>
      
      <div class="register-link">
        <a href="#" @click.prevent="goToRegister">立即注册</a>
      </div>
    </div>
    
    <div class="footer">
      Copyright © 2023-2024 All Rights Reserved.
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import router from '@/router';
import CryptoJS from 'crypto-js'; // 引入加密库

// 表单数据
const username = ref('');
const password = ref('');
const captcha = ref('');
const rememberPassword = ref(false);
const SECRET_KEY = 'your-secret-key-123';
// 验证码
const captchaText = ref('A7B9');
const refreshCaptcha = () => {
  // 生成4位随机验证码
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789';
  captchaText.value = '';
  for (let i = 0; i < 4; i++) {
    captchaText.value += chars.charAt(Math.floor(Math.random() * chars.length));
  }
};

// 组件加载时读取保存的凭证
onMounted(() => {
  const savedData = localStorage.getItem('savedCredentials');
  if (savedData) {
    try {
      const bytes = CryptoJS.AES.decrypt(savedData, SECRET_KEY);
      const decryptedData = JSON.parse(bytes.toString(CryptoJS.enc.Utf8));
      
      username.value = decryptedData.username;
      password.value = decryptedData.password;
      rememberPassword.value = true;
    } catch (e) {
      console.error('解密失败:', e);
      localStorage.removeItem('savedCredentials');
    }
  }
  refreshCaptcha();
});

// 登录方法（添加记住密码逻辑）
const handleLogin = () => {
  if (!username.value || !password.value || !captcha.value) {
    alert('请填写完整登录信息');
    return;
  }
  
  if (captcha.value.toUpperCase() !== captchaText.value.toUpperCase()) {
    alert('验证码错误');
    refreshCaptcha();
    return;
  }

  // 记住密码处理
  if (rememberPassword.value) {
    const encryptedData = CryptoJS.AES.encrypt(
      JSON.stringify({
        username: username.value,
        password: password.value
      }),
      SECRET_KEY
    ).toString();
    localStorage.setItem('savedCredentials', encryptedData);
  } else {
    localStorage.removeItem('savedCredentials');
  }

  // 模拟登录成功
  console.log('登录信息:', {
    username: username.value,
    password: '******', // 实际日志不应记录密码
    remember: rememberPassword.value
  });
  
  alert('登录成功');
};

// 跳转注册（保持不变）
const goToRegister = () => {
  router.push('/register');
};
// 初始化验证码
refreshCaptcha();
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-container {
  font-family: Arial, sans-serif;
  background: #f0f2f5;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  position: relative;
}

.wireframe {
  border: 2px solid #333;
  background: white;
  width: 400px;
  padding: 40px;
  text-align: center;
}

.title {
  border: 1px solid #666;
  padding: 10px;
  margin-bottom: 30px;
  font-size: 16px;
  background: #f8f8f8;
}

.form-item {
  border: 1px solid #666;
  margin-bottom: 20px;
  padding: 12px;
  text-align: left;
  position: relative;
}

.form-item::before {
  content: attr(data-label);
  position: absolute;
  top: -8px;
  left: 10px;
  background: white;
  padding: 0 5px;
  font-size: 12px;
  color: #666;
}

.input-with-icon {
  display: flex;
  align-items: center;
}

.icon {
  width: 20px;
  height: 20px;
  border: 1px solid #999;
  margin-right: 10px;
  background: #f0f0f0;
}

.input-text {
  color: #333;
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
}

.input-text::placeholder {
  color: #999;
}

.captcha-container {
  display: flex;
  gap: 10px;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 40px;
  border: 1px solid #999;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  cursor: pointer;
  user-select: none;
}

.checkbox {
  margin: 20px 0;
  text-align: left;
  display: flex;
  align-items: center;
}

.checkbox input {
  margin-right: 8px;
}

.login-btn {
  width: 100%;
  border: 2px solid #333;
  background: #f0f0f0;
  padding: 12px;
  font-size: 16px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: background 0.3s;
}

.login-btn:hover {
  background: #e0e0e0;
}

.register-link {
  text-align: right;
  margin-top: 10px;
}

.register-link a {
  color: #666;
  text-decoration: underline;
  cursor: pointer;
}

.footer {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  color: #999;
  font-size: 12px;
}

.note {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  padding: 20px;
  margin: 20px;
  border-radius: 5px;
  position: absolute;
  top: 20px;
  left: 20px;
  max-width: 300px;
}

.note h3 {
  color: #856404;
  margin-bottom: 10px;
}

.note ul {
  text-align: left;
  color: #856404;
  padding-left: 20px;
}

.note li {
  margin-bottom: 5px;
}
</style>