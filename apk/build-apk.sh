#!/bin/bash

echo "🚀 开始构建 Android APK..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境"
    echo "请安装 Java 8 或更高版本"
    exit 1
fi

# 检查Java版本
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "✅ Java版本: $java_version"

# 给gradlew执行权限
chmod +x gradlew

echo "📦 开始下载依赖和构建..."

# 构建Debug版本
echo "🔨 构建Debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Debug APK 构建成功!"
    echo "📍 文件位置: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Debug APK 构建失败"
    exit 1
fi

# 构建Release版本（未签名）
echo "🔨 构建Release APK..."
./gradlew assembleRelease

if [ $? -eq 0 ]; then
    echo "✅ Release APK 构建成功!"
    echo "📍 文件位置: app/build/outputs/apk/release/app-release-unsigned.apk"
    echo ""
    echo "🎉 构建完成!"
    echo "📱 您可以安装 app-debug.apk 到手机上进行测试"
    echo "⚠️  Release版本需要签名后才能发布"
else
    echo "❌ Release APK 构建失败"
fi
