@echo off
echo ========================================
echo MikuDream UUID Uploader 插件编译脚本
echo ========================================
echo.

echo 正在检查Java版本...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java，请确保已安装Java 17+
    pause
    exit /b 1
)

echo.
echo 正在检查Maven...
mvn -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven，请确保已安装Maven
    pause
    exit /b 1
)

echo.
echo 正在清理并编译项目...
mvn clean package

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 编译成功！
    echo JAR文件位置: target\uuid-uploader-1.0.0.jar
    echo ========================================
    echo.
    echo 请将JAR文件复制到服务器的plugins文件夹中
    echo.
) else (
    echo.
    echo ========================================
    echo 编译失败！请检查错误信息
    echo ========================================
)

pause 