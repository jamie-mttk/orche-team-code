import http from '../utils/request'

// 模型相关API调用方法
export const modelAPI = {
  /**
   * 查询模型列表
   * GET /llmModels/query
   * 返回值 content里是JSON对象,代表一个个的返回对象,其他字段为分页信息
   */
  searchModels: (params?: any) => {
    return http.get('/llmModel/query', { params })
  },

  /**
   * 保存模型(新增和修改)
   * POST /save
   * post body里是希望保存的模型JSON,有_id字段是修改,没有是新增
   */
  saveModel: (data: any) => {
    return http.post('/llmModel/save', data)
  },

  /**
   * 单条获取模型
   * GET /{id}
   * {id}是模型_id字段
   */
  getModel: (id: string) => {
    return http.get(`/llmModel/${id}`)
  },

  /**
   * 删除模型
   * DELETE /{id}
   * {id}是模型_id字段
   */
  deleteModel: (id: string) => {
    return http.delete(`/llmModel/${id}`)
  }
}

// 智能体相关API调用方法
export const agentAPI = {
  /**
   * 查询智能体列表
   * GET /agent/query
   * 返回值 content里是JSON对象,代表一个个的返回对象,其他字段为分页信息
   */
  searchAgents: (params?: any) => {
    return http.get('/agent/query', { params })
  },

  /**
   * 保存智能体(新增和修改)
   * POST /save
   * post body里是希望保存的智能体JSON,有_id字段是修改,没有是新增
   */
  saveAgent: (data: any) => {
    return http.post('/agent/save', data)
  },

  /**
   * 单条获取智能体
   * GET /{id}
   * {id}是智能体_id字段
   */
  getAgent: (id: string) => {
    return http.get(`/agent/${id}`)
  },

  /**
   * 删除智能体
   * DELETE /{id}
   * {id}是智能体_id字段
   */
  deleteAgent: (id: string) => {
    return http.delete(`/agent/${id}`)
  }
}

// 智能体模板相关API调用方法
export const agentTemplateAPI = {
  /**
   * 查询智能体模板列表
   * GET /agentTemplate/query
   */
  searchTemplates: (params?: any) => {
    return http.get('/agentTemplate/query', { params })
  },

  /**
   * 根据key获取智能体模板
   * GET /agentTemplate/byKey/{key}
   */
  getTemplateByKey: (key: string) => {
    return http.get(`/agentTemplate/byKey/${key}`)
  }
}


// 系统配置相关API调用方法
export const systemConfigAPI = {
  /**
   * 获取系统配置
   * GET /systemConfig/obtain
   */
  getSystemConfig: () => {
    return http.get('/systemConfig/obtain')
  },

  /**
   * 保存系统配置
   * POST /systemConfig/save
   */
  saveSystemConfig: (data: any) => {
    return http.post('/systemConfig/save', data)
  }
}

// 智能体标签相关API调用方法（扩展）
export const agentTagAPI = {
  /**
   * 查询所有标签
   * GET /agentTag/query
   * 返回值是标签数组，每个标签包含_id和name字段
   */
  queryTags: () => {
    return http.get('/agentTag/query')
  },

  /**
   * 保存智能体标签（新增和修改）
   * POST /agentTag/save
   * post body里是希望保存的标签JSON,有_id字段是修改,没有是新增
   */
  saveTag: (data: any) => {
    return http.post('/agentTag/save', data)
  },

  /**
   * 删除智能体标签
   * DELETE /agentTag/{id}
   * {id}是标签_id字段
   */
  deleteTag: (id: string) => {
    return http.delete(`/agentTag/${id}`)
  }
}

// 定时任务相关API调用方法
export { schedulerAPI } from './scheduler'