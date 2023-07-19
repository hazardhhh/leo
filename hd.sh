#!/bin/bash

# Java ENV
# export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_161.jdk/Contents/Home
# export JRE_HOME=${JAVA_HOME}/jre

# Apps Info
# 应用存放地址
APP_HOME=/hhh/hazard/web/hd
# 应用名称
APP_NAME=hd
FILES=$(find ${APP_HOME} -name ${APP_NAME}*.jar)
APP_JAR=${FILES[0]}
echo ${APP_NAME} version: ${APP_JAR}
LOG_DIR=/hhh/hazard/logs/hd
JAVA_OPTS="-server -Xms512m -Xmx512m -Xmn256m -Xss512k -XX:+ClassUnloading -XX:+UseCMSCompactAtFullCollection -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+UseFastAccessorMethods -XX:ParallelGCThreads=4 -XX:+PrintGC -XX:+PrintGCDetails -Xloggc:${LOG_DIR}/jvmgc.log"
#调试端口配置
DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5999"
SPRING_OPTS="--spring.profiles.active=prod"
# Shell Info
SHELL_DIR="/hhh/hazard/sbin"
SHELL_NAME="hd.sh"
ARGV="$1"

if [ ! -d $LOG_DIR ] ; then
        mkdir -p $LOG_DIR
fi

#chown -R hhh:hhh $LOG_DIR


# 使用说明，用来提示输入参数
usage() {
    echo "Usage: sh boot [APP_NAME] [--start|--debug|--stop|--restart|--status]"
    exit 1
}

# 检查程序是否在运行

is_exist(){
        # 获取PID
        PID=$(ps -ef |grep ${APP_JAR} | grep -v $0 |grep -v grep |awk '{print $2}')
        # -z "${pid}"判断pid是否存在，如果不存在返回1，存在返回0
        if [ -z "${PID}" ]; then
                # 如果进程不存在返回1
                return 1
        else
                # 进程存在返回0
                return 0
        fi
}

# 定义启动程序函数
start(){
        #非DEBUG时，将DEBUG参数设置为空
        if [ ${ARGV} !=  "--debug" ]; then
                DEBUG_OPTS=""
        fi
         #使用root账户启动
        if [ `whoami` != "root" ]; then
                su root -c "${SHELL_DIR}/${SHELL_NAME} ${ARGV}"
                exit;
        fi
        is_exist
        if [ $? -eq "0" ]; then
                echo "${APP_NAME} is already running, PID=${PID}"
        else
                nohup java -Dloader.path=${APP_HOME}/lib,${APP_HOME}/resources -jar ${JAVA_OPTS} ${DEBUG_OPTS}  ${APP_JAR} ${SPRING_OPTS}  > ${LOG_DIR}/nohup.out 2>&1 &
                PID=$(echo $!)
                echo "${APP_NAME} start success, PID=$!"
        fi
}

# 停止进程函数
stop(){
        is_exist
        if [ $? -eq "0" ]; then
                kill -9 ${PID}
                echo "${APP_NAME} process stop, PID=${PID}"
        else    
                echo "There is not the process of ${APP_NAME}"
        fi
}

# 重启进程函数 
restart(){
        stop
        #设置为--start,避免因为切换账户调用导致的参数不对
        ARGV="--start"
        start
}

# 查看进程状态
status(){
        is_exist
        if [ $? -eq "0" ]; then
                echo "${APP_NAME} is running, PID=${PID}"
        else    
                echo "There is not the process of ${APP_NAME}"
        fi
}
case $1 in
"--start")
        start
        ;;
"--debug")
        start
        ;;
"--stop")
        stop
        ;;
"--restart")
        restart
        ;;
"--status")
       status
        ;;
        *)
        usage
        ;;
esac
exit 0
