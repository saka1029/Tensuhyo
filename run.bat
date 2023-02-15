@echo off
setlocal

java -Djava.util.logging.config.file=logging.properties ^
    -cp target/tensuhyo-1.0-jar-with-dependencies.jar ^
    saka1029.tensuhyo.main.Main ^
    %*

endlocal
