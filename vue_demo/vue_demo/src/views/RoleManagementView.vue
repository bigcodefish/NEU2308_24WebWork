<template>
  <div class="role-management">
    <div class="search-form">
      <div class="form-item">
        <label>角色名称</label>
        <input v-model="searchParams.name" type="text" placeholder="请输入角色名称">
      </div>
      <div class="form-item">
        <label>权限字符</label>
        <input v-model="searchParams.key" type="text" placeholder="请输入权限字符">
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
      </div>
    </div>
    
    <div class="toolbar">
      <div class="btn-group">
        <button class="btn btn-primary" @click="addRole">新增</button>
        <button class="btn btn-success" @click="editRole">修改</button>
        <button class="btn btn-danger" @click="deleteRole">删除</button>
        <button class="btn" @click="exportRoles">导出</button>
      </div>
      <button class="btn" @click="refresh">刷新</button>
    </div>
    
    <table class="table">
      <thead>
        <tr>
          <th><input type="checkbox" v-model="selectAll" @change="toggleSelectAll"></th>
          <th>角色编号</th>
          <th>角色名称</th>
          <th>权限字符</th>
          <th>显示顺序</th>
          <th>状态</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="role in roleList" :key="role.id">
          <td><input type="checkbox" v-model="selectedIds" :value="role.id"></td>
          <td>{{ role.id }}</td>
          <td>{{ role.name }}</td>
          <td>{{ role.key }}</td>
          <td>{{ role.order }}</td>
          <td>
            <span class="status-tag" :class="role.status === '0' ? 'status-normal' : 'status-disabled'">
              {{ role.status === '0' ? '正常' : '停用' }}
            </span>
          </td>
          <td>{{ role.createTime }}</td>
          <td>
            <button class="link" @click="editRole(role.id)">修改</button>
            <button class="link" @click="deleteRole(role.id)">删除</button>
            <button class="link" @click="setDataPermission(role.id)">数据权限</button>
            <button class="link" @click="assignUsers(role.id)">分配用户</button>
          </td>
        </tr>
      </tbody>
    </table>
    
    <div class="pagination">
      共 {{ totalItems }} 条记录，每页 {{ pageSize }} 条
      <span 
        v-for="page in pageNumbers" 
        :key="page"
        @click="changePage(page)"
        :class="{ active: currentPage === page }"
      >
        {{ page }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

interface Role {
  id: number;
  name: string;
  key: string;
  order: number;
  status: string;
  createTime: string;
}

const searchParams = ref({
  name: '',
  key: '',
  status: '',
});

const roleList = ref<Role[]>([
  { id: 1, name: '超级管理员', key: 'admin', order: 1, status: '0', createTime: '2024-01-01 00:00:00' },
  { id: 2, name: '运维人员', key: 'operator', order: 2, status: '0', createTime: '2024-02-15 10:30:00' },
  { id: 3, name: '普通用户', key: 'user', order: 3, status: '0', createTime: '2024-03-01 14:20:00' }
]);

const selectedIds = ref<number[]>([]);
const selectAll = ref(false);
const totalItems = ref(50);
const pageSize = ref(10);
const currentPage = ref(1);

const pageNumbers = computed(() => {
  const pages = [];
  const totalPages = Math.ceil(totalItems.value / pageSize.value);
  const maxVisiblePages = 5;
  
  let startPage = Math.max(1, currentPage.value - Math.floor(maxVisiblePages / 2));
  let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
  
  if (endPage - startPage + 1 < maxVisiblePages) {
    startPage = Math.max(1, endPage - maxVisiblePages + 1);
  }
  
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  
  return pages;
});

const search = () => {
  console.log('搜索角色:', searchParams.value);
};

const reset = () => {
  searchParams.value = {
    name: '',
    key: '',
    status: '',
  };
};

const addRole = () => {
  console.log('新增角色');
};

const editRole = (id?: number) => {
  console.log('修改角色:', id || selectedIds.value);
};

const deleteRole = (id?: number) => {
  console.log('删除角色:', id || selectedIds.value);
};

const exportRoles = () => {
  console.log('导出角色');
};

const refresh = () => {
  console.log('刷新角色列表');
};

const setDataPermission = (id: number) => {
  console.log('设置数据权限:', id);
};

const assignUsers = (id: number) => {
  console.log('分配用户:', id);
};

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedIds.value = roleList.value.map(role => role.id);
  } else {
    selectedIds.value = [];
  }
};

const changePage = (page: number) => {
  currentPage.value = page;
  console.log('切换到页码:', page);
};
</script>

<style scoped>
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