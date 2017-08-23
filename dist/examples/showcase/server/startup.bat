rem -Xms64m -Xmx2048m

@echo off
setlocal & pushd
set APP_ENTRY=org.tio.examples.showcase.remote.ShowcaseServerStarter
set BASE=%~dp0
set CP=%BASE%\config;%BASE%\lib\*
java -XX:+HeapDumpOnOutOfMemoryError -Dtio.default.read.buffer.size=512 -XX:HeapDumpPath=c:/java-t-io-showcase-remote-pid.hprof -cp "%CP%" %APP_ENTRY%
endlocal & popd
