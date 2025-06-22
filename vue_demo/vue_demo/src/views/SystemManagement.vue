<template>
  <div class="system-management">
    <div class="page-title">系统管理</div>
    
    <div class="main-container">
      <!-- 左侧菜单 -->
      <div class="sidebar">
        <div 
          class="menu-item" 
          :class="{ active: activeTab === 'user' }" 
          @click="showTab('user')"
        >
          👤 用户管理
        </div>
        <div 
          class="menu-item" 
          :class="{ active: activeTab === 'role' }" 
          @click="showTab('role')"
        >
          🔑 角色管理
        </div>
        <div 
          class="menu-item" 
          :class="{ active: activeTab === 'menu' }" 
          @click="showTab('menu')"
        >
          📋 菜单管理
        </div>
        <div 
          class="menu-item" 
          :class="{ active: activeTab === 'dept' }" 
          @click="showTab('dept')"
        >
          🏢 部门管理
        </div>
        <div 
          class="menu-item" 
          :class="{ active: activeTab === 'config' }" 
          @click="showTab('config')"
        >
          ⚙️ 参数配置
        </div>
      </div>
      
      <!-- 右侧内容区域 -->
      <div class="content-area">
        <UserManagementView v-show="activeTab === 'user'" />
        <RoleManagementView v-show="activeTab === 'role'" />
        <MenuManagementView v-show="activeTab === 'menu'" />
        <DeptManagementView v-show="activeTab === 'dept'" />
        <ConfigManagementView v-show="activeTab === 'config'" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import UserManagementView from './UserManagementView.vue';
import RoleManagementView from './RoleManagementView.vue';
import MenuManagementView from './MenuManagementView.vue';
import DeptManagementView from './DeptManagementView.vue';
import ConfigManagementView from './ConfigManagementView.vue';

const activeTab = ref('user');

const showTab = (tabName: string) => {
  activeTab.value = tabName;
};
</script>

<style scoped>
.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border: 2px solid #333;
}

.main-container {
  display: flex;
  gap: 0;
  border: 2px solid #333;
  background: white;
  min-height: 600px;
}

.sidebar {
  width: 200px;
  background: #f8f8f8;
  border-right: 2px solid #333;
}

.menu-item {
  padding: 15px 20px;
  border-bottom: 1px solid #ddd;
  cursor: pointer;
  background: #f8f8f8;
  font-weight: 500;
}

.menu-item.active {
  background: #e6f3ff;
  font-weight: bold;
  border-left: 4px solid #0066cc;
}

.menu-item:hover {
  background: #e6f3ff;
}

.content-area {
  flex: 1;
  padding: 20px;
}
</style>