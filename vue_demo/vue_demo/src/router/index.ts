import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import SystemManagement from '@/views/SystemManagement.vue'
import DefectManagement from '@/views/DefectManagement.vue'

declare module 'vue-router' {
  interface RouteMeta {
    title: string
    requiresAuth?: boolean
    permissions?: string[]
    icon?: string
  }
}

// 系统管理子路由
const systemRoutes: RouteRecordRaw[] = [
  {
    path: 'user',
    name: 'UserManagement',
    component: () => import('@/views/UserManagementView.vue'),
    meta: {
      title: '用户管理',
      icon: '👤',
      permissions: ['system:user:view']
    }
  },
  {
    path: 'role',
    name: 'RoleManagement',
    component: () => import('@/views/RoleManagementView.vue'),
    meta: {
      title: '角色管理',
      icon: '🔑',
      permissions: ['system:role:view']
    }
  },
  {
    path: 'menu',
    name: 'MenuManagement',
    component: () => import('@/views/MenuManagementView.vue'),
    meta: {
      title: '菜单管理',
      icon: '📋',
      permissions: ['system:menu:view']
    }
  },
  {
    path: 'dept',
    name: 'DeptManagement',
    component: () => import('@/views/DeptManagementView.vue'),
    meta: {
      title: '部门管理',
      icon: '🏢',
      permissions: ['system:dept:view']
    }
  },
  {
    path: 'config',
    name: 'ConfigManagement',
    component: () => import('@/views/ConfigManagementView.vue'),
    meta: {
      title: '参数配置',
      icon: '⚙️',
      permissions: ['system:config:view']
    }
  }
]

const routes: RouteRecordRaw[] = [
  {
		path: '/',
		name: 'login',
		component: () => import('@/views/Login.vue'),
		meta: {
			title: '登录'
		}
	},
  {
		path: '/register',
		name: 'register',
		component: () => import('@/views/Register.vue'),
		meta: {
			title: '注册'
		}
	},
  {
    path: '/system',
    name: 'System',
    component: SystemManagement,
    meta: { title: '系统管理', requiresAuth: true },
    redirect: '/system/user',
    children: systemRoutes
  },
  {
		path: '/homescreen',
		name: 'HomeScreen',
		component: () => import('@/views/HomeScreen.vue'),
		meta: {
			title: '首页',
			requiresAuth: true
		}
	},
  {
		path: '/defect',
		name: 'DefectManagement',
		component: DefectManagement,
		meta: {
			title: '缺陷管理',
			requiresAuth: true,
			permissions: ['defect:view']
		}
	},
  {
		path: '/task',
		name: 'TaskManagement',
		component: () => import('@/views/TaskManagement.vue'),
		meta: {
			title: '任务管理',
			requiresAuth: true
		}
	},
	{
		path: '/task-detail/:id',
		name: 'TaskDetailPage',
		component: () => import('@/components/TaskDetailPage.vue'),
		meta: {
			title: '任务详情',
			requiresAuth: true
		}
	},
  {
    path: '/:pathMatch(.*)*',
    redirect: '/system/user'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 系统管理` : '系统管理'

  // 模拟权限验证
  if (to.meta.requiresAuth) {
    const hasAuth = true // 实际项目中从store或cookie获取
    if (!hasAuth) return next('/')
  }

  next()
})

export default router
