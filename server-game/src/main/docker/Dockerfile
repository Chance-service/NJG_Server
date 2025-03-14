from 192.168.1.11:10080/gamejp/java:v1
MAINTAINER hanchao <420811944@qq.com>
RUN mkdir -p /app/gameserver
COPY Game.jar /app/gameserver/
COPY lib /app/gameserver/lib
COPY script /app/gameserver/script/
WORKDIR /app/gameserver/
CMD ["java", "-jar", "Game.jar"]