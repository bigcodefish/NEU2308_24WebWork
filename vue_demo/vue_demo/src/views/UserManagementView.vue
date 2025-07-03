<template>
  <div class="user-management">
    <!-- 搜索区域 -->
    <div class="search-form">
      <div class="form-item">
        <label>用户名</label>
        <el-input 
          v-model="searchParams.username" 
          placeholder="请输入用户名" 
          clearable 
          @keyup.enter="handleSearch"
        />
      </div>
      <div class="form-item">
        <label>姓名</label>
        <el-input 
          v-model="searchParams.name" 
          placeholder="请输入姓名" 
          clearable 
          @keyup.enter="handleSearch"
        />
      </div>
      <div class="form-item">
        <label>手机号</label>
        <el-input 
          v-model="searchParams.phone" 
          placeholder="请输入手机号" 
          clearable 
          @keyup.enter="handleSearch"
        />
      </div>
      <div class="form-item">
        <label>邮箱</label>
        <el-input 
          v-model="searchParams.email" 
          placeholder="请输入邮箱" 
          clearable 
          @keyup.enter="handleSearch"
        />
      </div>
      <div class="form-item">
        <label>状态</label>
        <el-select 
          v-model="searchParams.status" 
          placeholder="请选择状态" 
          clearable
          style="width: 100%"
        >
          <el-option label="正常" value="0" />
          <el-option label="禁用" value="1" />
          <el-option label="锁定" value="2" />
        </el-select>
      </div>
      <div class="form-item">
        <label>部门</label>
        <el-select 
          v-model="searchParams.departmentId" 
          placeholder="请选择部门" 
          clearable
          style="width: 100%"
          filterable
        >
          <el-option
            v-for="dept in departmentOptions"
            :key="dept.id"
            :label="dept.name"
            :value="dept.id"
          />
        </el-select>
      </div>
      <div class="form-item date-range">
        <label>创建时间</label>
        <div class="date-picker-group">
          <el-date-picker
            v-model="searchParams.startTime"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            :clearable="false"
          />
          <span class="date-separator">至</span>
          <el-date-picker
            v-model="searchParams.endTime"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            :clearable="false"
          />
        </div>
      </div>
      <div class="search-buttons">
        <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
        <el-button @click="handleReset" :icon="RefreshLeft">重置</el-button>
      </div>
    </div>

    <!-- 操作按钮区域 -->
    <div class="operation-buttons">
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增</el-button>
      <el-button 
        type="success" 
        :disabled="selectedRows.length !== 1" 
        @click="handleEdit"
        :icon="Edit"
      >
        修改
      </el-button>
      <el-button 
        type="danger" 
        :disabled="selectedRows.length === 0" 
        @click="handleBatchDelete"
        :icon="Delete"
      >
        删除
      </el-button>
      <el-button type="info" @click="handleExport" :icon="Download">导出</el-button>
      <el-button @click="refreshTable" :icon="RefreshRight">刷新</el-button>
    </div>

    <!-- 用户表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
      highlight-current-row
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="id" label="用户ID" width="80" sortable align="center" />
      <el-table-column prop="username" label="用户名" width="120" sortable />
      <el-table-column prop="name" label="姓名" width="100" sortable />
      <el-table-column label="部门" width="120">
        <template #default="{ row }">
          {{ getDepartmentName(row.departmentId) }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column label="状态" width="100" sortable align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)" effect="light">
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
      <el-table-column label="操作" width="220" fixed="right" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row.id)" :icon="EditPen">编辑</el-button>
          <el-dropdown>
            <el-button size="small">
              更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleResetPassword(row.id)" :icon="Key">
                  重置密码
                </el-dropdown-item>
                <el-dropdown-item @click="handleAssignRoles(row.id)" :icon="User">
                  分配角色
                </el-dropdown-item>
                <el-dropdown-item 
                  @click="handleChangeStatus(row.id, row.status === '0' ? '1' : '0')"
                  :icon="row.status === '0' ? CircleClose : CircleCheck"
                >
                  {{ row.status === '0' ? '禁用' : '启用' }}
                </el-dropdown-item>
                <el-dropdown-item 
                  @click="handleDelete(row.id)" 
                  :icon="Delete"
                  style="color: #f56c6c"
                >
                  删除
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
        background
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
      :close-on-press-escape="false"
    >
      <el-form 
        ref="userForm" 
        :model="formData" 
        :rules="formRules" 
        label-width="100px"
        label-position="left"
      >
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
          <el-select 
            v-model="formData.departmentId" 
            placeholder="请选择部门" 
            style="width: 100%"
            filterable
          >
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
           <el-radio :value="0">正常</el-radio>
               <el-radio :value="1">禁用</el-radio>
               <el-radio :value="2">锁定</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select
            v-model="formData.roleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
            filterable
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
          <el-input 
            v-model="formData.password" 
            type="password" 
            show-password 
            placeholder="默认密码: 123456"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="formSubmitting">
          确定
        </el-button>
      </template>
    </el-dialog>

 <!-- 重置密码对话框（精简正确版） -->
 <el-dialog 
   v-model="resetPasswordDialogVisible" 
   title="重置密码" 
   width="400px"
 >
   <el-form 
     ref="resetPasswordFormRef"
     :model="resetPasswordForm"
     :rules="resetPasswordRules"
     label-width="100px"
   >
     <el-form-item label="新密码" prop="newPassword">
       <el-input
         v-model="resetPasswordForm.newPassword"
         type="password"
         show-password
       />
     </el-form-item>
     <el-form-item label="确认密码" prop="confirmPassword">
       <el-input
         v-model="resetPasswordForm.confirmPassword"
         type="password"
         show-password
       />
     </el-form-item>
   </el-form>
   
   <template #footer>
     <span class="dialog-footer">
       <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
       <el-button 
         type="primary" 
         @click="submitResetPassword"
       >
         确定
       </el-button>
     </span>
   </template>
 </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog 
      v-model="assignRolesDialogVisible" 
      title="分配角色" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-checkbox-group v-model="selectedRoleIds" class="role-checkbox-group">
        <el-checkbox
          v-for="role in roleOptions"
          :key="role.id"
          :label="role.id"
          class="role-checkbox"
        >
          {{ role.name }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="assignRolesDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="submitAssignRoles"
          :loading="roleSubmitting"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { 
  ElMessage, 
  ElMessageBox, 
  type FormInstance, 
  type FormRules,
} from 'element-plus'
import { 
  Plus, 
  Edit, 
  Delete, 
  Download, 
  RefreshRight, 
  ArrowDown,
  Search,
  RefreshLeft,
  EditPen,
  Key,
  User,
  CircleClose,
  CircleCheck
} from '@element-plus/icons-vue'
import axios from 'axios'
import dayjs from 'dayjs'

// 修改后的axios配置
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// 添加请求拦截器
apiClient.interceptors.request.use(config => {
  // 确保不携带第三方API请求
  if (config.url?.includes('ipify.org')) {
    throw new Error('Blocked external API request')
  }
  return config
})

// 添加响应拦截器
apiClient.interceptors.response.use(
  response => {
    // 处理后端返回的真实数据（根据你的实际返回结构调整）
    return response.data?.data || response.data
  },
  error => {
    // 统一错误处理
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('请先登录')
          break
        case 403:
          ElMessage.error('没有操作权限')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(`请求失败: ${error.message}`)
      }
    } else if (error.request) {
      ElMessage.error('网络连接异常，请检查网络')
    } else {
      ElMessage.error(`请求配置错误: ${error.message}`)
    }
    return Promise.reject(error)
  }
)

