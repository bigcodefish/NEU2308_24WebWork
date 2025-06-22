<template>
  <div class="dept-management">
    <div class="search-form">
      <div class="form-item">
        <label>部门名称</label>
        <input v-model="searchParams.name" type="text" placeholder="请输入部门名称">
      </div>
      <div class="form-item">
        <label>状态</label>
        <select v-model="searchParams.status">
          <option value="">全部</option>
          <option value="0">正常</option>
          <option value="1">停用</option>
        </select>
      </div>
      <div class="search-buttons">
        <button class="btn btn-primary" @click="search">搜索</button>
        <button class="btn" @click="reset">重置</button>
        <button class="btn" @click="toggleExpand">{{ isExpanded ? '折叠' : '展开' }}</button>
      </div>
    </div>
    
    <div class="toolbar">
      <div class="btn-group">
        <button class="btn btn-primary" @click="addDept">新增</button>
        <button class="btn btn-success" @click="editDept">修改</button>
        <button class="btn btn-danger" @click="deleteDept">删除</button>
      </div>
      <button class="btn" @click="refresh">刷新</button>
    </div>
    
    <div class="tree-view">
      <div 
        v-for="dept in depts" 
        :key="dept.id"
        class="tree-node"
        :class="{ 
          parent: !dept.parentId, 
          child: dept.parentId,
          'd-none': !isExpanded && dept.parentId
        }"
      >
        {{ dept.type === 'company' ? '🏢' : '🏬' }} {{ dept.name }}
        <span style="float: right;">
          <button class="link" @click="editDept(dept.id)">修改</button>
          <button class="link" @click="addSubDept(dept.id)">新增</button>
          <button class="link" @click="deleteDept(dept.id)">删除</button>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface Dept {
  id: number;
  name: string;
  type: 'company' | 'department';
  parentId: number | null;
  status: string;
}

const searchParams = ref({
  name: '',
  status: '',
});

const depts = ref<Dept[]>([
  { id: 1, name: '集团总部', type: 'company', parentId: null, status: '0' },
  { id: 2, name: '技术部', type: 'department', parentId: 1, status: '0' },
  { id: 3, name: '运维部', type: 'department', parentId: 1, status: '0' },
  { id: 4, name: '管理部', type: 'department', parentId: 1, status: '0' },
  { id: 5, name: '财务部', type: 'department', parentId: 1, status: '0' }
]);

const isExpanded = ref(true);

const search = () => {
  console.log('搜索部门:', searchParams.value);
};

const reset = () => {
  searchParams.value = {
    name: '',
    status: '',
  };
};

const addDept = () => {
  console.log('新增部门');
};

const addSubDept = (parentId: number) => {
  console.log('新增子部门，父部门ID:', parentId);
};

const editDept = (id: number) => {
  console.log('修改部门:', id);
};

const deleteDept = (id: number) => {
  console.log('删除部门:', id);
};

const refresh = () => {
  console.log('刷新部门列表');
};

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value;
};
</script>

<style scoped>
.tree-view {
  border: 1px solid #666;
  background: #f9f9f9;
  padding: 15px;
  max-height: 400px;
  overflow-y: auto;
}

.tree-node {
  margin: 5px 0;
  padding: 5px;
  border: 1px solid #ddd;
  background: white;
}

.tree-node.parent {
  font-weight: bold;
  background: #e6f3ff;
}

.tree-node.child {
  margin-left: 30px;
  background: #f8f9fa;
}

.d-none {
  display: none;
}

.search-form {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #666;
  background: #f8f8f8;
  flex-wrap: wrap;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-item label {
  font-weight: bold;
  font-size: 12px;
}

.form-item input,
.form-item select {
  border: 1px solid #999;
  padding: 8px;
  width: 120px;
}

.search-buttons {
  display: flex;
  gap: 10px;
  align-items: end;
}

.btn {
  border: 1px solid #333;
  padding: 8px 15px;
  background: #f0f0f0;
  cursor: pointer;
}

.btn-primary {
  background: #e6f3ff;
}

.btn-success {
  background: #d4edda;
}

.btn-danger {
  background: #f8d7da;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #666;
  background: #f8f8f8;
}

.btn-group {
  display: flex;
  gap: 10px;
}

.table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #666;
}

.table th,
.table td {
  border: 1px solid #ccc;
  padding: 10px;
  text-align: left;
}

.table th {
  background: #f0f0f0;
  font-weight: bold;
}

.table tbody tr:nth-child(even) {
  background: #f9f9f9;
}

.link {
  color: #0066cc;
  text-decoration: underline;
  cursor: pointer;
  margin-right: 10px;
  background: none;
  border: none;
  padding: 0;
}

.status-tag {
  padding: 4px 8px;
  border-radius: 3px;
  font-size: 12px;
  display: inline-block;
}

.status-normal {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status-disabled {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.pagination {
  margin-top: 20px;
  text-align: right;
  border: 1px solid #666;
  padding: 10px;
  background: #f8f8f8;
}

.pagination span {
  margin: 0 5px;
  cursor: pointer;
}

.pagination span.active {
  font-weight: bold;
}
</style>