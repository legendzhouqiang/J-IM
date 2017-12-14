call mvn -Dmaven.test.skip=true clean install

call rd ..\..\..\..\dist\examples\im\server /s /q
call xcopy target\dist\tio-examples-im-server-2.0.3.v20171215-RELEASE ..\..\..\..\dist\examples\im\server\ /s /e /q /y

