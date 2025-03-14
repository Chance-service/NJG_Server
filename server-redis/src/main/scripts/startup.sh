#!/bin/bash
pid=$(ps -ef | grep gameserver.jar | grep -v grep | awk '{print $2}')
echo $pid
if [[ ! -z "$pid" ]];then
        echo 'kill process '.$pid
        kill -9 $pid
fi
echo 'begin start server-pay'

nohup java -jar -server -Xms1024m -Xmx2048m -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError server-pay-0.0.1-SNAPSHOT-cliRun.jar >>./console.log &