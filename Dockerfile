# OrcheTeam Docker 部署文件
# 基于最新版本的Linux系统，安装Java OpenJDK 25、MongoDB社区版，并部署OrcheTeam应用

# 使用最新的Ubuntu LTS作为基础镜像
FROM ubuntu:24.04

# 设置环境变量，避免交互式安装
ENV DEBIAN_FRONTEND=noninteractive
ENV JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# 设置工作目录
WORKDIR /data

# 更新系统包列表并安装必要的工具
RUN apt-get update && \
    apt-get install -y \
    wget \
    curl \
    gnupg \
    software-properties-common \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# 安装Java OpenJDK 25
# 添加OpenJDK官方仓库
RUN wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add - && \
    echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list && \
    apt-get update && \
    apt-get install -y temurin-25-jdk && \
    rm -rf /var/lib/apt/lists/*

# 验证Java安装
RUN java -version && javac -version

# 安装MongoDB社区版
# 导入MongoDB GPG密钥
RUN wget -qO - https://www.mongodb.org/static/pgp/server-7.0.asc | apt-key add -

# 添加MongoDB仓库
RUN echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu $(lsb_release -cs)/mongodb-org/7.0 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-7.0.list

# 更新包列表并安装MongoDB
RUN apt-get update && \
    apt-get install -y mongodb-org && \
    rm -rf /var/lib/apt/lists/*

# 创建MongoDB数据目录
RUN mkdir -p /data/db && \
    chown -R mongodb:mongodb /data/db

# 复制OrcheTeam应用包到容器
COPY release/OrcheTeam.zip /data/

# 解压OrcheTeam.zip到指定目录
RUN unzip /data/OrcheTeam.zip -d /data/ && \
    rm /data/OrcheTeam.zip

# 修改所有shell脚本为可执行
RUN find /data/orcheTeam/bin -name "*.sh" -type f -exec chmod +x {} \;

# 创建启动脚本
RUN echo '#!/bin/bash\n\
    # 启动MongoDB服务\n\
    mongod --fork --logpath /var/log/mongodb.log --dbpath /data/db\n\
    \n\
    # 等待MongoDB启动完成\n\
    sleep 5\n\
    \n\
    # 启动OrcheTeam应用\n\
    cd /data/orcheTeam\n\
    ./bin/startup.sh\n\
    \n\
    # 保持容器运行\n\
    tail -f /dev/null' > /data/start.sh && \
    chmod +x /data/start.sh

# 暴露端口
# 7474: OrcheTeam应用端口
# 27017: MongoDB端口
EXPOSE 7474 27017

# 设置健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:7474/health || exit 1

# 启动服务
CMD ["/data/start.sh"]