interface User {
  id: number
  username: string
  password?: string
  name: string
  email: string
  phone: string
  departmentId: number
  status: string  // 注意后端返回的是"A"/"I"，不是"0"/"1"
  lastLoginTime: string | null
  createTime: string
  roleIds?: number[] | null
  roleNames?: string[] | null
}

interface Department {
  id: number
  name: string
}

interface Role {
  id: number
  name: string
}

// 状态变量
const tableData = ref<User[]>([])
const selectedRows = ref<User[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const formSubmitting = ref(false)
const passwordSubmitting = ref(false)
const roleSubmitting = ref(false)

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

const departmentOptions = ref<Department[]>([])
const roleOptions = ref<Role[]>([])

const resetPasswordDialogVisible = ref(false)
const resetPasswordForm = ref({
  userId : 0,
  newPassword: '',
  confirmPassword: ''
});
// 表单引用
const resetPasswordFormRef = ref<FormInstance>()

// 定义 resetPasswordRules
const resetPasswordRules = reactive({
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetPasswordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
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

// 工具函数
const formatDate = (dateString: string | undefined) => {
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

const getDepartmentName = (id: number | null) => {
  if (!id) return '未分配'
  const dept = departmentOptions.value.find(d => d.id === id)
  return dept ? dept.name : '未知部门'
}

const fetchUserList = async () => {
  try {
    loading.value = true;
    
    // 构造查询参数（过滤undefined值）
    const params = {
      username: searchParams.username || undefined,
      name: searchParams.name || undefined,
      phone: searchParams.phone || undefined,
      email: searchParams.email || undefined,
      departmentId: searchParams.departmentId || undefined,
      status: searchParams.status || undefined,
      startTime: searchParams.startTime || undefined,
      endTime: searchParams.endTime || undefined,
      page: currentPage.value,
      size: pageSize.value
    };

    // 过滤掉undefined参数
    const filteredParams = Object.fromEntries(
      Object.entries(params).filter(([_, v]) => v !== undefined)
    );

    // 发送请求
    const response = await apiClient.get('/api/users', { 
      params: filteredParams,
      // 添加请求取消令牌（可选）
      cancelToken: new axios.CancelToken(c => {
        // 可以在这里保存cancel函数用于取消请求
      })
    });

    // 关键修改：直接使用返回的数组（根据你的curl测试结果）
    const users = Array.isArray(response) ? response : [];
    
    // 处理数据（添加departmentName等前端需要的字段）
    tableData.value = users.map(user => ({
      ...user,
      // 添加部门名称显示
      departmentName: departmentOptions.value.find(d => d.id === user.departmentId)?.name || '未分配',
      // 确保roleIds是数组
      roleIds: Array.isArray(user.roleIds) ? user.roleIds : []
    }));

    // 设置总数（如果是分页接口，应该从响应头或响应体中获取total）
    total.value = users.length; // 如果没有分页信息，使用数组长度

  } catch (error) {
    if (!axios.isCancel(error)) {
      ElMessage.error('获取用户列表失败');
      console.error('获取用户列表失败:', error);
      tableData.value = [];
      total.value = 0;
    }
  } finally {
    loading.value = false;
  }
};

const fetchDepartments = async () => {
  try {
    const response = await apiClient.get('/api/departments')
    departmentOptions.value = response || []
  } catch (error) {
    departmentOptions.value = []
    console.error('获取部门列表失败:', error)
  }
}

const fetchRoles = async () => {
  try {
    const response = await apiClient.get('/api/roles')
    roleOptions.value = response || []
  } catch (error) {
    roleOptions.value = []
    console.error('获取角色列表失败:', error)
  }
}

// 操作方法
const handleSearch = () => {
  currentPage.value = 1
  fetchUserList()
}

const handleReset = () => {
  searchParams.username = ''
  searchParams.name = ''
  searchParams.phone = ''
  searchParams.email = ''
  searchParams.status = ''
  searchParams.departmentId = null
  searchParams.startTime = ''
  searchParams.endTime = ''
  handleSearch()
}

const refreshTable = () => {
  currentPage.value = 1
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
  isEditMode.value = false;
  dialogTitle.value = '新增用户';
  dialogVisible.value = true; // 先显示对话框
  nextTick(() => {
    resetForm(); // 在下一个DOM更新周期重置表单
  });
};

const handleEdit = async (id?: number) => {
  const userId = id || selectedRows.value[0]?.id;
  if (!userId) return;

  try {
    loading.value = true;
    // 获取用户基本信息
    const user = await apiClient.get(`/api/users/${userId}`);
    
    // 获取角色ID - 修改这里
    const roleIds = await apiClient.get(`/api/users/${userId}/roles`); // 使用正确接口
    
    Object.assign(formData, {
      ...user,
      roleIds: roleIds || []
    });
    
    isEditMode.value = true;
    dialogTitle.value = '编辑用户';
    dialogVisible.value = true;
  } catch (error) {
    ElMessage.error('获取用户信息失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};
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
  });
  nextTick(() => {
    userForm.value?.clearValidate(); // 清除校验状态
  });
};

const submitForm = async () => {
  try {
    await userForm.value?.validate();
    formSubmitting.value = true;

    // 构造请求数据（新增时不带id字段）
    const payload = {
      username: formData.username,
      password: formData.password, // 确保包含密码
      name: formData.name,
      email: formData.email,
      phone: formData.phone,
      departmentId: formData.departmentId,
      status: formData.status,
      roleIds: formData.roleIds
    };

    if (isEditMode.value) {
      // 编辑模式包含ID
      await apiClient.put(`/api/users/${formData.id}`, {
        ...payload,
        id: formData.id
      });
      ElMessage.success('更新成功');
    } else {
      // 新增模式
      const response = await apiClient.post('/api/users', payload);
      console.log('创建响应:', response);
      ElMessage.success(`用户创建成功，ID: ${response.id}`);
    }
    
    dialogVisible.value = false;
    await fetchUserList();
  } catch (error) {
    console.error('完整错误:', error);
    let errorMsg = error.response?.data?.message || error.message;
    // 处理特定错误信息
    if (errorMsg.includes('唯一约束') ){
      errorMsg = '用户ID冲突，请联系管理员重置数据库序列';
    }
    ElMessage.error(`操作失败: ${errorMsg}`);
  } finally {
    formSubmitting.value = false;
  }
};

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
    loading.value = true
    await apiClient.post('/api/users/batch-delete', {
      ids: selectedRows.value.map(user => user.id)
    })
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    fetchUserList()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleResetPassword = (userId: number) => {
  resetPasswordForm.value.userId = userId
  resetPasswordForm.value.newPassword = ''
  resetPasswordForm.value.confirmPassword = ''
  resetPasswordDialogVisible.value = true
}

// 提交重置密码
const submitResetPassword = async () => {
  try {
    await resetPasswordFormRef.value?.validate()
    passwordSubmitting.value = true
    
    await apiClient.put(`/api/users/${resetPasswordForm.value.userId}/password`, {
      newPassword: resetPasswordForm.value.newPassword
    })
    
    ElMessage.success('密码重置成功')
    resetPasswordDialogVisible.value = false
  } catch (error) {
    ElMessage.error('密码重置失败')
    console.error(error)
  } finally {
    passwordSubmitting.value = false
  }
}
// 关闭对话框时调用
const closeResetPasswordDialog = () => {
  resetPasswordDialogVisible.value = false;
  // 重置表单内容
  resetPasswordForm.value = {
    userId: 0,
    newPassword: '',
    confirmPassword: ''
  };
  // 清除验证状态
  resetPasswordFormRef.value?.clearValidate();
};


const handleAssignRoles = async (userId: number) => {
  currentAssignUserId.value = userId
  try {
    loading.value = true
    const roleIds = await apiClient.get(`/api/users/${userId}/role-ids`)
    selectedRoleIds.value = roleIds || []
    assignRolesDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户角色失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const submitAssignRoles = async () => {
  try {
    roleSubmitting.value = true
    await apiClient.put(`/api/users/${currentAssignUserId.value}/roles`, {
      roleIds: selectedRoleIds.value
    })
    ElMessage.success('角色分配成功')
    assignRolesDialogVisible.value = false
    fetchUserList()
  } catch (error) {
    ElMessage.error('角色分配失败')
    console.error(error)
  } finally {
    roleSubmitting.value = false
  }
}

const handleChangeStatus = async (userId: number, newStatus: string) => {
  try {
    await apiClient.patch(`/api/users/${userId}/status?status=${newStatus}`)
    ElMessage.success('状态更新成功')
    fetchUserList()
  } catch (error) {
    ElMessage.error('状态更新失败')
    console.error(error)
  }
}


const handleExport = async () => {
  try {
    loading.value = true;

    // 手动构造干净的查询参数对象
    const exportParams = {
      ...(searchParams.username && { username: searchParams.username }),
      ...(searchParams.name && { name: searchParams.name }),
      ...(searchParams.phone && { phone: searchParams.phone }),
      ...(searchParams.email && { email: searchParams.email }),
      ...(searchParams.status && { status: searchParams.status }),
      ...(searchParams.departmentId && { departmentId: searchParams.departmentId }),
      ...(searchParams.startTime && { startTime: searchParams.startTime }),
      ...(searchParams.endTime && { endTime: searchParams.endTime }),
    };

    const response = await apiClient.get('/api/users/user-export', {
      params: exportParams,
      responseType: 'blob'
    });

    const url = window.URL.createObjectURL(new Blob([response]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `用户列表_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    ElMessage.error('导出失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};


// 初始化
onMounted(() => {
  fetchUserList()
  fetchDepartments()
  fetchRoles()
})
</script>

<style scoped lang="css">
.user-management {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f8f8;
  border-radius: 4px;

  .form-item {
    display: flex;
    flex-direction: column;
    min-width: 200px;

    label {
      margin-bottom: 5px;
      font-weight: bold;
      font-size: 14px;
      color: #606266;
    }

    &.date-range {
      min-width: 380px;

      .date-picker-group {
        display: flex;
        align-items: center;

        .date-separator {
          margin: 0 8px;
          color: #909399;
        }
      }
    }
  }

  .search-buttons {
    display: flex;
    align-items: flex-end;
    gap: 10px;
    margin-left: auto;
  }
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

.role-checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .role-checkbox {
    margin-left: 0;
    display: block;
  }
}

.el-table {
  margin-top: 10px;

  :deep(.el-table__cell) {
    padding: 8px 0;
  }
}

.el-dialog {
  :deep(.el-dialog__body) {
    padding: 20px 30px;
  }
}
</style>