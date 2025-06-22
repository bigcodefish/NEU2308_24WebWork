import { createRouter, createWebHistory} from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import SystemManagement from '@/views/SystemManagement.vue'

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
    component: () => import('@/views/MenuManagementView.vue'), // 注意文件名修正
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

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/system', // 直接重定向到系统管理
  },
  {
    path: '/system',
    name: 'System',
    component: SystemManagement,
    meta: {
      title: '系统管理',
      requiresAuth: true
    },
    redirect: '/system/user', // 默认重定向到用户管理
    children: systemRoutes
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫 - 权限控制
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 智能巡线车云平台` : '智能巡线车云平台'

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 这里添加你的认证逻辑
    const isAuthenticated = true // 假设已认证
    
    if (!isAuthenticated) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // 检查权限
    if (to.meta.permissions) {
      // 这里添加你的权限检查逻辑
      const hasPermission = true // 假设有权限
      
      if (!hasPermission) {
        next('/403') // 无权限页面
        return
      }
    }
  }

  next()
})

export default router