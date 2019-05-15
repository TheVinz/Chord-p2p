package utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Logger;


/**
 * LogFormatter for colored one line logging
 */
public final class LogFormatter extends Formatter {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final Map<Level, String> color;
	private static final String ESCAPERESET = "\u001B[0m";

	static {
		Map<Level, String> myColor = new HashMap();
		myColor.put(Level.INFO, "\u001B[34m");
		myColor.put(Level.SEVERE, "\u001B[35m");
		myColor.put(Level.FINE, "\u001B[32m");
		myColor.put(Level.WARNING, "\u001B[33m");

		color = Collections.unmodifiableMap(myColor);
	}

    public static void logSetup(Level thresholdToPrint) {
        Logger rootLog =Logger.getLogger("");
        rootLog.setLevel(Level.ALL);

        rootLog.removeHandler(rootLog.getHandlers()[0]);
        LogFormatter formatter = new LogFormatter();

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(thresholdToPrint);
        rootLog.addHandler(consoleHandler);
    }

    /**
	 * @param record LogRecord
	 * @return one colored line formatted log message
	 */
	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		String colorStr = "";
		String resetStr = "";
		if (color.containsKey(record.getLevel())) {
			colorStr = color.get(record.getLevel());
			resetStr = ESCAPERESET;
		}

		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy-MM-dd HH.mm.ss");
		String dataStr = sdf.format(new Date(record.getMillis()));

		sb.append(dataStr)
				.append(" ")
				.append(record.getLoggerName())
				.append(" ")
				.append(colorStr)
				.append(record.getLevel().getName())
				.append(": ")
				.append(formatMessage(record))
				.append(resetStr)
				.append(LINE_SEPARATOR);

		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
				// ignore
			}
		}

		return sb.toString();
	}
}