package insane96mcp.mobspropertiesrandomness.utils;

import insane96mcp.mobspropertiesrandomness.config.ModConfig;

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

	public static void log(LogType logType, String message) {
		try {
			writer.write(String.format("[%s] %s %s", logType, message, System.lineSeparator()));
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void debug(String message) {
		if (!ModConfig.debug)
			return;

		log(LogType.DEBUG, message);
	}

	public static void info(String message) {
		log(LogType.INFO, message);
	}

	public static void warn(String message) {
		log(LogType.WARN, message);
	}

	public static void error(String message) {
		log(LogType.ERROR, message);
	}

	public enum LogType {
		DEBUG,
		INFO,
		WARN,
		ERROR
	}
}
