<template>
  <div class="defect-management">
    <div class="page-title">缺陷管理</div>

    <!-- 快速筛选按钮 -->
    <div class="quick-filters">
      <button class="btn" @click="filterToday">今日上报</button>
      <button class="btn" @click="filterThisWeek">本周上报</button>
      <button class="btn" @click="filterPending">待确认缺陷</button>
      <button class="btn" @click="filterConfirmed">已确认缺陷</button>
      <button class="btn" @click="filterProcessing">处理中缺陷</button>
      <button class="btn" @click="filterFixed">已整改缺陷</button>
    </div>

    <div class="wireframe">
      <form class="search-form" @submit.prevent="onSearch">
        <div class="form-item">
          <label for="taskName">所属任务</label>
          <input id="taskName" v-model="filters.taskName" type="text" placeholder="请输入所属任务" />
        </div>
        <div class="form-item">
          <label for="location">具体位置</label>
          <input id="location" v-model="filters.location" type="text" placeholder="请输入具体位置" />
        </div>
        <div class="form-item">
          <label for="defectType">缺陷类型</label>
          <input id="defectType" v-model="filters.defectType" type="text" placeholder="请输入缺陷类型" />
        </div>
        <div class="form-item">
          <label for="isReal">是否属实</label>
          <select id="isReal" v-model="filters.isReal">
            <option value="">请选择是否属实</option>
            <option value="true">是</option>
            <option value="false">否</option>
          </select>
        </div>
        <div class="form-item">
          <label for="status">状态</label>
          <select id="status" v-model="filters.status">
            <option value="">全部</option>
            <option value="待确认">待确认</option>
            <option value="已确认">已确认</option>
            <option value="处理中">处理中</option>
            <option value="已整改">已整改</option>
          </select>
        </div>
        <div class="form-item">
          <label for="severity">严重程度</label>
          <select id="severity" v-model="filters.severity">
            <option value="">全部</option>
            <option value="高">高</option>
            <option value="中">中</option>
            <option value="低">低</option>
          </select>
        </div>
        <div class="form-item">
          <label for="dateRange">时间范围</label>
          <input type="date" v-model="filters.startDate" placeholder="开始日期">
          <span>至</span>
          <input type="date" v-model="filters.endDate" placeholder="结束日期">
        </div>
        <div class="form-item">
          <label for="minDistance">最小距离(m)</label>
          <input id="minDistance" v-model.number="filters.minDistance" type="number" min="0" step="0.1" placeholder="最小距离" />
        </div>
        <div class="form-item">
          <label for="maxDistance">最大距离(m)</label>
          <input id="maxDistance" v-model.number="filters.maxDistance" type="number" min="0" step="0.1" placeholder="最大距离" />
        </div>
        <div class="form-item">
          <label for="discoverer">发现人员</label>
          <input id="discoverer" v-model="filters.discoverer" type="text" placeholder="请输入发现人员" />
        </div>
        <div class="form-item">
          <label for="discoveryMethod">发现方式</label>
          <input id="discoveryMethod" v-model="filters.discoveryMethod" type="text" placeholder="请输入发现方式" />
        </div>
        <div class="form-item">
          <label for="processor">处理人员</label>
          <input id="processor" v-model="filters.processor" type="text" placeholder="请输入处理人员" />
        </div>
        <div class="search-buttons">
          <button type="submit" class="btn btn-primary">搜索</button>
          <button type="button" class="btn" @click="onReset">重置</button>
        </div>
      </form>

      <div class="toolbar">
        <div style="display: flex; gap: 10px;">
          <button class="btn" @click="exportCSV">导出CSV</button>
          <button class="btn" @click="exportExcel">导出Excel</button>
          <button class="btn" @click="batchConfirmReal" :disabled="selectedIds.length === 0">批量确认属实</button>
          <button class="btn" @click="batchMarkFixed" :disabled="selectedIds.length === 0">批量标记整改</button>
        </div>
        <button class="btn" @click="loadData">刷新</button>
      </div>

      <div class="table-container">
        <table class="table">
          <colgroup>
            <col style="width: 3%">   <!-- 选择 -->
            <col style="width: 4%">   <!-- 序号 -->
            <col style="width: 9%">  <!-- 缺陷编号 -->
            <col style="width: 7%">  <!-- 任务名称 -->
            <col style="width: 7%">  <!-- 具体位置 -->
            <col style="width: 6%">   <!-- 缺陷类型 -->
            <col style="width: 5%">   <!-- 缺陷距离 -->
            <col style="width: 8%">   <!-- 缺陷图片 -->
            <col style="width: 5%">   <!-- 是否属实 -->
            <col style="width: 6%">   <!-- 严重程度 -->
            <col style="width: 5%">   <!-- 状态 -->
            <col style="width: 6%">   <!-- 缺陷长度 -->
            <col style="width: 6%">   <!-- 缺陷面积 -->
            <col style="width: 6%">   <!-- 缺陷数量 -->
            <col style="width: 6%">   <!-- 发现人员 -->
            <col style="width: 6%">   <!-- 发现方式 -->
            <col style="width: 6%">   <!-- 处理人员 -->
            <col style="width: 8%">  <!-- 操作 -->
          </colgroup>

          <thead>
            <tr>
              <th><input type="checkbox" v-model="selectAll"></th>
              <th>序号</th>
              <th>缺陷编号</th>
              <th>任务名称</th>
              <th>具体位置</th>
              <th>缺陷类型</th>
              <th>距离</th>
              <th>缺陷图片及详情</th>
              <th>属实</th>
              <th>严重程度</th>
              <th>状态</th>
              <th>缺陷长度</th>
              <th>缺陷面积</th>
              <th>缺陷数量</th>
              <th>发现人员</th>
              <th>发现方式</th>
              <th>处理人员</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="paginatedData.length === 0">
              <td colspan="18" style="text-align:center; padding: 20px;">
                <span v-if="loading">数据加载中...</span>
                <span v-else>暂无数据</span>
              </td>
            </tr>
            <tr v-for="(item, index) in paginatedData" :key="item.id">
              <td><input type="checkbox" v-model="selectedIds" :value="item.id"></td>
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td class="ellipsis-cell" :title="item.defectNo || 'DEF-' + item.id">{{ item.defectNo || 'DEF-' + item.id }}</td>
              <td class="ellipsis-cell" :title="item.taskName || '-'">{{ item.taskName || '-' }}</td>
              <td class="ellipsis-cell" :title="item.location || '-'">{{ item.location || '-' }}</td>
              <td class="ellipsis-cell" :title="item.defectType || '-'">{{ item.defectType || '-' }}</td>
              <td>{{ item.distance != null ? item.distance + 'm' : '-' }}</td>
              <td class="image-cell">
                <div class="flaw-image" @click="showImageModal(item)">
                  <span v-if="!item.imageUrl">
                    缺陷图片<br>[点击预览]
                  </span>
                  <img v-else :src="getImageUrl(item.imageUrl)" alt="缺陷图片" />
                </div>
              </td>
              <td>
                <span
                  :class="['tag', item.isReal ? 'tag-success' : 'tag-danger']"
                >
                  {{ item.isReal ? '是' : '否' }}
                </span>
              </td>
              <td>{{ item.severity || '-' }}</td>
              <td>
                <span :class="getStatusClass(item.status)">{{ item.status || '-' }}</span>
              </td>
              <td>{{ item.defectLength != null ? item.defectLength + 'm' : '-' }}</td>
              <td>{{ item.defectArea != null ? item.defectArea + '㎡' : '-' }}</td>
              <td>{{ item.defectCount != null ? item.defectCount : '-' }}</td>
              <td>{{ item.discoverer || '-' }}</td>
              <td>{{ item.discoveryMethod || '-' }}</td>
              <td>{{ item.processor || '-' }}</td>
              <td class="actions">
                <div class="action-buttons">
                  <!-- 状态流转按钮 -->
                  <span v-if="item.status === '待确认' && !item.isReal" 
                        class="link" 
                        @click="confirmReal(item)">
                    确认属实
                  </span>
                  
                  <span v-if="item.status === '已确认'" 
                        class="link" 
                        @click="startProcessing(item)">
                    开始处理
                  </span>
                  
                  <span v-if="item.status === '处理中'" 
                        class="link" 
                        @click="markFixed(item)">
                    标记整改
                  </span>
                  
                  <!-- 任何状态都可以添加备注 -->
                  <span class="link" @click="showRemarkModal(item)">
                    添加备注
                  </span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

           <div class="pagination">
                <button @click="prevPage" :disabled="currentPage === 1">«</button>
                
                <span v-for="(page, index) in displayedPages" :key="index">
                  <template v-if="page === '...'">
                    <span class="ellipsis">...</span>
                  </template>
                  <template v-else>
                    <span :class="{ active: page === currentPage }" @click="goToPage(page as number)">
                      [{{ page }}]
                    </span>
                  </template>
                </span>
                
                <button @click="nextPage" :disabled="currentPage === pageCount">»</button>
                
                <span class="page-jump">
                  跳至 <input type="number" v-model.number="jumpPage" min="1" :max="pageCount" /> 页
                  <button @click="jumpToPage">确定</button>
                </span>
                <span class="total-records">共 {{ defects.length }} 条记录</span>
              </div>
    </div>

    <!-- 图片模态框 -->
    <div v-if="modalVisible" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
              <div class="modal-image">
                <!-- 图片控制栏 -->
                <div class="image-controls">
                  <button class="control-btn" @click="zoomIn">放大</button>
                  <button class="control-btn" @click="zoomOut">缩小</button>
                  <button class="control-btn" @click="rotateImage">旋转</button>
                  <button class="control-btn" 
                          @click="downloadImage" 
                          :disabled="downloading || !modalData.imageUrl">
                    {{ downloading ? '下载中...' : '下载' }}
                  </button>
                  <button class="control-btn" @click="resetImage">重置</button>
                </div>
                
                <div class="image-container" ref="imageContainer">
                  <span v-if="!modalData.imageUrl">高清缺陷图片预览区域</span>
                  <img v-else :src="getImageUrl(modalData.imageUrl)" 
                       alt="缺陷大图" 
                       ref="modalImage"
                       :style="{
                         transform: `scale(${imageScale}) rotate(${imageRotation}deg)`,
                         maxWidth: '100%',
                         maxHeight: '100%'
                       }" />
                </div>
              </div>
        <div class="flaw-details">
          <div class="detail-row">
            <div class="detail-label">缺陷编号:</div>
            <div class="detail-value">{{ modalData.defectNo || 'DEF-' + modalData.id }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">具体位置:</div>
            <div class="detail-value">{{ modalData.location || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">缺陷距离:</div>
            <div class="detail-value">{{ modalData.distance || '-' }}m</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">缺陷类型:</div>
            <div class="detail-value">{{ modalData.defectType || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">所属任务:</div>
            <div class="detail-value">
              <span v-if="modalData.taskName" class="link" @click="goToTask(modalData.taskName)">
                {{ modalData.taskName }}
              </span>
              <span v-else>-</span>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-label">严重程度:</div>
            <div class="detail-value">{{ modalData.severity || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">缺陷描述:</div>
            <div class="detail-value">{{ modalData.description || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">缺陷长度:</div>
            <div class="detail-value">{{ modalData.defectLength || '-' }}m</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">缺陷面积:</div>
            <div class="detail-value">{{ modalData.defectArea || '-' }}㎡</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">缺陷数量:</div>
            <div class="detail-value">{{ modalData.defectCount || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">整改建议:</div>
            <div class="detail-value">{{ modalData.suggestion || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">是否属实:</div>
            <div class="detail-value">
              <span :class="['tag', modalData.isReal ? 'tag-success' : 'tag-danger']">
                {{ modalData.isReal ? '是' : '否' }}
              </span>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-label">状态:</div>
            <div class="detail-value">
              <span :class="getStatusClass(modalData.status)">{{ modalData.status || '-' }}</span>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-label">发现人员:</div>
            <div class="detail-value">{{ modalData.discoverer || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">发现方式:</div>
            <div class="detail-value">{{ modalData.discoveryMethod || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">处理人员:</div>
            <div class="detail-value">{{ modalData.processor || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">确认时间:</div>
            <div class="detail-value">{{ formatDate(modalData.confirmTime) }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">确认负责人:</div>
            <div class="detail-value">{{ modalData.confirmer || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">处理开始时间:</div>
            <div class="detail-value">{{ formatDate(modalData.processStartTime) }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">处理完成时间:</div>
            <div class="detail-value">{{ formatDate(modalData.processEndTime) }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">处理结果:</div>
            <div class="detail-value">{{ modalData.processResult || '-' }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">处理备注:</div>
            <div class="detail-value">
              <div v-if="!editingRemark">{{ modalData.remark || '无备注' }}</div>
              <textarea v-else v-model="remarkContent" rows="3" style="width:100%"></textarea>
              <span class="link" @click="toggleRemarkEdit">
                {{ editingRemark ? '保存' : '编辑' }}
              </span>
            </div>
          </div>
          <div class="detail-row">
            <div class="detail-label">上报时间:</div>
            <div class="detail-value">{{ formatDate(modalData.reportTime) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 备注模态框 -->
    <div v-if="remarkModalVisible" class="modal" @click.self="closeRemarkModal">
      <div class="modal-content" style="width: 500px;">
        <span class="close" @click="closeRemarkModal">&times;</span>
        <h3 style="margin-bottom: 20px;">添加处理备注</h3>
        <div class="form-item" style="width: 100%;">
          <textarea v-model="remarkContent" placeholder="请输入处理备注..." rows="6" style="width: 100%; padding: 10px;"></textarea>
        </div>
        <div style="display: flex; justify-content: flex-end; margin-top: 20px;">
          <button class="btn" @click="closeRemarkModal">取消</button>
          <button class="btn btn-primary" @click="saveRemark" style="margin-left: 10px;">保存备注</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import * as XLSX from 'xlsx'
import axios from 'axios'

interface Defect {
  id: number
  defectNo: string | null
  taskName: string | null
  location: string | null // 具体位置
  defectType: string | null
  distance: number | null
  imageUrl: string | null
  isReal: boolean
  severity: string | null
  defectLength: number | null
  defectArea: number | null
  defectCount: number | null
  suggestion: string | null
  reportTime: string | Date | null
  status: string | null
  discoverer: string | null
  discoveryMethod: string | null
  processor: string | null
  processStartTime: Date | null
  processEndTime: Date | null
  processResult: string | null
  description: string | null // 缺陷描述
  confirmTime: Date | null // 确认时间
  confirmer: string | null // 确认负责人
  remark: string | null // 处理过程备注
}

const API_URL = 'http://localhost:8080/api/defects'

const filters = ref({
  taskName: '',
  location: '', // 具体位置
  defectType: '',
  isReal: '',
  status: '',
  severity: '',
  startDate: '',
  endDate: '',
  minDistance: null as number | null,
  maxDistance: null as number | null,
  discoverer: '',
  discoveryMethod: '',
  processor: ''
})

const defects = ref<Defect[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 10
const selectedIds = ref<number[]>([])
const selectAll = computed({
  get: () => defects.value.length > 0 && selectedIds.value.length === defects.value.length,
  set: (value) => {
    if (value) {
      selectedIds.value = defects.value.map(d => d.id)
    } else {
      selectedIds.value = []
    }
  }
})

const modalVisible = ref(false)
const modalData = ref<Defect>({} as Defect)

const remarkModalVisible = ref(false)
const currentRemarkId = ref(0)
const remarkContent = ref('')
const editingRemark = ref(false)



// 获取原图URL
function getImageUrl(imageName: string | null) {
  if (!imageName) return ''
  return `http://localhost:8080/uploads/workservice/${imageName}`
}

const pageCount = computed(() => Math.ceil(defects.value.length / pageSize))
// 跳转页码输入框的值
const jumpPage = ref(1)

// 分页显示逻辑
const displayedPages = computed(() => {
  const pages = []
  const total = pageCount.value
  const current = currentPage.value
  const maxVisible = 5  // 最多显示的页码数
  
  if (total <= maxVisible) {
    // 总页数较少时显示所有页码
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // 计算起始页和结束页
    let start = Math.max(1, current - Math.floor(maxVisible / 2))
    let end = Math.min(total, start + maxVisible - 1)
    
    // 调整起始页确保显示maxVisible个页码
    if (end - start < maxVisible - 1) {
      start = end - maxVisible + 1
    }
    
    // 添加第一页和省略号
    if (start > 1) {
      pages.push(1)
      if (start > 2) {
        pages.push('...')
      }
    }
    
    // 添加中间页码
    for (let i = start; i <= end; i++) {
      pages.push(i)
    }
    
    // 添加省略号和最后一页
    if (end < total) {
      if (end < total - 1) {
        pages.push('...')
      }
      pages.push(total)
    }
  }
  
  return pages
})

// 翻页方法
function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

function nextPage() {
  if (currentPage.value < pageCount.value) {
    currentPage.value++
  }
}

function goToPage(page: number) {
  if (page >= 1 && page <= pageCount.value) {
    currentPage.value = page
  }
}

function jumpToPage() {
  if (jumpPage.value >= 1 && jumpPage.value <= pageCount.value) {
    currentPage.value = jumpPage.value
  } else {
    alert(`请输入有效的页码 (1-${pageCount.value})`)
  }
}

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return defects.value.slice(start, start + pageSize)
})

function getStatusClass(status: string | null) {
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

function formatDate(date: string | Date | null) {
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

// 辅助函数：格式化日期为YYYY-MM-DD
function formatDateForInput(date: Date) {
  return date.toISOString().split('T')[0];
}

// 快速筛选方法
function filterToday() {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  
  filters.value = {
    ...filters.value,
    startDate: formatDateForInput(today),
    endDate: formatDateForInput(new Date())
  };
  
  onSearch();
}

function filterThisWeek() {
  const today = new Date();
  const firstDayOfWeek = new Date(today);
  firstDayOfWeek.setDate(today.getDate() - today.getDay() + (today.getDay() === 0 ? -6 : 1));
  firstDayOfWeek.setHours(0, 0, 0, 0);
  
  filters.value = {
    ...filters.value,
    startDate: formatDateForInput(firstDayOfWeek),
    endDate: formatDateForInput(today)
  };
  
  onSearch();
}

function filterPending() {
  filters.value = {
    ...filters.value,
    status: '待确认',
    isReal: ''
  };
  onSearch();
}

function filterConfirmed() {
  filters.value = {
    ...filters.value,
    status: '已确认',
    isReal: 'true'
  };
  onSearch();
}

function filterProcessing() {
  filters.value = {
    ...filters.value,
    status: '处理中'
  };
  onSearch();
}

function filterFixed() {
  filters.value = {
    ...filters.value,
    status: '已整改'
  };
  onSearch();
}

async function loadData() {
  try {
    loading.value = true
    const params = {
      taskName: filters.value.taskName.trim(),
      location: filters.value.location.trim(), // 具体位置
      defectType: filters.value.defectType.trim(),
      isReal: filters.value.isReal !== '' ? filters.value.isReal === 'true' : undefined,
      status: filters.value.status,
      severity: filters.value.severity,
      startTime: filters.value.startDate ? new Date(filters.value.startDate) : undefined,
      endTime: filters.value.endDate ? new Date(filters.value.endDate) : undefined,
      minDistance: filters.value.minDistance,
      maxDistance: filters.value.maxDistance,
      discoverer: filters.value.discoverer.trim(),
      discoveryMethod: filters.value.discoveryMethod.trim(),
      processor: filters.value.processor.trim()
    }

    const res = await axios.get(API_URL, { params })
    defects.value = res.data
    
    // 重置选择
    selectedIds.value = []
    
    // 检查当前页码是否超出范围
    const totalPages = Math.ceil(defects.value.length / pageSize)
    if (currentPage.value > totalPages && totalPages > 0) {
      currentPage.value = totalPages
    }
  } catch (e) {
    const errorMessage = axios.isAxiosError(e) 
      ? e.response?.data?.message || e.message 
      : (e as Error).message
    alert('加载数据失败: ' + errorMessage)
  } finally {
    loading.value = false
  }
}

function onSearch() {
  currentPage.value = 1
  loadData()
}

function onReset() {
  filters.value = {
    taskName: '',
    location: '', // 具体位置
    defectType: '',
    isReal: '',
    status: '',
    severity: '',
    startDate: '',
    endDate: '',
    minDistance: null,
    maxDistance: null,
    discoverer: '',
    discoveryMethod: '',
    processor: ''
  }
  currentPage.value = 1
  loadData()
}

function showImageModal(item: Defect) {
  modalData.value = item
  modalVisible.value = true
}

function showRemarkModal(item: Defect) {
  currentRemarkId.value = item.id
  remarkContent.value = item.remark || ''
  remarkModalVisible.value = true
}

function closeRemarkModal() {
  remarkModalVisible.value = false
  remarkContent.value = ''
}

async function saveRemark() {
  if (!remarkContent.value.trim()) {
    alert('请输入备注内容')
    return
  }
  
  try {
    await axios.post(`${API_URL}/${currentRemarkId.value}/remark`, {
      remark: remarkContent.value
    })
    alert('备注保存成功')
    loadData()
    closeRemarkModal()
  } catch (e) {
    const errorMessage = axios.isAxiosError(e) 
      ? e.response?.data?.message || e.message 
      : (e as Error).message
    alert(`保存备注失败: ${errorMessage}`)
  }
}

function toggleRemarkEdit() {
  if (editingRemark.value) {
    saveModalRemark();
  } else {
    remarkContent.value = modalData.value.remark || '';
  }
  editingRemark.value = !editingRemark.value;
}

async function saveModalRemark() {
  if (!remarkContent.value.trim()) {
    alert('请输入备注内容');
    return;
  }
  
  try {
    await axios.post(`${API_URL}/${modalData.value.id}/remark`, {
      remark: remarkContent.value
    });
    alert('备注保存成功');
    
    // 更新本地数据
    modalData.value.remark = remarkContent.value;
    const index = defects.value.findIndex(d => d.id === modalData.value.id);
    if (index !== -1) {
      defects.value[index].remark = remarkContent.value;
    }
  } catch (e) {
    const errorMessage = axios.isAxiosError(e) 
      ? e.response?.data?.message || e.message 
      : (e as Error).message
    alert(`保存备注失败: ${errorMessage}`)
  }
}

async function confirmReal(item: Defect) {
  const confirmer = prompt('请输入确认负责人姓名：');
  if (!confirmer) return;
  
  if (confirm('确认此缺陷属实吗？')) {
    try {
      // 更新是否属实状态
      await axios.put(`${API_URL}/${item.id}/real?isReal=true`);
      
      // 更新确认信息
      await axios.post(`${API_URL}/${item.id}/confirmation?confirmer=${confirmer}`);
      
      alert('确认属实成功');
      loadData();
    } catch (e) {
      const errorMessage = axios.isAxiosError(e) 
        ? e.response?.data?.message || e.message 
        : (e as Error).message
      alert(`操作失败: ${errorMessage}`)
    }
  }
}

// 开始处理缺陷
async function startProcessing(item: Defect) {
  const processor = prompt('请输入处理人员姓名：');
  if (!processor) return;
  
  if (confirm('确定要开始处理此缺陷吗？')) {
    try {
      // 更新处理开始时间和处理人员
      await axios.put(`${API_URL}/${item.id}/start-processing`, { processor });
      
      // 更新状态为"处理中"
      await axios.put(`${API_URL}/${item.id}/status?status=处理中`);
      
      alert('已开始处理');
      loadData();
    } catch (e) {
      const errorMessage = axios.isAxiosError(e) 
        ? e.response?.data?.message || e.message 
        : (e as Error).message
      alert(`操作失败: ${errorMessage}`)
    }
  }
}

// 标记整改完成
async function markFixed(item: Defect) {
  const processResult = prompt('请输入处理结果：');
  if (processResult === null) return; // 用户取消
  
  if (confirm('标记此缺陷已整改？')) {
    try {
      // 更新处理完成时间和处理结果
      await axios.put(`${API_URL}/${item.id}/complete-processing`, { processResult });
      
      // 更新状态为"已整改"
      await axios.put(`${API_URL}/${item.id}/status?status=已整改`);
      
      alert('标记已整改成功');
      loadData();
    } catch (e) {
      const errorMessage = axios.isAxiosError(e) 
        ? e.response?.data?.message || e.message 
        : (e as Error).message
      alert(`操作失败: ${errorMessage}`)
    }
  }
}

async function batchConfirmReal() {
  if (selectedIds.value.length === 0) {
    alert('请选择要操作的缺陷')
    return
  }
  
  const selectedDefects = defects.value.filter(d => selectedIds.value.includes(d.id))
  const allPending = selectedDefects.every(d => d.status === '待确认')
  
  if (!allPending) {
    alert('只能选择状态为"待确认"的缺陷进行批量确认属实')
    return
  }

  const confirmer = prompt('请输入确认负责人姓名：');
  if (!confirmer) return;

  if (confirm(`确认批量设置 ${selectedIds.value.length} 个缺陷为属实状态吗？`)) {
    try {
      // 更新是否属实状态
      await axios.put(`${API_URL}/batch/real?isReal=true&ids=${selectedIds.value.join(',')}`);
      
      // 更新确认信息
      for (const id of selectedIds.value) {
        await axios.post(`${API_URL}/${id}/confirmation?confirmer=${confirmer}`);
      }
      
      alert('批量确认成功');
      loadData();
    } catch (e) {
      const errorMessage = axios.isAxiosError(e) 
        ? e.response?.data?.message || e.message 
        : (e as Error).message
      alert(`批量操作失败: ${errorMessage}`)
    }
  }
}

async function batchMarkFixed() {
  if (selectedIds.value.length === 0) {
    alert('请选择要操作的缺陷');
    return;
  }
  
  const processResult = prompt('请输入统一的处理结果：');
  if (processResult === null) return; // 用户取消
  
  if (confirm(`确认批量设置 ${selectedIds.value.length} 个缺陷为已整改状态吗？`)) {
    try {
      // 更新状态为"已整改"
      await axios.put(`${API_URL}/batch/status?status=已整改&ids=${selectedIds.value.join(',')}`);
      
      // 批量更新处理完成时间和处理结果
      for (const id of selectedIds.value) {
        await axios.put(`${API_URL}/${id}/complete-processing`, { processResult });
      }
      
      alert('批量标记成功');
      loadData();

    } catch (e) {
      const errorMessage = axios.isAxiosError(e) 
        ? e.response?.data?.message || e.message 
        : (e as Error).message
      alert(`批量操作失败: ${errorMessage}`)
    }
  }
}

function goToTask(taskName: string) {
  alert(`跳转到任务: ${taskName}`);
  // 实际项目中: router.push(`/tasks/${taskName}`);
}

const imageScale = ref(1);
const imageRotation = ref(0);
const modalImage = ref<HTMLImageElement | null>(null);
const imageContainer = ref<HTMLDivElement | null>(null);

// 放大图片
function zoomIn() {
  if (imageScale.value < 3) {
    imageScale.value += 0.1;
  }
}

// 缩小图片
function zoomOut() {
  if (imageScale.value > 0.5) {
    imageScale.value -= 0.1;
  }
}

// 旋转图片
function rotateImage() {
  imageRotation.value = (imageRotation.value + 90) % 360;
}

// 重置图片
function resetImage() {
  imageScale.value = 1;
  imageRotation.value = 0;
}

// 下载图片
const downloading = ref(false);

// 在脚本顶部添加错误类型声明
type DownloadError = {
  message: string;
};

// 更新 downloadImage 函数
async function downloadImage() {
  if (!modalData.value.imageUrl) {
    alert('没有可下载的图片');
    return;
  }
  
  if (downloading.value) return;
  downloading.value = true;
  
  const imageUrl = getImageUrl(modalData.value.imageUrl);
  
  // 使用缺陷编号作为文件名，格式：缺陷编号_时间戳
  const defectId = modalData.value.defectNo || `DEF-${modalData.value.id}`;
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
  const extension = getFileExtension(modalData.value.imageUrl);
  const filename = `${defectId}_${timestamp}.${extension}`;
  
  try {
    // 使用fetch获取图片（解决跨域问题）
    const response = await fetch(imageUrl);
    if (!response.ok) {
      throw new Error(`图片下载失败: ${response.status} ${response.statusText}`);
    }
    
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    
    // 创建下载链接
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    
    // 清理资源
    setTimeout(() => {
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    }, 100);
    
    // 添加成功提示
    setTimeout(() => {
      alert(`图片已保存为: ${filename}`);
    }, 300);
    
  } catch (err) {
    // 安全处理错误类型
    const error = err as Error;
    console.error('下载失败:', error);
    alert(`图片下载失败: ${error.message || '未知错误'}`);
  } finally {
    downloading.value = false;
  }
}

// 获取文件扩展名
function getFileExtension(filename: string): string {
  const parts = filename.split('.');
  if (parts.length > 1) {
    const ext = parts.pop()?.toLowerCase() || '';
    // 只允许图片扩展名
    if (['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(ext)) {
      return ext;
    }
  }
  return 'jpg'; // 默认扩展名
}

// 在关闭模态框时重置图片状态
function closeModal() {
  resetImage();
  modalVisible.value = false;
  // 重置下载状态
  downloading.value = false;
}

// CSV导出功能
function exportCSV() {
  if (defects.value.length === 0) {
    alert('无数据导出')
    return
  }
  
  const headers = [
    '缺陷编号', '具体位置', '缺陷距离(m)', '任务名称', '缺陷类型', 
    '是否属实', '严重程度', '状态', '缺陷描述', 
    '缺陷长度(m)', '缺陷面积(㎡)', '缺陷数量', '发现人员', '发现方式',
    '处理人员', '确认时间', '确认负责人', '处理开始时间', '处理完成时间',
    '处理结果', '处理备注', '上报时间'
  ]
  
  let csvContent = headers.join(',') + '\n'
  
  defects.value.forEach((d, idx) => {
    const row = [
      d.defectNo || `DEF-${d.id}`,
      `"${d.location || ''}"`,
      d.distance != null ? d.distance : '',
      `"${d.taskName || ''}"`,
      `"${d.defectType || ''}"`,
      d.isReal ? '是' : '否',
      `"${d.severity || ''}"`,
      `"${d.status || ''}"`,
      `"${d.description || ''}"`,
      d.defectLength != null ? d.defectLength : '',
      d.defectArea != null ? d.defectArea : '',
      d.defectCount != null ? d.defectCount : '',
      `"${d.discoverer || ''}"`,
      `"${d.discoveryMethod || ''}"`,
      `"${d.processor || ''}"`,
      `"${formatDate(d.confirmTime)}"`,
      `"${d.confirmer || ''}"`,
      `"${formatDate(d.processStartTime)}"`,
      `"${formatDate(d.processEndTime)}"`,
      `"${d.processResult || ''}"`,
      `"${d.remark || ''}"`,
      `"${formatDate(d.reportTime)}"`
    ]
    csvContent += row.join(',') + '\n'
  })

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.setAttribute('download', `缺陷管理_${new Date().toLocaleDateString()}.csv`)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

// Excel导出功能
function exportExcel() {
  if (defects.value.length === 0) {
    alert('无数据导出')
    return
  }
  
  // 准备数据
  const excelData = defects.value.map((d, index) => ({
    序号: index + 1,
    缺陷编号: d.defectNo || `DEF-${d.id}`,
    具体位置: d.location || '',
    缺陷距离: d.distance != null ? d.distance + 'm' : '',
    任务名称: d.taskName || '',
    缺陷类型: d.defectType || '',
    是否属实: d.isReal ? '是' : '否',
    严重程度: d.severity || '',
    状态: d.status || '',
    缺陷描述: d.description || '',
    缺陷长度: d.defectLength != null ? d.defectLength + 'm' : '',
    缺陷面积: d.defectArea != null ? d.defectArea + '㎡' : '',
    缺陷数量: d.defectCount != null ? d.defectCount : '',
    发现人员: d.discoverer || '',
    发现方式: d.discoveryMethod || '',
    处理人员: d.processor || '',
    确认时间: formatDate(d.confirmTime),
    确认负责人: d.confirmer || '',
    处理开始时间: formatDate(d.processStartTime),
    处理完成时间: formatDate(d.processEndTime),
    处理结果: d.processResult || '',
    处理备注: d.remark || '',
    上报时间: formatDate(d.reportTime)
  }))
  
  // 创建工作簿
  const wb = XLSX.utils.book_new()
  const ws = XLSX.utils.json_to_sheet(excelData)
  
  // 添加工作表到工作簿
  XLSX.utils.book_append_sheet(wb, ws, '缺陷管理')
  
  // 导出文件
  XLSX.writeFile(wb, `缺陷管理_${new Date().toLocaleDateString()}.xlsx`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border: 2px solid #333;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.wireframe {
  border: 2px solid #333;
  background: white;
  min-height: 600px;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
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

.form-item input,
.form-item select {
  border: 1px solid #999;
  padding: 8px;
  width: 150px;
  border-radius: 4px;
  font-size: 14px;
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
  font-size: 14px;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn:hover {
  background: #e0e0e0;
  transform: translateY(-1px);
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #e6f3ff;
}

.btn-primary:hover {
  background: #d0e6ff;
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
  width: 100%;
  overflow-x: auto;
}

.table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #666;
  font-size: 14px;
  table-layout: fixed;
}

.table th,
.table td {
  border: 1px solid #ccc;
  padding: 10px;
  text-align: left;
  vertical-align: middle;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-cell,
.actions {
  overflow: visible !important;
  white-space: normal !important;
}

.actions {
  white-space: nowrap;
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

.table tbody tr:hover {
  background-color: #f0f8ff;
}

.image-cell {
  text-align: center;
}

.flaw-image {
  width: 80px;
  height: 60px;
  border: 1px solid #999;
  background: #f0f0f0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: #666;
  cursor: pointer;
  overflow: hidden;
  border-radius: 3px;
  transition: all 0.2s;
}

.flaw-image:hover {
  border-color: #0066cc;
  box-shadow: 0 0 5px rgba(0,102,204,0.5);
}

.flaw-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.tag {
  padding: 4px 8px;
  border-radius: 3px;
  font-size: 12px;
  display: inline-block;
}

.tag-success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.tag-danger {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.tag-warning {
  background: #fff3cd;
  color: #856404;
  border: 1px solid #ffeeba;
}

.tag-info {
  background: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

.tag-waiting {
  background: #e0e0e0;
  color: #333;
  border: 1px solid #ccc;
}

.tag-processing {
  background: #e2f0fb;
  color: #0a58ca;
  border: 1px solid #b6d4fe;
}

.link {
  color: #0066cc;
  text-decoration: underline;
  cursor: pointer;
  margin-right: 10px;
  font-size: 14px;
  display: inline-block;
  padding: 2px 5px;
  white-space: nowrap;
}

.link:hover {
  color: #004999;
  text-decoration: none;
  background: #e6f3ff;
  border-radius: 3px;
}

.link-disabled {
  color: #999;
  cursor: not-allowed;
  margin-right: 10px;
  font-size: 14px;
  display: inline-block;
  padding: 2px 5px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
  border: 1px solid #666;
  padding: 10px;
  background: #f8f8f8;
  font-size: 14px;
  border-radius: 4px;
}

.pagination span {
  display: inline-block;
  margin: 0 3px;
  padding: 2px 6px;
  cursor: pointer;
  border-radius: 3px;
  transition: background 0.2s;
}

.pagination span:hover {
  background: #e6f3ff;
}

.pagination .active {
  font-weight: bold;
  background: #e6f3ff;
  border: 1px solid #b3d7ff;
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
  background: white;
  padding: 20px;
  border-radius: 5px;
  max-width: 90%;
  max-height: 90%;
  overflow: auto;
  width: 800px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.modal-image {
  width: 100%;
  height: 300px;
  border: 2px solid #333;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.modal-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.close {
  position: absolute;
  top: 10px;
  right: 15px;
  font-size: 28px;
  font-weight: bold;
  cursor: pointer;
  color: #333;
  transition: color 0.2s;
}

.close:hover {
  color: #000;
}

.flaw-details {
  margin-top: 15px;
  padding: 15px;
  border: 1px solid #ddd;
  background: #f9f9f9;
  border-radius: 4px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.detail-row {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.5;
}

.detail-label {
  font-weight: bold;
  width: 100px;
  color: #555;
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
  word-break: break-word;
}

.ellipsis-cell {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.image-controls {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
  display: flex;
  gap: 5px;
  background: rgba(255, 255, 255, 0.7);
  padding: 5px;
  border-radius: 4px;
}

.control-btn {
  padding: 5px 10px;
  background: #fff;
  border: 1px solid #ccc;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
}

.control-btn:hover {
  background: #f0f0f0;
}

.image-container {
  width: 100%;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

/* 新增快速筛选按钮样式 */
.quick-filters {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  padding: 10px;
  background: #f8f8f8;
  border: 1px solid #666;
  border-radius: 4px;
  flex-wrap: wrap;
}

.quick-filters .btn {
  padding: 6px 12px;
  font-size: 13px;
  background: #e0e0e0;
  border: 1px solid #bbb;
}

.quick-filters .btn:hover {
  background: #d0d0d0;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #666;
  background: #f8f8f8;
  border-radius: 4px;
  flex-wrap: wrap;
}

.pagination button {
  padding: 5px 10px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  border-radius: 3px;
  min-width: 30px;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination span {
  display: inline-block;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 3px;
}

.pagination span:hover:not(.ellipsis) {
  background: #e6f3ff;
}

.pagination .active {
  background: #e6f3ff;
  border: 1px solid #b3d7ff;
  font-weight: bold;
}

.ellipsis {
  cursor: default;
}

.page-jump {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-left: 10px;
}

.page-jump input {
  width: 50px;
  padding: 5px;
  border: 1px solid #ccc;
  border-radius: 3px;
  text-align: center;
}

.page-jump button {
  padding: 5px 10px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-form {
    flex-direction: column;
  }
  
  .table-container {
    overflow-x: auto;
  }
  
  .flaw-details {
    grid-template-columns: 1fr;
  }
}
</style>