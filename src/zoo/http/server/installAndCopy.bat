call mvn -Dmaven.test.skip=true clean install

call rd ..\..\..\..\dist\examples\im\server /s /q
call xcopy target\dist\tio-examples-im-server-2.3.0.v20180506-RELEASE ..\..\..\..\dist\examples\im\server\ /s /e /q /y

