<template>
  <div class="user-management">
    <!-- 搜索区域 -->
    <div class="search-form">
      <div class="form-item">
        <label>用户名</label>
        <el-input v-model="searchParams.username" placeholder="请输入用户名" clearable />
      </div>
      <div class="form-item">
        <label>姓名</label>
        <el-input v-model="searchParams.name" placeholder="请输入姓名" clearable />
      </div>
      <div class="form-item">
        <label>手机号</label>
        <el-input v-model="searchParams.phone" placeholder="请输入手机号" clearable />
      </div>
      <div class="form-item">
        <label>邮箱</label>
        <el-input v-model="searchParams.email" placeholder="请输入邮箱" clearable />
      </div>
      <div class="form-item">
        <label>状态</label>
        <el-select v-model="searchParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="禁用" value="1" />
          <el-option label="锁定" value="2" />
        </el-select>
      </div>
      <div class="form-item">
        <label>部门</label>
        <el-select v-model="searchParams.departmentId" placeholder="请选择部门" clearable>
          <el-option
            v-for="dept in departmentOptions"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>
      </div>
      <div class="form-item">
        <label>创建时间</label>
        <el-date-picker
          v-model="searchParams.startTime"
          type="date"
          placeholder="开始日期"
          value-format="YYYY-MM-DD"
        />
        <span style="margin: 0 5px">至</span>
        <el-date-picker
          v-model="searchParams.endTime"
          type="date"
          placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </div>
      <div class="search-buttons">
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 操作按钮区域 -->
    <div class="operation-buttons">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增
      </el-button>
      <el-button type="success" :disabled="selectedRows.length !== 1" @click="handleEdit">
        <el-icon><Edit /></el-icon>修改
      </el-button>
      <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>删除
      </el-button>
      <el-button type="info" @click="handleExport">
        <el-icon><Download /></el-icon>导出
      </el-button>
      <el-button @click="refreshTable">
        <el-icon><Refresh /></el-icon>刷新
      </el-button>
    </div>

    <!-- 用户表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="用户ID" width="80" sortable />
      <el-table-column prop="username" label="用户名" width="120" sortable />
      <el-table-column prop="name" label="姓名" width="100" sortable />
      <el-table-column prop="departmentName" label="部门" width="120" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column label="状态" width="100" sortable>
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" sortable>
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginTime" label="最后登录" width="180">
        <template #default="{ row }">
          {{ row.lastLoginTime ? formatDate(row.lastLoginTime) : '从未登录' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row.id)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          <el-dropdown>
            <el-button size="small">
              更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleResetPassword(row.id)">重置密码</el-dropdown-item>
                <el-dropdown-item @click="handleAssignRoles(row.id)">分配角色</el-dropdown-item>
                <el-dropdown-item 
                  @click="handleChangeStatus(row.id, row.status === '0' ? '1' : '0')"
                >
                  {{ row.status === '0' ? '禁用' : '启用' }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="userForm" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEditMode" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <el-select v-model="formData.departmentId" placeholder="请选择部门" style="width: 100%">
            <el-option
              v-for="dept in departmentOptions"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">禁用</el-radio>
            <el-radio label="2">锁定</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select
            v-model="formData.roleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isEditMode" label="初始密码" prop="password">
          <el-input v-model="formData.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPasswordDialogVisible" title="重置密码" width="400px">
      <el-form ref="resetPasswordForm" :model="resetPasswordForm" :rules="resetPasswordRules">
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="resetPasswordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="resetPasswordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResetPassword">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="assignRolesDialogVisible" title="分配角色" width="500px">
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox
          v-for="role in roleOptions"
          :key="role.id"
          :label="role.id"
          >{{ role.name }}</el-checkbox
        >
      </el-checkbox-group>
      <template #footer>
        <el-button @click="assignRolesDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Edit, Delete, Download, Refresh, ArrowDown } from '@element-plus/icons-vue'
import axios from 'axios'
import dayjs from 'dayjs'

// 1. 创建配置好的axios实例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  transformRequest: [function (data) {
    return JSON.stringify(data)
  }],
  transformResponse: [function (data) {
    try {
      return JSON.parse(data)
    } catch (error) {
      console.error('JSON解析失败:', error)
      return data
    }
  }]
})

