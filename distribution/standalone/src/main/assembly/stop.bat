
@echo off
title stopping vnfsdk-functest

set HOME=%~dp0
set Main_Class="org.openo.vnfsdk.functest.VnfSdkFuncTestApp"

echo ================== extsys-service info =============================================
echo HOME=$HOME
echo Main_Class=%Main_Class%
echo ===============================================================================

echo ### Stopping vnfsdk-functest
cd /d %HOME%

for /f "delims=" %%i in ('"%JAVA_HOME%\bin\jcmd"') do (
  call find_kill_process "%%i" %Main_Class%
)
exit