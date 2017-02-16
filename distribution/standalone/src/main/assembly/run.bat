::==============================================================================
::    Copyright 2017 Huawei Technologies Co., Ltd.
  
::    Licensed under the Apache License, Version 2.0 (the "License");
::    you may not use this file except in compliance with the License.
::    You may obtain a copy of the License at
   
::        http://www.apache.org/licenses/LICENSE-2.0
   
::       Unless required by applicable law or agreed to in writing, software
::    distributed under the License is distributed on an "AS IS" BASIS,
::    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
::    See the License for the specific language governing permissions and
::    limitations under the License.
::==============================================================================
 
@echo off
title vnfsdk-functest

set RUNHOME=%~dp0
echo ### RUNHOME: %RUNHOME%
echo ### Starting vnfsdk-functest

set JAVA="%JAVA_HOME%\bin\java.exe"
set port=8312
set jvm_opts=-Xms50m -Xmx128m
rem set jvm_opts=%jvm_opts% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=%port%,server=y,suspend=n
set class_path=%RUNHOME%;%RUNHOME%vnf-sdk-function-test.jar
echo ### jvm_opts: %jvm_opts%
echo ### class_path: %class_path%

%JAVA% -classpath %class_path% %jvm_opts% org.openo.vnfsdk.functest.VnfSdkFuncTestApp server %RUNHOME%conf/vnfsdkfunctest.yml

IF ERRORLEVEL 1 goto showerror
exit
:showerror
echo WARNING: Error occurred during startup or Server abnormally stopped by way of killing the process,Please check!
echo After checking, press any key to close 
pause
exit