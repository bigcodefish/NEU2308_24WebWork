<template>

	<!-- 模拟数据 -->

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
								<div class="stat-number">123</div>
								<div class="stat-label">今日巡视数</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">98</div>
								<div class="stat-label">昨日巡视数</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">25%</div>
								<div class="stat-label">环比增长</div>
							</div>
						</div>
						<div class="stats-grid">
							<div class="stat-item">
								<div class="stat-number">1.2km</div>
								<div class="stat-label">今日巡视距离</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">0.9km</div>
								<div class="stat-label">昨日巡视距离</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">33%</div>
								<div class="stat-label">环比增长</div>
							</div>
						</div>
					</div>

					<div class="card">
						<div class="card-title">缺陷类型统计</div>
						<div class="chart-area" ref="pieChart" style="height: 100%; min-height: 200px;"></div>
					</div>

					<div class="card">
						<div class="card-title">人员数据统计</div>
						<div class="chart-area">柱状图 - 人员任务统计</div>
					</div>
				</div>

				<!-- 中间面板 -->
				<div class="center-panel">
					<div class="center-stats">
						<div class="center-stats-row">
							<div class="center-stat">
								<div class="center-stat-number">1,234</div>
								<div class="center-stat-label">巡视总距离</div>
							</div>
							<div class="center-stat">
								<div class="center-stat-number">5,678</div>
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
					<div class="card">
						<div class="card-title">缺陷数据统计</div>
						<div class="stats-grid">
							<div class="stat-item">
								<div class="stat-number">{{ defectStats.totalDefects || 0 }}</div>
								<div class="stat-label">累计缺陷数</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">{{ defectStats.confirmedDefects || 0 }}</div>
								<div class="stat-label">确认缺陷数</div>
							</div>
							<div class="stat-item">
								<div class="stat-number">{{ defectStats.falseDefects || 0 }}</div>
								<div class="stat-label">误报缺陷数</div>
							</div>
						</div>
					</div>

					<div class="card">
						<div class="card-title">每月巡检次数</div>
						<div class="chart-area">柱状图 - 月度巡检趋势</div>
					</div>

					<div class="card">
						<div class="card-title">缺陷数据变化</div>
						<div class="chart-area">折线图 - 缺陷趋势变化</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	import { ref, onMounted, nextTick } from 'vue'
	import router from '@/router'
	import axios from 'axios'
	import * as echarts from 'echarts'

	const API_URL = 'http://localhost:8080/api/defects'

	// 缺陷统计
	const defectStats = ref({
		totalDefects: 0,
		confirmedDefects: 0,
		falseDefects: 0
	})

	// 缺陷类型数据
	const defectTypes = ref<{ type : string; count : number }[]>([])
	const pieChart = ref<HTMLElement | null>(null)

	const enterSystem = () => {
		router.push('/system')
	}

	// 获取缺陷统计数据
	const fetchDefectStats = async () => {
		try {
			const res = await axios.get(`${API_URL}/stats`)
			defectStats.value = res.data
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
		if (!pieChart.value || defectTypes.value.length === 0) return

		// 初始化ECharts实例
		const chart = echarts.init(pieChart.value)

		// 配置项
		const option = {
			tooltip: {
				trigger: 'item',
				formatter: '{a} <br/>{b}: {c} ({d}%)'
			},
			legend: {
				orient: 'horizontal',
				bottom: 10,
				data: defectTypes.value.map(item => item.type)
			},
			series: [
				{
					name: '缺陷类型分布',
					type: 'pie',
					radius: ['40%', '70%'],
					avoidLabelOverlap: false,
					itemStyle: {
						borderRadius: 10,
						borderColor: '#000',
						borderWidth: 2
					},
					label: {
						show: false,
						position: 'center'
					},
					emphasis: {
						label: {
							show: true,
							fontSize: '18',
							fontWeight: 'bold'
						}
					},
					labelLine: {
						show: false
					},
					data: defectTypes.value.map(item => ({
						value: item.count,
						name: item.type
					}))
				}
			],
			color: [
				'#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
				'#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#2ec7c9'
			]
		}

		// 设置配置项并渲染图表
		chart.setOption(option)

		// 响应窗口大小变化
		window.addEventListener('resize', () => {
			chart.resize()
		})
	}

	onMounted(() => {
		fetchDefectStats()
		fetchDefectTypeStats()
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
</style>
