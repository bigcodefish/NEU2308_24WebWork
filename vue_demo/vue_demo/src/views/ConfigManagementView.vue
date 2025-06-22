<template>
  <div class="config-management">
    <div class="search-form">
      <div class="form-item">
        <label>参数名称</label>
        <input v-model="searchParams.name" type="text" placeholder="请输入参数名称">
      </div>
      <div class="form-item">
        <label>参数键名</label>
        <input v-model="searchParams.key" type="text" placeholder="请输入参数键名">
      </div>
      <div class="form-item">
        <label>系统内置</label>
        <select v-model="searchParams.isSystem">
          <option value="">全部</option>
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
      </div>
      <div class="search-buttons">
        <button class="btn btn-primary" @click="search">搜索</button>
        <button class="btn" @click="reset">重置</button>
      </div>
    </div>
    
    <div class="toolbar">
      <div class="btn-group">
        <button class="btn btn-primary" @click="addConfig">新增</button>
        <button class="btn btn-success" @click="editConfig">修改</button>
        <button class="btn btn-danger" @click="deleteConfig">删除</button>
        <button class="btn" @click="exportConfigs">导出</button>
        <button class="btn" @click="refreshConfigCache">刷新缓存</button>
      </div>
      <button class="btn" @click="refresh">刷新</button>
    </div>
    
    <table class="table">
      <thead>
        <tr>
          <th><input type="checkbox" v-model="selectAll" @change="toggleSelectAll"></th>
          <th>参数编号</th>
          <th>参数名称</th>
          <th>参数键名</th>
          <th>参数键值</th>
          <th>系统内置</th>
          <th>备注</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="config in configs" :key="config.id">
          <td><input type="checkbox" v-model="selectedIds" :value="config.id"></td>
          <td>{{ config.id }}</td>
          <td>{{ config.name }}</td>
          <td>{{ config.key }}</td>
          <td>{{ config.value }}</td>
          <td>{{ config.isSystem === 'Y' ? '是' : '否' }}</td>
          <td>{{ config.remark }}</td>
          <td>{{ config.createTime }}</td>
          <td>
            <button class="link" @click="editConfig(config.id)">修改</button>
            <button class="link" @click="deleteConfig(config.id)">删除</button>
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

interface Config {
  id: number;
  name: string;
  key: string;
  value: string;
  isSystem: 'Y' | 'N';
  remark: string;
  createTime: string;
}

const searchParams = ref({
  name: '',
  key: '',
  isSystem: '',
});

const configs = ref<Config[]>([
  { 
    id: 1, 
    name: '主框架页-默认皮肤样式名称', 
    key: 'sys.index.skinName', 
    value: 'skin-blue', 
    isSystem: 'Y', 
    remark: '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', 
    createTime: '2024-01-01 00:00:00' 
  },
  { 
    id: 2, 
    name: '用户管理-账号初始密码', 
    key: 'sys.user.initPassword', 
    value: '123456', 
    isSystem: 'Y', 
    remark: '初始化密码 123456', 
    createTime: '2024-01-01 00:00:00' 
  },
  { 
    id: 3, 
    name: '主框架页-侧边栏主题', 
    key: 'sys.index.sideTheme', 
    value: 'theme-dark', 
    isSystem: 'Y', 
    remark: '深色主题theme-dark，浅色主题theme-light', 
    createTime: '2024-01-01 00:00:00' 
  }
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
  console.log('搜索参数:', searchParams.value);
};

const reset = () => {
  searchParams.value = {
    name: '',
    key: '',
    isSystem: '',
  };
};

const addConfig = () => {
  console.log('新增参数');
};

const editConfig = (id?: number) => {
  console.log('修改参数:', id || selectedIds.value);
};

const deleteConfig = (id?: number) => {
  console.log('删除参数:', id || selectedIds.value);
};

const exportConfigs = () => {
  console.log('导出参数');
};

const refresh = () => {
  console.log('刷新参数列表');
};

const refreshConfigCache = () => {
  console.log('刷新参数缓存');
};

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedIds.value = configs.value.map(config => config.id);
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