<template>
  <div class="dept-management">
    <!-- 搜索区域 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchParams">
        <el-form-item label="部门名称">
          <el-input v-model="searchParams.name" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item label="部门编码">
          <el-input v-model="searchParams.code" placeholder="请输入部门编码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="operation-buttons">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增部门
      </el-button>
      <el-button @click="refreshTree">
        <el-icon><Refresh /></el-icon>刷新
      </el-button>
    </div>

    <!-- 部门树结构 - 使用递归组件 -->
    <div class="dept-container">
      <DeptTreeNode 
        v-for="dept in deptTreeData" 
        :key="dept.id" 
        :dept="dept"
        @node-click="handleDeptSelect"
        @add-child="handleAddSub"
        @edit="handleEditDept"
        @toggle-status="handleToggleStatus"
        @delete="handleDeleteDept"
      />
    </div>

    <!-- 部门编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="部门名称" prop="name" required>
          <el-input v-model="formData.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="code" required>
          <el-input v-model="formData.code" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="上级部门">
          <el-select 
            v-model="formData.parentId" 
            clearable 
            placeholder="请选择上级部门"
            style="width: 100%"
          >
            <el-option
              v-for="item in deptOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" required>
          <el-select 
            v-model="formData.status" 
            placeholder="请选择状态"
            style="width: 100%"
          >
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="formData.leader" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import DeptTreeNode from './DeptTreeNode.vue'

// API客户端配置
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 状态管理
const deptTreeData = ref([])
const searchParams = ref({
  name: '',
  code: '',
  status: ''
})
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const formData = ref({
  id: null,
  name: '',
  code: '',
  parentId: null,
  status: '正常',
  orderNum: 0,
  leader: '',
  phone: '',
  email: ''
})
const deptOptions = ref([])

// 获取部门树
const fetchDeptTree = async () => {
  try {
    const response = await apiClient.get('/departments/tree')
    // 转换状态值为前端显示格式
    deptTreeData.value = processTreeData(response.data || [])
  } catch (error) {
    ElMessage.error('获取部门树失败: ' + error.message)
  }
}

// 处理树形数据
const processTreeData = (data) => {
  return data.map(item => ({
    ...item,
    status: item.status === '0' ? '正常' : '禁用', // 转换状态值
    expanded: false,
    children: item.children ? processTreeData(item.children) : []
  }))
}

// 部门选择
const handleDeptSelect = (dept) => {
  console.log('选中部门:', dept)
}

// 编辑部门
const handleEditDept = (dept) => {
  dialogTitle.value = '编辑部门'
  formData.value = { 
    ...dept,
    status: dept.status // 保持显示值
  }
  dialogVisible.value = true
}

// 切换部门状态
const handleToggleStatus = ({ id, status }) => {
  const newStatus = status === '正常' ? '1' : '0' // 转换为后端格式
  apiClient.put(`/departments/${id}/status`, { status: newStatus })
    .then(() => {
      ElMessage.success('状态更新成功')
      fetchDeptTree()
    })
    .catch(error => {
      ElMessage.error('状态更新失败: ' + error.message)
    })
}

// 删除部门
const handleDeleteDept = (dept) => {
  ElMessageBox.confirm(`确定删除部门 "${dept.name}" 吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    apiClient.delete(`/departments/${dept.id}`)
      .then(() => {
        ElMessage.success('删除成功')
        fetchDeptTree()
      })
      .catch(error => {
        ElMessage.error('删除失败: ' + error.message)
      })
  }).catch(() => {})
}

// 获取部门选项
const fetchDeptOptions = async () => {
  try {
    const response = await apiClient.get('/departments')
    deptOptions.value = response.data || []
  } catch (error) {
    ElMessage.error('获取部门选项失败')
  }
}

// 搜索部门
const handleSearch = () => {
  fetchDeptTree()
}

// 重置搜索
const handleReset = () => {
  searchParams.value = { name: '', code: '', status: '' }
  fetchDeptTree()
}

// 刷新部门树
const refreshTree = () => {
  fetchDeptTree()
}

// 新增部门
const handleAdd = () => {
  dialogTitle.value = '新增部门'
  formData.value = {
    id: null,
    name: '',
    code: '',
    parentId: null,
    status: '正常', // 默认正常
    orderNum: 0,
    leader: '',
    phone: '',
    email: ''
  }
  dialogVisible.value = true
}

// 新增子部门
const handleAddSub = (parentId) => {
  dialogTitle.value = '新增子部门'
  formData.value = {
    id: null,
    name: '',
    code: '',
    parentId: parentId,
    status: '正常', // 默认正常
    orderNum: 0,
    leader: '',
    phone: '',
    email: ''
  }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  // 验证必填字段
  if (!formData.value.name || !formData.value.code) {
    ElMessage.error('部门名称和编码不能为空')
    return
  }

  // 准备提交数据，转换状态值为后端格式
  const submitData = {
    ...formData.value,
    status: formData.value.status === '正常' ? '0' : '1',
    orderNum: formData.value.orderNum || 0,
    createTime: formData.value.id ? undefined : new Date().toISOString()
  }

  try {
    if (formData.value.id) {
      await apiClient.put(`/departments/${formData.value.id}`, submitData)
      ElMessage.success('更新成功')
    } else {
      await apiClient.post('/departments', submitData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchDeptTree()
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  }
}

// 初始化
onMounted(() => {
  fetchDeptTree()
  fetchDeptOptions()
})
</script>

<style scoped>
.dept-management {
  padding: 20px;
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.operation-buttons {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.dept-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  background-color: #fff;
  max-height: 600px;
  overflow-y: auto;
}

.dept-item {
  margin-bottom: 8px;
}

.dept-parent {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e6e6e6;
  
  &:hover {
    background-color: #e6f7ff;
    border-color: #c0e0ff;
  }
}

.dept-parent span {
  font-weight: 500;
  color: #333;
}

.dept-parent .el-icon {
  transition: transform 0.3s ease;
  color: #666;
}

.dept-parent .rotate-icon {
  transform: rotate(180deg);
}

.dept-children {
  margin-left: 24px;
  margin-top: 8px;
  border-left: 2px dashed #d9d9d9;
  padding-left: 15px;
  transition: all 0.3s ease;
}

.dept-child {
  padding: 8px 15px;
  margin-bottom: 5px;
  background-color: #fafafa;
  border-radius: 3px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #f0f0f0;
  
  &:hover {
    background-color: #f0f7ff;
    border-color: #d0e3ff;
    transform: translateX(3px);
  }
}

/* 滚动条样式 */
.dept-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.dept-container::-webkit-scrollbar-thumb {
  background-color: #c1c1c1;
  border-radius: 3px;
}

.dept-container::-webkit-scrollbar-thumb:hover {
  background-color: #a8a8a8;
}

/* 对话框样式 */
.el-dialog__body {
  padding: 20px;
}

.el-form-item {
  margin-bottom: 18px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .search-form {
    padding: 15px;
  }
}
</style>