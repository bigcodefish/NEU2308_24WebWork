<template>
	<div class="dashboard-container">
		<div class="wireframe">
			<div class="header">
				<div class="title">地铁隧道巡线大数据仿真和分析平台</div>
				<div class="enter-btn" @click="enterSystem">进入系统</div>
			</div>

			<div class="main-content">
				<!-- 左侧面板 -->
				<div class="left-panel">
					<div class="card">
						<div class="card-title">巡视数据统计</div>
						<div class="stats-grid">
							<div class="stat-item">
								<div class="stat-number">{{ todayTaskCount }}</div>
								<div class="stat-label">今日巡视数</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">{{ yesterdayTaskCount }}</div>
								<div class="stat-label">昨日巡视数</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">{{ taskGrowthRate }}%</div>
								<div class="stat-label">环比增长</div>
							</div>
						</div>
						<div class="stats-grid">
							<div class="stat-item">
								<div class="stat-number">{{ todayDistance }}km</div>
								<div class="stat-label">今日巡视距离</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">{{ yesterdayDistance }}km</div>
								<div class="stat-label">昨日巡视距离</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">{{ distanceGrowthRate }}%</div>
								<div class="stat-label">环比增长</div>
							</div>
						</div>
					</div>

					<div class="card" style="min-height: 300px;">
						<div class="card-title">缺陷类型统计 (共 {{ defectTypeCount }} 种)</div>
						<div class="chart-area" ref="pieChart" style="height: 100%; min-height: 250px;"></div>
					</div>

					<div class="card">
						<div class="card-title">人员数据统计</div>
						<div class="chart-area" ref="personTaskChart" style="height: 100%; min-height: 250px;"></div>
					</div>
				</div>

				<!-- 中间面板 -->
				<div class="center-panel">
					<div class="center-stats">
						<div class="center-stats-row">
							<div class="center-stat">
								<div class="center-stat-number">
									{{ formatDistance(taskStore.taskStats.totalDistance) }}
								</div>
								<div class="center-stat-label">巡视总距离(公里)</div>
							</div>
							<div class="center-stat">
								<div class="center-stat-number">
									{{ taskStore.taskStats.totalTasks || 0 }}
								</div>
								<div class="center-stat-label">巡视总次数</div>
							</div>
						</div>
					</div>

					<div class="earth-section">
						<div class="map-layers">
							<div>图层1: 路线图</div>
							<div>图层2: 地铁图</div>
							<div>图层3: 地图背景</div>
						</div>
						地图可视化区域<br>
						(地铁路线、巡视轨迹、实时位置)
					</div>
				</div>

				<!-- 右侧面板 -->
				<div class="right-panel">
					<div class="card" style="min-height: auto;">
						<div class="card-title">缺陷数据统计</div>
						<div class="stats-grid">
							<div class="stat-item">
								<div class="stat-number large">{{ defectStats.totalDefects || 0 }}</div>
								<div class="stat-label">累计缺陷数</div>
							</div>
							<div class="stat-item">
								<div class="stat-number large">{{ defectStats.confirmedDefects || 0 }}</div>
								<div class="stat-label">确认缺陷数</div>
							</div>
							<div class="stat-item">
								<div class="stat-number large">{{ defectStats.falseDefects || 0 }}</div>
								<div class="stat-label">误报缺陷数</div>
							</div>
						</div>
						<div class="stats-grid">
							<div class="stat-item">
								<div class="stat-number large">{{ processedDefects || 0 }}</div>
								<div class="stat-label">已处理缺陷</div>
							</div>
							<div class="stat-item">
								<div class="stat-number large">{{ processingRate }}%</div>
								<div class="stat-label">处理率</div>
							</div>
						</div>
						<div class="stats-grid">
							<div class="stat-item">
								<div class="stat-number">{{ severityStats.high || 0 }}</div>
								<div class="stat-label">严重</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">{{ severityStats.medium || 0 }}</div>
								<div class="stat-label">一般</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">{{ severityStats.low || 0 }}</div>
								<div class="stat-label">轻微</div>
							</div>
						</div>
					</div>

					<div class="card">
						<div class="card-title">每月巡检次数</div>
						<div class="chart-area" ref="monthlyTaskChart" style="height: 100%; min-height: 200px;"></div>
					</div>

					<div class="card">
						<div class="card-title">缺陷数据变化</div>
						<div class="chart-area" ref="lineChart" style="height: 100%; min-height: 200px;"></div>
					</div>
				</div>
			</div>
		</div>

		<!-- 缺陷类型详情模态框 -->
		<div v-if="defectTypeModalVisible" class="modal" @click.self="closeDefectTypeModal">
			<div class="modal-content" style="width: 800px; max-height: 80vh;">
				<span class="close" @click="closeDefectTypeModal">&times;</span>
				<h3 style="margin-bottom: 20px;">缺陷类型详情 - {{ selectedDefectType }}</h3>

				<div class="defect-stats">
					<div class="stat-row">
						<div class="stat-item">
							<div class="stat-number">{{ defectTypeStats.total }}</div>
							<div class="stat-label">总数</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ defectTypeStats.confirmed }}</div>
							<div class="stat-label">已确认</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ defectTypeStats.falseDefects }}</div>
							<div class="stat-label">误报</div>
						</div>
					</div>
					<div class="stat-row">
						<div class="stat-item">
							<div class="stat-number">{{ defectTypeStats.highSeverity }}</div>
							<div class="stat-label">高严重</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ defectTypeStats.mediumSeverity }}</div>
							<div class="stat-label">中严重</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ defectTypeStats.lowSeverity }}</div>
							<div class="stat-label">低严重</div>
						</div>
					</div>
				</div>

				<div class="defect-list">
					<h4>该类型缺陷记录</h4>
					<table class="mini-table">
						<thead>
							<tr>
								<th>缺陷编号</th>
								<th>位置</th>
								<th>严重程度</th>
								<th>状态</th>
								<th>上报时间</th>
							</tr>
						</thead>
						<tbody>
							<tr v-for="defect in recentDefects" :key="defect.id">
								<td>{{ defect.defectNo || 'DEF-' + defect.id }}</td>
								<td>{{ defect.location || '-' }}</td>
								<td :class="getSeverityClass(defect.severity)" style="background-color: transparent;">
									{{ defect.severity || '-' }}
								</td>
								<td :class="getStatusClass(defect.status)" style="background-color: transparent;">
									{{ defect.status || '-' }}
								</td>
								<td>{{ formatDate(defect.reportTime) }}</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>

		<!-- 月度详情模态框模板 -->
		<div v-if="monthlyModalVisible" class="modal" @click.self="closeMonthlyModal">
			<div class="modal-content" style="width: 1000px; max-height: 90vh;">
				<span class="close" @click="closeMonthlyModal">&times;</span>
				<h3 style="margin-bottom: 20px;">{{ selectedMonth }} 缺陷统计</h3>

				<div class="defect-stats">
					<div class="stat-row">
						<div class="stat-item">
							<div class="stat-number">{{ monthlyStats.total }}</div>
							<div class="stat-label">总数</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ monthlyStats.confirmed }}</div>
							<div class="stat-label">已确认</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ monthlyStats.falseDefects }}</div>
							<div class="stat-label">误报</div>
						</div>
					</div>
					<div class="stat-row">
						<div class="stat-item">
							<div class="stat-number">{{ monthlyStats.highSeverity }}</div>
							<div class="stat-label">高严重</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ monthlyStats.mediumSeverity }}</div>
							<div class="stat-label">中严重</div>
						</div>
						<div class="stat-item">
							<div class="stat-number">{{ monthlyStats.lowSeverity }}</div>
							<div class="stat-label">低严重</div>
						</div>
					</div>
				</div>

				<!-- 缺陷列表 -->
				<div class="defect-list">
					<h4>全部缺陷记录 (共 {{ monthlyDefects.length }} 条)</h4>
					<div style="max-height: 500px; overflow-y: auto;">
						<table class="mini-table">
							<thead>
								<tr>
									<th>缺陷编号</th>
									<th>类型</th>
									<th>位置</th>
									<th>严重程度</th>
									<th>状态</th>
									<th>上报时间</th>
								</tr>
							</thead>
							<tbody>
								<tr v-for="defect in monthlyDefects" :key="defect.id">
									<td>{{ defect.defectNo || 'DEF-' + defect.id }}</td>
									<td>{{ defect.defectType || '-' }}</td>
									<td>{{ defect.location || '-' }}</td>
									<td>{{ defect.severity || '-' }}</td>
									<td :class="getStatusClass(defect.status)">
										{{ defect.status || '-' }}
									</td>
									<td>{{ formatDate(defect.reportTime) }}</td>
								</tr>
								<tr v-if="monthlyDefects.length === 0">
									<td colspan="6" style="text-align: center;">暂无数据</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>

		<!-- 人员任务详情模态框 -->
		<div v-if="personTaskModalVisible" class="modal" @click.self="closePersonTaskModal">
			<div class="modal-content" style="width: 800px; max-height: 80vh;">
				<span class="close" @click="closePersonTaskModal">&times;</span>
				<h3 style="margin-bottom: 20px;">人员任务详情 - {{ selectedPerson }}</h3>

				<div class="defect-stats">
					<div class="stat-row">
						<div class="stat-item">
							<div class="stat-number">{{ personTaskStatsDetails.total }}</div>
							<div class="stat-label">任务总数</div>
						</div>
						<!-- 可以根据实际需求添加更多统计信息 -->
					</div>
				</div>

				<div class="defect-list">
					<h4>该人员任务记录</h4>
					<table class="mini-table">
						<thead>
							<tr>
								<th>任务编号</th>
								<th>任务名称</th>
								<th>任务状态</th>
								<th>执行时间</th>
								<th>巡检范围</th>
								<th>巡检距离</th>
							</tr>
						</thead>
						<tbody>
							<tr v-for="task in personTaskDetails" :key="task.id">
								<td>{{ task.taskNo || 'TASK-' + task.id }}</td>
								<td>{{ task.taskName || '-' }}</td>
								<td :class="getStatusClass(task.status)">{{ task.status || '-' }}</td>
								<td>{{ formatDate(task.plannedStartTime) }} 至 {{ formatDate(task.plannedEndTime) }}</td>
								<td>{{ task.inspectionScope || '-' }}</td>
								<td>{{ task.inspectionDistance ? task.inspectionDistance + ' 公里' : '未设置' }}</td>
							</tr>
							<tr v-if="personTaskDetails.length === 0">
								<td colspan="7" style="text-align: center;">暂无数据</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>

		<!-- 每月巡检详情模态框 -->
		<div v-if="monthlyTaskModalVisible" class="modal" @click.self="monthlyTaskModalVisible = false">
			<div class="modal-content" style="width: 800px; max-height: 80vh;">
				<span class="close" @click="monthlyTaskModalVisible = false">&times;</span>
				<h3 style="margin-bottom: 20px;">{{ selectedMonthlyTaskMonth }} 巡检详情</h3>

				<div class="defect-list">
					<h4>该月任务记录</h4>
					<table class="mini-table">
						<thead>
							<tr>
								<th>任务编号</th>
								<th>任务名称</th>
								<th>任务状态</th>
								<th>计划开始时间</th>
								<th>计划结束时间</th>
								<th>巡检线路</th>
								<th>巡检范围</th>
								<th>巡视距离</th>
							</tr>
						</thead>
						<tbody>
							<tr v-for="task in monthlyTaskDetails" :key="task.id">
								<td>{{ task.taskNo || 'TASK-' + task.id }}</td>
								<td>{{ task.taskName || '-' }}</td>
								<td :class="getStatusClass(task.status)">{{ task.status || '-' }}</td>
								<td>{{ formatDate(task.plannedStartTime) }}</td>
								<td>{{ formatDate(task.plannedEndTime) }}</td>
								<td>{{ task.inspectionLine || '-' }}</td>
								<td>{{ task.inspectionScope || '-' }}</td>
								<td>{{ task.inspectionDistance ? task.inspectionDistance + ' 公里' : '未设置' }}</td>
							</tr>
							<tr v-if="monthlyTaskDetails.length === 0">
								<td colspan="8" style="text-align: center;">暂无数据</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { ref, onMounted, nextTick, computed, onUnmounted, watch } from 'vue'
	import router from '@/router'
	import axios from 'axios'
	import * as echarts from 'echarts'
	import { useTaskStore } from '../stores/task'

	const API_URL = 'http://localhost:8080/api/defects'
	const API_URL_TASK = 'http://localhost:8080/api/tasks'


	const taskStore = useTaskStore()

	const todayTaskCount = ref(0);
	const yesterdayTaskCount = ref(0);
	const taskGrowthRate = ref(0);
	const todayDistance = ref(0);
	const yesterdayDistance = ref(0);
	const distanceGrowthRate = ref(0);

	const monthlyTaskChart = ref<HTMLElement | null>(null)
	const monthlyTaskData = ref<{ month : string; count : number }[]>([])

	const personTaskChart = ref<HTMLElement | null>(null)
	const personTaskStats = ref<{ executor : string; taskCount : number }[]>([])

	const personTaskModalVisible = ref(false)
	const selectedPerson = ref('')
	const personTaskStatsDetails = ref({
		total: 0
	})
	const personTaskDetails = ref<any[]>([])

	const monthlyTaskModalVisible = ref(false);
	const selectedMonthlyTaskMonth = ref('');
	const monthlyTaskDetails = ref<any[]>([]);

	// 添加获取严重程度样式类的方法
	const getSeverityClass = (severity : string | null) => {
		if (!severity) return '';
		switch (severity) {
			case '高': return 'severity-high';
			case '中': return 'severity-medium';
			case '低': return 'severity-low';
			default: return '';
		}
	};


	// 获取人员的任务统计信息
	const fetchPersonTaskStats = async () => {
		try {
			const res = await axios.get(`${API_URL_TASK}/stats/person`)
			personTaskStats.value = res.data
			renderPersonTaskChart()
		} catch (e) {
			console.error('获取人员任务统计信息失败', e)
		}
	}

	// 渲染人员任务统计柱状图
	const renderPersonTaskChart = () => {
		if (!personTaskChart.value || personTaskStats.value.length === 0) return;

		const chart = echarts.init(personTaskChart.value);

		const xData = personTaskStats.value.map(item => item.executor);
		const yData = personTaskStats.value.map(item => item.taskCount);

		const option : echarts.EChartsOption = {
			tooltip: {
				trigger: 'axis',
				axisPointer: {
					type: 'shadow'
				},
				backgroundColor: 'rgba(0, 0, 0, 0.7)',
				textStyle: {
					color: '#fff'
				}
			},
			xAxis: {
				type: 'category',
				data: xData,
				axisLabel: {
					color: '#fff',
					rotate: 45,
					fontSize: 12
				},
				axisLine: {
					lineStyle: {
						color: '#00a8e8'
					}
				},
				axisTick: {
					show: false
				}
			},
			yAxis: {
				type: 'value',
				axisLabel: {
					color: '#fff',
					fontSize: 12
				},
				axisLine: {
					lineStyle: {
						color: '#00a8e8'
					}
				},
				splitLine: {
					lineStyle: {
						color: 'rgba(0, 168, 232, 0.2)'
					}
				},
				axisTick: {
					show: false
				}
			},
			series: [
				{
					data: yData,
					type: 'bar',
					itemStyle: {
						color: new echarts.graphic.LinearGradient(
							0, 0, 0, 1,
							[
								{ offset: 0, color: '#00a8e8' },
								{ offset: 1, color: '#007bbf' }
							]
						),
						borderRadius: [5, 5, 0, 0]
					},
					barWidth: '60%',
					label: {
						show: true,
						position: 'top',
						color: '#fff',
						fontSize: 12
					}
				}
			],
			grid: {
				left: '5%',
				right: '5%',
				bottom: '15%',
				top: '10%',
				containLabel: true
			}
		};

		chart.setOption(option);

		// 添加点击事件
		chart.on('click', (params : echarts.ECElementEvent) => {
			const data = params.data as number;
			const executor = xData[params.dataIndex];
			if (executor) {
				selectedPerson.value = executor;
				(async () => {
					await fetchPersonTaskDetails(executor);
					personTaskModalVisible.value = true;
				})();
			}
		});

		// 窗口大小变化时重绘
		const resizeHandler = () => chart.resize();
		window.addEventListener('resize', resizeHandler);
		onUnmounted(() => {
			window.removeEventListener('resize', resizeHandler);
			chart.dispose();
		});
	};

	// 获取指定人员的任务详细信息
	const fetchPersonTaskDetails = async (executor : string) => {
		try {
			const res = await axios.get(`${API_URL_TASK}/person-detail`, {
				params: {
					executor
				}
			});
			personTaskStatsDetails.value = {
				total: res.data.length
			};
			personTaskDetails.value = res.data;
		} catch (e) {
			console.error('获取人员任务详情失败', e);
		}
	};

	// 获取每月的巡检次数
	const fetchMonthlyTaskCount = async () => {
		try {
			const res = await axios.get(`${API_URL_TASK}/monthly-count`)
			monthlyTaskData.value = res.data.map((item : any) => ({
				month: item.month,
				count: item.count || 0
			}))

			// 数据获取后渲染图表
			renderMonthlyTaskChart()
		} catch (e) {
			console.error('获取每月巡检次数失败', e)
			monthlyTaskData.value = []
		}
	}

	const closePersonTaskModal = () => {
		personTaskModalVisible.value = false
	}

	// 渲染每月巡检次数折线图
	const renderMonthlyTaskChart = () => {
		if (!monthlyTaskChart.value || monthlyTaskData.value.length === 0) return;

		const chart = echarts.init(monthlyTaskChart.value);

		// 只提取月份信息
		const xData = monthlyTaskData.value.map(item => item.month.split('-')[1]);
		const yData = monthlyTaskData.value.map(item => item.count);

		// 配置项
		const option : echarts.EChartsOption = {
			tooltip: {
				trigger: 'axis',
				backgroundColor: 'rgba(0, 0, 0, 0.7)', // 修改提示框背景颜色
				textStyle: {
					color: '#fff' // 修改提示框文字颜色
				}
			},
			xAxis: {
				type: 'category',
				data: xData,
				axisLabel: {
					color: '#fff', // 修改 x 轴标签颜色为白色
					rotate: 45, // 旋转 x 轴标签
					fontSize: 14 // 增大 x 轴标签字体大小
				},
				axisLine: {
					lineStyle: {
						color: '#999' // 修改 x 轴轴线颜色
					}
				},
				splitLine: {
					show: false // 隐藏 x 轴分割线
				}
			},
			yAxis: {
				type: 'value',
				axisLabel: {
					color: '#fff', // 修改 y 轴标签颜色为白色
					fontSize: 14 // 增大 y 轴标签字体大小
				},
				axisLine: {
					lineStyle: {
						color: '#999' // 修改 y 轴轴线颜色
					}
				},
				splitLine: {
					lineStyle: {
						color: '#eee' // 修改 y 轴分割线颜色
					}
				}
			},
			series: [
				{
					data: yData,
					type: 'line',
					smooth: true,
					itemStyle: {
						color: '#007BFF' // 修改数据点颜色
					},
					lineStyle: {
						color: '#007BFF', // 修改线条颜色
						width: 2 // 修改线条宽度
					},
					areaStyle: {
						color: 'rgba(0, 123, 255, 0.1)' // 添加区域填充颜色
					}
				}
			],
			grid: {
				left: '5%',
				right: '5%',
				bottom: '15%',
				top: '10%',
				containLabel: true
			}
		};

		chart.setOption(option);

		// 添加点击事件
		chart.on('click', (params : echarts.ECElementEvent) => {
			const month = monthlyTaskData.value[params.dataIndex].month;
			if (month) {
				selectedMonthlyTaskMonth.value = month;
				(async () => {
					await fetchMonthlyTaskDetails(month);
					monthlyTaskModalVisible.value = true;
				})();
			}
		});

		// 窗口大小变化时重绘
		const resizeHandler = () => chart.resize();
		window.addEventListener('resize', resizeHandler);
		onUnmounted(() => {
			window.removeEventListener('resize', resizeHandler);
			chart.dispose();
		});
	};

	// 获取指定月份的巡检详细信息
	const fetchMonthlyTaskDetails = async (month : string) => {
		try {
			const res = await axios.get(`${API_URL_TASK}/monthly-detail`, {
				params: { month }
			});
			monthlyTaskDetails.value = res.data;
		} catch (e) {
			console.error('获取每月巡检详情失败', e);
			monthlyTaskDetails.value = [];
		}
	};

	// 获取今日巡检数量、昨日巡检数量、今日巡视距离、昨日巡视距离
	const fetchDailyTaskStats = async () => {
		try {
			const res = await axios.get(`${API_URL_TASK}/daily-stats`);
			todayTaskCount.value = res.data.todayTaskCount;
			yesterdayTaskCount.value = res.data.yesterdayTaskCount;
			todayDistance.value = res.data.todayDistance;
			yesterdayDistance.value = res.data.yesterdayDistance;

			// 计算环比增长
			if (yesterdayTaskCount.value !== 0) {
				taskGrowthRate.value = parseFloat((((todayTaskCount.value - yesterdayTaskCount.value) / yesterdayTaskCount.value) * 100).toFixed(1));
			}
			if (yesterdayDistance.value !== 0) {
				distanceGrowthRate.value = parseFloat((((todayDistance.value - yesterdayDistance.value) / yesterdayDistance.value) * 100).toFixed(1));
			}
		} catch (e) {
			console.error('获取每日任务统计数据失败', e);
		}
	};

	// 添加格式化距离的方法
	const formatDistance = (distance : number | undefined) => {
		if (distance === undefined || distance === null) return '0.0'
		return distance.toFixed(1)
	}

	// 缺陷统计
	const defectStats = ref({
		totalDefects: 0,
		confirmedDefects: 0,
		falseDefects: 0,
		highSeverity: 0,
		mediumSeverity: 0,
		lowSeverity: 0
	})

	// 状态数据
	const defectTypeModalVisible = ref(false)
	const selectedDefectType = ref('')
	const defectTypeStats = ref({
		total: 0,
		confirmed: 0,
		falseDefects: 0,
		highSeverity: 0,
		mediumSeverity: 0,
		lowSeverity: 0
	})
	const recentDefects = ref<any[]>([])

	// 月度状态数据
	const monthlyModalVisible = ref(false)
	const selectedMonth = ref('')
	const monthlyStats = ref({
		total: 0,
		confirmed: 0,
		falseDefects: 0,
		highSeverity: 0,
		mediumSeverity: 0,
		lowSeverity: 0
	})
	const monthlyDefects = ref<any[]>([])

	// 缺陷类型数据
	const defectTypes = ref<{ type : string; count : number }[]>([])
	const pieChart = ref<HTMLElement | null>(null)

	// 计算缺陷类型数量
	const defectTypeCount = computed(() => {
		return defectTypes.value.length
	})

	const lineChart = ref<HTMLElement | null>(null)
	const monthlyDefectStats = ref<{ month : string; count : number }[]>([])

	// 新增的状态数据
	const processedDefects = ref(0)
	const severityStats = ref({
		high: 0,
		medium: 0,
		low: 0
	})

	// 计算处理率
	const processingRate = computed(() => {
		if (defectStats.value.confirmedDefects === 0) return 0
		return Math.round((processedDefects.value / defectStats.value.confirmedDefects) * 100)
	})

	const enterSystem = () => {
		router.push('/task')
	}

	// 获取缺陷统计数据
	const fetchDefectStats = async () => {
		try {
			const res = await axios.get(`${API_URL}/stats`)
			defectStats.value = res.data

			// 获取已处理缺陷数
			const fixedRes = await axios.get(API_URL, {
				params: {
					status: '已整改'
				}
			})
			processedDefects.value = fixedRes.data.length

			// 获取缺陷等级分布
			const highRes = await axios.get(API_URL, {
				params: {
					severity: '高'
				}
			})
			const mediumRes = await axios.get(API_URL, {
				params: {
					severity: '中'
				}
			})
			const lowRes = await axios.get(API_URL, {
				params: {
					severity: '低'
				}
			})

			severityStats.value = {
				high: highRes.data.length,
				medium: mediumRes.data.length,
				low: lowRes.data.length
			}
		} catch (e) {
			console.error('获取缺陷统计数据失败', e)
		}
	}

	// 获取缺陷类型统计数据
	const fetchDefectTypeStats = async () => {
		try {
			const res = await axios.get(`${API_URL}/type-stats`)
			defectTypes.value = res.data.map((item : any) => ({
				type: item.defectType,
				count: item.count
			}))

			// 数据获取后渲染图表
			renderPieChart()
		} catch (e) {
			console.error('获取缺陷类型统计数据失败', e)
		}
	}

	// 渲染饼图
	const renderPieChart = () => {
		if (!pieChart.value || defectTypes.value.length === 0) return;

		const chart = echarts.init(pieChart.value);

		// 配置项
		const option : echarts.EChartsOption = {
			tooltip: {
				trigger: 'item',
				formatter: '{a} <br/>{b}: {c} ({d}%)'
			},
			legend: {
				orient: 'vertical',
				right: 10,
				top: 'center',
				textStyle: {
					color: '#fff',
					fontSize: 14 // 增大图例文字大小
				},
				itemWidth: 14,
				itemHeight: 14,
				itemGap: 10,
				formatter: (name) => {
					const data = defectTypes.value.find(item => item.type === name);
					return `${name} (${data?.count || 0})`;
				}
			},
			series: [
				{
					name: '缺陷类型分布',
					type: 'pie',
					radius: ['50%', '70%'],
					center: ['40%', '50%'],
					avoidLabelOverlap: false,
					itemStyle: {
						borderRadius: 6,
						borderColor: '#000',
						borderWidth: 2,
						shadowBlur: 10,
						shadowColor: 'rgba(0, 0, 0, 0.5)'
					},
					label: {
						show: true,
						formatter: '{b}: {d}%',
						position: 'outside',
						alignTo: 'edge',
						margin: 5,
						color: '#fff',
						fontSize: 12
					},
					labelLine: {
						show: false // 移除标签线
					},
					emphasis: {
						scale: true,
						scaleSize: 8,
						label: {
							show: true,
							fontSize: '14',
							fontWeight: 'bold',
							color: '#fff'
						},
						itemStyle: {
							shadowBlur: 10,
							shadowOffsetX: 0,
							shadowColor: 'rgba(0, 0, 0, 0.5)'
						}
					},
					data: defectTypes.value.map(item => ({
						value: item.count,
						name: item.type
					}))
				}
			],
			color: ['#00fff7', '#002eff', '#8af7eb', '#a5c9f4', '#6f6490', '#ae74ff', '#7a3cff', '#0b89ff', '#068bff', '#03afff'],
			grid: {
				top: 0,
				left: 0,
				right: 0,
				bottom: 0,
				containLabel: true
			}
		};

		chart.setOption(option);

		// 添加点击事件
		chart.on('click', (params : echarts.ECElementEvent) => {
			const data = params.data as { name : string; value : number };
			if (data?.name) {
				selectedDefectType.value = data.name;
				(async () => {
					await fetchDefectTypeDetails(data.name);
					defectTypeModalVisible.value = true;
				})();
			}
		});

		window.addEventListener('resize', () => {
			chart.resize();
		});
	};


	// 获取缺陷类型详情
	const fetchDefectTypeDetails = async (defectType : string) => {
		try {
			// 获取统计信息
			const statsRes = await axios.get(`${API_URL}/type-details`, {
				params: { defectType }
			})
			defectTypeStats.value = statsRes.data

			// 获取最近5条记录
			const recentRes = await axios.get(API_URL, {
				params: {
					defectType,
					pageSize: 5,
					sortField: 'report_time',
					sortDirection: 'desc'
				}
			})
			recentDefects.value = recentRes.data
		} catch (e) {
			console.error('获取缺陷类型详情失败', e)
			defectTypeStats.value = {
				total: 0,
				confirmed: 0,
				falseDefects: 0,
				highSeverity: 0,
				mediumSeverity: 0,
				lowSeverity: 0
			}
			recentDefects.value = []
		}
	}

	const closeDefectTypeModal = () => {
		defectTypeModalVisible.value = false
	}

	function getStatusClass(status : string | null) {
		if (!status) return ''
		switch (status) {
			case '待确认': return 'tag-waiting'
			case '已确认': return 'tag-info'
			case '处理中': return 'tag-processing'
			case '已整改': return 'tag-success'
			case '已关闭': return 'tag'
			default: return 'tag'
		}
	}

	function formatDate(date : string | Date | null) {
		if (!date) return ''

		try {
			const d = date instanceof Date ? date : new Date(date)
			if (isNaN(d.getTime())) return '无效日期'

			return d.toLocaleString('zh-CN', {
				year: 'numeric',
				month: '2-digit',
				day: '2-digit',
				hour: '2-digit',
				minute: '2-digit'
			}).replace(/\//g, '-')
		} catch (e) {
			return '无效日期'
		}
	}

	// 获取月度缺陷数据方法
	const fetchMonthlyDefectStats = async () => {
		try {
			const res = await axios.get(`${API_URL}/monthly-stats`);
			monthlyDefectStats.value = res.data.map((item : any) => ({
				month: item.month,
				count: item.count || 0
			}));

			// 数据获取后渲染图表
			renderLineChart();
		} catch (e) {
			console.error('获取月度缺陷统计数据失败', e);
			monthlyDefectStats.value = [];
		}
	};

	// 面积图
	const renderLineChart = () => {
		if (!lineChart.value) {
			console.error('图表容器未找到');
			return;
		}

		// 如果数据为空，显示无数据提示
		if (!monthlyDefectStats.value || monthlyDefectStats.value.length === 0) {
			console.error('月度缺陷统计数据为空');
			const chart = echarts.init(lineChart.value);
			chart.setOption({
				title: {
					text: '暂无数据',
					left: 'center',
					top: 'center',
					textStyle: {
						color: '#999',
						fontSize: 14
					}
				}
			});
			return;
		}

		const chart = echarts.init(lineChart.value);

		// 只提取月份信息
		const months = monthlyDefectStats.value.map(item => item.month.split('-')[1]);
		const counts = monthlyDefectStats.value.map(item => item.count);

		// 配置项 - 面积图
		const option = {
			tooltip: {
				trigger: 'axis',
				formatter: '{b}<br/>缺陷数量: {c}个',
				backgroundColor: 'rgba(0,0,0,0.8)',
				borderColor: '#00a8e8',
				borderWidth: 1,
				textStyle: {
					color: '#fff',
					fontSize: 14 // 增大提示框文字大小
				},
				axisPointer: {
					type: 'shadow',
					shadowStyle: {
						color: 'rgba(0, 168, 232, 0.2)'
					}
				}
			},
			grid: {
				left: '3%',
				right: '4%',
				bottom: '10%',
				top: '5%',
				containLabel: true
			},
			xAxis: {
				type: 'category',
				boundaryGap: false,
				data: months,
				axisLine: {
					lineStyle: {
						color: '#00a8e8',
						width: 1
					}
				},
				axisLabel: {
					color: '#fff', // 修改 x 轴标签颜色为白色
					fontSize: 14, // 增大 x 轴标签字体大小
					rotate: 30
				},
				axisTick: {
					alignWithLabel: true
				}
			},
			yAxis: {
				type: 'value',
				axisLine: {
					show: true,
					lineStyle: {
						color: '#00a8e8',
						width: 1
					}
				},
				axisLabel: {
					color: '#fff', // 修改 y 轴标签颜色为白色
					fontSize: 14 // 增大 y 轴标签字体大小
				},
				splitLine: {
					lineStyle: {
						color: 'rgba(0, 168, 232, 0.1)',
						type: 'dashed'
					}
				},
				minInterval: 1
			},
			series: [{
				name: '缺陷数量',
				type: 'line',
				stack: '总量',
				areaStyle: {
					color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
						{ offset: 0, color: 'rgba(0, 168, 232, 0.5)' },
						{ offset: 1, color: 'rgba(0, 168, 232, 0.1)' }
					])
				},
				emphasis: {
					focus: 'series',
					itemStyle: {
						color: '#FFA500' // 高亮颜色
					}
				},
				data: counts,
				smooth: true,
				symbol: 'circle',
				symbolSize: 8, // 增大数据点大小
				showSymbol: true,
				lineStyle: {
					width: 3, // 增大线条宽度
					color: '#00a8e8'
				},
				itemStyle: {
					color: '#00a8e8',
					borderColor: '#fff',
					borderWidth: 1
				}
			}],
			color: ['#00a8e8']
		};

		chart.setOption(option);

		// 修点击事件处理 - 更可靠的实现
		chart.off('click'); // 先移除旧的事件
		chart.on('click', (params) => {
			if (params.componentType === 'series') {
				const month = monthlyDefectStats.value[params.dataIndex].month;
				console.log('点击月份:', month); // 调试日志
				selectedMonth.value = month;
				fetchMonthlyDetails(month).then(() => {
					console.log('获取到的数据:', monthlyDefects.value); // 调试日志
					monthlyModalVisible.value = true;
				}).catch(error => {
					console.error('获取详情失败:', error);
				});
			}
		});

		// 窗口大小变化时重绘
		const resizeHandler = () => chart.resize();
		window.addEventListener('resize', resizeHandler);
		onUnmounted(() => {
			window.removeEventListener('resize', resizeHandler);
			chart.dispose();
		});
	};

	// 获取月度缺陷详情方法
	const fetchMonthlyDetails = async (month : string) => {
		try {
			console.log(`正在获取 ${month} 的数据...`); // 调试日志
			const res = await axios.get(`${API_URL}/monthly-details`, {
				params: { month, pageSize: 1000 }
			});

			console.log('接口响应数据:', res.data); // 调试日志

			if (res.data) {
				monthlyStats.value = {
					total: res.data.stats?.totalDefects || 0,
					confirmed: res.data.stats?.confirmedDefects || 0,
					falseDefects: res.data.stats?.falseDefects || 0,
					highSeverity: res.data.stats?.highSeverity || 0,
					mediumSeverity: res.data.stats?.mediumSeverity || 0,
					lowSeverity: res.data.stats?.lowSeverity || 0
				};

				monthlyDefects.value = res.data.defects || [];
				console.log('处理后的数据:', monthlyDefects.value); // 调试日志
			}
		} catch (e) {
			console.error('获取月度缺陷详情失败', e);
			monthlyStats.value = {
				total: 0,
				confirmed: 0,
				falseDefects: 0,
				highSeverity: 0,
				mediumSeverity: 0,
				lowSeverity: 0
			};
			monthlyDefects.value = [];
		}
	};

	const closeMonthlyModal = () => {
		monthlyModalVisible.value = false
	}

	onMounted(async () => {
		try {
			await taskStore.fetchTaskStats()
			await Promise.all([
				fetchDefectStats(),
				fetchDefectTypeStats(),
				fetchMonthlyDefectStats(),
				fetchDailyTaskStats(),
				fetchMonthlyTaskCount(),
				fetchPersonTaskStats()
			])
		} catch (error) {
			console.error('初始化数据失败:', error)
		}
	})
