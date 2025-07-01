<template>
  <div class="user-management">
    <div class="search-form">
      <div class="form-item">
        <label>用户名称</label>
        <input v-model="searchParams.username" type="text" placeholder="请输入用户名称">
      </div>
      <div class="form-item">
        <label>姓名</label>
        <input v-model="searchParams.name" type="text" placeholder="请输入姓名">
      </div>
      <div class="form-item">
        <label>手机号码</label>
        <input v-model="searchParams.phone" type="text" placeholder="请输入手机号码">
      </div>
      <div class="form-item">
        <label>状态</label>
        <select v-model="searchParams.status">
          <option value="">全部</option>
          <option value="0">正常</option>
          <option value="1">停用</option>
          <option value="2">锁定</option>
        </select>
      </div>
      <div class="search-buttons">
        <button class="btn btn-primary" @click="search">搜索</button>
        <button class="btn" @click="reset">重置</button>
      </div>
    </div>
    
    <div class="toolbar">
      <div class="btn-group">
        <button class="btn btn-primary" @click="showAddDialog">新增</button>
        <button class="btn btn-success" :disabled="selectedIds.length !== 1" @click="showEditDialog">修改</button>
        <button class="btn btn-danger" :disabled="selectedIds.length === 0" @click="batchDelete">删除</button>
        <button class="btn" @click="showImportDialog">导入</button>
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
          <th>姓名</th>
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
          <td>{{ user.name }}</td>
          <td>{{ user.departmentName }}</td>
          <td>{{ user.phone }}</td>
          <td>
            <span class="status-tag" :class="getStatusClass(user.status)">
              {{ getStatusText(user.status) }}
            </span>
          </td>
          <td>{{ formatDate(user.createTime) }}</td>
          <td>
            <button class="link" @click="showEditDialog(user.id)">修改</button>
            <button class="link" @click="confirmDelete(user.id)">删除</button>
            <button class="link" @click="showResetPasswordDialog(user.id)">重置密码</button>
            <button class="link" @click="showAssignRolesDialog(user.id)">分配角色</button>
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

    <!-- 新增/编辑用户对话框 -->
    <div class="dialog" v-if="showUserDialog">
      <div class="dialog-content">
        <div class="dialog-header">
          <h3>{{ isEditMode ? '编辑用户' : '新增用户' }}</h3>
          <button class="close-btn" @click="closeUserDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="currentUser.username" type="text" :disabled="isEditMode">
          </div>
          <div class="form-group">
            <label>姓名</label>
            <input v-model="currentUser.name" type="text">
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="currentUser.email" type="email">
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input v-model="currentUser.phone" type="tel">
          </div>
          <div class="form-group">
            <label>部门</label>
            <select v-model="currentUser.departmentId">
              <option v-for="dept in departmentList" :key="dept.id" :value="dept.id">{{ dept.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>状态</label>
            <select v-model="currentUser.status">
              <option value="0">正常</option>
              <option value="1">停用</option>
              <option value="2">锁定</option>
            </select>
          </div>
          <div class="form-group">
            <label>角色</label>
            <div class="checkbox-group">
              <label v-for="role in roleList" :key="role.id">
                <input type="checkbox" 
                       v-model="currentUser.roleIds" 
                       :value="role.id">
                {{ role.name }}
              </label>
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn btn-primary" @click="saveUser">保存</button>
          <button class="btn" @click="closeUserDialog">取消</button>
        </div>
      </div>
    </div>

    <!-- 重置密码对话框 -->
    <div class="dialog" v-if="showResetPasswordDialog">
      <div class="dialog-content">
        <div class="dialog-header">
          <h3>重置密码</h3>
          <button class="close-btn" @click="closeResetPasswordDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>新密码</label>
            <input v-model="newPassword" type="password" placeholder="请输入新密码">
          </div>
          <div class="form-group">
            <label>确认密码</label>
            <input v-model="confirmPassword" type="password" placeholder="请再次输入密码">
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn btn-primary" @click="resetPassword">确定</button>
          <button class="btn" @click="closeResetPasswordDialog">取消</button>
        </div>
      </div>
    </div>

    <!-- 分配角色对话框 -->
    <div class="dialog" v-if="showAssignRolesDialog">
      <div class="dialog-content">
        <div class="dialog-header">
          <h3>分配角色</h3>
          <button class="close-btn" @click="closeAssignRolesDialog">&times;</button>
        </div>
        <div class="dialog-body">
          <div class="checkbox-group">
            <label v-for="role in roleList" :key="role.id">
              <input type="checkbox" 
                     v-model="selectedRoleIds" 
                     :value="role.id">
              {{ role.name }}
            </label>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn btn-primary" @click="assignRoles">保存</button>
          <button class="btn" @click="closeAssignRolesDialog">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

interface User {
  id: number;
  username: string;
  name: string;
  departmentId: number;
  departmentName: string;
  phone: string;
  email: string;
  status: string;
  createTime: string;
  roleIds: number[];
}

interface Department {
  id: number;
  name: string;
}

interface Role {
  id: number;
  name: string;
}

const searchParams = ref({
  username: '',
  name: '',
  phone: '',
  status: '',
});

const userList = ref<User[]>([]);
const departmentList = ref<Department[]>([]);
const roleList = ref<Role[]>([]);
const selectedIds = ref<number[]>([]);
const selectAll = ref(false);
const totalItems = ref(0);
const pageSize = ref(10);
const currentPage = ref(1);

// 对话框相关状态
const showUserDialog = ref(false);
const isEditMode = ref(false);
const currentUser = ref<User>({
  id: 0,
  username: '',
  name: '',
  departmentId: 0,
  departmentName: '',
  phone: '',
  email: '',
  status: '0',
  createTime: '',
  roleIds: []
});

const showResetPasswordDialog = ref(false);
const resetPasswordUserId = ref(0);
const newPassword = ref('');
const confirmPassword = ref('');

const showAssignRolesDialog = ref(false);
const assignRolesUserId = ref(0);
const selectedRoleIds = ref<number[]>([]);

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

onMounted(() => {
  fetchUsers();
  fetchDepartments();
  fetchRoles();
});

const fetchUsers = async () => {
  try {
    const response = await axios.get('/api/users', {
      params: {
        ...searchParams.value,
        page: currentPage.value,
        size: pageSize.value
      }
    });
    userList.value = response.data;
    totalItems.value = response.headers['x-total-count'] || response.data.length;
  } catch (error) {
    console.error('获取用户列表失败:', error);
  }
};

const fetchDepartments = async () => {
  try {
    const response = await axios.get('/api/departments');
    departmentList.value = response.data;
  } catch (error) {
    console.error('获取部门列表失败:', error);
  }
};

const fetchRoles = async () => {
  try {
    const response = await axios.get('/api/roles');
    roleList.value = response.data;
  } catch (error) {
    console.error('获取角色列表失败:', error);
  }
};

const search = () => {
  currentPage.value = 1;
  fetchUsers();
};

const reset = () => {
  searchParams.value = {
    username: '',
    name: '',
    phone: '',
    status: '',
  };
  search();
};

const refresh = () => {
  fetchUsers();
};

const showAddDialog = () => {
  isEditMode.value = false;
  currentUser.value = {
    id: 0,
    username: '',
    name: '',
    departmentId: departmentList.value[0]?.id || 0,
    departmentName: '',
    phone: '',
    email: '',
    status: '0',
    createTime: '',
    roleIds: []
  };
  showUserDialog.value = true;
};

const showEditDialog = async (userId?: number) => {
  const id = userId || selectedIds.value[0];
  if (!id) return;

  try {
    const response = await axios.get(`/api/users/${id}`);
    currentUser.value = response.data;
    isEditMode.value = true;
    showUserDialog.value = true;
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};

const closeUserDialog = () => {
  showUserDialog.value = false;
};

const saveUser = async () => {
  try {
    if (isEditMode.value) {
      await axios.put(`/api/users/${currentUser.value.id}`, currentUser.value);
    } else {
      await axios.post('/api/users', currentUser.value);
    }
    closeUserDialog();
    fetchUsers();
  } catch (error) {
    console.error('保存用户失败:', error);
  }
};

const confirmDelete = (userId: number) => {
  if (confirm('确定要删除该用户吗？')) {
    deleteUser(userId);
  }
};

const deleteUser = async (userId: number) => {
  try {
    await axios.delete(`/api/users/${userId}`);
    fetchUsers();
  } catch (error) {
    console.error('删除用户失败:', error);
  }
};

const batchDelete = async () => {
  if (selectedIds.value.length === 0) return;
  if (!confirm(`确定要删除选中的 ${selectedIds.value.length} 个用户吗？`)) return;

  try {
    await Promise.all(selectedIds.value.map(id => axios.delete(`/api/users/${id}`)));
    selectedIds.value = [];
    fetchUsers();
  } catch (error) {
    console.error('批量删除用户失败:', error);
  }
};

const showResetPasswordDialog = (userId: number) => {
  resetPasswordUserId.value = userId;
  newPassword.value = '';
  confirmPassword.value = '';
  showResetPasswordDialog.value = true;
};

const closeResetPasswordDialog = () => {
  showResetPasswordDialog.value = false;
};

const resetPassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    alert('两次输入的密码不一致');
    return;
  }

  try {
    await axios.put(`/api/users/${resetPasswordUserId.value}/password`, null, {
      params: { password: newPassword.value }
    });
    closeResetPasswordDialog();
    alert('密码重置成功');
  } catch (error) {
    console.error('重置密码失败:', error);
  }
};

const showAssignRolesDialog = async (userId: number) => {
  assignRolesUserId.value = userId;
  try {
    const response = await axios.get(`/api/users/${userId}`);
    selectedRoleIds.value = response.data.roleIds || [];
    showAssignRolesDialog.value = true;
  } catch (error) {
    console.error('获取用户角色失败:', error);
  }
};

const closeAssignRolesDialog = () => {
  showAssignRolesDialog.value = false;
};

const assignRoles = async () => {
  try {
    const user = { ...currentUser.value, roleIds: selectedRoleIds.value };
    await axios.put(`/api/users/${assignRolesUserId.value}`, user);
    closeAssignRolesDialog();
    fetchUsers();
  } catch (error) {
    console.error('分配角色失败:', error);
  }
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
  fetchUsers();
};

const getStatusText = (status: string) => {
  switch (status) {
    case '0': return '正常';
    case '1': return '停用';
    case '2': return '锁定';
    default: return '未知';
  }
};

const getStatusClass = (status: string) => {
  switch (status) {
    case '0': return 'status-normal';
    case '1': return 'status-disabled';
    case '2': return 'status-locked';
    default: return '';
  }
};

const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString();
};
</script>

<style scoped>
/* 原有样式保持不变 */

.dialog {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog-content {
  background-color: white;
  border-radius: 5px;
  width: 600px;
  max-width: 90%;
  max-height: 90%;
  overflow-y: auto;
}

.dialog-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.dialog-body {
  padding: 15px;
}

.dialog-footer {
  padding: 15px;
  border-top: 1px solid #eee;
  text-align: right;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.checkbox-group label {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.status-locked {
  background: #fff3cd;
  color: #856404;
  border: 1px solid #ffeeba;
}
</style>