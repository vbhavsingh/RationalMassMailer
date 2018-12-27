package net.rationalminds.massmailer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
	
	private static final SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss");

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(df.format(new Date()));
		sb.append(" <");
	    sb.append(record.getLevel());
	    sb.append("> : ");
	    sb.append(record.getMessage());
	    sb.append(System.lineSeparator());
	    return sb.toString();
	}

}
