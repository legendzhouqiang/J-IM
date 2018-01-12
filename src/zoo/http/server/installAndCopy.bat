call mvn -Dmaven.test.skip=true clean install

call rd ..\..\..\..\dist\examples\im\server /s /q
call xcopy target\dist\tio-examples-im-server-2.0.5.v20180115-RELEASE ..\..\..\..\dist\examples\im\server\ /s /e /q /y

