#-Xverify:none -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider
#-Xrunjdwp:transport=dt_socket,address=8888,suspend=n,remote=y

nohup java -Xverify:none -Xms64m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError -Dtio.default.read.buffer.size=1024 -XX:HeapDumpPath=./tio-httpserver-demo1-pid.hprof -cp ./config:./lib/* org.tio.http.remote.demo1.HttpServerDemoStarter &