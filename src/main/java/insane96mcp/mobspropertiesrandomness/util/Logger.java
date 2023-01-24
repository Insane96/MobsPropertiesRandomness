package insane96mcp.mobspropertiesrandomness.util;

import insane96mcp.mobspropertiesrandomness.module.Modules;
import insane96mcp.mobspropertiesrandomness.module.base.feature.BaseFeature;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Logger {
	private static File logFile;
	private static Writer writer;
	private static PrintWriter printWriter;

	public static void init(String filePath) {
		logFile = new File(filePath);
		try {
			writer = new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8);
			printWriter = new PrintWriter(writer);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Could not create / open log file " + logFile);
		}
	}

	public static void log(LogType logType, String message, Object... params) {
		try {
			writer.write(String.format("[%s] %s %s", logType, String.format(message, params), System.lineSeparator()));
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void debug(String message, Object... params) {
		if (!BaseFeature.debug)
			return;

		log(LogType.DEBUG, message, params);
	}

	public static void info(String message, Object... params) {
		log(LogType.INFO, message, params);
	}

	public static void warn(String message, Object... params) {
		log(LogType.WARN, message, params);
	}

	public static void error(String message, Object... params) {
		log(LogType.ERROR, message, params);
	}

	public enum LogType {
		DEBUG,
		INFO,
		WARN,
		ERROR
	}

	public static String getStackTrace(Exception e) {
		StringBuilder r = new StringBuilder();
		r.append(e.toString());
		r.append("\r\n");
		StackTraceElement[] trace = e.getStackTrace();
		for (StackTraceElement traceElement : trace)
			r.append("\tat ").append(traceElement).append("\r\n");

		return r.toString();
	}
}
