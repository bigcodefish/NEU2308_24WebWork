<template>
  <div class="user-management">
    <div class="search-form">
      <div class="form-item">
        <label>用户名称</label>
        <input v-model="searchParams.username" type="text" placeholder="请输入用户名称">
      </div>
      <div class="form-item">
        <label>手机号码</label>
        <input v-model="searchParams.phone" type="text" placeholder="请输入手机号码">
      </div>
      <div class="form-item">
        <label>状态</label>
        <select v-model="searchParams.status">
          <option value="">用户状态</option>
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
        <button class="btn btn-primary" @click="addUser">新增</button>
        <button class="btn btn-success" @click="editUser">修改</button>
        <button class="btn btn-danger" @click="deleteUser">删除</button>
        <button class="btn" @click="importUsers">导入</button>
        <button class="btn" @click="exportUsers">导出</button>
      </div>
      <button class="btn" @click="refresh">刷新</button>
    </div>
    
    <table class="table">
      <thead>
        <tr>
          <th><input type="checkbox" v-model="selectAll" @change="toggleSelectAll"></th>
          <th>用户编号</th>
          <th>用户名称</th>
          <th>用户昵称</th>
          <th>部门</th>
          <th>手机号码</th>
          <th>状态</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in userList" :key="user.id">
          <td><input type="checkbox" v-model="selectedIds" :value="user.id"></td>
          <td>{{ user.id }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.nickname }}</td>
          <td>{{ user.department }}</td>
          <td>{{ user.phone }}</td>
          <td>
            <span class="status-tag" :class="user.status === '0' ? 'status-normal' : 'status-disabled'">
              {{ user.status === '0' ? '正常' : '停用' }}
            </span>
          </td>
          <td>{{ user.createTime }}</td>
          <td>
            <button class="link" @click="editUser(user.id)">修改</button>
            <button class="link" @click="deleteUser(user.id)">删除</button>
            <button class="link" @click="resetPassword(user.id)">重置密码</button>
            <button class="link" @click="assignRoles(user.id)">分配角色</button>
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

interface User {
  id: number;
  username: string;
  nickname: string;
  department: string;
  phone: string;
  status: string;
  createTime: string;
}

const searchParams = ref({
  username: '',
  phone: '',
  status: '',
});

const userList = ref<User[]>([
  { id: 1, username: 'admin', nickname: '超级管理员', department: '系统部门', phone: '15888888888', status: '0', createTime: '2024-01-01 00:00:00' },
  { id: 2, username: 'zhangsan', nickname: '张三', department: '运维部', phone: '13666666666', status: '0', createTime: '2024-02-15 10:30:00' },
  { id: 3, username: 'lisi', nickname: '李四', department: '技术部', phone: '13777777777', status: '1', createTime: '2024-03-01 14:20:00' }
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
  console.log('搜索用户:', searchParams.value);
};

const reset = () => {
  searchParams.value = {
    username: '',
    phone: '',
    status: '',
  };
};

const addUser = () => {
  console.log('新增用户');
};

const editUser = (id?: number) => {
  console.log('修改用户:', id || selectedIds.value);
};

const deleteUser = (id?: number) => {
  console.log('删除用户:', id || selectedIds.value);
};

const importUsers = () => {
  console.log('导入用户');
};

const exportUsers = () => {
  console.log('导出用户');
};

const refresh = () => {
  console.log('刷新用户列表');
};

const resetPassword = (id: number) => {
  console.log('重置密码:', id);
};

const assignRoles = (id: number) => {
  console.log('分配角色:', id);
};

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedIds.value = userList.value.map(user => user.id);
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