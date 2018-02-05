call mvn -Dmaven.test.skip=true clean install

call rd ..\..\..\..\dist\examples\im\server /s /q
call xcopy target\dist\tio-examples-im-server-2.0.6.v20180205-RELEASE ..\..\..\..\dist\examples\im\server\ /s /e /q /y

