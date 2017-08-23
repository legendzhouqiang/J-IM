call mvn clean install

call rd ..\..\..\..\dist\examples\im\remote /s /q
call xcopy target\dist\tio-examples-im-remote-2.0.0.v20170806-RELEASE ..\..\..\..\dist\examples\im\remote\ /s /e /q /y

