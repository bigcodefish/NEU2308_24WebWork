<template>
	<div class="list-view">
		<div class="search-form">
			<div class="form-item">
				<label>任务编号</label>
				<input type="text" placeholder="请输入任务编号" v-model="searchForm.taskId">
			</div>
			<div class="form-item">
				<label>任务名称</label>
				<input type="text" placeholder="请输入任务名称" v-model="searchForm.taskName">
			</div>
			<div class="form-item">
				<label>创建人</label>
				<input type="text" placeholder="请输入创建人" v-model="searchForm.creator">
			</div>
			<div class="form-item">
				<label>执行人</label>
				<input type="text" placeholder="请输入执行人" v-model="searchForm.assignee">
			</div>
			<div class="search-buttons">

				<div class="btn btn-primary" @click="searchTasks">搜索</div>
				<div class="btn" @click="resetSearch">重置</div>
			</div>
		</div>

		<div class="toolbar">
			<div style="display: flex; gap: 10px;">
				<div class="btn btn-primary" @click="handleCreateTask">创建任务</div>
				<div class="btn" @click="handleExport">导出</div>
			</div>
			<div class="btn" @click="refreshTasks" :class="{ 'loading': taskStore.loading }">
				{{ taskStore.loading ? '刷新中...' : '刷新' }}
			</div>
		</div>

		<div class="table-container">
			<table class="table">
				<thead>
					<tr>
						<th>序号</th>
						<th>任务编号</th>
						<th>任务名称</th>
						<th>任务类型</th>
						<th>起始地点</th>
						<th>终止地点</th>
						<th>巡视距离</th>
						<th>优先级</th>
						<th>执行人</th>
						<th>协助人</th>
						<th>计划开始</th>
						<th>计划结束</th>
						<th>状态</th>
						<th>完成度</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="(task, index) in pagedTasks" :key="task.id">
						<td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
						<td><span class="link" @click="viewTask(task)">{{ task.taskId }}</span></td>
						<td>{{ task.taskName }}</td>
						<td>{{ task.taskType }}</td>
						<td>{{ task.startLocation }}</td>
						<td>{{ task.endLocation }}</td>
						<td>{{ task.inspectionDistance ? task.inspectionDistance + ' 公里' : '-' }}</td>
						<td>{{ task.priority }}</td>
						<td>{{ task.executor }}</td>
						<td>{{ task.assistants }}</td>
						<td>{{ task.plannedStartTime }}</td>
						<td>{{ task.plannedEndTime }}</td>
						<td>{{ task.status }}</td>
						<td>{{ task.completionRate }}%</td>
						<td>
							<span class="link" @click="handleEditTask(task)">编辑</span>
							<span class="link" @click="handleDeleteTask(task)"
								style="margin-left: 10px; color: #ff4444;">删除</span>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div class="pagination">
			共 {{ filteredTasks.length }} 条记录，每页 {{ pageSize }} 条
			<span v-for="page in totalPages" :key="page" :class="['page-btn', { active: page === currentPage } ]"
				@click="handlePageChange(page)">
				{{ page }}
			</span>
		</div>
		<!-- 统一任务详情弹窗 -->
		<TaskDetailDialog :visible="taskDetailVisible" :task="selectedTask" @close="taskDetailVisible = false" />

		<!-- 任务编辑弹窗 -->
		<TaskEditDialog :visible="taskEditVisible" :task="selectedTask" :is-edit="isEditMode"
			@close="taskEditVisible = false" @submit="handleTaskSubmit" />

		<div v-if="exportDialogVisible" class="export-dialog-mask">
			<div class="export-dialog">
				<div class="export-dialog-title">导出任务表格</div>
				<div class="export-dialog-content">
					<label>文件名：</label>
					<input v-model="exportFileName"
						style="width: 220px; padding: 6px; border-radius: 4px; border: 1px solid #ccc;" />
				</div>
				<div class="export-dialog-actions">
					<button @click="confirmExport" class="btn btn-primary">导出</button>
					<button @click="exportDialogVisible = false" class="btn">取消</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { ref, onMounted, computed, watch } from 'vue';
	import TaskDetailDialog from './TaskDetailDialog.vue';
	import TaskEditDialog from './TaskEditDialog.vue';
	import { useTaskStore, type Task } from '../stores/task';

	// 使用任务store
	const taskStore = useTaskStore();

	// 从store获取数据和方法
	const {
		filteredTasks,
		searchForm,
		fetchTasks,
		resetSearch,
		deleteTask
	} = taskStore;

	const searchTasks = async () => {
		await taskStore.searchTasks({
			taskId: searchForm.taskId,
			taskName: searchForm.taskName,
			creator: searchForm.creator,
			assignee: searchForm.assignee
		});
		currentPage.value = 1;
	};

	const refreshTasks = async () => {
		try {
			console.log('ListView: Refreshing tasks...');
			console.log('ListView: Current tasks before refresh:', taskStore.tasks.length);
			await fetchTasks();
			console.log('ListView: Tasks refreshed successfully');
			console.log('ListView: Current tasks after refresh:', taskStore.tasks.length);
		} catch (error) {
			console.error('ListView: Failed to refresh tasks:', error);
			alert('刷新失败，请重试');
		}
	};

	const taskDetailVisible = ref(false);
	const taskEditVisible = ref(false);
	const selectedTask = ref<Task | null>(null);
	const isEditMode = ref(false);

	const pageSize = 5;
	const currentPage = ref(1);

	const pagedTasks = computed(() => {
		const start = (currentPage.value - 1) * pageSize;
		return filteredTasks.slice(start, start + pageSize);
	});

	const totalPages = computed(() => Math.ceil(filteredTasks.length / pageSize));

	function handlePageChange(page : number) {
		currentPage.value = page;
	}

	// 当筛选条件变化时，自动回到第一页
	watch(filteredTasks, () => {
		currentPage.value = 1;
	});

	const viewTask = (task : Task) => {
		selectedTask.value = task;
		taskDetailVisible.value = true;
	};

	const handleDeleteTask = async (task : Task) => {
		if (confirm(`确定要删除任务 "${task.taskName}" 吗？`)) {
			try {
				await deleteTask(task.id);
				alert(`任务 "${task.taskName}" 已删除`);
				// 刷新任务列表
				await refreshTasks();
			} catch {
				alert('删除失败，请重试');
			}
		}
	};

	const handleCreateTask = () => {
		selectedTask.value = null;
		isEditMode.value = false;
		taskEditVisible.value = true;
	};

	const handleEditTask = (task : Task) => {
		selectedTask.value = task;
		isEditMode.value = true;
		taskEditVisible.value = true;
	};

	const handleTaskSubmit = async (taskData : Partial<Task>) => {
		try {
			console.log('ListView: Handling task submit:', { taskData, isEditMode: isEditMode.value })
			if (isEditMode.value && selectedTask.value) {
				// 编辑模式
				await taskStore.updateTask(selectedTask.value.id, taskData);
				alert('任务更新成功');
				// 刷新任务列表
				await refreshTasks();
			} else {
				// 创建模式
				console.log('ListView: Creating new task with data:', taskData)
				await taskStore.createTask(taskData as Omit<Task, 'id'>);
				alert('任务创建成功');
				// 刷新任务列表
				await refreshTasks();
			}
			taskEditVisible.value = false;
		} catch (error) {
			console.error('ListView: 任务操作失败:', error);
			alert('操作失败，请重试');
		}
	};

	// 导出弹窗相关
	const exportDialogVisible = ref(false);
	const exportFileName = ref(getDefaultExportFileName());

	function getDefaultExportFileName() {
		const now = new Date();
		const yyyy = now.getFullYear();
		const mm = String(now.getMonth() + 1).padStart(2, '0');
		const dd = String(now.getDate()).padStart(2, '0');
		return `任务列表${yyyy}${mm}${dd}`;
	}

	function handleExport() {
		exportFileName.value = getDefaultExportFileName();
		exportDialogVisible.value = true;
	}

	function confirmExport() {
		exportDialogVisible.value = false;
		exportTasksToCSV(exportFileName.value);
	}

	function exportTasksToCSV(fileName : string) {
		// 1. 生成 CSV 内容
		const headers = [
			'序号', '任务编号', '任务名称', '任务类型', '起始地点', '终止地点', '巡视距离', '优先级', '执行人', '协助人', '计划开始', '计划结束', '状态', '完成度'
		];
		const rows = filteredTasks.map((task, idx) => [
			idx + 1,
			task.taskId,
			task.taskName,
			task.taskType,
			task.startLocation,
			task.endLocation,
			task.inspectionDistance ? task.inspectionDistance + ' 公里' : '-',
			task.priority,
			task.executor,
			task.assistants,
			task.plannedStartTime,
			task.plannedEndTime,
			task.status,
			task.completionRate + '%'
		]);
		const csvContent = [headers, ...rows].map(row => row.map(cell => `"${String(cell).replace(/"/g, '""')}"`).join(',')).join('\r\n');

		// 2. 添加 UTF-8 BOM 以防止中文乱码
		const BOM = '\uFEFF';
		const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' });
		const url = URL.createObjectURL(blob);
		const link = document.createElement('a');
		link.href = url;
		link.download = (fileName || '任务列表') + '.csv';
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
		URL.revokeObjectURL(url);
	}

	// 组件挂载时获取数据
	onMounted(() => {
		fetchTasks();
	});