// 2. 添加响应拦截器
apiClient.interceptors.response.use(
  response => {
    if (response.data?.code !== undefined && response.data.code !== 200) {
      return Promise.reject(new Error(response.data.message || '请求失败'))
    }
    return response.data?.data !== undefined ? response.data.data : response.data
  },
  error => {
    console.error('请求出错:', error)
    const errMsg = error.response?.data?.message || 
                  error.message || 
                  '网络请求失败'
    ElMessage.error(errMsg)
    return Promise.reject(error)
  }
)

// 类型定义
interface User {
  id: number
  username: string
  password?: string
  name: string
  email: string
  phone: string
  departmentId: number | null
  departmentName: string
  status: string
  lastLoginTime: string | null
  createTime: string
  updateTime: string | null
  roleIds: number[]
  roleNames?: string[]
}

interface Department {
  id: number
  name: string
}

interface Role {
  id: number
  name: string
}

// 3. 状态变量定义
const tableData = ref<User[]>([])
const selectedRows = ref<User[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const searchParams = reactive({
  username: '',
  name: '',
  phone: '',
  email: '',
  status: '',
  departmentId: null as number | null,
  startTime: '',
  endTime: ''
})

const dialogVisible = ref(false)
const isEditMode = ref(false)
const dialogTitle = ref('')
const userForm = ref<FormInstance>()
const formData = reactive({
  id: 0,
  username: '',
  name: '',
  email: '',
  phone: '',
  departmentId: null as number | null,
  status: '0',
  roleIds: [] as number[],
  password: '123456'
})

const formRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '长度在4到20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '长度在2到10个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
  ]
})

const departmentOptions = ref<Department[]>([])
const roleOptions = ref<Role[]>([])

const resetPasswordDialogVisible = ref(false)
const resetPasswordForm = reactive({
  userId: 0,
  newPassword: '',
  confirmPassword: ''
})

