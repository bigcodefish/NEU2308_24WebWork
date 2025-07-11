<template>
  <div class="menu-management">
    <div class="search-form">
      <div class="form-item">
        <label>菜单名称</label>
        <input v-model="searchParams.menuName" type="text" placeholder="请输入菜单名称">
      </div>
      <div class="form-item">
        <label>状态</label>
        <select v-model="searchParams.status">
          <option value="">全部</option>
          <option value="0">正常</option>
          <option value="1">停用</option>
        </select>
      </div>
      <div class="form-item">
        <label>显示状态</label>
        <select v-model="searchParams.visible">
          <option value="">全部</option>
          <option value="0">显示</option>
          <option value="1">隐藏</option>
        </select>
      </div>
      <div class="search-buttons">
        <button class="btn btn-primary" @click="search" :disabled="loading">
          搜索
        </button>
        <button class="btn" @click="reset" :disabled="loading">重置</button>
        <button class="btn" @click="toggleExpand">{{ isExpanded ? '折叠' : '展开' }}</button>
      </div>
    </div>
    
    <div class="toolbar">
      <div class="btn-group">
        <button class="btn btn-primary" @click="addMenu">新增</button>
        <button class="btn btn-success" @click="batchEdit" :disabled="!selectedMenus.length">批量修改</button>
        <button class="btn btn-danger" @click="batchDelete" :disabled="!selectedMenus.length">批量删除</button>
        <button class="btn" @click="refresh" :disabled="loading">刷新</button>
      </div>
    </div>
    
    <!-- 新增/编辑菜单对话框 -->
    <div v-if="showDialog" class="dialog-overlay">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ dialogTitle }}</h3>
          <button class="close-btn" @click="closeDialog">×</button>
        </div>
        <div class="dialog-body">
          <form @submit.prevent="submitForm">
            <div class="form-group">
              <label>上级菜单</label>
              <TreeSelect 
                v-model="form.parentId"
                :options="menuTreeOptions"
                placeholder="选择上级菜单"
                :disabled="isEditMode && form.menuType === 'M'"
              />
            </div>
            <div class="form-group">
              <label>菜单名称 <span class="required">*</span></label>
              <input v-model="form.menuName" type="text" required>
            </div>
            <div class="form-group">
              <label>菜单类型 <span class="required">*</span></label>
              <select v-model="form.menuType" @change="onMenuTypeChange" required>
                <option value="M">目录</option>
                <option value="C">菜单</option>
                <option value="F">按钮</option>
              </select>
            </div>
            <div class="form-group" v-if="form.menuType !== 'F'">
              <label>路由地址</label>
              <input v-model="form.path" type="text">
            </div>
            <div class="form-group" v-if="form.menuType === 'C'">
              <label>组件路径</label>
              <input v-model="form.component" type="text">
            </div>
            <div class="form-group">
              <label>显示顺序</label>
              <input v-model="form.orderNum" type="number" min="0">
            </div>
            <div class="form-group">
              <label>状态</label>
              <select v-model="form.status">
                <option value="0">正常</option>
                <option value="1">停用</option>
              </select>
            </div>
            <div class="form-group">
              <label>显示状态</label>
              <select v-model="form.visible">
                <option value="0">显示</option>
                <option value="1">隐藏</option>
              </select>
            </div>
            <div class="form-group">
              <label>图标</label>
              <div class="icon-selector">
                <input v-model="form.icon" type="text" placeholder="选择图标">
                <button type="button" class="btn-icon" @click="showIconPicker = true">
                  <span v-if="form.icon">{{ form.icon }}</span>
                  <span v-else>选择图标</span>
                </button>
              </div>
            </div>
            <div class="form-group" v-if="form.menuType !== 'M'">
              <label>权限标识</label>
              <input v-model="form.perms" type="text">
            </div>
            <div class="form-group">
              <label>备注</label>
              <textarea v-model="form.remark" rows="3"></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="btn" @click="closeDialog">取消</button>
              <button type="submit" class="btn btn-primary" :disabled="loading">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 图标选择器 -->
    <IconPicker 
      v-if="showIconPicker"
      @select="handleIconSelect"
      @close="showIconPicker = false"
    />

    <div class="tree-view">
      <div v-if="loading" class="loading">加载中...</div>
      <div 
        v-for="menu in menus" 
        :key="menu.menuId"
        class="tree-node"
        :class="{ 
          parent: !menu.parentId, 
          child: menu.parentId,
          'd-none': !isExpanded && menu.parentId,
          selected: selectedMenus.includes(menu.menuId)
        }"
        @click="toggleSelect(menu.menuId)"
      >
        <span class="menu-content">
          <input 
            type="checkbox" 
            :checked="selectedMenus.includes(menu.menuId)"
            @click.stop="toggleSelect(menu.menuId)"
          >
          <span class="menu-icon">{{ getMenuTypeIcon(menu.menuType) }} {{ menu.icon || '' }}</span>
          {{ menu.menuName }}
          <span class="menu-info">
            <span class="status-tag" :class="menu.status === '0' ? 'status-normal' : 'status-disabled'">
              {{ getStatusText(menu.status) }}
            </span>
            <span class="visible-tag" :class="menu.visible === '0' ? 'visible-show' : 'visible-hide'">
              {{ getVisibleText(menu.visible) }}
            </span>
            <span class="menu-type-tag">
              {{ getMenuTypeText(menu.menuType) }}
            </span>
          </span>
        </span>
        <span class="menu-actions">
          <button class="link" @click.stop="editMenu(menu.menuId)">修改</button>
          <button class="link" @click.stop="addSubMenu(menu.menuId)" v-if="menu.menuType === 'M'">新增</button>
          <button class="link" @click.stop="deleteMenu(menu.menuId)">删除</button>
        </span>
      </div>
      <div v-if="menus.length === 0 && !loading" class="empty">
        暂无菜单数据
      </div>
    </div>
    
    <!-- 批量操作对话框 -->
    <div v-if="showBatchDialog" class="dialog-overlay">
      <div class="dialog">
        <div class="dialog-header">
          <h3>批量修改菜单</h3>
          <button class="close-btn" @click="closeBatchDialog">×</button>
        </div>
        <div class="dialog-body">
          <form @submit.prevent="submitBatchForm">
            <div class="form-group">
              <label>状态</label>
              <select v-model="batchForm.status">
                <option value="">不修改</option>
                <option value="0">正常</option>
                <option value="1">停用</option>
              </select>
            </div>
            <div class="form-group">
              <label>显示状态</label>
              <select v-model="batchForm.visible">
                <option value="">不修改</option>
                <option value="0">显示</option>
                <option value="1">隐藏</option>
              </select>
            </div>
            <div class="form-actions">
              <button type="button" class="btn" @click="closeBatchDialog">取消</button>
              <button type="submit" class="btn btn-primary" :disabled="loading">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';


