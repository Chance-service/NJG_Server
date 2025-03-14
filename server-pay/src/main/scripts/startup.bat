@echo off
java -jar -server -Xms1024m -Xmx2048m -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError server-pay-0.0.1-SNAPSHOT.jar
pause