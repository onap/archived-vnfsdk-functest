
echo %1 | findstr %2 >NUL
echo ERRORLEVEL=%ERRORLEVEL%
IF ERRORLEVEL 1 goto findend
for /f "tokens=1" %%a in (%1) do (  
    echo kill %1
    taskkill /F /pid %%a
)
:findend