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
    <div class="dept-container">
         <div 
           v-for="dept in deptTreeData" 
           :key="dept.id"
           class="dept-item"
         >
           <div class="dept-parent" @click="toggleDept(dept)">
             <span>{{ dept.name }}</span>
             <el-icon :class="{ 'rotate-icon': dept.expanded }">
               <ArrowDown />
             </el-icon>
           </div>
           
           <div v-show="dept.expanded" class="dept-children">
             <div 
               v-for="child in dept.children" 
               :key="child.id"
               class="dept-child"
               @click.stop="handleDeptSelect(child)"
             >
               {{ child.name }}
             </div>
           </div>
         </div>
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
import { Plus, Refresh, FolderAdd ,ArrowDown,ArrowRight} from '@element-plus/icons-vue'

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
  children: 'children',
  hasChildren: 'hasChildren'  // 添加这个属性
}

// 方法定义
const fetchDeptTree = async () => {
  try {
    const response = await apiClient.get('/departments/tree');
    console.log('完整的部门树数据:', JSON.parse(JSON.stringify(response.data)));
    deptTreeData.value = response.data || [];
  } catch (error) {
    ElMessage.error('获取部门树失败: ' + error.message);
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
  // Element UI树组件会自动处理子节点展开
  console.log('选中部门:', data);
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

// 在方法定义部分添加以下代码
const toggleDept = (dept) => {
  // 如果不存在expanded属性，则添加并设置为true
  if (dept.expanded === undefined) {
    dept.expanded = true;
  } else {
    dept.expanded = !dept.expanded;
  }
  
  // 如果需要点击时才加载子部门，可以添加以下逻辑
  if (dept.expanded && (!dept.children || dept.children.length === 0)) {
    loadChildren(dept.id);
  }
}

// 可选：加载子部门的方法
const loadChildren = async (parentId) => {
  try {
    const response = await apiClient.get(`/departments/${parentId}/children`);
    const parentDept = findDeptInTree(parentId, deptTreeData.value);
    if (parentDept) {
      parentDept.children = response.data;
    }
  } catch (error) {
    ElMessage.error('加载子部门失败');
  }
}

// 辅助方法：在树中查找部门
const findDeptInTree = (id, tree) => {
  for (const dept of tree) {
    if (dept.id === id) return dept;
    if (dept.children) {
      const found = findDeptInTree(id, dept.children);
      if (found) return found;
    }
  }
  return null;
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
  
  .dept-parent, .dept-child {
    padding: 8px 12px;
  }
  
  .dept-children {
    margin-left: 15px;
  }
}
</style>