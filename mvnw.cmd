@echo off
setlocal
set "MAVEN_HOME=C:\Users\Lenovo\.maven\apache-maven-3.9.6"
if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    call "%MAVEN_HOME%\bin\mvn.cmd" %*
) else (
    mvn %*
)
endlocal
