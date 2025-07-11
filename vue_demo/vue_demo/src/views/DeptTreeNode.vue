<template>
  <div class="dept-node">
    <!-- 当前部门节点 -->
    <div 
      class="dept-item"
      :class="{ 'has-children': hasChildren }"
      @click="handleNodeClick"
    >
      <span 
        class="dept-name"
        :style="{ paddingLeft: `${level * 20 + (hasChildren ? 0 : 20)}px` }"
      >
        <el-icon v-if="hasChildren" class="expand-icon" :class="{ rotated: dept.expanded }">
          <ArrowRight />
        </el-icon>
        {{ dept.name }}
        <span v-if="showUserCount" class="user-count">({{ dept.userCount || 0 }})</span>
      </span>
      
      <!-- 操作按钮 -->
      <div class="dept-actions" v-if="showActions" @click.stop>
        <el-button size="small" @click="handleAddChild(dept.id)" v-if="allowAddChild">
          <el-icon><Plus /></el-icon>
        </el-button>
        <el-button size="small" type="primary" @click="handleEdit(dept)">
          <el-icon><Edit /></el-icon>
        </el-button>
        <el-button 
          size="small" 
          :type="dept.status === '1' ? 'success' : 'warning'" 
          @click="handleToggleStatus(dept)"
        >
          <el-icon>
            <CircleCheck v-if="dept.status === '0'" />
            <CircleClose v-else />
          </el-icon>
        </el-button>
        <el-button size="small" type="danger" @click="handleDelete(dept)" v-if="allowDelete">
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 子部门列表 -->
    <div class="dept-children" v-if="dept.expanded && hasChildren">
      <dept-tree-node
        v-for="child in dept.children"
        :key="child.id"
        :dept="child"
        :level="level + 1"
        :show-actions="showActions"
        :show-user-count="showUserCount"
        :allow-add-child="allowAddChild"
        :allow-delete="allowDelete"
        :max-depth="maxDepth"
        @node-click="$emit('node-click', $event)"
        @add-child="$emit('add-child', $event)"
        @edit="$emit('edit', $event)"
        @toggle-status="$emit('toggle-status', $event)"
        @delete="$emit('delete', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { 
  ArrowRight, 
  Plus, 
  Edit, 
  Delete, 
  CircleCheck, 
  CircleClose 
} from '@element-plus/icons-vue'

const props = defineProps({
  dept: { type: Object, required: true },
  level: { type: Number, default: 0 },
  showActions: { type: Boolean, default: true },
  showUserCount: { type: Boolean, default: false },
  allowAddChild: { type: Boolean, default: true },
  allowDelete: { type: Boolean, default: true },
  maxDepth: { type: Number, default: 5 } // 最大层级深度
})

const emit = defineEmits([
  'node-click',
  'add-child',
  'edit',
  'toggle-status',
  'delete'
])

const hasChildren = computed(() => {
  return props.dept.children && props.dept.children.length > 0
})

const handleNodeClick = () => {
  if (hasChildren.value) {
    props.dept.expanded = !props.dept.expanded
  }
  emit('node-click', props.dept)
}

const handleAddChild = (parentId) => {
  if (props.level >= props.maxDepth - 1) {
    ElMessage.warning(`部门层级不能超过${props.maxDepth}级`)
    return
  }
  emit('add-child', parentId)
}

const handleEdit = (dept) => {
  emit('edit', dept)
}

const handleToggleStatus = (dept) => {
  emit('toggle-status', {
    id: dept.id,
    status: dept.status === '0' ? '1' : '0'
  })
}

const handleDelete = (dept) => {
  emit('delete', dept)
}
</script>

<style scoped>
.dept-node {
  font-size: 14px;
}

.dept-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  margin: 2px 0;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.dept-item:hover {
  background-color: #f5f7fa;
}

.dept-item.has-children {
  font-weight: 500;
}

.dept-name {
  display: flex;
  align-items: center;
  flex-grow: 1;
}

.expand-icon {
  margin-right: 6px;
  transition: transform 0.3s;
  font-size: 12px;
}

.expand-icon.rotated {
  transform: rotate(90deg);
}

.user-count {
  margin-left: 6px;
  color: #909399;
  font-size: 12px;
}

.dept-actions {
  display: flex;
  gap: 4px;
  margin-left: 10px;
}

.dept-actions .el-button {
  padding: 5px;
}

.dept-children {
  margin-left: 20px;
  border-left: 1px dashed #e0e0e0;
  padding-left: 8px;
}
</style>