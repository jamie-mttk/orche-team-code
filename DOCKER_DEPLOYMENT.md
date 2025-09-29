# OrcheTeam Docker 部署指南

## 概述

本指南将帮助您使用Docker容器化部署OrcheTeam项目。Dockerfile已经配置了所有必要的组件，包括Java OpenJDK 25、MongoDB社区版和OrcheTeam应用。

## 前置条件

- 已安装Docker和Docker Compose
- 确保release/OrcheTeam.zip文件存在
- 确保有足够的磁盘空间（建议至少2GB）

## 构建Docker镜像

### 1. 构建镜像

在项目根目录下执行以下命令：

```bash
# 构建Docker镜像
docker build -t orcheteam:latest .
```

### 2. 验证镜像构建

```bash
# 查看构建的镜像
docker images | grep orcheteam
```

## 运行Docker容器

### 1. 基本运行

```bash
# 运行容器
docker run -d \
  --name orcheteam-container \
  -p 7474:7474 \
  -p 27017:27017 \
  orcheteam:latest
```

### 2. 带数据持久化的运行

```bash
# 创建数据卷
docker volume create orcheteam-data
docker volume create orcheteam-mongodb

# 运行容器并挂载数据卷
docker run -d \
  --name orcheteam-container \
  -p 7474:7474 \
  -p 27017:27017 \
  -v orcheteam-data:/data/orcheTeam \
  -v orcheteam-mongodb:/data/db \
  orcheteam:latest
```

### 3. 使用Docker Compose（推荐）

创建`docker-compose.yml`文件：

```yaml
version: '3.8'

services:
  orcheteam:
    build: .
    container_name: orcheteam-container
    ports:
      - "7474:7474"
      - "27017:27017"
    volumes:
      - orcheteam-data:/data/orcheTeam
      - orcheteam-mongodb:/data/db
    restart: unless-stopped
    environment:
      - JAVA_OPTS=-Xmx2g -Xms1g
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:7474/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

volumes:
  orcheteam-data:
  orcheteam-mongodb:
```

然后运行：

```bash
# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

## 访问应用

### 1. Web管理界面

- 访问地址：http://localhost:7474/admin
- 默认端口：7474

### 2. MongoDB数据库

- 连接地址：localhost:27017
- 数据库名：orcheteam

## 容器管理

### 1. 查看容器状态

```bash
# 查看运行中的容器
docker ps

# 查看容器日志
docker logs orcheteam-container

# 实时查看日志
docker logs -f orcheteam-container
```

### 2. 进入容器

```bash
# 进入容器shell
docker exec -it orcheteam-container /bin/bash

# 查看应用状态
docker exec -it orcheteam-container ps aux
```

### 3. 重启容器

```bash
# 重启容器
docker restart orcheteam-container

# 停止容器
docker stop orcheteam-container

# 启动容器
docker start orcheteam-container
```

## 故障排除

### 1. 检查容器健康状态

```bash
# 查看容器健康状态
docker inspect orcheteam-container | grep -A 10 "Health"
```

### 2. 查看详细日志

```bash
# 查看应用日志
docker exec -it orcheteam-container tail -f /data/orcheTeam/logs/application.log

# 查看MongoDB日志
docker exec -it orcheteam-container tail -f /var/log/mongodb.log
```

### 3. 常见问题

**问题1：端口冲突**
```bash
# 检查端口占用
netstat -tulpn | grep :7474
netstat -tulpn | grep :27017

# 使用不同端口
docker run -d --name orcheteam-container -p 8080:7474 -p 27018:27017 orcheteam:latest
```

**问题2：内存不足**
```bash
# 调整Java内存参数
docker run -d --name orcheteam-container \
  -p 7474:7474 \
  -p 27017:27017 \
  -e JAVA_OPTS="-Xmx1g -Xms512m" \
  orcheteam:latest
```

**问题3：数据持久化问题**
```bash
# 检查数据卷
docker volume ls
docker volume inspect orcheteam-data
```

## 生产环境部署建议

### 1. 资源限制

```bash
# 设置资源限制
docker run -d \
  --name orcheteam-container \
  --memory="2g" \
  --cpus="2" \
  -p 7474:7474 \
  -p 27017:27017 \
  orcheteam:latest
```

### 2. 网络配置

```bash
# 创建自定义网络
docker network create orcheteam-network

# 使用自定义网络运行
docker run -d \
  --name orcheteam-container \
  --network orcheteam-network \
  -p 7474:7474 \
  -p 27017:27017 \
  orcheteam:latest
```

### 3. 安全配置

```bash
# 使用非root用户运行
docker run -d \
  --name orcheteam-container \
  --user 1000:1000 \
  -p 7474:7474 \
  -p 27017:27017 \
  orcheteam:latest
```

## 更新和维护

### 1. 更新应用

```bash
# 停止容器
docker stop orcheteam-container

# 删除旧容器
docker rm orcheteam-container

# 重新构建镜像
docker build -t orcheteam:latest .

# 启动新容器
docker run -d --name orcheteam-container -p 7474:7474 -p 27017:27017 orcheteam:latest
```

### 2. 备份数据

```bash
# 备份MongoDB数据
docker exec orcheteam-container mongodump --out /data/backup

# 复制备份文件到主机
docker cp orcheteam-container:/data/backup ./mongodb-backup
```

## 总结

通过以上步骤，您可以成功部署OrcheTeam项目到Docker容器中。Dockerfile已经包含了所有必要的组件和配置，确保应用能够正常运行。

如有任何问题，请检查容器日志或联系项目维护者。
