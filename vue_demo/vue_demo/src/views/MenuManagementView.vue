<template>
  <div class="app-container">
    <!-- 搜索/操作区域 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input
          v-model="queryParams.menuName"
          placeholder="请输入菜单名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Sort"
          @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
    </el-row>

    <!-- 表格数据 -->
    <el-table
      v-if="refreshTable"
      :data="menuList"
      row-key="menuId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="200"></el-table-column>
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template #default="scope">
          <component :is="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="60"></el-table-column>
      <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
         <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template #default="scope">
          <span>{{ new Date(scope.row.createTime).toLocaleString() }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)">新增</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="menuRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                v-model="form.parentId"
                :data="menuOptions"
                :props="{ value: 'menuId', label: 'menuName', children: 'children' }"
                value-key="menuId"
                placeholder="选择上级菜单"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio label="M">目录</el-radio>
                <el-radio label="C">菜单</el-radio>
                <el-radio label="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'F'">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
           <el-col :span="12" v-if="form.menuType === 'C'">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
          <el-col :span-="12" v-if="form.menuType !== 'M'">
            <el-form-item label="权限标识" prop="perms">
              <el-input v-model="form.perms" placeholder="请输入权限标识" />
            </el-form-item>
          </el-col>
           <el-col :span="12" v-if="form.menuType !== 'F'">
            <el-form-item label="显示状态">
              <el-radio-group v-model="form.visible">
                 <el-radio label="0">显示</el-radio>
                 <el-radio label="1">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'F'">
             <el-form-item label="菜单状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const showSearch = ref(true);
const queryParams = reactive({
  menuName: '',
  status: ''
});
const menuList = ref([]);
const open = ref(false);
const title = ref("");
const menuOptions = ref([]);
const isExpandAll = ref(false);
const refreshTable = ref(true);

const menuRef = ref(null);
const form = ref({});
const rules = ref({
  menuName: [{ required: true, message: "菜单名称不能为空", trigger: "blur" }],
  orderNum: [{ required: true, message: "菜单顺序不能为空", trigger: "blur" }],
  path: [{ required: true, message: "路由地址不能为空", trigger: "blur" }]
});

/** 查询菜单列表 */
async function getList() {
  try {
    const response = await axios.get(`${API_BASE_URL}/menu/list`, { params: queryParams });
    if (response.data.success) {
      menuList.value = response.data.data;
    } else {
      ElMessage.error(response.data.message || '查询失败');
    }
  } catch (error) {
    ElMessage.error('请求菜单列表失败');
    console.error(error);
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.menuName = '';
  queryParams.status = '';
  handleQuery();
}

/** 新增按钮操作 */
async function handleAdd(row) {
  reset();
  await getTreeselect();
  if (row != null && row.menuId) {
    form.value.parentId = row.menuId;
  } else {
    form.value.parentId = null;
  }
  open.value = true;
  title.value = "添加菜单";
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset();
  await getTreeselect();
  try {
    const response = await axios.get(`${API_BASE_URL}/menu/${row.menuId}`);
    if(response.data.success){
      form.value = response.data.data;
      open.value = true;
      title.value = "修改菜单";
    } else {
      ElMessage.error(response.data.message || '获取菜单详情失败');
    }
  } catch(error) {
    ElMessage.error('请求菜单详情失败');
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  ElMessageBox.confirm(`是否确认删除名称为"${row.menuName}"的数据项？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
  .then(() => axios.delete(`${API_BASE_URL}/menu/${row.menuId}`))
  .then((response) => {
    if (response.data.success) {
      getList();
      ElMessage.success("删除成功");
    } else {
      ElMessage.error(response.data.message || '删除失败');
    }
  })
  .catch((error) => {
      if(error !== 'cancel') {
          ElMessage.error('删除请求失败');
      }
  });
}

/** 提交按钮 */
function submitForm() {
  menuRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response;
        if (form.value.menuId != null) {
          response = await axios.put(`${API_BASE_URL}/menu`, form.value);
        } else {
          response = await axios.post(`${API_BASE_URL}/menu`, form.value);
        }

        if (response.data.success) {
          ElMessage.success(response.data.message);
          open.value = false;
          getList();
        } else {
          ElMessage.error(response.data.message || '操作失败');
        }
      } catch (error) {
        ElMessage.error('请求失败');
      }
    }
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    menuId: undefined,
    parentId: 0,
    menuName: undefined,
    icon: undefined,
    menuType: "M",
    orderNum: 0,
    isFrame: "1",
    visible: "0",
    status: "0",
  };
   if (menuRef.value) {
    menuRef.value.resetFields();
  }
}

/** 查询菜单下拉树结构 */
async function getTreeselect() {
  try {
    // 始终获取完整的菜单列表，不受搜索条件影响
    const response = await axios.get(`${API_BASE_URL}/menu/list`);
    if(response.data.success) {
      const menu = { menuId: null, menuName: '主类目', children: [] };
      menu.children = response.data.data;
      menuOptions.value = [menu];
    }
  } catch (error) {
      ElMessage.error('加载菜单树失败');
  }
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  setTimeout(() => {
    refreshTable.value = true;
  }, 10);
}

onMounted(() => {
  getList();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.mb8 {
  margin-bottom: 8px;
}
</style>