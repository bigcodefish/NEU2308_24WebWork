<template>
  <div class="role-management">
    <!-- 搜索区域 -->
    <div class="search-form">
      <div class="form-item">
        <label>角色名称</label>
        <el-input v-model="searchParams.name" placeholder="请输入角色名称" clearable />
      </div>
      <div class="form-item">
        <label>角色编码</label>
        <el-input v-model="searchParams.code" placeholder="请输入角色编码" clearable />
      </div>
      <div class="form-item">
        <label>状态</label>
        <el-select v-model="searchParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" value="0" />
          <el-option label="禁用" value="1" />
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
      <el-button type="success" :disabled="selectedRows.length !== 1" @click="handleEdit(selectedRows[0].id)">
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

    <!-- 角色表格 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      border
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="角色ID" width="80" sortable />
      <el-table-column prop="name" label="角色名称" width="150" sortable />
      <el-table-column prop="code" label="角色编码" width="150" sortable />
      <el-table-column prop="description" label="角色描述" width="200" />
      <el-table-column label="数据权限" width="120">
        <template #default="{ row }">
          {{ getDataScopeText(row.dataScope) }}
        </template>
      </el-table-column>
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
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row.id)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          <el-button size="small" @click="handleAssignMenus(row.id)">权限分配</el-button>
          <el-button size="small" @click="handleAssignUsers(row.id)">分配用户</el-button>
          <el-button 
            size="small" 
            :type="row.status === '0' ? 'warning' : 'success'" 
            @click="handleChangeStatus(row.id, row.status === '0' ? '1' : '0')"
          >
            {{ row.status === '0' ? '禁用' : '启用' }}
          </el-button>
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

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="roleForm" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="formData.code" :disabled="isEditMode" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="数据权限" prop="dataScope">
          <el-select v-model="formData.dataScope" placeholder="请选择数据权限" style="width: 100%">
            <el-option label="全部数据" value="all" />
            <el-option label="本部门数据" value="dept" />
            <el-option label="仅本人数据" value="self" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog v-model="assignMenusDialogVisible" title="权限分配" width="700px">
      <el-tree
        ref="menuTree"
        :data="menuTreeData"
        show-checkbox
        node-key="id"
        :props="treeProps"
        :default-checked-keys="checkedMenuKeys"
        :default-expanded-keys="expandedMenuKeys"
      />
      <template #footer>
        <el-button @click="assignMenusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignMenus">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配用户对话框 -->
    <el-dialog v-model="assignUsersDialogVisible" title="分配用户" width="800px">
      <div class="user-assign-container">
        <div class="user-list">
          <div class="search-box">
            <el-input v-model="userSearchParams.keyword" placeholder="搜索用户" clearable />
            <el-button type="primary" @click="searchUsers">搜索</el-button>
          </div>
          <el-table
            :data="userList"
            border
            height="400"
            @selection-change="handleUserSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="departmentName" label="部门" width="120" />
            <el-table-column prop="phone" label="手机号" width="120" />
            <el-table-column prop="email" label="邮箱" width="180" />
          </el-table>
          <el-pagination
            v-model:current-page="userCurrentPage"
            v-model:page-size="userPageSize"
            :total="userTotal"
            @current-change="handleUserPageChange"
            layout="prev, pager, next"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="assignUsersDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignUsers">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Edit, Delete, Download, Refresh } from '@element-plus/icons-vue'
import axios from 'axios'
import dayjs from 'dayjs'

// 使用与用户管理相同的axios配置
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
interface Role {
  id: number
  name: string
  code: string
  description: string
  dataScope: string
  status: string
  createTime: string
  updateTime: string | null
  menuIds?: number[]
}

interface Menu {
  id: number
  name: string
  parentId: number | null
  children?: Menu[]
}

interface User {
  id: number
  username: string
  name: string
  departmentName: string
  phone: string
  email: string
}

// 状态变量定义
const tableData = ref<Role[]>([])
const selectedRows = ref<Role[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const searchParams = reactive({
  name: '',
  code: '',
  status: '',
  startTime: '',
  endTime: ''
})

const dialogVisible = ref(false)
const isEditMode = ref(false)
const dialogTitle = ref('')
const roleForm = ref<FormInstance>()
const formData = reactive({
  id: 0,
  name: '',
  code: '',
  description: '',
  dataScope: 'all',
  status: '0'
})

const formRules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2到20个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2到20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  dataScope: [{ required: true, message: '请选择数据权限', trigger: 'change' }]
})

