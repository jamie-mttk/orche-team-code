// 环境配置
export const config = {
  // API基础URL - 测试环境以 http://localhost:8787/开头,生产环境以"/"开头
  apiBaseURL: import.meta.env.DEV 
    ? 'http://localhost:7474' 
    : '/',
  
  // 应用名称
  appName: 'AgentDemoUi',
  
  // 版本号
  version: '1.0.0',
  
  // 开发模式
  isDev: import.meta.env.DEV,
  
  // 生产模式
  isProd: import.meta.env.PROD
}

export default config