</script>

<style scoped>
	.dashboard-container {
		background-color: #000;
		min-height: 100vh;
		padding: 20px;
		box-sizing: border-box;
	}

	.wireframe {
		border: 2px solid #00a8e8;
		background: rgba(0, 168, 232, 0.05);
		min-height: calc(100vh - 40px);
		position: relative;
		border-radius: 8px;
		display: flex;
		flex-direction: column;
	}

	.header {
		text-align: center;
		padding: 20px 0;
		border-bottom: 1px solid #00a8e8;
		position: relative;
		background: rgba(0, 168, 232, 0.1);
		flex-shrink: 0;
	}

	.title {
		font-size: 24px;
		font-weight: bold;
		color: #fff;
	}

	.enter-btn {
		position: absolute;
		top: 20px;
		right: 30px;
		border: 2px solid #00a8e8;
		padding: 10px 20px;
		background: rgba(0, 168, 232, 0.2);
		cursor: pointer;
		color: #fff;
		border-radius: 4px;
		transition: all 0.3s ease;
	}

	.enter-btn:hover {
		background: rgba(0, 168, 232, 0.4);
	}

	.main-content {
		display: flex;
		flex: 1;
		gap: 20px;
		padding: 20px;
		overflow: auto;
	}

	.left-panel,
	.right-panel {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 20px;
		min-width: 300px;
	}

	.center-panel {
		flex: 1.5;
		display: flex;
		flex-direction: column;
		gap: 20px;
	}

	.card {
		border: 1px solid #00a8e8;
		background: rgba(0, 168, 232, 0.08);
		padding: 15px;
		flex: 1;
		border-radius: 6px;
		display: flex;
		flex-direction: column;
		min-height: 200px;
	}

	.card-title {
		font-size: 16px;
		margin-bottom: 15px;
		text-align: center;
		border-bottom: 1px solid rgba(0, 168, 232, 0.3);
		padding-bottom: 10px;
		color: #fff;
	}

	.stats-grid {
		display: grid;
		grid-template-columns: 1fr 1fr 1fr;
		gap: 10px;
		margin-bottom: 15px;
	}

	.stat-item {
		text-align: center;
		border: 1px solid rgba(0, 168, 232, 0.3);
		padding: 10px 5px;
		border-radius: 4px;
		background: rgba(0, 168, 232, 0.05);
	}

	.stat-number {
		font-size: 18px;
		font-weight: bold;
		color: #00a8e8;
	}

	.stat-number.large {
		font-size: 22px;
		padding: 5px 0;
	}

	.stat-label {
		font-size: 12px;
		margin-top: 5px;
		color: #aaa;
	}

	.chart-area {
		border: 1px solid rgba(0, 168, 232, 0.3);
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(0, 168, 232, 0.02);
		color: #666;
		border-radius: 4px;
		flex: 1;
		min-height: 150px;
		overflow: hidden;
	}

	.center-stats {
		border: 1px solid #00a8e8;
		background: rgba(0, 168, 232, 0.1);
		padding: 20px;
		text-align: center;
		border-radius: 6px;
	}

	.center-stats-row {
		display: flex;
		justify-content: space-around;
		margin-bottom: 10px;
	}

	.center-stat {
		text-align: center;
	}

	.center-stat-number {
		font-size: 36px;
		font-weight: bold;
		color: #00a8e8;
	}

	.center-stat-label {
		font-size: 14px;
		color: #aaa;
		margin-top: 5px;
	}

	.earth-section {
		flex: 2;
		border: 1px solid #00a8e8;
		background: rgba(0, 168, 232, 0.05);
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 18px;
		color: #666;
		position: relative;
		border-radius: 6px;
		min-height: 400px;
	}

	.map-layers {
		position: absolute;
		top: 20px;
		left: 20px;
		font-size: 12px;
		color: #999;
	}

	.modal {
		position: fixed;
		z-index: 1000;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.8);
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.modal-content {
		position: relative;
		background: #0a1a2a;
		padding: 20px;
		border-radius: 5px;
		max-width: 90%;
		max-height: 90%;
		overflow: auto;
		border: 1px solid #00a8e8;
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
	}

	.close {
		position: absolute;
		top: 10px;
		right: 15px;
		font-size: 28px;
		font-weight: bold;
		cursor: pointer;
		color: #00a8e8;
		transition: color 0.2s;
	}

	.close:hover {
		color: #fff;
	}

	.defect-stats {
		margin-bottom: 20px;
		padding: 15px;
		background: rgba(0, 168, 232, 0.1);
		border-radius: 4px;
	}

	.stat-row {
		display: flex;
		justify-content: space-around;
		margin-bottom: 10px;
	}

	.stat-item {
		text-align: center;
		padding: 10px;
		flex: 1;
	}

	.stat-number {
		font-size: 24px;
		font-weight: bold;
		color: #00a8e8;
	}

	.stat-label {
		font-size: 14px;
		color: #aaa;
	}

	.defect-list {
		margin-top: 20px;
	}

	.mini-table {
		width: 100%;
		border-collapse: collapse;
		font-size: 14px;
	}

	.mini-table th,
	.mini-table td {
		border: 1px solid #1a3a5a;
		padding: 8px 12px;
		text-align: left;
	}

	.mini-table th {
		background: rgba(0, 168, 232, 0.2);
		color: #00a8e8;
	}

	.mini-table tbody tr:nth-child(even) {
		background: rgba(0, 168, 232, 0.05);
	}

	.mini-table tbody tr:hover {
		background: rgba(0, 168, 232, 0.1);
	}

	.tag {
		display: inline-block;
		padding: 2px 6px;
		border-radius: 3px;
		font-size: 12px;
	}

	.tag-waiting {
		background: #e0e0e0;
		color: #333;
	}

	.tag-info {
		background: #d1ecf1;
		color: #0c5460;
	}

	.tag-processing {
		background: #e2f0fb;
		color: #0a58ca;
	}

	.tag-success {
		background: #d4edda;
		color: #155724;
	}

	.modal-content {
		background: #0a1a2a;
		color: #e0e0e0;
	}

	.detail-row {
		color: #e0e0e0;
	}

	.detail-label {
		color: #00a8e8;
	}

	.mini-table {
		color: #e0e0e0;
	}

	.mini-table th {
		background: rgba(0, 168, 232, 0.3);
	}

	.mini-table td {
		border-color: #1a3a5a;
	}

	.tag-waiting {
		background: #e0e0e0;
		color: #333;
		padding: 2px 6px;
		border-radius: 3px;
		font-size: 12px;
	}

	.tag-info {
		background: #d1ecf1;
		color: #0c5460;
		padding: 2px 6px;
		border-radius: 3px;
		font-size: 12px;
	}

	.tag-processing {
		background: #e2f0fb;
		color: #0a58ca;
		padding: 2px 6px;
		border-radius: 3px;
		font-size: 12px;
	}

	.tag-success {
		background: #d4edda;
		color: #155724;
		padding: 2px 6px;
		border-radius: 3px;
		font-size: 12px;
	}

	.mini-table {
		width: 100%;
		border-collapse: collapse;
		margin-top: 20px;
	}

	.mini-table th,
	.mini-table td {
		padding: 10px;
		border: 1px solid #00a8e8;
		color: #fff;
		text-align: center;
	}

	.mini-table th {
		background-color: rgba(0, 168, 232, 0.2);
	}

	.mini-table tr:nth-child(even) {
		background-color: rgba(0, 168, 232, 0.1);
	}

	.mini-table tr:hover {
		background-color: rgba(0, 168, 232, 0.3);
	}

	.tag-waiting {
		color: #FFC300;
	}

	.tag-info {
		color: #87CEFA;
	}

	.tag-processing {
		color: #98FB98;
	}

	.tag-success {
		color: #00FF00;
	}

	.severity-high {
		color: #FF0000;
	}

	.severity-medium {
		color: #FFA500;
	}

	.severity-low {
		color: #FFFF00;
	}
</style>