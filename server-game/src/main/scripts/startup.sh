#!/bin/bash
pid=$(ps -ef | grep server-game-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{print $2}')
echo $pid
if [[ ! -z "$pid" ]];then
        echo 'kill process '.$pid
        kill -9 $pid
fi
echo 'begin start server-game'

nohup java -jar -XX:PermSize=64m -XX:MaxPermSize=256m -XX:-OmitStackTraceInFastThrow -XX:CompileCommand=exclude,com/hawk/game/util/QuickPhotoUtil,attachEquipInfo -Xms2048m -Xmx4096m server-game-0.0.1-SNAPSHOT.jar >>./console.log &
