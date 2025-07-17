@echo off
echo 🚀 开始构建 Android APK...

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到Java环境
    echo 请安装 Java 8 或更高版本
    pause
    exit /b 1
)

echo ✅ Java环境检查通过

echo 📦 开始下载依赖和构建...

REM 构建Debug版本
echo 🔨 构建Debug APK...
gradlew.bat assembleDebug

if %errorlevel% equ 0 (
    echo ✅ Debug APK 构建成功!
    echo 📍 文件位置: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo ❌ Debug APK 构建失败
    pause
    exit /b 1
)

REM 构建Release版本
echo 🔨 构建Release APK...
gradlew.bat assembleRelease

if %errorlevel% equ 0 (
    echo ✅ Release APK 构建成功!
    echo 📍 文件位置: app\build\outputs\apk\release\app-release-unsigned.apk
    echo.
    echo 🎉 构建完成!
    echo 📱 您可以安装 app-debug.apk 到手机上进行测试
    echo ⚠️  Release版本需要签名后才能发布
) else (
    echo ❌ Release APK 构建失败
)

pause
