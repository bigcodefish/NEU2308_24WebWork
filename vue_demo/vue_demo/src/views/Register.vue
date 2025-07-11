<template>
  <div class="register-container">


    <div class="wireframe">
      <div class="title">地铁隧道巡线大数据仿真和分析平台</div>
      
      <!-- 密码注册表单 -->
      <div class="password-register">
        <div class="form-item" data-label="用户名">
          <div class="input-with-icon">
            <div class="icon"></div>
            <input 
              class="input-text" 
              type="text" 
              v-model="form.username" 
              placeholder="请输入用户名"
            />
          </div>
        </div>
        
        <div class="form-item" data-label="密码">
          <div class="input-with-icon">
            <div class="icon"></div>
            <input 
              class="input-text" 
              type="password" 
              v-model="form.password" 
              placeholder="请输入密码"
              @input="checkPasswordStrength"
            />
          </div>
          <div class="password-strength">
            <span :class="{ active: strengthLevel >= 1 }">弱</span>
            <span :class="{ active: strengthLevel >= 2 }">中</span>
            <span :class="{ active: strengthLevel >= 3 }">强</span>
          </div>
        </div>
        
        <div class="form-item" data-label="确认密码">
          <div class="input-with-icon">
            <div class="icon"></div>
            <input 
              class="input-text" 
              type="password" 
              v-model="form.confirmPassword" 
              placeholder="请再次输入密码"
            />
          </div>
        </div>
        
        <button class="register-btn" @click="handleRegister">注 册</button>
      </div>
      
      <div class="login-link">
        <span>已有账号？</span>
        <a href="#" @click.prevent="goToLogin">立即登录</a>
      </div>
    </div>
    
    <div class="footer">
      Copyright © 2023-2024 All Rights Reserved.
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
	const username = ref('');
	const password = ref('');
const router = useRouter();

// 表单数据
const form = ref({
  username: '',
  password: '',
  confirmPassword: ''
});

// 密码强度等级 (0: 无, 1: 弱, 2: 中, 3: 强)
const strengthLevel = ref(0);

// 检查密码强度
const checkPasswordStrength = () => {
  const password = form.value.password;
  let level = 0;
  
  // 长度至少6位
  if (password.length >= 6) level = 1;
  
  // 包含字母和数字
  if (/[a-zA-Z]/.test(password) && /[0-9]/.test(password)) level = 2;
  
  // 包含特殊字符
  if (/[^a-zA-Z0-9]/.test(password)) level = 3;
  
  strengthLevel.value = level;
};
  const checkUsername = async () => {
    const res = await axios.get('http://localhost:8080/api/users/checkUsername', {
      params: { username: form.value.username }
    });
    if (res.data.exists) {
      alert('用户名已存在');
      return false;
    }
    return true;
  };
// 处理注册
const handleRegister = async () => {
  const { username, password, confirmPassword } = form.value;
  
  if (!username || !password || !confirmPassword) {
    alert('请填写完整注册信息');
    return;
  }
  
  if (password !== confirmPassword) {
    alert('两次输入的密码不一致');
    return;
  }
  
  if (strengthLevel.value === 0) {
    alert('请设置更复杂的密码');
    return;
  }
  if (!await checkUsername()) return;
    
    try {
      const res = await axios.post('http://localhost:8080/api/users/register', form.value);
      if (res.data.success) {
        alert('注册成功');
        router.push('/');
      } else {
        alert(res.data.message);
      }
    } catch (error) {
      alert('注册失败');
    }
  };


// 跳转到登录页面
const goToLogin = () => {
  router.push('/');
};
</script>

<style scoped>
.register-container {
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

.password-strength {
  display: flex;
  margin-top: 8px;
  gap: 10px;
}

.password-strength span {
  flex: 1;
  text-align: center;
  padding: 2px 0;
  color: #999;
  background: #f0f0f0;
}

.password-strength span.active {
  color: white;
}

.password-strength span:nth-child(1).active {
  background: #ff4d4f; /* 弱 - 红色 */
}

.password-strength span:nth-child(2).active {
  background: #faad14; /* 中 - 橙色 */
}

.password-strength span:nth-child(3).active {
  background: #52c41a; /* 强 - 绿色 */
}

.register-btn {
  width: 100%;
  border: 2px solid #333;
  background: #f0f0f0;
  padding: 12px;
  font-size: 16px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: background 0.3s;
}

.register-btn:hover {
  background: #e0e0e0;
}

.login-link {
  text-align: right;
  margin-top: 10px;
  font-size: 14px;
}

.login-link a {
  color: #666;
  text-decoration: underline;
  cursor: pointer;
  margin-left: 5px;
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
