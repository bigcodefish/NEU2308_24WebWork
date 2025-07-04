import axios from 'axios'
import type { Task } from '../stores/task'

// 基础API配置
const API_BASE_URL = 'http://localhost:8080/api'

// 获取任务列表
export const getTasks = async (): Promise<Task[]> => {
  console.log('API: Fetching tasks from backend...');
  const response = await axios.get(`${API_BASE_URL}/tasks`, {
    params: {
      _t: Date.now() // 添加时间戳避免缓存
    }
  })
  console.log('API: Received tasks from backend:', response.data);
  return response.data
}

// 删除任务
export const deleteTask = async (id: number): Promise<void> => {
  await axios.delete(`${API_BASE_URL}/tasks/${id}`)
}

// 创建任务
export const createTask = async (task: Omit<Task, 'id'>): Promise<Task> => {
  console.log('Creating task with data:', task)
  try {
    const response = await axios.post(`${API_BASE_URL}/task`, task)
    console.log('Create task response:', response.data)
    return response.data
  } catch (error) {
    console.error('Create task error:', error)
    if (axios.isAxiosError(error)) {
      console.error('Response data:', error.response?.data)
      console.error('Response status:', error.response?.status)
      console.error('Request data that was sent:', task)
    }
    throw error
  }
}

// 更新任务
export const updateTask = async (id: number, task: Partial<Task>): Promise<Task> => {
  const response = await axios.put(`${API_BASE_URL}/tasks/${id}`, task)
  return response.data
}

// 根据ID获取任务详情
export const getTaskById = async (id: number): Promise<Task> => {
  const response = await axios.get(`${API_BASE_URL}/tasks/${id}`)
  return response.data
}

export const searchTasks = async (params: any) => {
  return axios.post('/api/tasks/search', params);
}

export const getTaskStats = async (): Promise<{totalTasks: number; totalDistance: number}> => {
  const response = await axios.get(`${API_BASE_URL}/tasks/stats`)
  return response.data
}
