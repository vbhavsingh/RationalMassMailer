package net.rationalminds.massmailer.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileLogger {
	
	static private FileHandler fileTxt;
	
    static private Formatter formatterTxt;
    
    static public void setup() throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // suppress the logging output to the console
        Handler[] handlers = logger.getHandlers();
        if(handlers != null && handlers.length > 0) {
        	if (handlers[0] instanceof ConsoleHandler) {
            	logger.removeHandler(handlers[0]);
        	}
        }
        
        logger.setLevel(Level.INFO);
        fileTxt = new FileHandler("rational-massmail.txt");

        // create a TXT formatter
        formatterTxt = new LogFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

    }

}
