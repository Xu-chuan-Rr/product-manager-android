@echo off
echo 🔧 Java自动安装工具
echo =====================

echo.
echo 📋 检查当前Java状态...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Java已安装:
    java -version 2>&1
    echo.
    echo 是否要重新安装Java? (y/N)
    set /p choice=
    if /i not "%choice%"=="y" goto :end
)

echo.
echo 📥 正在下载Java 17...
echo 这可能需要几分钟，请耐心等待...

REM 创建临时目录
if not exist "temp" mkdir temp

REM 下载Java 17
echo 下载中...
powershell -Command "& {$ProgressPreference = 'SilentlyContinue'; Invoke-WebRequest -Uri 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.9%%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.9_9.msi' -OutFile 'temp\openjdk17.msi'}"

if exist "temp\openjdk17.msi" (
    echo ✅ 下载完成
    echo.
    echo 🚀 开始安装Java...
    echo 请在弹出的安装向导中点击"下一步"完成安装
    
    REM 静默安装Java
    msiexec /i "temp\openjdk17.msi" /quiet /norestart
    
    echo ✅ Java安装完成
    
    REM 清理临时文件
    del "temp\openjdk17.msi"
    rmdir temp
    
    echo.
    echo 🔄 请重启命令行窗口，然后运行 环境检查.bat 验证安装
    
) else (
    echo ❌ 下载失败
    echo.
    echo 💡 手动安装步骤:
    echo 1. 访问 https://adoptium.net/temurin/releases/
    echo 2. 选择 OpenJDK 17 LTS
    echo 3. 下载 Windows x64 MSI安装包
    echo 4. 运行安装包并按提示安装
    echo 5. 重启命令行窗口
)

:end
echo.
pause