interface Menu {
  menuId: number;
  menuName: string;
  menuType: string;
  parentId: number | null;
  status: string;
  visible: string;
  path?: string;
  component?: string;
  icon?: string;
  orderNum?: number;
  perms?: string;
  isFrame?: string;
  remark?: string;
  children?: Menu[];
}

const searchParams = ref({
  menuName: '',
  status: '',
  visible: ''
});

const showDialog = ref(false);
const dialogTitle = ref('新增菜单');
const isEditMode = ref(false);
const currentMenuId = ref<number | null>(null);
const currentParentId = ref<number | null>(null);
const showIconPicker = ref(false);
const showBatchDialog = ref(false);
const selectedMenus = ref<number[]>([]);

const form = ref({
  menuName: '',
  menuType: 'M',
  parentId: null as number | null,
  status: '0',
  visible: '0',
  path: '',
  component: '',
  icon: '',
  orderNum: 0,
  perms: '',
  isFrame: '1',
  remark: ''
});

const batchForm = ref({
  status: '',
  visible: ''
});

const menus = ref<Menu[]>([]);
const isExpanded = ref(true);
const loading = ref(false);

// 计算属性：生成树形选择器选项
interface TreeOption {
  value: number;
  label: string;
  children: TreeOption[];
}

const menuTreeOptions = computed(() => {
  const buildOptions = (menuList: Menu[], level = 0): TreeOption[] => {
    return menuList.map(menu => ({
      value: menu.menuId,
      label: '　'.repeat(level) + (menu.menuType === 'M' ? '📁 ' : '') + menu.menuName,
      children: menu.children ? buildOptions(menu.children, level + 1) : []
    }));
  };
  
  return [
    { value: null, label: '顶级菜单', children: [] },
    ...buildOptions(menus.value)
  ];
});
const fetchMenus = async () => {
  try {
    loading.value = true;
    selectedMenus.value = [];
    const response = await axios.get('/api/menu/list', {
      params: searchParams.value
    });
    if (response.data.success) {
      menus.value = response.data.data;
    } else {
      console.error('获取菜单列表失败:', response.data.message);
    }
  } catch (error) {
    console.error('获取菜单列表出错:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchMenus();
});

const search = () => {
  fetchMenus();
};

const reset = () => {
  searchParams.value = {
    menuName: '',
    status: '',
    visible: ''
  };
  fetchMenus();
};

const addMenu = () => {
  dialogTitle.value = '新增菜单';
  isEditMode.value = false;
  currentMenuId.value = null;
  currentParentId.value = null;
  
  form.value = {
    menuName: '',
    menuType: 'M',
    parentId: null,
    status: '0',
    visible: '0',
    path: '',
    component: '',
    icon: '',
    orderNum: 0,
    perms: '',
    isFrame: '1',
    remark: ''
  };
  
  showDialog.value = true;
};

const addSubMenu = (parentId: number) => {
  dialogTitle.value = '新增子菜单';
  isEditMode.value = false;
  currentMenuId.value = null;
  currentParentId.value = parentId;
  
  form.value = {
    menuName: '',
    menuType: 'C',
    parentId: parentId,
    status: '0',
    visible: '0',
    path: '',
    component: '',
    icon: '',
    orderNum: 0,
    perms: '',
    isFrame: '1',
    remark: ''
  };
  
  showDialog.value = true;
};

const editMenu = async (menuId: number) => {
  try {
    loading.value = true;
    const response = await axios.get(`/api/menu/${menuId}`);
    if (response.data.success) {
      const menu = response.data.data;
      dialogTitle.value = '修改菜单';
      isEditMode.value = true;
      currentMenuId.value = menuId;
      
      form.value = {
        menuName: menu.menuName,
        menuType: menu.menuType,
        parentId: menu.parentId,
        status: menu.status || '0',
        visible: menu.visible || '0',
        path: menu.path || '',
        component: menu.component || '',
        icon: menu.icon || '',
        orderNum: menu.orderNum || 0,
        perms: menu.perms || '',
        isFrame: menu.isFrame || '1',
        remark: menu.remark || ''
      };
      
      showDialog.value = true;
    } else {
      alert('获取菜单详情失败: ' + response.data.message);
    }
  } catch (error) {
    console.error('获取菜单详情出错:', error);
    alert('获取菜单详情出错');
  } finally {
    loading.value = false;
  }
};

const submitForm = async () => {
  try {
    loading.value = true;
    let response;
    
    if (isEditMode.value && currentMenuId.value) {
      response = await axios.put('/api/menu', {
        ...form.value,
        menuId: currentMenuId.value
      });
    } else {
      response = await axios.post('/api/menu', form.value);
    }
    
    if (response.data.success) {
      alert(response.data.message);
      closeDialog();
      fetchMenus();
    } else {
      alert(response.data.message || '操作失败');
    }
  } catch (error) {
    console.error('保存菜单出错:', error);
    alert('保存菜单出错');
  } finally {
    loading.value = false;
  }
};

const closeDialog = () => {
  showDialog.value = false;
};

const onMenuTypeChange = () => {
  if (form.value.menuType === 'M') {
    form.value.component = '';
    form.value.perms = '';
  }
};

const deleteMenu = async (menuId: number) => {
  if (!confirm('确定要删除此菜单吗？')) return;
  
  try {
    loading.value = true;
    const response = await axios.delete(`/api/menu/${menuId}`);
    if (response.data.success) {
      alert('删除成功');
      fetchMenus();
    } else {
      alert(`删除失败: ${response.data.message}`);
    }
  } catch (error) {
    console.error('删除菜单出错:', error);
    alert('删除菜单时出错');
  } finally {
    loading.value = false;
  }
};

const batchEdit = () => {
  if (!selectedMenus.value.length) {
    alert('请先选择要操作的菜单');
    return;
  }
  showBatchDialog.value = true;
  batchForm.value = {
    status: '',
    visible: ''
  };
};

const submitBatchForm = async () => {
  try {
    loading.value = true;
    const response = await axios.put('/api/menu/batch', {
      menuIds: selectedMenus.value,
      ...batchForm.value
    });
    
    if (response.data.success) {
      alert(response.data.message);
      closeBatchDialog();
      fetchMenus();
    } else {
      alert(response.data.message || '批量操作失败');
    }
  } catch (error) {
    console.error('批量操作出错:', error);
    alert('批量操作出错');
  } finally {
    loading.value = false;
  }
};

const closeBatchDialog = () => {
  showBatchDialog.value = false;
};

const batchDelete = async () => {
  if (!selectedMenus.value.length) {
    alert('请先选择要删除的菜单');
    return;
  }
  
  if (!confirm(`确定要删除选中的 ${selectedMenus.value.length} 个菜单吗？`)) return;
  
  try {
    loading.value = true;
    const response = await axios.delete('/api/menu/batch', {
      data: { menuIds: selectedMenus.value }
    });
    
    if (response.data.success) {
      alert('批量删除成功');
      fetchMenus();
    } else {
      alert(`批量删除失败: ${response.data.message}`);
    }
  } catch (error) {
    console.error('批量删除出错:', error);
    alert('批量删除出错');
  } finally {
    loading.value = false;
  }
};

const toggleSelect = (menuId: number) => {
  const index = selectedMenus.value.indexOf(menuId);
  if (index === -1) {
    selectedMenus.value.push(menuId);
  } else {
    selectedMenus.value.splice(index, 1);
  }
};

const refresh = () => {
  fetchMenus();
};

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value;
};

