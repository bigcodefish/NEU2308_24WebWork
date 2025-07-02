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

    <!-- 部门树 -->
    <div class="tree-container">
      <el-tree
        :data="deptTreeData"
        :props="treeProps"
        node-key="id"
        highlight-current
        @node-click="handleNodeClick"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span>{{ data.name }}</span>
            <span class="tree-actions">
              <el-button link type="primary" @click.stop="handleAddSub(data.id)">
                <el-icon><FolderAdd /></el-icon>添加子部门
              </el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input v-model="formData.code" />
        </el-form-item>
        <el-form-item label="上级部门">
          <el-select v-model="formData.parentId" clearable placeholder="请选择">
            <el-option
              v-for="item in deptOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
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
import { ElMessage } from 'element-plus'
import { Plus, Refresh, FolderAdd } from '@element-plus/icons-vue'

// API客户端配置（关键修正）
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', // 确保包含/api前缀
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 类型定义
const Department = {
  id: Number,
  name: String,
  code: String,
  parentId: Number,
  status: String
}

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
  status: '0'
})
const deptOptions = ref([])
const treeProps = {
  label: 'name',
  children: 'children'
}

// 方法定义
const fetchDeptTree = async () => {
  try {
    const response = await apiClient.get('/departments/tree')
    deptTreeData.value = response.data || []
  } catch (error) {
    ElMessage.error('获取部门树失败: ' + error.message)
    console.error('API Error:', error)
  }
}

const fetchDeptOptions = async () => {
  try {
    const response = await apiClient.get('/departments')
    deptOptions.value = response.data || []
  } catch (error) {
    ElMessage.error('获取部门选项失败')
  }
}

const handleSearch = () => {
  fetchDeptTree()
}

const handleReset = () => {
  searchParams.value = { name: '', code: '', status: '' }
  fetchDeptTree()
}

const refreshTree = () => {
  fetchDeptTree()
}

const handleNodeClick = (data) => {
  console.log('选中部门:', data)
}

const handleAdd = () => {
  dialogTitle.value = '新增部门'
  formData.value = {
    id: null,
    name: '',
    code: '',
    parentId: null,
    status: '0'
  }
  dialogVisible.value = true
}

const handleAddSub = (parentId) => {
  dialogTitle.value = '新增子部门'
  formData.value = {
    id: null,
    name: '',
    code: '',
    parentId: parentId,
    status: '0'
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (formData.value.id) {
      await apiClient.put(`/departments/${formData.value.id}`, formData.value)
      ElMessage.success('更新成功')
    } else {
      await apiClient.post('/departments', formData.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchDeptTree()
  } catch (error) {
    ElMessage.error('操作失败: ' + error.response?.data?.message || error.message)
  }
}

// 生命周期钩子
onMounted(() => {
  fetchDeptTree()
  fetchDeptOptions()
})
</script>

<style scoped>
.dept-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.operation-buttons {
  margin-bottom: 20px;
}

.tree-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 20px;
  background: #fff;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.tree-actions {
  visibility: hidden;
}

.el-tree-node__content:hover .tree-actions {
  visibility: visible;
}
</style>