cd ..\src\parent
call mvn clean install
cd ..\..


cd .\src\example\im\remote
call dir
call installAndCopy.bat
cd ..\..\..\..

cd .\src\example\im\client
call installAndCopy.bat
cd ..\..\..\..



cd .\src\example\im-simple\remote
call dir
call installAndCopy.bat
cd ..\..\..\..

cd .\src\example\im-simple\client
call installAndCopy.bat
cd ..\..\..\..



cd .\src\example\showcase\remote
call installAndCopy.bat
cd ..\..\..\..

cd .\src\example\showcase\client
call installAndCopy.bat
cd ..\..\..\..



cd .\src\example\helloworld\remote
call installAndCopy.bat
cd ..\..\..\..

cd .\src\example\helloworld\client
call installAndCopy.bat
cd ..\..\..\..
