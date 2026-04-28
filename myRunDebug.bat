@echo off
title Spring Boot FHIR - DEBUG MODE

if exist myLid.properties (
    goto thecommand
)

set "psCommand=powershell -Command "$plid = Read-Host 'Enter User' -AsSecureString; $b=[Runtime.InteropServices.Marshal]::SecureStringToBSTR($plid); [Runtime.InteropServices.Marshal]::PtrToStringAuto($b)""

for /f "usebackq delims=" %%p in (`%psCommand%`) do set lid=%%p

set "psCommand=powershell -Command "$pword = Read-Host 'Enter Password' -AsSecureString; $b=[Runtime.InteropServices.Marshal]::SecureStringToBSTR($pword); [Runtime.InteropServices.Marshal]::PtrToStringAuto($b)""

for /f "usebackq delims=" %%p in (`%psCommand%`) do set pwd=%%p

:thecommand
echo Starting Spring Boot in DEBUG mode on port 5005...

"C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot\bin\java" ^
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 ^
-Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" -jar -Dspring.profiles.active=ingestion .\build\libs\CT_DATA_INGESTION-1.0.jar

goto :eof