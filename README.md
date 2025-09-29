# OrcheTeam 项目

## 项目介绍

OrcheTeam 是一个企业级智能代理（Agent）管理平台，采用模块化架构设计，支持多种代理类型和插件扩展。

项目基于**任务规划智能体**和**工具智能体**的协作模式，通过智能任务分解和动态流程创建，实现复杂任务的自动化执行。相比传统的固定流程实现，OrcheTeam 提供了更加灵活和智能的任务处理能力。

虽然通用人工智能还有很长的路要走，但我们相信通过提供合适的工具和规划能力，可以实现针对特定场景的通用智能体。本项目旨在为场景智能体提供完整的开发、管理和运行平台。由于时间关系，大量想法和思路没有实现，当前发布可称为技术预览版。

### 主要特性

- **扩展性强** - 可以快速增加新的智能体实现，扩展功能。场景化的智能体需要场景特定的功能
- **MCP支持** - 支持把MCP转换成智能体，并提供一定的定制能力，如选择加入那些MCP功能，修改方法和参数备注等
- **执行可视化** - 实时展示任务执行过程，可中断。后续会允许用户动态交互
- **任务调度** - 集成Quartz调度器，支持任务调度；提供定时任务管理功能
- **数据持久化** - 基于MongoDB的高性能数据存储，支持大数据量处理
- **任务管理** - 任务管理和回放

### 屏幕截图

![模型配置](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/model.png)
![网络搜索智能体](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/webSearch_agent.png)
![ReAct智能体](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/ReAct_agent.png)
![执行界面1](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/execute_1.png)
![执行界面2](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/execute_2.png)
![执行界面3](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/execute_3.png)
![执行界面4](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/execute_4.png)
![执行界面5](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/execute_5.png)
![执行界面6](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/execute_6.png)
![任务管理](https://github.com/jamie-mttk/orche-team/blob/main/screenCaptures/task.png)



## 安装
### 快速安装
#### 环境准备
- 安装 MongoDB 最新版本
- 安装 Java 24 或更新版本

#### 安装步骤
1. 在任意目录下下载安装包：

 - 国外github下载命令: git clone https://github.com/jamie-mttk/orche-team

 - 国内Gitee下载命令: git clone https://gitee.com/jamie0828/orche-team

2. 修改配置：
- `d:\orche-team\conf\mongo.properties` 修改 mongodb 配置（如果 mongodb 在本地且无需认证可不修改）
- `d:\orche-team\bin\server.bat` (Linux 下为 `server.sh`) 修改 JAVA_HOME（如果环境变量里有则无需设置）

#### 启动运行

运行 
```
cd  d:\orche-team\bin\
startup.bat 
```

(Linux 下为 `startup.sh`)，第一次启动较慢，系统需要做一些初始化。

启动完成后，使用浏览器打开 http://localhost:7474 访问系统。

### Docker安装
1. 在任意目录下下载安装包：

- 国外github下载命令: git clone https://github.com/jamie-mttk/orche-team
- 国内Gitee下载命令: git clone https://gitee.com/jamie0828/orche-team

2. 进入orche-team目录
```
cd  orche-team
```
3. build docker
```
docker build -t orcheteam .
```
4.启动 Docker
```
docker run -d   -p 7474:7474      -v orcheteam_db:/data/db  --name orcheteam   orcheteam
```
5.访问
用浏览器打开 http://localhost:7474 访问系统。


## 快速开始

### 模型配置
测试过千问和 DeepSeek V3，模型必须支持 Function calling，建议使用最大 Token 数大的模型。

### 智能体配置
- **互联网搜索**：需要 Serper API Key，可访问 https://serpapi.com/ 注册，免费赠送额度足够使用很长时间
- **报告生成助手**：生成 HTML/Markdown/PPT 格式报告
- **React 智能体**：把互联网搜索和报告生成助手作为合作智能体加入

### 开始使用
点击 React 智能体的运行图标开始使用，譬如可以使用下面的问题提问

**搜索2024年中国各省市GDP并生成报告**

## 许可证与联系方式

本项目采用 MIT 许可证。

欢迎提交 Issue 和 Pull Request 来帮助改进项目。对本项目感兴趣的请与我联系，欢迎合作开发。

**联系方式：**
- 项目维护者：Jamie Wang
- 邮件地址：jamie0828@163.com
- 项目地址：
  - https://github.com/jamie-mttk/orche-team-code
  - https://gitee.com/jamie0828/orche-team-code
