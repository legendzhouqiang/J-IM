call mvn clean install

call rd ..\..\..\..\dist\examples\showcase\remote /s /q
call xcopy target\dist\tio-examples-showcase-remote-2.0.0.v20170806-RELEASE ..\..\..\..\dist\examples\showcase\remote\ /s /e /q /y