</script>

<style scoped>
	.list-view {
		display: flex;
		flex-direction: column;
		height: 100%;

	}

	.search-form {
		display: flex;
		gap: 15px;
		margin-bottom: 20px;
		padding: 15px;
		border: 1px solid #666;
		background: #f8f8f8;
		flex-wrap: wrap;
		border-radius: 4px;
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

	.form-item input {
		border: 1px solid #999;
		padding: 8px;
		width: 120px;
		border-radius: 4px;
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
		border-radius: 4px;
		transition: background 0.3s;
	}

	.btn:hover {
		background: #e0e0e0;
	}

	.btn-primary {
		background: #e6f3ff;
	}

	.btn-primary:hover {
		background: #cce5ff;
	}

	.btn.loading {
		opacity: 0.7;
		cursor: not-allowed;
		background: #e0e0e0;
	}

	.toolbar {
		display: flex;
		justify-content: space-between;
		margin-bottom: 15px;
		padding: 10px;
		border: 1px solid #666;
		background: #f8f8f8;
		border-radius: 4px;
	}

	.table-container {
		flex: 1;
		overflow: auto;
		border: 1px solid #666;
		border-radius: 4px;
	}

	.table {
		width: 100%;
		border-collapse: collapse;
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
		position: sticky;
		top: 0;
	}

	.table tbody tr:nth-child(even) {
		background: #f9f9f9;
	}

	.link {
		color: #0066cc;
		text-decoration: underline;
		cursor: pointer;
	}

	.link:hover {
		color: #004d99;
	}

	.pagination {
		margin-top: 20px;
		text-align: right;
		border: 1px solid #666;
		padding: 10px;
		background: #f8f8f8;
		border-radius: 4px;
	}

	.page-btn {
		display: inline-block;
		margin: 0 4px;
		padding: 2px 8px;
		border: 1px solid #aaa;
		border-radius: 3px;
		cursor: pointer;
		background: #fff;
	}

	.page-btn.active {
		background: #409eff;
		color: #fff;
		border-color: #409eff;
	}

	@media (max-width: 1200px) {
		.table-container {
			overflow-x: auto;
		}
	}

	@media (max-width: 768px) {
		.search-form {
			flex-direction: column;
			gap: 10px;
		}

		.form-item {
			width: 100%;
		}

		.form-item input {
			width: 100%;
		}

		.search-buttons {
			width: 100%;
			justify-content: center;
			margin-top: 10px;
		}

		.toolbar {
			flex-direction: column;
			gap: 10px;
		}

		.table th,
		.table td {
			padding: 8px 5px;
			font-size: 12px;
		}
	}

	.export-dialog-mask {
		position: fixed;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.25);
		z-index: 9999;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.export-dialog {
		background: #fff;
		border-radius: 8px;
		min-width: 320px;
		box-shadow: 0 2px 16px rgba(0, 0, 0, 0.18);
		padding: 24px 20px 18px 20px;
		display: flex;
		flex-direction: column;
		gap: 16px;
	}

	.export-dialog-title {
		font-size: 18px;
		font-weight: bold;
		margin-bottom: 6px;
	}

	.export-dialog-content {
		display: flex;
		align-items: center;
		gap: 8px;
	}

	.export-dialog-actions {
		display: flex;
		gap: 16px;
		justify-content: flex-end;
	}
</style>