const resetPasswordRules = reactive<FormRules>({
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetPasswordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

const assignRolesDialogVisible = ref(false)
const selectedRoleIds = ref<number[]>([])
const currentAssignUserId = ref(0)

// 4. 工具函数
const formatDate = (dateString: string | null) => {
  if (!dateString) return ''
  return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
}

const getStatusText = (status: string) => {
  switch (status) {
    case '0': return '正常'
    case '1': return '禁用'
    case '2': return '锁定'
    default: return '未知'
  }
}

const getStatusTagType = (status: string) => {
  switch (status) {
    case '0': return 'success'
    case '1': return 'danger'
    case '2': return 'warning'
    default: return 'info'
  }
}

// 5. API请求方法
const fetchUserList = async () => {
  try {
    loading.value = true
    const response = await apiClient.get('/api/users', {
      params: {
        ...searchParams,
        page: currentPage.value,
        size: pageSize.value
      }
    })
    
    tableData.value = Array.isArray(response) ? response : []
    total.value = response.totalCount || 
                response.total || 
                (Array.isArray(response) ? response.length : 0)
  } catch (error) {
    tableData.value = []
    total.value = 0
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchDepartments = async () => {
  try {
    const response = await apiClient.get('/api/departments')
    departmentOptions.value = Array.isArray(response) ? response : []
  } catch (error) {
    departmentOptions.value = []
    console.error('获取部门列表失败:', error)
  }
}

const fetchRoles = async () => {
  try {
    const response = await apiClient.get('/api/roles')
    roleOptions.value = Array.isArray(response) ? response : []
  } catch (error) {
    roleOptions.value = []
    console.error('获取角色列表失败:', error)
  }
}

// 6. 操作函数
const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

const handleReset = () => {
  Object.assign(searchParams, {
    username: '',
    name: '',
    phone: '',
    email: '',
    status: '',
    departmentId: null,
    startTime: '',
    endTime: ''
  })
  handleSearch()
}

const refreshTable = () => {
  fetchUserList()
}

const handleSelectionChange = (rows: User[]) => {
  selectedRows.value = rows
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchUserList()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchUserList()
}

const handleAdd = () => {
  isEditMode.value = false
  dialogTitle.value = '新增用户'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (id?: number) => {
  const userId = id || selectedRows.value[0]?.id
  if (!userId) return

  try {
    const user = await apiClient.get(`/api/users/${userId}`)
    Object.assign(formData, {
      ...user,
      roleIds: user.roleIds || []
    })
    isEditMode.value = true
    dialogTitle.value = '编辑用户'
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户信息失败')
    console.error(error)
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await apiClient.delete(`/api/users/${id}`)
    ElMessage.success('删除成功')
    fetchUserList()
  } catch (error) {
    console.error(error)
  }
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个用户吗？`, '提示', {
      type: 'warning'
    })
    await apiClient.post('/api/users/batch-delete', {
      ids: selectedRows.value.map(user => user.id)
    })
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    fetchUserList()
  } catch (error) {
    console.error(error)
  }
}

const handleExport = async () => {
  try {
    const response = await apiClient.get('/api/users/export', {
      responseType: 'blob',
      params: searchParams
    })
    
    const url = window.URL.createObjectURL(new Blob([response]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `用户列表_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    ElMessage.error('导出失败')
    console.error(error)
  }
}

const resetForm = () => {
  Object.assign(formData, {
    id: 0,
    username: '',
    name: '',
    email: '',
    phone: '',
    departmentId: null,
    status: '0',
    roleIds: [],
    password: '123456'
  })
}

const submitForm = async () => {
  try {
    await userForm.value?.validate()
    
    if (isEditMode.value) {
      await apiClient.put(`/api/users/${formData.id}`, formData)
      ElMessage.success('更新成功')
    } else {
      await apiClient.post('/api/users', formData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchUserList()
  } catch (error) {
    console.error('表单提交失败:', error)
  }
}

const handleResetPassword = (userId: number) => {
  resetPasswordForm.userId = userId
  resetPasswordForm.newPassword = ''
  resetPasswordForm.confirmPassword = ''
  resetPasswordDialogVisible.value = true
}

const submitResetPassword = async () => {
  try {
    await apiClient.put(`/api/users/${resetPasswordForm.userId}/password`, {
      newPassword: resetPasswordForm.newPassword
    })
    ElMessage.success('密码重置成功')
    resetPasswordDialogVisible.value = false
  } catch (error) {
    ElMessage.error('密码重置失败')
    console.error(error)
  }
}

const handleAssignRoles = async (userId: number) => {
  currentAssignUserId.value = userId
  try {
    const user = await apiClient.get(`/api/users/${userId}`)
    selectedRoleIds.value = user.roleIds || []
    assignRolesDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户角色失败')
    console.error(error)
  }
}

const submitAssignRoles = async () => {
  try {
    await apiClient.put(`/api/users/${currentAssignUserId.value}/roles`, {
      roleIds: selectedRoleIds.value
    })
    ElMessage.success('角色分配成功')
    assignRolesDialogVisible.value = false
    fetchUserList()
  } catch (error) {
    ElMessage.error('角色分配失败')
    console.error(error)
  }
}

const handleChangeStatus = async (userId: number, newStatus: string) => {
  try {
    await apiClient.patch(`/api/users/${userId}/status`, {
      status: newStatus
    })
    ElMessage.success('状态更新成功')
    fetchUserList()
  } catch (error) {
    ElMessage.error('状态更新失败')
    console.error(error)
  }
}

// 7. 生命周期钩子
onMounted(() => {
  fetchUserList()
  fetchDepartments()
  fetchRoles()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f8f8;
  border-radius: 4px;
}

.form-item {
  display: flex;
  flex-direction: column;
  min-width: 200px;
}

.form-item label {
  margin-bottom: 5px;
  font-weight: bold;
  font-size: 14px;
}

.search-buttons {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}

.operation-buttons {
  margin-bottom: 15px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.el-date-editor {
  width: 150px;
}
</style>