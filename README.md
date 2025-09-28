# OrcheTeam 项目

## 项目介绍

OrcheTeam 是一个基于Java的企业级智能代理（Agent）管理平台，采用模块化架构设计，支持多种代理类型和插件扩展。项目使用Maven进行依赖管理，支持集群部署和动态加载功能。

### 主要特性

- 🚀 **模块化架构** - 采用Maven多模块设计，各模块职责清晰
- 🤖 **智能代理系统** - 支持多种类型的智能代理，包括基础代理和示例代理
- 🔌 **插件化扩展** - 支持动态加载和部署插件
- 🌐 **Web管理界面** - 基于Spring MVC的Web管理控制台
- 📊 **集群支持** - 支持多实例集群部署
- ⏰ **任务调度** - 集成Quartz调度器，支持定时任务
- 💾 **数据持久化** - 基于MongoDB的数据存储

## 模块结构

### 核心模块

#### 1. **bootstrap** - 启动模块
- **作用**: 系统启动入口，负责初始化系统环境
- **功能**: 
  - 系统启动引导
  - 类加载器管理
  - 升级支持
- **依赖**: 无外部依赖

#### 2. **util** - 工具模块
- **作用**: 提供通用工具类和基础功能
- **功能**:
  - 字符串处理
  - 日期时间工具
  - 文件操作
  - 网络工具
  - 加密解密
  - 动态加载支持
- **依赖**: SLF4J日志框架

#### 3. **core** - 核心模块
- **作用**: 定义系统核心接口和基础架构
- **功能**:
  - 服务接口定义
  - 生命周期管理
  - 插件系统架构
  - 代理框架
  - 事件系统
  - 数据持久化接口
- **依赖**: util, MongoDB驱动, Jackson, Quartz

### 实现模块

#### 4. **impl** - 实现模块
- **作用**: 核心接口的具体实现
- **功能**:
  - 服务实现
  - 代理执行引擎
  - 集群管理
  - 任务调度实现
  - 健康检查
  - 国际化支持
  - 系统监控
- **依赖**: bootstrap, core, Quartz-MongoDB, Log4j, Jackson, OSHI

#### 5. **sys** - 系统模块
- **作用**: 系统级服务和基础设施
- **功能**:
  - HTTP服务器（Jetty）
  - 系统配置管理
  - 证书管理
  - 系统信息收集
- **依赖**: core, util, Jetty

### 应用模块

#### 6. **web** - Web应用模块
- **作用**: Web管理界面和API服务
- **功能**:
  - Spring MVC Web应用
  - RESTful API
  - 管理控制台
  - 文件上传下载
  - 用户界面
- **依赖**: impl, Spring MVC, Commons FileUpload

#### 7. **basicAgent** - 基础代理模块
- **作用**: 提供基础智能代理功能
- **功能**:
  - 上下文总结代理
  - Java代码执行代理
  - MCP（Model Context Protocol）支持
  - 报告生成代理
  - 智能反应代理
  - Web搜索代理
  - SQL查询代理
  - 邮件发送代理
- **依赖**: core, JSoup, JavaMail, MySQL驱动

#### 8. **sampleAgent** - 示例代理模块
- **作用**: 提供示例代理实现
- **功能**:
  - 简单分析代理
  - 多项式回归分析
  - 数据矩阵处理
  - CSV数据处理
- **依赖**: core, Commons CSV, Commons Math

## 技术栈

- **Java**: JDK 24
- **构建工具**: Maven 3.x
- **Web框架**: Spring MVC 5.3.30
- **数据库**: MongoDB 3.12.14
- **任务调度**: Quartz 2.3.1
- **Web服务器**: Jetty 9.4.58
- **日志**: Log4j 2.25.1
- **JSON处理**: Jackson 2.19.2
- **系统监控**: OSHI 6.8.3

## 项目结构

```
OrcheTeam/
├── bootstrap/          # 启动模块
├── util/              # 工具模块
├── core/              # 核心模块
├── impl/              # 实现模块
├── sys/               # 系统模块
├── web/               # Web应用模块
├── basicAgent/        # 基础代理模块
├── sampleAgent/       # 示例代理模块
├── ui/                # 前端界面（Vue.js）
├── quartz-mongodb/    # Quartz MongoDB集成
└── .github/           # GitHub Actions配置
```

## 快速开始

### 环境要求

- JDK 24+
- Maven 3.6+
- MongoDB 4.0+

### 构建项目

```bash
# 克隆项目
git clone https://github.com/jamie-mttk/orche-team-code.git
cd orche-team-code

# 编译项目
mvn clean compile

# 打包项目
mvn clean package
```

### 运行项目

```bash
# 启动系统
java -jar impl/target/impl-1.0.jar

# 访问Web管理界面
# http://localhost:8080/admin
```

## 开发指南

### 添加新的代理

1. 在 `basicAgent` 模块中创建新的代理类
2. 实现 `Agent` 接口
3. 添加相应的注解配置
4. 在 `web` 模块中注册新的代理

### 添加新的服务

1. 在 `core` 模块中定义服务接口
2. 在 `impl` 模块中实现服务
3. 在 `sys` 模块中注册服务

## 许可证

本项目采用 MIT 许可证。

## 贡献

欢迎提交 Issue 和 Pull Request 来帮助改进项目。

## 联系方式

- 项目维护者: jamie-mttk
- 项目地址: https://github.com/jamie-mttk/orche-team-code
