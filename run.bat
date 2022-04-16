setlocal

set CP=lib/*;target/tensuhyo-1.0.jar
set VMARGS=-Djava.util.logging.config.file=logging.properties 
set MAIN=saka1029.tensuhyo.main.Main

java %VMARGS% -cp %CP% %MAIN% %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal
