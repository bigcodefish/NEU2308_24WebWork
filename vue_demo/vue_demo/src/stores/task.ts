import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getTasks, deleteTask as deleteTaskApi, createTask as createTaskApi, updateTask as updateTaskApi, searchTasks as apiSearchTasks } from '../api/task'

// 任务类型定义
export interface Task {
  id: number
  taskId: string
  taskName: string
  taskType: string
  priority: string
  description: string | null
  executor: string
  assistants: string | null
  plannedStartTime: string | null
  plannedEndTime: string | null
  actualStartTime: string | null
  actualEndTime: string | null
  inspectionLine: string | null
  startLocation: string | null
  endLocation: string | null
  inspectionScope: string | null
  status: string
  completionRate: number
  executionResult: string | null
  issuesFound: number
}

// 搜索表单类型
export interface SearchForm {
  taskId: string
  taskName: string
  creator: string
  assignee: string
}

export const useTaskStore = defineStore('task', () => {
  // 状态
  const tasks = ref<Task[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const searchForm = ref<SearchForm>({
    taskId: '',
    taskName: '',
    creator: '',
    assignee: ''
  })

  // 计算属性
  const filteredTasks = computed(() => {
    return tasks.value.filter(task => {
      // 任务编号
      if (searchForm.value.taskId && !(task.taskId || '').toLowerCase().includes(searchForm.value.taskId.toLowerCase())) return false;
      // 任务名称
      if (searchForm.value.taskName && !(task.taskName || '').toLowerCase().includes(searchForm.value.taskName.toLowerCase())) return false;
      // 创建人
      if (searchForm.value.creator && !(task.executor || '').toLowerCase().includes(searchForm.value.creator.toLowerCase())) return false;
      // 执行人
      if (searchForm.value.assignee && !(task.executor || '').toLowerCase().includes(searchForm.value.assignee.toLowerCase())) return false;
      return true;
    });
  })

  // 获取任务列表
  const fetchTasks = async () => {
    loading.value = true
    error.value = null
    try {
      tasks.value = await getTasks()
      console.log('后端返回任务：', tasks.value)
    } catch (err: unknown) {
      error.value = (err as Error).message || '获取任务列表失败'
      console.error('获取任务列表失败:', err)
    } finally {
      loading.value = false
    }
  }

  // 更新搜索条件
  const updateSearchForm = (form: Partial<SearchForm>) => {
    searchForm.value = { ...searchForm.value, ...form }
  }

  // 重置搜索条件
  const resetSearch = () => {
    searchForm.value = {
      taskId: '',
      taskName: '',
      creator: '',
      assignee: ''
    }
  }

  // 创建任务
  const createTask = async (taskData: Omit<Task, 'id'>) => {
    try {
      await createTaskApi(taskData)
      await fetchTasks()
    } catch (err) {
      console.error('Store: Create task failed:', err)
      if (err instanceof Error) {
        error.value = err.message
      } else {
        error.value = '创建任务失败'
      }
      throw err
    }
  }

  // 更新任务
  const updateTask = async (taskId: number, taskData: Partial<Task>) => {
    try {
      await updateTaskApi(taskId, taskData)
      await fetchTasks()
    } catch (err) {
      error.value = err instanceof Error ? err.message : '更新任务失败'
      console.error('更新任务失败:', err)
      throw err
    }
  }

  // 删除任务
  const deleteTask = async (taskId: number) => {
    try {
      await deleteTaskApi(taskId)
      await fetchTasks()
    } catch (err) {
      error.value = err instanceof Error ? err.message : '删除任务失败'
      console.error('删除任务失败:', err)
      throw err
    }
  }

  // 根据计划开始时间获取任务（假设以plannedStartTime的日期为日历依据）
  const getTasksByDate = (date: string) => {
    return tasks.value.filter(task => task.plannedStartTime && task.plannedStartTime.startsWith(date))
  }

  // 搜索任务
  const searchTasks = async (params: Partial<SearchForm>) => {
    loading.value = true
    error.value = null
    try {
      const res = await apiSearchTasks(params)
      // 后端直接返回任务数组
      tasks.value = res.data
    } catch (err: unknown) {
      error.value = (err as Error).message || '搜索任务失败'
      console.error('搜索任务失败:', err)
    } finally {
      loading.value = false
    }
  }

  return {
    tasks,
    loading,
    error,
    searchForm,
    filteredTasks,
    fetchTasks,
    updateSearchForm,
    resetSearch,
    deleteTask,
    createTask,
    updateTask,
    getTasksByDate,
    searchTasks
  }
})
