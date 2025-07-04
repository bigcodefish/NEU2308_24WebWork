<template>
	<el-dialog :model-value="visible" :title="isEdit ? '编辑任务' : '创建任务'" width="800px" :show-close="true" append-to-body
		@close="handleClose">
		<el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" class="task-form">
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="任务编号" prop="taskId">
						<el-input v-model="formData.taskId" placeholder="请输入任务编号" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="任务名称" prop="taskName">
						<el-input v-model="formData.taskName" placeholder="请输入任务名称" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="任务类型" prop="taskType">
						<el-select v-model="formData.taskType" placeholder="请选择任务类型" style="width: 100%">
							<el-option label="巡检任务" value="巡检任务" />
							<el-option label="维护任务" value="维护任务" />
							<el-option label="检查任务" value="检查任务" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="优先级" prop="priority">
						<el-select v-model="formData.priority" placeholder="请选择优先级" style="width: 100%">
							<el-option label="高" value="高" />
							<el-option label="中" value="中" />
							<el-option label="低" value="低" />
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="执行人" prop="executor">
						<el-input v-model="formData.executor" placeholder="请输入执行人" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="协助人" prop="assistants">
						<el-input v-model="formData.assistants" placeholder="请输入协助人" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="计划开始时间" prop="plannedStartTime">
						<el-date-picker v-model="formData.plannedStartTime" type="datetime" placeholder="选择开始时间"
							style="width: 100%" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="计划结束时间" prop="plannedEndTime">
						<el-date-picker v-model="formData.plannedEndTime" type="datetime" placeholder="选择结束时间"
							style="width: 100%" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="起始地点" prop="startLocation">
						<el-input v-model="formData.startLocation" placeholder="请输入起始地点" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="终止地点" prop="endLocation">
						<el-input v-model="formData.endLocation" placeholder="请输入终止地点" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="巡检线路" prop="inspectionLine">
						<el-input v-model="formData.inspectionLine" placeholder="请输入巡检线路" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="巡检范围" prop="inspectionScope">
						<el-input v-model="formData.inspectionScope" placeholder="请输入巡检范围" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="巡视距离" prop="inspectionDistance">
						<el-input-number v-model="formData.inspectionDistance" :min="0" :max="9999" :precision="2"
							placeholder="请输入巡视距离" style="width: 100%" />
						<span style="margin-left: 5px; color: #666;">公里</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="任务状态" prop="status">
						<el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
							<el-option label="待执行" value="待执行" />
							<el-option label="执行中" value="执行中" />
							<el-option label="已完成" value="已完成" />
							<el-option label="已暂停" value="已暂停" />
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="完成度" prop="completionRate">
						<el-slider v-model="formData.completionRate" :min="0" :max="100" :step="5" show-input
							input-size="small" />
					</el-form-item>
				</el-col>
			</el-row>

			<el-form-item label="任务描述" prop="description">
				<el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
			</el-form-item>

			<el-form-item label="执行结果" prop="executionResult">
				<el-input v-model="formData.executionResult" type="textarea" :rows="3" placeholder="请输入执行结果" />
			</el-form-item>

			<el-form-item label="发现问题数" prop="issuesFound">
				<el-input-number v-model="formData.issuesFound" :min="0" :max="999" placeholder="发现问题数量" />
			</el-form-item>
		</el-form>

		<template #footer>
			<el-button @click="handleClose">取消</el-button>
			<el-button type="primary" @click="handleSubmit" :loading="loading">
				{{ isEdit ? '更新' : '创建' }}
			</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
	import { ref, reactive, watch } from 'vue'
	import type { FormInstance, FormRules } from 'element-plus'
	import type { Task } from '../stores/task'

	interface Props {
		visible : boolean
		task : Task | null
		isEdit : boolean
	}

	interface Emits {
		(e : 'close') : void
		(e : 'submit', data : Partial<Task>) : void
	}

	const props = defineProps<Props>()
	const emit = defineEmits<Emits>()

	const formRef = ref<FormInstance>()
	const loading = ref(false)

	// 表单数据
	const formData = reactive({
		taskId: '',
		taskName: '',
		taskType: '',
		priority: '',
		description: '',
		executor: '',
		assistants: '',
		plannedStartTime: '',
		plannedEndTime: '',
		actualStartTime: '',
		actualEndTime: '',
		inspectionLine: '',
		startLocation: '',
		endLocation: '',
		inspectionScope: '',
		inspectionDistance: null as number | null,
		status: '待执行',
		completionRate: 0,
		executionResult: '',
		issuesFound: 0
	})

	// 表单验证规则
	const rules : FormRules = {
		taskId: [
			{ required: true, message: '请输入任务编号', trigger: 'blur' }
		],
		taskName: [
			{ required: true, message: '请输入任务名称', trigger: 'blur' }
		],
		taskType: [
			{ required: true, message: '请选择任务类型', trigger: 'change' }
		],
		priority: [
			{ required: true, message: '请选择优先级', trigger: 'change' }
		],
		executor: [
			{ required: true, message: '请输入执行人', trigger: 'blur' }
		],
		plannedStartTime: [
			{ required: true, message: '请选择计划开始时间', trigger: 'change' }
		],
		plannedEndTime: [
			{ required: true, message: '请选择计划结束时间', trigger: 'change' }
		]
	}

	// 监听任务数据变化，填充表单
	watch([() => props.task, () => props.visible, () => props.isEdit], ([newTask, visible, isEdit]) => {
		if (visible) {
			if (newTask && isEdit) {
				// 编辑模式，填充现有数据
				// 逐个设置表单字段，确保响应式更新
				formData.taskId = newTask.taskId || ''
				formData.taskName = newTask.taskName || ''
				formData.taskType = newTask.taskType || ''
				formData.priority = newTask.priority || ''
				formData.description = newTask.description || ''
				formData.executor = newTask.executor || ''
				formData.assistants = newTask.assistants || ''
				formData.plannedStartTime = newTask.plannedStartTime || ''
				formData.plannedEndTime = newTask.plannedEndTime || ''
				formData.actualStartTime = newTask.actualStartTime || ''
				formData.actualEndTime = newTask.actualEndTime || ''
				formData.inspectionLine = newTask.inspectionLine || ''
				formData.startLocation = newTask.startLocation || ''
				formData.endLocation = newTask.endLocation || ''
				formData.inspectionScope = newTask.inspectionScope || ''
				formData.inspectionDistance = newTask.inspectionDistance || null
				formData.status = newTask.status || '待执行'
				formData.completionRate = newTask.completionRate || 0
				formData.executionResult = newTask.executionResult || ''
				formData.issuesFound = newTask.issuesFound || 0
			} else {
				// 创建模式，重置表单，并自动生成唯一任务编号
				formData.taskId = 'TASK-' + Date.now() + '-' + Math.floor(Math.random() * 1000)
				formData.taskName = ''
				formData.taskType = ''
				formData.priority = ''
				formData.description = ''
				formData.executor = ''
				formData.assistants = ''
				formData.plannedStartTime = ''
				formData.plannedEndTime = ''
				formData.actualStartTime = ''
				formData.actualEndTime = ''
				formData.inspectionLine = ''
				formData.startLocation = ''
				formData.endLocation = ''
				formData.inspectionScope = ''
				formData.inspectionDistance = null
				formData.status = '待执行'
				formData.completionRate = 0
				formData.executionResult = ''
				formData.issuesFound = 0
			}
		}
	}, { immediate: true })

	// 关闭对话框
	const handleClose = () => {
		emit('close')
	}

	// 提交表单
	const handleSubmit = async () => {
		if (!formRef.value) return

		function formatDate(dateStr : string) {
			if (!dateStr) return undefined;
			const d = new Date(dateStr);
			const pad = (n : number) => n.toString().padStart(2, '0');
			return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
		}

		try {
			await formRef.value.validate()
			loading.value = true

			// 处理数据格式，确保与后端兼容
			const submitData : Partial<Task> = {
				taskId: formData.taskId || undefined,
				taskName: formData.taskName || undefined,
				taskType: formData.taskType || undefined,
				priority: formData.priority || undefined,
				description: formData.description || undefined,
				executor: formData.executor || undefined,
				assistants: formData.assistants || undefined,
				// 时间字段格式化为 'yyyy-MM-ddTHH:mm:ss'
				plannedStartTime: formatDate(formData.plannedStartTime),
				plannedEndTime: formatDate(formData.plannedEndTime),
				actualStartTime: formatDate(formData.actualStartTime),
				actualEndTime: formatDate(formData.actualEndTime),
				inspectionLine: formData.inspectionLine || undefined,
				startLocation: formData.startLocation || undefined,
				endLocation: formData.endLocation || undefined,
				inspectionScope: formData.inspectionScope || undefined,
				inspectionDistance: formData.inspectionDistance !== null ? Number(formData.inspectionDistance) : undefined,
				status: formData.status || undefined,
				completionRate: Number(formData.completionRate),
				executionResult: formData.executionResult || undefined,
				issuesFound: Number(formData.issuesFound)
			}

			// 移除undefined字段，但保留空字符串（因为某些字段可能允许为空）
			Object.keys(submitData).forEach(key => {
				if (submitData[key as keyof Task] === undefined) {
					delete submitData[key as keyof Task]
				}
			})

			console.log('TaskEditDialog: Submitting form data:', submitData)
			emit('submit', submitData)
		} catch (error) {
			console.error('表单验证失败:', error)
		} finally {
			loading.value = false
		}
	}
</script>

<style scoped>
	.task-form {
		padding: 20px 0;
	}

	.el-form-item {
		margin-bottom: 18px;
	}
</style>