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
      :data="tableData"
      border
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="用户ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="departmentName" label="部门" width="120" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
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
import {
  Plus,
  Edit,
  Delete,
  Download,
  Refresh,
  ArrowDown
} from '@element-plus/icons-vue'
import axios from 'axios'

interface User {
  id: number
  username: string
  name: string
  email: string
  phone: string
  departmentId: number
  departmentName: string
  status: string
  createTime: string
  roleIds: number[]
}

interface Department {
  id: number
  name: string
}

interface Role {
  id: number
  name: string
}

// 表格数据
const tableData = ref<User[]>([])
const selectedRows = ref<User[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索参数
const searchParams = reactive({
  username: '',
  name: '',
  phone: '',
  status: '',
  departmentId: null as number | null
})

// 对话框相关
const dialogVisible = ref(false)
const isEditMode = ref(false)
const dialogTitle = ref('')
const formData = reactive({
  id: 0,
  username: '',
  name: '',
  email: '',
  phone: '',
  departmentId: null as number | null,
  status: '0',
  roleIds: [] as number[],
  password: '123456' // 默认密码
})

// 表单验证规则
const formRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '长度在4到20个字符', trigger: 'blur' }
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

// 部门选项
const departmentOptions = ref<Department[]>([])
// 角色选项
const roleOptions = ref<Role[]>([])

// 重置密码相关
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

// 分配角色相关
const assignRolesDialogVisible = ref(false)
const selectedRoleIds = ref<number[]>([])
const currentAssignUserId = ref(0)

// 初始化数据
onMounted(() => {
  fetchUserList()
  fetchDepartments()
  fetchRoles()
})

// 获取用户列表
const fetchUserList = async () => {
  try {
    const response = await axios.get('/api/users', {
      params: {
        ...searchParams,
        page: currentPage.value,
        size: pageSize.value
      }
    })
    tableData.value = response.data
    total.value = response.headers['x-total-count'] || response.data.length
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error(error)
  }
}

// 获取部门列表
const fetchDepartments = async () => {
  try {
    const response = await axios.get('/api/departments')
    departmentOptions.value = response.data
  } catch (error) {
    ElMessage.error('获取部门列表失败')
    console.error(error)
  }
}

// 获取角色列表
const fetchRoles = async () => {
  try {
    const response = await axios.get('/api/roles')
    roleOptions.value = response.data
  } catch (error) {
    ElMessage.error('获取角色列表失败')
    console.error(error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchParams, {
    username: '',
    name: '',
    phone: '',
    status: '',
    departmentId: null
  })
  handleSearch()
}

// 刷新表格
const refreshTable = () => {
  fetchUserList()
}

// 表格选择变化
const handleSelectionChange = (rows: User[]) => {
  selectedRows.value = rows
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchUserList()
}

// 当前页变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchUserList()
}

// 新增用户
const handleAdd = () => {
  isEditMode.value = false
  dialogTitle.value = '新增用户'
  resetForm()
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = async (id?: number) => {
  const userId = id || selectedRows.value[0]?.id
  if (!userId) return

  try {
    const response = await axios.get(`/api/users/${userId}`)
    Object.assign(formData, response.data)
    isEditMode.value = true
    dialogTitle.value = '编辑用户'
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户信息失败')
    console.error(error)
  }
}

// 删除用户
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      type: 'warning'
    })
    await axios.delete(`/api/users/${id}`)
    ElMessage.success('删除成功')
    fetchUserList()
  } catch (error) {
    console.error(error)
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个用户吗？`, '提示', {
      type: 'warning'
    })
    const deletePromises = selectedRows.value.map(user =>
      axios.delete(`/api/users/${user.id}`)
    )
    await Promise.all(deletePromises)
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    fetchUserList()
  } catch (error) {
    console.error(error)
  }
}

// 导出用户
const handleExport = () => {
  ElMessage.info('导出功能待实现')
}

// 重置表单
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

// 提交表单
const submitForm = async () => {
  try {
    if (isEditMode.value) {
      await axios.put(`/api/users/${formData.id}`, formData)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/users', formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchUserList()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error(error)
  }
}

// 重置密码
const handleResetPassword = (userId: number) => {
  resetPasswordForm.userId = userId
  resetPasswordForm.newPassword = ''
  resetPasswordForm.confirmPassword = ''
  resetPasswordDialogVisible.value = true
}

// 提交重置密码
const submitResetPassword = async () => {
  try {
    await axios.put(`/api/users/${resetPasswordForm.userId}/password`, null, {
      params: { password: resetPasswordForm.newPassword }
    })
    ElMessage.success('密码重置成功')
    resetPasswordDialogVisible.value = false
  } catch (error) {
    ElMessage.error('密码重置失败')
    console.error(error)
  }
}

// 分配角色
const handleAssignRoles = async (userId: number) => {
  currentAssignUserId.value = userId
  try {
    const response = await axios.get(`/api/users/${userId}`)
    selectedRoleIds.value = response.data.roleIds || []
    assignRolesDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户角色失败')
    console.error(error)
  }
}

// 提交分配角色
const submitAssignRoles = async () => {
  try {
    await axios.put(`/api/users/${currentAssignUserId.value}`, {
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

// 修改状态
const handleChangeStatus = async (userId: number, newStatus: string) => {
  try {
    await axios.put(`/api/users/${userId}/status`, null, {
      params: { status: newStatus }
    })
    ElMessage.success('状态更新成功')
    fetchUserList()
  } catch (error) {
    ElMessage.error('状态更新失败')
    console.error(error)
  }
}

// 获取状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case '0':
      return '正常'
    case '1':
      return '禁用'
    case '2':
      return '锁定'
    default:
      return '未知'
  }
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  switch (status) {
    case '0':
      return 'success'
    case '1':
      return 'danger'
    case '2':
      return 'warning'
    default:
      return 'info'
  }
}
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
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>