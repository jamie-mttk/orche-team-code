import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/home/index.vue')
  },
  {
    path: '/agent',
    name: 'Agent',
    component: () => import('../views/agent/index.vue')
  },
  {
    path: '/task',
    name: 'Task',
    component: () => import('../views/task/index.vue')
  },
  {
    path: '/modelSetting',
    name: 'ModelSetting',
    component: () => import('../views/model-settings/index.vue')
  },
  {
    path: '/agentExecute/:agentId',
    name: 'AgentExecute',
    component: () => import('../views/agentExecute/index.vue')
  },
  {
    path: '/test',
    name: 'test',
    component: () => import('../views/test/index.vue')
  },
  {
    path: '/scheduler',
    name: 'Scheduler',
    component: () => import('../views/scheduler/index.vue')
  },
  {
    path: '/deploy',
    name: 'Deploy',
    component: () => import('../views/deploy/index.vue')
  },
  {
    path: '/systemConfig',
    name: 'System Config',
    component: () => import('../views/systemConfig/index.vue')
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
