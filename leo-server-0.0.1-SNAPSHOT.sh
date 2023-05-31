#!/bin/bash
#name:jar包启动脚本;
#jar包所在位置
HOME='/hhh/hazard/thsWeb'

#服务名称
JAVA_NAME=leo-server-0.0.1-SNAPSHOT

#debug端口
PORT=40000
 
#此处修改脚本名称：
JAR_NAME=$JAVA_NAME.jar

#脚本菜单项
usage() {
 echo "Usage: sh $JAVA_NAME.sh [start|stop|restart|status|log|debug|redebug]"
 exit 1
}

is_exist(){
 pid=`ps -ef|grep $JAR_NAME|grep -v grep|awk '{print $2}' `
 #如果不存在返回1，存在返回0
 if [ -z "${pid}" ]; then
 return 1
 else
 return 0
 fi
}
#启动脚本
start(){
 is_exist
 if [ $? -eq "0" ]; then
 echo "${JAR_NAME} is already running. pid=${pid} ."
 else
#此处注意修改jar和log文件文件位置：
 nohup java -jar $HOME/$JAR_NAME > $HOME/$JAVA_NAME.log   2>&1 &
#此处打印log日志：
 #tail -f $HOME/$JAVA_NAME.log
 fi
}
#debug启动脚本
debug(){
 is_exist
 if [ $? -eq "0" ]; then
 echo "${JAR_NAME} is already running. pid=${pid} ."
 else
#此处注意修改jar和log文件文件位置：
 nohup java -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$PORT -jar $HOME/$JAR_NAME > $HOME/$JAVA_NAME.log   2>&1 &
#此处打印log日志：
 #tail -f $HOME/$JAVA_NAME.log
 fi
}
#停止脚本
stop(){
 is_exist
 if [ $? -eq "0" ]; then
 kill -9 $pid
 else
 echo "${JAR_NAME} is not running"
 fi
}
#显示当前jar运行状态
status(){
 is_exist
 if [ $? -eq "0" ]; then
 echo "${JAR_NAME} is running. Pid is ${pid}"
 else
 echo "${JAR_NAME} is NOT running."
 fi
}
#重启脚本
restart(){
 stop
 start
}
redebug(){
 stop
 debug
}
#显示当前jar运行日志
log(){
 is_exist
 if [ $? -eq "0" ]; then
 tail -f $HOME/$JAVA_NAME.log
 else
 echo "${JAR_NAME} is NOT running."
 fi
}
case "$1" in
 "start")
 start
 ;;
 "stop")
 stop
 ;;
 "status")
 status
 ;;
 "restart")
 restart
 ;;
 "log")
 log
 ;;
 "debug")
 debug
 ;;
 "redebug")
 redebug
 ;;
 *)
 usage
 ;;
esac