// 权限分配相关
const assignMenusDialogVisible = ref(false)
const menuTreeData = ref<Menu[]>([])
const treeProps = {
  children: 'children',
  label: 'name'
}
const checkedMenuKeys = ref<number[]>([])
const expandedMenuKeys = ref<number[]>([])
const currentAssignRoleId = ref(0)

// 分配用户相关
const assignUsersDialogVisible = ref(false)
const userList = ref<User[]>([])
const selectedUserIds = ref<number[]>([])
const userSearchParams = reactive({
  keyword: ''
})
const userCurrentPage = ref(1)
const userPageSize = ref(10)
const userTotal = ref(0)

// 工具函数
const formatDate = (dateString: string | null) => {
  if (!dateString) return ''
  return dayjs(dateString).format('YYYY-MM-DD HH:mm:ss')
}

const getStatusText = (status: string) => {
  switch (status) {
    case '0': return '启用'
    case '1': return '禁用'
    default: return '未知'
  }
}

const getStatusTagType = (status: string) => {
  switch (status) {
    case '0': return 'success'
    case '1': return 'danger'
    default: return 'info'
  }
}

const getDataScopeText = (dataScope: string) => {
  switch (dataScope) {
    case 'all': return '全部数据'
    case 'dept': return '本部门数据'
    case 'self': return '仅本人数据'
    default: return dataScope
  }
}

