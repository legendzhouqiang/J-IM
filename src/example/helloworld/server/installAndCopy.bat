call mvn clean install

call rd ..\..\..\..\dist\examples\helloworld\remote /s /q
call xcopy target\dist\tio-examples-helloworld-remote-2.0.0.v20170806-RELEASE ..\..\..\..\dist\examples\helloworld\remote\ /s /e /q /y

