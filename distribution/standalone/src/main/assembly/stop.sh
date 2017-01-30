
DIRNAME=`dirname $0`
HOME=`cd $DIRNAME/; pwd`
Main_Class="org.openo.vnfsdk.functest.VnfSdkFuncTestApp"

echo ================== catalog-service info =============================================
echo HOME=$HOME
echo Main_Class=$Main_Class
echo ===============================================================================
cd $HOME; pwd

echo @WORK_DIR@ $HOME

function save_service_pid(){
    service_pid=`ps -ef | grep $Main_Class | grep -v grep | awk '{print $2}'`
    echo @service_pid@ $service_pid
}

function kill_service_process(){
    ps -p $service_pid
    if [ $? == 0 ]; then
        kill -9 $service_pid
    fi
}

save_service_pid;
echo @C_CMD@ kill -9 $service_pid
kill_service_process;
