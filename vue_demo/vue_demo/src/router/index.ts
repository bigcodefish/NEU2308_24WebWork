import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import SystemManagement from '@/views/SystemManagement.vue'
import DefectManagement from '@/views/DefectManagement.vue' // 缺陷管理页面

// 定义路由元信息类型
declare module 'vue-router' {
  interface RouteMeta {
    title: string
    requiresAuth?: boolean
    permissions?: string[]
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
      permissions: ['system:user:view']
    }
  },
  {
    path: 'role',
    name: 'RoleManagement',
    component: () => import('@/views/RoleManagementView.vue'),
    meta: {
      title: '角色管理',
      permissions: ['system:role:view']
    }
  },
  {
    path: 'menu',
    name: 'MenuManagement',
    component: () => import('@/views/MenuManagementView.vue'),
    meta: {
      title: '菜单管理',
      permissions: ['system:menu:view']
    }
  },
  {
    path: 'dept',
    name: 'DeptManagement',
    component: () => import('@/views/DeptManagementView.vue'),
    meta: {
      title: '部门管理',
      permissions: ['system:dept:view']
    }
  },
  {
    path: 'config',
    name: 'ConfigManagement',
    component: () => import('@/views/ConfigManagementView.vue'),
    meta: {
      title: '参数配置',
      permissions: ['system:config:view']
    }
  }
]

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/system', // 默认跳转
  },
  {
    path: '/system',
    name: 'System',
    component: SystemManagement,
    meta: {
      title: '系统管理',
      requiresAuth: true
    },
    redirect: '/system/user',
    children: systemRoutes
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
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 智能巡线车云平台` : '智能巡线车云平台'

  if (to.meta.requiresAuth) {
    const isAuthenticated = true

    if (!isAuthenticated) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    if (to.meta.permissions) {
      const hasPermission = true

      if (!hasPermission) {
        next('/403')
        return
      }
    }
  }

  next()
})

export default router
