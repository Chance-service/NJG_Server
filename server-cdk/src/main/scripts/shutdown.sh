#!/bin/bash
pid=$(ps -ef | grep server-cdk-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{print $2}')
echo $pid
if [[ ! -z "$pid" ]];then
        echo 'kill process '.$pid
        kill -9 $pid
        echo 'shutdown server-cdk process'
else
		echo 'have not server-cdk to shutdown'
fi
