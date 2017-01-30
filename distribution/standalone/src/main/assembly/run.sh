
DIRNAME=`dirname $0`
RUNHOME=`cd $DIRNAME/; pwd`
echo @RUNHOME@ $RUNHOME

echo @JAVA_HOME@ $JAVA_HOME
JAVA="$JAVA_HOME/bin/java"
echo @JAVA@ $JAVA

JAVA_OPTS="-Xms50m -Xmx128m"
port=8312
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$port,server=y,suspend=n"
echo @JAVA_OPTS@ $JAVA_OPTS

class_path="$RUNHOME/:$RUNHOME/org.openo.vnfsdk.functest.jar"
echo @class_path@ $class_path

"$JAVA" $JAVA_OPTS -classpath "$class_path" org.openo.vnfsdk.functest.VnfSdkFuncTestApp server "$RUNHOME/conf/vnfsdkfunctest.yml"

