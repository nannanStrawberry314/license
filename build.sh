#!/bin/bash

# 从 .env 文件中导入环境变量
if [ -f ".env" ]; then
    export $(cat .env | sed 's/#.*//g' | xargs)
else
    echo ".env file not found"
    exit 1
fi

# 使用环境变量中的用户名和密码尝试登录Docker Hub
docker login -u="${HUB_USER}" -p="${HUB_PASS}"
status=$?

# 检查登录命令的退出状态
if [ $status -ne 0 ]; then
    echo "Docker login failed, exiting..."
    exit $status
else
    echo "Docker login successful."
fi

# 创建并使用一个新的 Buildx 构建器实例，如果已存在则使用现有的
BUILDER_NAME=multi-platform-build
docker buildx create --name $BUILDER_NAME --use || true
docker buildx use $BUILDER_NAME
docker buildx inspect --bootstrap

# VERSION
VERSION=1.2.0

# 使用 Docker Buildx 构建镜像，同时标记为 latest 和 VERSION，支持多架构
docker buildx build \
  --no-cache \
  --platform linux/amd64,linux/arm64 \
  -t ${HUB_USER}/${HUB_REPO}:$VERSION \
  -t ${HUB_USER}/${HUB_REPO} . \
  --push \
  --progress=plain

# 登出 Docker Hub
docker logout