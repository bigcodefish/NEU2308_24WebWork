<template>
  <div class="calendar-view">
    <div class="calendar-toolbar">
      <h3>{{ currentYear }}年{{ currentMonth }}月 任务日历</h3>
      <div class="calendar-nav">
        <el-input
          v-model="input"
          style="width: 240px"
          placeholder="搜索任务"
          clearable
        />
        <div class="nav-btn" @click="prevMonth">上月</div>
        <div class="nav-btn" @click="goToday">今日</div>
        <div class="nav-btn" @click="nextMonth">下月</div>
      </div>
    </div>

    <FullCalendar
      ref="calendarRef"
      :options="calendarOptions"
      :key="tasks.length"
      class="full-calendar"
    />

    <!-- 统一任务详情弹窗 -->
    <TaskDetailDialog
      :visible="taskDialogVisible"
      :task="selectedTask"
      :onClose="() => (taskDialogVisible = false)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import zhLocale from '@fullcalendar/core/locales/zh-cn';

import TaskDetailDialog from './TaskDetailDialog.vue';
// 导入需要的类型
import type { DayCellContentArg, CalendarApi, EventClickArg, EventContentArg } from '@fullcalendar/core';
import { useTaskStore, type Task } from '../stores/task';

const input = ref('');
const calendarRef = ref<InstanceType<typeof FullCalendar> | null>(null);
const taskDialogVisible = ref(false);
const selectedTask = ref<Task | null>(null);

// 使用任务store
const taskStore = useTaskStore();
const { tasks, fetchTasks, getTasksByDate } = taskStore;

const currentDate = ref(new Date());
const currentYear = computed(() => currentDate.value.getFullYear());
const currentMonth = computed(() => currentDate.value.getMonth() + 1);

// 将任务转换为FullCalendar事件格式
const calendarEvents = computed(() => {
  return tasks.map(task => ({
    id: task.id.toString(),
    title: task.taskType + ' - ' + task.taskName, // 显示任务类型和名称
    start: task.plannedStartTime,
    allDay: true,
    extendedProps: {
      rawData: task
    }
  }));
});

// 事件处理函数
const handleEventClick = (info: EventClickArg) => {
  const task = info.event.extendedProps.rawData as Task;
  viewTask(task);
};

const viewTask = (task: Task) => {
  selectedTask.value = task;
  taskDialogVisible.value = true;
};

const updateCurrentDate = (calendarApi: CalendarApi) => {
  const currentView = calendarApi.view;
  currentDate.value = currentView.currentStart;
};

const calendarOptions = ref({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: 'dayGridMonth',
  locales: [zhLocale],
  locale: 'zh-cn',
  firstDay: 0, // 设置周日为每周的第一天
  events: calendarEvents,
  headerToolbar: false as const,
  dayMaxEvents: true, // 允许显示更多事件
  eventClick: handleEventClick,
  eventContent: (arg: EventContentArg) => {
    // 自定义事件内容，显示任务类型和名称
    const task = arg.event.extendedProps.rawData as Task;
    return { html: `<div style='font-size:12px;color:#0066cc;'>${task.taskType} - ${task.taskName}</div>` };
  },
  dayCellClassNames: (arg: DayCellContentArg) => {
    const classes = [];
    const today = new Date();
    const cellDate = arg.date;

    // 添加当前日期样式
    if (cellDate.getDate() === today.getDate() &&
        cellDate.getMonth() === today.getMonth() &&
        cellDate.getFullYear() === today.getFullYear()) {
      classes.push('fc-today');
    }

    // 添加非当前月样式
    if (arg.view.type === 'dayGridMonth' && cellDate.getMonth() !== currentDate.value.getMonth()) {
      classes.push('fc-other-month');
    }

    return classes;
  },
  // 添加自定义日期单元格内容
  dayCellContent: (arg: DayCellContentArg) => {
    const dateStr = arg.date.toISOString().split('T')[0];
    const tasksForDay = getTasksByDate(dateStr);
    if (tasksForDay.length === 0) return { html: '' };
    // 创建任务缩略信息HTML
    const tasksHtml = tasksForDay.map(task =>
      `<div class="task-thumbnail">${task.taskType}</div>`
    ).join('');
    return { html: `<div class="day-tasks">${tasksHtml}</div>` };
  }
});

const prevMonth = () => {
  const calendarApi = calendarRef.value!.getApi();
  calendarApi.prev();
  updateCurrentDate(calendarApi);
};

const nextMonth = () => {
  const calendarApi = calendarRef.value!.getApi();
  calendarApi.next();
  updateCurrentDate(calendarApi);
};

const goToday = () => {
  const calendarApi = calendarRef.value!.getApi();
  calendarApi.today();
  updateCurrentDate(calendarApi);
};

// 初始化时更新日历视图
onMounted(() => {
  // 获取任务数据
  fetchTasks();

  if (calendarRef.value) {
    const calendarApi = calendarRef.value.getApi();
    updateCurrentDate(calendarApi);
  }
});
</script>

<style scoped>
.calendar-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 1093px;
}

.calendar-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px;
  border: 1px solid #666;
  background: #f8f8f8;
  border-radius: 4px;
}

.calendar-nav {
  display: flex;
  gap: 10px;
  align-items: center;
}

.nav-btn {
  border: 1px solid #333;
  padding: 8px 15px;
  background: #f0f0f0;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.3s;
}

.nav-btn:hover {
  background: #e0e0e0;
}

.full-calendar {
  flex: 1;
  border: 1px solid #666;
  border-radius: 4px;
  overflow: hidden;
}

/* 任务详情样式 */
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

/* 自定义FullCalendar样式 */
:deep(.fc) {
  font-family: inherit;
}

:deep(.fc-daygrid-day) {
  min-height: 80px;
}

:deep(.fc-daygrid-day.fc-day-today) {
  background-color: rgba(0, 102, 204, 0.1);
}

:deep(.fc-daygrid-day.fc-day-today .fc-daygrid-day-top) {
  position: relative;
}

:deep(.fc-daygrid-day.fc-day-today .fc-daygrid-day-number) {
  background-color: #0066cc;
  color: white;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 2px;
}

:deep(.fc-daygrid-day.fc-day-today) .fc-daygrid-day-frame {
  background-color: transparent;
}

:deep(.fc-event) {
  background: #e6f3ff;
  border: 1px solid #0066cc;
  padding: 2px 5px;
  margin-bottom: 2px;
  font-size: 12px;
  border-radius: 3px;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.fc-event:hover) {
  background: #cce5ff;
}

:deep(.fc-daygrid-day.fc-day-other) {
  background-color: #f9f9f9;
}

:deep(.fc-daygrid-day.fc-day-other .fc-daygrid-day-number) {
  color: #aaa;
}

:deep(.fc-daygrid-event) {
  margin: 1px 2px;
}

/* 添加任务缩略信息样式 */
:deep(.day-tasks) {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 2px;
  max-height: 50px;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.8);
  border-top: 1px solid #eee;
  font-size: 10px;
}

:deep(.task-thumbnail) {
  padding: 1px 3px;
  margin: 1px 0;
  background: #e6f3ff;
  border-left: 2px solid #0066cc;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 768px) {
  .calendar-toolbar {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }

  :deep(.fc-daygrid-day) {
    min-height: 60px;
  }

  :deep(.day-tasks) {
    max-height: 30px;
    font-size: 8px;
  }
}
</style>
