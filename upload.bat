@echo off
setlocal

java -Djava.util.logging.config.file=logging.properties ^
    -cp lib/*;target/tensuhyo-1.0.jar ^
    saka1029.tensuhyo.main.Upload ^
    %*

endlocal