const handleIconSelect = (icon: string) => {
  form.value.icon = icon;
  showIconPicker.value = false;
};

const getMenuTypeIcon = (type: string) => {
  switch (type) {
    case 'M': return '📁';
    case 'C': return '📄';
    case 'F': return '🔘';
    default: return '';
  }
};

const getMenuTypeText = (type: string) => {
  switch (type) {
    case 'M': return '目录';
    case 'C': return '菜单';
    case 'F': return '按钮';
    default: return '';
  }
};

const getStatusText = (status: string) => {
  return status === '0' ? '正常' : '停用';
};

const getVisibleText = (visible: string) => {
  return visible === '0' ? '显示' : '隐藏';
};
</script>

<style scoped>
.menu-management {
  background: white;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-form {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #eaeaea;
  background: #f8f8f8;
  border-radius: 5px;
  flex-wrap: wrap;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 150px;
}

.form-item label {
  font-weight: bold;
  font-size: 14px;
}

.form-item input,
.form-item select {
  border: 1px solid #ddd;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.search-buttons {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.btn {
  border: 1px solid #ddd;
  padding: 8px 15px;
  background: #fff;
  cursor: pointer;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s;
}

.btn:hover {
  background: #f0f0f0;
}

.btn-primary {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}

.btn-primary:hover {
  background: #40a9ff;
  border-color: #40a9ff;
}

.btn-success {
  background: #52c41a;
  color: white;
  border-color: #52c41a;
}

.btn-success:hover {
  background: #73d13d;
  border-color: #73d13d;
}

.btn-danger {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

.btn-danger:hover {
  background: #ff7875;
  border-color: #ff7875;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #eaeaea;
  background: #f8f8f8;
  border-radius: 5px;
}

.btn-group {
  display: flex;
  gap: 10px;
}

.tree-view {
  border: 1px solid #eaeaea;
  background: #f9f9f9;
  padding: 15px;
  border-radius: 5px;
  max-height: 500px;
  overflow-y: auto;
}

.tree-node {
  margin: 5px 0;
  padding: 10px 15px;
  border: 1px solid #eee;
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 4px;
  transition: all 0.3s;
  cursor: pointer;
}

.tree-node:hover {
  background: #f5f5f5;
}

.tree-node.selected {
  background: #e6f7ff;
  border-color: #91d5ff;
}

.tree-node.parent {
  font-weight: bold;
  background: #e6f7ff;
}

.tree-node.child {
  margin-left: 30px;
  background: #fafafa;
}

.d-none {
  display: none;
}

.link {
  color: #1890ff;
  text-decoration: none;
  cursor: pointer;
  margin-left: 10px;
  background: none;
  border: none;
  padding: 0;
  font-size: 14px;
}

.link:hover {
  text-decoration: underline;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 4px;
  margin-left: 10px;
  font-size: 12px;
}

.status-normal {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-disabled {
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}

.visible-tag {
  padding: 2px 8px;
  border-radius: 4px;
  margin-left: 5px;
  font-size: 12px;
}

.visible-show {
  background: #fafafa;
  color: #595959;
  border: 1px solid #d9d9d9;
}

.visible-hide {
  background: #f0f0f0;
  color: #8c8c8c;
  border: 1px solid #d9d9d9;
}

.menu-type-tag {
  padding: 2px 8px;
  border-radius: 4px;
  margin-left: 5px;
  font-size: 12px;
  background: #f0f0f0;
  color: #595959;
  border: 1px solid #d9d9d9;
}

.menu-content {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.menu-content input[type="checkbox"] {
  margin-right: 10px;
}

.menu-icon {
  margin-right: 8px;
}

.menu-info {
  margin-left: 15px;
  display: flex;
}

.loading {
  padding: 20px;
  text-align: center;
  color: #999;
}

.empty {
  padding: 20px;
  text-align: center;
  color: #999;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 8px;
  width: 600px;
  max-width: 90%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.dialog-header {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #666;
}

.dialog-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #40a9ff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.icon-selector {
  display: flex;
  gap: 10px;
}

.btn-icon {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fafafa;
  cursor: pointer;
}

.btn-icon:hover {
  border-color: #40a9ff;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.required {
  color: #ff4d4f;
  margin-left: 4px;
}
</style>