// API请求方法
const fetchRoleList = async () => {
  try {
    loading.value = true
    const response = await apiClient.get('/api/roles', {
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
    console.error('获取角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchMenus = async () => {
  try {
    const response = await apiClient.get('/api/menus/tree')
    menuTreeData.value = Array.isArray(response) ? response : []
    
    // 默认展开第一级
    if (menuTreeData.value.length > 0) {
      expandedMenuKeys.value = menuTreeData.value.map(menu => menu.id)
    }
  } catch (error) {
    menuTreeData.value = []
    console.error('获取菜单树失败:', error)
  }
}

const fetchRoleMenus = async (roleId: number) => {
  try {
    const response = await apiClient.get(`/api/roles/${roleId}`)
    checkedMenuKeys.value = response.menuIds || []
  } catch (error) {
    checkedMenuKeys.value = []
    console.error('获取角色菜单权限失败:', error)
  }
}

const fetchUsers = async () => {
  try {
    const response = await apiClient.get('/api/users', {
      params: {
        keyword: userSearchParams.keyword,
        page: userCurrentPage.value,
        size: userPageSize.value
      }
    })
    
    userList.value = Array.isArray(response) ? response : []
    userTotal.value = response.totalCount || 
                    response.total || 
                    (Array.isArray(response) ? response.length : 0)
  } catch (error) {
    userList.value = []
    userTotal.value = 0
    console.error('获取用户列表失败:', error)
  }
}

const fetchRoleUsers = async (roleId: number) => {
  try {
    const response = await apiClient.get(`/api/roles/${roleId}/users`)
    selectedUserIds.value = Array.isArray(response) ? response : []
  } catch (error) {
    selectedUserIds.value = []
    console.error('获取角色用户列表失败:', error)
  }
}

// 操作函数
const handleSearch = () => {
  currentPage.value = 1
  fetchRoleList()
}

const handleReset = () => {
  Object.assign(searchParams, {
    name: '',
    code: '',
    status: '',
    startTime: '',
    endTime: ''
  })
  handleSearch()
}

const refreshTable = () => {
  fetchRoleList()
}

const handleSelectionChange = (rows: Role[]) => {
  selectedRows.value = rows
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchRoleList()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchRoleList()
}

const handleAdd = () => {
  isEditMode.value = false
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (id?: number) => {
  try {
    const roleId = id ?? (selectedRows.value.length === 1 ? selectedRows.value[0].id : undefined);
    
    if (!roleId) {
      ElMessage.warning('请选择要编辑的角色');
      return;
    }

    console.log('请求角色ID:', roleId);
    
    // 使用新的axios实例避免配置冲突
    const response = await axios.get(`http://localhost:8080/api/roles/${roleId}`, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    console.log('完整响应:', response);
    
    // 检查响应结构
    const responseData = response.data?.data || response.data;
    if (!responseData) {
      throw new Error('响应数据为空');
    }

    // 更新表单数据
    Object.assign(formData, {
      id: responseData.id,
      name: responseData.name,
      code: responseData.code,
      description: responseData.description || '',
      dataScope: responseData.dataScope || 'all',
      status: responseData.status || '0'
    });
    
    isEditMode.value = true;
    dialogTitle.value = '编辑角色';
    dialogVisible.value = true;
    
  } catch (error: any) {
    console.error('完整错误:', error);
    
    let errorMessage = '获取角色信息失败';
    if (error.response) {
      errorMessage += ` (状态码: ${error.response.status})`;
      if (error.response.data) {
        errorMessage += `: ${JSON.stringify(error.response.data)}`;
      }
    } else if (error.request) {
      errorMessage = '无法连接到服务器';
    } else {
      errorMessage = error.message;
    }
    
    ElMessage.error(errorMessage);
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      type: 'warning'
    })
    await apiClient.delete(`/api/roles/${id}`)
    ElMessage.success('删除成功')
    fetchRoleList()
  } catch (error) {
    console.error(error)
  }
}

const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return;

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个角色吗？`, '提示', {
      type: 'warning'
    });
    
    const response = await apiClient.post('/api/roles/batch-delete', {
      ids: selectedRows.value.map(role => role.id)
    });
    
    if (response.success) {
      ElMessage.success(response.message);
    } else {
      ElMessage.warning(response.message);
    }
    
    selectedRows.value = [];
    fetchRoleList();
  } catch (error: any) {
    console.error('批量删除失败:', error);
    const errMsg = error.response?.data?.message || 
                  error.message || 
                  '批量删除失败';
    ElMessage.error(errMsg);
  }
}

const handleExport = async () => {
  try {
    loading.value = true;
    
    // 使用新的axios实例避免transformResponse干扰
    const response = await axios.get('/api/roles/export', {
      baseURL: 'http://localhost:8080',
      params: {
        name: searchParams.name,
        code: searchParams.code,
        status: searchParams.status,
        startTime: searchParams.startTime,
        endTime: searchParams.endTime
      },
      responseType: 'blob' // 重要：指定响应类型为blob
    });
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `角色列表_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败');
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  Object.assign(formData, {
    id: 0,
    name: '',
    code: '',
    description: '',
    dataScope: 'all',
    status: '0'
  })
}

const submitForm = async () => {
  try {
    await roleForm.value?.validate()
    
    if (isEditMode.value) {
      await apiClient.put(`/api/roles/${formData.id}`, formData)
      ElMessage.success('更新成功')
    } else {
      await apiClient.post('/api/roles', formData)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchRoleList()
  } catch (error) {
    console.error('表单提交失败:', error)
  }
}

const handleAssignMenus = async (roleId: number) => {
  currentAssignRoleId.value = roleId
  try {
    await fetchMenus()
    await fetchRoleMenus(roleId)
    assignMenusDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取权限数据失败')
    console.error(error)
  }
}

const submitAssignMenus = async () => {
  try {
    const checkedKeys = (menuTreeData.value as any).getCheckedKeys()
    await apiClient.put(`/api/roles/${currentAssignRoleId.value}/menus`, {
      menuIds: checkedKeys
    })
    ElMessage.success('权限分配成功')
    assignMenusDialogVisible.value = false
  } catch (error) {
    ElMessage.error('权限分配失败')
    console.error(error)
  }
}

const handleAssignUsers = async (roleId: number) => {
  currentAssignRoleId.value = roleId
  try {
    await fetchUsers()
    await fetchRoleUsers(roleId)
    assignUsersDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户数据失败')
    console.error(error)
  }
}

const handleUserSelectionChange = (rows: User[]) => {
  selectedUserIds.value = rows.map(user => user.id)
}

const searchUsers = () => {
  userCurrentPage.value = 1
  fetchUsers()
}

const handleUserPageChange = (page: number) => {
  userCurrentPage.value = page
  fetchUsers()
}

const submitAssignUsers = async () => {
  try {
    await apiClient.post(`/api/roles/${currentAssignRoleId.value}/users`, selectedUserIds.value)
    ElMessage.success('用户分配成功')
    assignUsersDialogVisible.value = false
  } catch (error) {
    ElMessage.error('用户分配失败')
    console.error(error)
  }
}

const handleChangeStatus = async (roleId: number, newStatus: string) => {
  try {
    // 修改为使用查询参数发送状态
    await apiClient.put(`/api/roles/${roleId}/status?status=${newStatus}`, null)
    ElMessage.success('状态更新成功')
    fetchRoleList()
  } catch (error: any) {
    console.error('状态更新失败:', error)
    const errMsg = error.response?.data?.message || 
                  error.message || 
                  '状态更新失败'
    ElMessage.error(errMsg)
  }
}

// 生命周期钩子
onMounted(() => {
  fetchRoleList()
})
</script>

<style scoped>
.role-management {
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

.user-assign-container {
  display: flex;
  gap: 20px;
}

.user-list {
  flex: 1;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
</style>