<template>
  <div id="task-management">
    <div class="page-title">任务管理</div>

    <div class="tabs">
      <div class="tab" :class="{ active: activeView === 'calendar' }" @click="showCalendarView">日历视图</div>
      <div class="tab" :class="{ active: activeView === 'list' }" @click="showListView">列表视图</div>
    </div>

    <div class="wireframe">
      <CalendarView v-if="activeView === 'calendar'" />
      <ListView v-else />
    </div>
  </div>
</template>

<script lang="ts">
import { ref } from 'vue';
import CalendarView from '../components/CalendarView.vue';
import ListView from '../components/ListView.vue';

export default {
  name: 'TaskManagement',
  components: {
    CalendarView,
    ListView
  },
  setup() {
    const activeView = ref('calendar');

    const showCalendarView = () => {
      activeView.value = 'calendar';
      console.log('切换到日历视图');
    };

    const showListView = () => {
      activeView.value = 'list';
      console.log('切换到列表视图');
    };

    return {
      activeView,
      showCalendarView,
      showListView
    };
  }
}
</script>

<style scoped>
#task-management {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border: 2px solid #333;
  border-radius: 4px;
}

.tabs {
  display: flex;
  margin-bottom: 20px;
  border: 1px solid #333;
  background: white;
  border-radius: 4px;
  overflow: hidden;
}

.tab {
  flex: 1;
  padding: 15px;
  text-align: center;
  border-right: 1px solid #333;
  cursor: pointer;
  background: #f8f8f8;
  transition: all 0.3s ease;
}

.tab:hover {
  background: #e0e0e0;
}

.tab.active {
  background: #e6f3ff;
  font-weight: bold;
}

.tab:last-child {
  border-right: none;
}

.wireframe {
  border: 2px solid #333;
  background: white;
  flex: 1;
  display: flex;
  min-height: 0;
  padding: 20px;
  border-radius: 4px;
}

@media (max-width: 768px) {
  .tabs {
    flex-direction: column;
  }

  .tab {
    border-right: none;
    border-bottom: 1px solid #333;
  }

  .tab:last-child {
    border-bottom: none;
  }
}
</style>
