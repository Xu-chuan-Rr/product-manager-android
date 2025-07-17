@echo off
echo 🔍 Android开发环境检查工具
echo ================================

echo.
echo 📋 检查Java环境...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Java已安装
    java -version 2>&1 | findstr "version"
) else (
    echo ❌ Java未安装或未配置
    echo.
    echo 💡 解决方案:
    echo 1. 访问 https://adoptium.net/
    echo 2. 下载 OpenJDK 17 LTS
    echo 3. 安装后重启命令行
    goto :end
)

echo.
echo 📋 检查JAVA_HOME环境变量...
if defined JAVA_HOME (
    echo ✅ JAVA_HOME已设置: %JAVA_HOME%
) else (
    echo ⚠️  JAVA_HOME未设置（通常不影响构建）
)

echo.
echo 📋 检查Gradle Wrapper...
if exist "gradlew.bat" (
    echo ✅ gradlew.bat存在
) else (
    echo ❌ gradlew.bat不存在
)

if exist "gradle\wrapper\gradle-wrapper.jar" (
    echo ✅ gradle-wrapper.jar存在
) else (
    echo ❌ gradle-wrapper.jar不存在
    echo 💡 运行 setup-gradle.bat 来下载
)

if exist "gradle\wrapper\gradle-wrapper.properties" (
    echo ✅ gradle-wrapper.properties存在
) else (
    echo ❌ gradle-wrapper.properties不存在
)

echo.
echo 📋 检查项目文件...
if exist "build.gradle" (
    echo ✅ 根build.gradle存在
) else (
    echo ❌ 根build.gradle不存在
)

if exist "app\build.gradle" (
    echo ✅ app/build.gradle存在
) else (
    echo ❌ app/build.gradle不存在
)

if exist "app\src\main\AndroidManifest.xml" (
    echo ✅ AndroidManifest.xml存在
) else (
    echo ❌ AndroidManifest.xml不存在
)

echo.
echo 📋 检查网络连接...
ping -n 1 google.com >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ 网络连接正常
) else (
    echo ⚠️  网络连接可能有问题
    echo 💡 构建时可能需要VPN或代理
)

echo.
echo 📋 系统信息...
echo 操作系统: %OS%
echo 处理器架构: %PROCESSOR_ARCHITECTURE%

echo.
echo 🎯 建议的构建步骤:
echo 1. 确保Java已正确安装
echo 2. 运行 setup-gradle.bat（如果需要）
echo 3. 运行 simple-build.bat 开始构建
echo.
echo 🌐 或者使用在线构建（推荐）:
echo 1. 上传代码到GitHub
echo 2. 使用GitHub Actions自动构建
echo 3. 下载生成的APK文件

:end
echo.
pause
