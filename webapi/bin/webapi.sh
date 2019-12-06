#!/bin/sh

SERVICE_NAME=webapi
JAVA_PROGRAM="java -jar ru.complitex.webapi-1.0.0-SNAPSHOT.jar"
PID_PATH_NAME=./webapi.pid
case $1 in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup $JAVA_PROGRAM >> ./webapi-console.log 2>&1&
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started"
        else
            echo "$SERVICE_NAME is already running"
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill -15 $PID;
            echo "$SERVICE_NAME stopped"
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running"
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill -15 $PID;
            sleep 3s
            echo "$SERVICE_NAME stopped";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."            
            nohup $JAVA_PROGRAM >> ./webapi-console.log 2>&1&
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started"
        else
            echo "$SERVICE_NAME is not running"
        fi
    ;;
esac