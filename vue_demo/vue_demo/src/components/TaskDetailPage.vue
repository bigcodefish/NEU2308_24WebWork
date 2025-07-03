<template>
  <div class="task-detail-page">
    <div class="header">
      <h2>任务详情</h2>
      <button class="close-btn" @click="closeWindow">×</button>
    </div>
    <div v-if="task" class="task-detail">
      <div class="detail-row"><span class="label">任务名称：</span><span class="value">{{ task.name }}</span></div>
      <div class="detail-row"><span class="label">任务类型：</span><span class="value">{{ task.type }}</span></div>
      <div class="detail-row"><span class="label">优先级：</span><span class="value">{{ task.priority }}</span></div>
      <div class="detail-row"><span class="label">执行人：</span><span class="value">{{ task.executor }}</span></div>
      <div class="detail-row"><span class="label">协助人：</span><span class="value">{{ task.assistant }}</span></div>
      <div class="detail-row"><span class="label">计划时间：</span><span class="value">{{ task.planStartTime }} 至 {{ task.planEndTime }}</span></div>
      <div class="detail-row"><span class="label">实际时间：</span><span class="value">{{ task.actualStartTime || '未开始' }} 至 {{ task.actualEndTime || '未结束' }}</span></div>
      <div class="detail-row"><span class="label">巡检线路：</span><span class="value">{{ task.line }}</span></div>
      <div class="detail-row"><span class="label">巡检范围：</span><span class="value">{{ task.inspectionArea }}</span></div>
      <div class="detail-row"><span class="label">任务状态：</span><span class="value">{{ task.status }}</span></div>
      <div class="detail-row"><span class="label">完成度：</span><span class="value">{{ task.completion }}%</span></div>
      <div class="detail-row"><span class="label">发现问题：</span><span class="value">{{ task.issues }}个</span></div>
      <div class="detail-row"><span class="label">任务描述：</span><span class="value">{{ task.description }}</span></div>
    </div>
    <div v-else class="not-found">未找到该任务</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

interface Task {
  id: number;
  name: string;
  type: string;
  priority: string;
  description: string;
  executor: string;
  assistant: string;
  planStartTime: string;
  planEndTime: string;
  actualStartTime: string;
  actualEndTime: string;
  status: string;
  completion: number;
  result: string;
  issues: number;
  line: string;
  startLocation: string;
  endLocation: string;
  inspectionArea: string;
  date: string;
}

const route = useRoute();
const task = ref<Task | null>(null);

// 假设window.opener传递了任务数据，或可通过API获取。这里用localStorage模拟。
onMounted(() => {
  const id = Number(route.params.id);
  // 尝试从localStorage获取任务列表
  const tasksStr = localStorage.getItem('all_tasks');
  if (tasksStr) {
    const tasks: Task[] = JSON.parse(tasksStr);
    task.value = tasks.find(t => t.id === id) || null;
  }
});

function closeWindow() {
  window.close();
}
</script>

<style scoped>
.task-detail-page {
  max-width: 600px;
  margin: 30px auto;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  padding: 0 0 20px 0;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding: 16px 24px 8px 24px;
}
.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: #888;
  line-height: 1;
}
.close-btn:hover {
  color: #d00;
}
.task-detail {
  padding: 20px 24px 0 24px;
}
.detail-row {
  display: flex;
  margin-bottom: 10px;
  line-height: 1.5;
}
.label {
  width: 100px;
  font-weight: bold;
  color: #333;
}
.value {
  flex: 1;
  color: #666;
}
.not-found {
  color: #d00;
  text-align: center;
  padding: 40px 0;
}
</style>
