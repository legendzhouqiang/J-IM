call mvn clean install

call rd ..\..\..\..\dist\examples\im\server /s /q
call xcopy target\dist\tio-examples-im-server-2.0.0.v20170824-RELEASE ..\..\..\..\dist\examples\im\server\ /s /e /q /y

