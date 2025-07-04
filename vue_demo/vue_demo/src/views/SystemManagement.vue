<template>
  <div class="system-management">
    <div class="header">
      <div class="header-left">
        <span class="system-title">智能管理系统</span>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
            <span class="user-name">管理员</span>
            <el-icon><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item>系统设置</el-dropdown-item>
              <el-dropdown-item divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="main-container">
      <!-- 左侧菜单 -->
      <div class="sidebar">
        <div class="menu-logo">
          <span>系统管理</span>
        </div>
        <div 
          v-for="tab in tabs"
          :key="tab.name"
          class="menu-item"
          :class="{ active: activeTab === tab.name }"
          @click="switchTab(tab)"
        >
          <span class="menu-icon">{{ tab.icon }}</span>
          <span class="menu-title">{{ tab.title }}</span>
          <span class="menu-arrow" v-if="activeTab === tab.name">›</span>
        </div>
      </div>
      
      <!-- 右侧内容区域 -->
      <div class="content-area">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const tabs = [
  { name: 'user', title: '用户管理', icon: '👤' },
  { name: 'role', title: '角色管理', icon: '🔑' },
  { name: 'menu', title: '菜单管理', icon: '📋' },
  { name: 'dept', title: '部门管理', icon: '🏢' },
  { name: 'config', title: '参数配置', icon: '⚙️' }
]

const activeTab = ref(route.path.split('/').pop() || 'user')

const switchTab = (tab: { name: string }) => {
  router.push(`/system/${tab.name}`)
}

watch(
  () => route.path,
  (newPath) => {
    const tabName = newPath.split('/').pop()
    if (tabName && tabs.some(tab => tab.name === tabName)) {
      activeTab.value = tabName
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.system-management {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.system-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-name {
  margin: 0 8px;
  font-size: 14px;
}

.main-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 220px;
  background-color: #001529;
  color: #bfcbd9;
  transition: width 0.3s;
}

.menu-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #002140;
}

.menu-item {
  display: flex;
  align-items: center;
  height: 50px;
  padding: 0 20px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.menu-item:hover {
  background-color: rgba(24, 144, 255, 0.3);
}

.menu-item.active {
  background-color: #1890ff;
  color: #fff;
}

.menu-icon {
  font-size: 16px;
  margin-right: 10px;
}

.menu-title {
  flex: 1;
  font-size: 14px;
}

.menu-arrow {
  font-size: 16px;
  font-weight: bold;
}

.content-area {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background-color: #f0f2f5;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>