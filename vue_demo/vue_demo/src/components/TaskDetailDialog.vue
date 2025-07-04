<template>
  <el-dialog
    :model-value="visible"
    title="任务详情"
    width="600px"
    :show-close="true"
    append-to-body
    @close="$emit('close')"
  >
    <div v-if="task" class="task-detail">
      <div class="detail-row">
        <span class="label">任务名称：</span>
        <span class="value">{{ task.taskName }}</span>
      </div>
      <div class="detail-row">
        <span class="label">任务类型：</span>
        <span class="value">{{ task.taskType }}</span>
      </div>
      <div class="detail-row">
        <span class="label">优先级：</span>
        <span class="value">{{ task.priority }}</span>
      </div>
      <div class="detail-row">
        <span class="label">执行人：</span>
        <span class="value">{{ task.executor }}</span>
      </div>
      <div class="detail-row">
        <span class="label">协助人：</span>
        <span class="value">{{ task.assistants }}</span>
      </div>
      <div class="detail-row">
        <span class="label">计划时间：</span>
        <span class="value">{{ task.plannedStartTime }} 至 {{ task.plannedEndTime }}</span>
      </div>
      <div class="detail-row">
        <span class="label">实际时间：</span>
        <span class="value">{{ task.actualStartTime || '未开始' }} 至 {{ task.actualEndTime || '未结束' }}</span>
      </div>
      <div class="detail-row">
        <span class="label">巡检线路：</span>
        <span class="value">{{ task.inspectionLine }}</span>
      </div>
      <div class="detail-row">
        <span class="label">巡检范围：</span>
        <span class="value">{{ task.inspectionScope }}</span>
      </div>
      <div class="detail-row">
        <span class="label">任务状态：</span>
        <span class="value">{{ task.status }}</span>
      </div>
      <div class="detail-row">
        <span class="label">完成度：</span>
        <span class="value">{{ task.completionRate }}%</span>
      </div>
      <div class="detail-row">
        <span class="label">发现问题：</span>
        <span class="value">{{ task.issuesFound }}个</span>
      </div>
      <div class="detail-row">
        <span class="label">任务描述：</span>
        <span class="value">{{ task.description }}</span>
      </div>
    </div>
    <template #footer>
      <el-button @click="$emit('close')">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
import type { Task } from '../stores/task';

defineProps<{
  visible: boolean;
  task: Task | null;
}>();

defineEmits(['close']);
</script>

<style scoped>
.task-detail {
  padding: 10px;
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
</style>
