@echo off

if exist myLid.properties {
	goto thecommand
}

set "psCommand=powershell -Command '$plid = read-host 'Enter User' -AsSecureString ; $BLID=[System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($plid); [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BLID)'"

for /f "usebackq delims" %%p in ('%psCommand%') do set lid=%%p


set "psCommand=powershell -Command '$pword = read-host 'Enter Password' -AsSecureString ; $BLID=[System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($pword); [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BLID)'"

for /f "usebackq delims" %%p in ('%psCommand%') do set lid=%%p

:thecommand
  @rem ./gradlew :app-liberty:libertyRun --no-daemon
  "C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot\bin\java" -jar -Dspring.profiles.active=ingestion .\build\libs\CT_DATA_INGESTION-1.0.jar
goto:eof