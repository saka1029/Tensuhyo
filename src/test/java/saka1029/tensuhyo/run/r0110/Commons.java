package saka1029.tensuhyo.run.r0110;

import java.util.logging.Logger;

public class Commons {

    static final String ENCODING = "utf-8";
    static final String GENGO = "令和";
    static final String YEAR = "01";
    static final String OLD_GENGO = "平成";
	static final String OLD_YEAR = "30";

	static Logger getLogger(String name) {
        System.setProperty("java.util.logging.config.file", "logging.properties");
	    return Logger.getLogger(name);
	}

}
