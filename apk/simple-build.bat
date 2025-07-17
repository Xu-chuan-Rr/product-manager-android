@echo off
echo 🚀 简化Android APK构建工具

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到Java环境
    echo.
    echo 请按以下步骤安装Java:
    echo 1. 访问 https://adoptium.net/
    echo 2. 下载并安装 OpenJDK 17
    echo 3. 重启命令行窗口
    echo.
    pause
    exit /b 1
)

echo ✅ Java环境检查通过

REM 检查gradle-wrapper.jar是否存在
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo 📥 首次运行，正在设置Gradle环境...
    call setup-gradle.bat
)

REM 设置环境变量
set JAVA_OPTS=-Xmx2g -XX:MaxMetaspaceSize=512m
set GRADLE_OPTS=-Xmx2g -Dorg.gradle.daemon=false

echo 📦 开始构建APK...
echo 这可能需要几分钟时间，请耐心等待...

REM 构建Debug APK
gradlew.bat clean assembleDebug --stacktrace --info

if %errorlevel% equ 0 (
    echo.
    echo 🎉 构建成功！
    echo 📍 APK文件位置: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 📱 安装说明:
    echo 1. 将APK文件传输到Android手机
    echo 2. 在手机设置中开启"未知来源"安装
    echo 3. 点击APK文件进行安装
    echo.
) else (
    echo.
    echo ❌ 构建失败
    echo 💡 可能的解决方案:
    echo 1. 检查网络连接
    echo 2. 确保Java版本为8-17
    echo 3. 尝试运行: gradlew.bat clean
    echo.
)

pause
