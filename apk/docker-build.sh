#!/bin/bash

echo "🐳 使用Docker构建Android APK..."

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ 错误: 未找到Docker"
    echo "请先安装Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

echo "✅ Docker环境检查通过"

# 构建Docker镜像
echo "🔨 构建Docker镜像..."
docker build -t android-builder .

if [ $? -eq 0 ]; then
    echo "✅ Docker镜像构建成功!"
else
    echo "❌ Docker镜像构建失败"
    exit 1
fi

# 运行容器并构建APK
echo "📦 在容器中构建APK..."
docker run --rm -v $(pwd)/output:/app/app/build/outputs/apk android-builder

echo "🎉 构建完成!"
echo "📍 APK文件已保存到 output/ 目录"
