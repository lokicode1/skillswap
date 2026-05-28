@REM ----------------------------------------------------------------------------
@REM Maven Wrapper for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal EnableExtensions EnableDelayedExpansion

set MVNW_VERBOSE=false
if not "%MVNW_VERBOSE%"=="true" (
  set MAVEN_OPTS=%MAVEN_OPTS%
)

set BASE_DIR=%~dp0
if "%BASE_DIR:~-1%"=="\" set BASE_DIR=%BASE_DIR:~0,-1%
cd /d "%BASE_DIR%"

set WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
set WRAPPER_PROPERTIES=.mvn\wrapper\maven-wrapper.properties
set WRAPPER_DOWNLOADER=.mvn\wrapper\MavenWrapperDownloader.java
set POWERSHELL_EXE=C:\Windows\System32\WindowsPowerShell\v1.0\powershell.exe
set WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

if not exist "%WRAPPER_JAR%" (
  echo Maven wrapper jar missing. Downloading...
  "%POWERSHELL_EXE%" -NoProfile -Command "Invoke-WebRequest -Uri '!WRAPPER_URL!' -OutFile '%WRAPPER_JAR%'" || exit /b 1
)

if "%JAVA_HOME%"=="" (
  set JAVA_EXE=java
) else (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"!JAVA_EXE!" %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" -Dmaven.multiModuleProjectDirectory="%BASE_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*

endlocal

