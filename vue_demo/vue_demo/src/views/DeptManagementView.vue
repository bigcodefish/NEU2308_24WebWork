<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="部门名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入部门名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="部门状态" clearable size="small">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-sort"
          size="mini"
          @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
    </el-row>

    <!-- 部门表格 -->
    <el-table
      v-if="refreshTable"
      :data="deptList"
      row-key="id"
      :default-expand-all="isExpandAll"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column prop="name" label="部门名称" width="260"></el-table-column>
      <el-table-column prop="userCount" label="人数" width="100"></el-table-column>
      <el-table-column prop="orderNum" label="排序" width="200"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <span>{{ scope.row.status === '正常' ? '正常' : '停用' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="200">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
          >新增</el-button>
          <el-button
            v-if="scope.row.parentId !== 0"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改部门对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="deptOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="选择上级部门"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
           <el-col :span="12">
            <el-form-item label="部门编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入部门编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门状态">
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

<script>
import axios from 'axios';
import { ElMessage } from 'element-plus';

export default {
  name: "DeptManagement",
  data() {
    return {
      showSearch: true,
      deptList: [],
      deptOptions: [],
      title: "",
      open: false,
      isExpandAll: true,
      refreshTable: true,
      queryParams: {
        name: undefined,
        status: undefined
      },
      form: {},
      rules: {
        name: [
          { required: true, message: "部门名称不能为空", trigger: "blur" }
        ],
        code: [
          { required: true, message: "部门编码不能为空", trigger: "blur" }
        ],
        orderNum: [
          { required: true, message: "显示排序不能为空", trigger: "blur" }
        ],
        email: [
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        phone: [
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      // 始终获取全量数据以构建完整的树
      axios.get('http://localhost:8080/api/departments').then(response => {
        const fullTree = this.handleTree(response.data);
        if (this.queryParams.name || this.queryParams.status) {
            this.deptList = this.filterTree(fullTree, this.queryParams);
        } else {
            this.deptList = fullTree;
        }
      }).catch(error => {
        ElMessage.error('获取部门列表失败: ' + error.message);
      });
    },
    getTreeselect() {
      axios.get('http://localhost:8080/api/departments').then(response => {
        this.deptOptions = [];
        const dept = { id: 0, name: '主类目', children: [] };
        dept.children = this.handleTree(response.data);
        this.deptOptions.push(dept);
      }).catch(error => {
        ElMessage.error('获取部门树失败: ' + error.message);
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        id: undefined,
        parentId: 0,
        name: undefined,
        code: undefined,
        orderNum: 0,
        leader: undefined,
        phone: undefined,
        email: undefined,
        status: "0"
      };
      if (this.$refs.form) {
        this.$refs.form.resetFields();
      }
    },
    handleQuery() {
      this.getList();
    },
    resetQuery() {
      this.queryParams = {
        name: undefined,
        status: undefined
      };
      if (this.$refs.queryForm) {
          this.$refs.queryForm.resetFields();
      }
      this.handleQuery();
    },
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.id) {
        this.form.parentId = row.id;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = "添加部门";
    },
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      axios.get('http://localhost:8080/api/departments/' + row.id).then(response => {
        this.form = response.data;
        this.form.status = this.form.status === '正常' ? '0' : '1';
        this.open = true;
        this.title = "修改部门";
      }).catch(error => {
        ElMessage.error('获取部门详情失败: ' + error.message);
      });
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const request = this.form.id !== undefined
            ? axios.put('http://localhost:8080/api/departments/' + this.form.id, this.form)
            : axios.post('http://localhost:8080/api/departments', this.form);

          request.then(() => {
            ElMessage.success(this.form.id !== undefined ? "修改成功" : "新增成功");
            this.open = false;
            this.getList();
          }).catch(error => {
            ElMessage.error('操作失败: ' + error.message);
          });
        }
      });
    },
    handleDelete(row) {
      this.$confirm('是否确认删除名称为"' + row.name + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        return axios.delete('http://localhost:8080/api/departments/' + row.id);
      }).then(() => {
        this.getList();
        ElMessage.success("删除成功");
      }).catch(() => {});
    },
    filterTree(nodes, queryParams) {
      if (!queryParams.name && !queryParams.status) {
        return nodes;
      }
      const results = [];
      for (const node of nodes) {
        const nameMatch = queryParams.name ? node.name.includes(queryParams.name) : true;
        const statusMatch = queryParams.status ? (queryParams.status === '0' ? node.status === '正常' : node.status === '停用') : true;
        const isMatch = nameMatch && statusMatch;

        let filteredChildren = [];
        if (node.children && node.children.length > 0) {
          filteredChildren = this.filterTree(node.children, queryParams);
        }
        
        if (isMatch || filteredChildren.length > 0) {
          results.push({
            ...node,
            children: filteredChildren,
          });
        }
      }
      return results;
    },
    handleTree(data, idKey = 'id', parentKey = 'parentId', childrenKey = 'children') {
      const nodeMap = data.reduce((map, node) => {
        map[node[idKey]] = { ...node, [childrenKey]: [] };
        return map;
      }, {});

      const treeData = [];
      const orphans = [];

      Object.values(nodeMap).forEach(node => {
        const parent = nodeMap[node[parentKey]];
        if (parent) {
          parent[childrenKey].push(node);
        } else {
          if (node[parentKey]) {
            orphans.push(node);
          }
          treeData.push(node);
        }
      });

      if (orphans.length > 0) {
        const orphanInfo = orphans.map(o => `${o.name} (父ID: ${o[parentKey]})`).join('、');
        this.$nextTick(() => {
            ElMessage({
                message: `数据警告：下列部门找不到父级，已被分开显示: ${orphanInfo}。请检查数据库中是否存在ID为这些父ID的部门记录。`,
                type: 'warning',
                duration: 0,
                showClose: true,
            });
        });
      }
      
      return treeData;
    },
    formatDateTime(dateTimeString) {
      if (!dateTimeString) return '';
      const date = new Date(dateTimeString);
      const Y = date.getFullYear() + '-';
      const M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
      const D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' ';
      const h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
      const m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
      const s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
      return Y + M + D + h + m + s;
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.mb8 {
  margin-bottom: 8px;
}
</style>