@echo off
echo 🔧 设置Gradle环境...

REM 创建gradle wrapper目录
if not exist "gradle\wrapper" mkdir gradle\wrapper

REM 下载gradle-wrapper.jar
echo 📥 下载Gradle Wrapper...
powershell -Command "& {Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'}"

if exist "gradle\wrapper\gradle-wrapper.jar" (
    echo ✅ Gradle Wrapper下载成功
) else (
    echo ❌ Gradle Wrapper下载失败，尝试备用方法...
    powershell -Command "& {Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.0-bin.zip' -OutFile 'gradle-8.0-bin.zip'}"
    powershell -Command "& {Expand-Archive -Path 'gradle-8.0-bin.zip' -DestinationPath '.' -Force}"
    copy "gradle-8.0\lib\gradle-wrapper-8.0.jar" "gradle\wrapper\gradle-wrapper.jar"
    rmdir /s /q gradle-8.0
    del gradle-8.0-bin.zip
)

echo 🚀 现在可以运行构建了！
echo 运行: gradlew.bat assembleDebug
pause
