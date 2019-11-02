/*
 *
 * Copy-pasted some code from https://github.com/CraftTweaker/CraftTweaker/blob/1.12/CraftTweaker2-MC1120-Main/src/main/java/crafttweaker/mc1120/logger/MCLogger.java
 *
 */

package insane.mobspropertiesrandomness.setup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class Logger {
	public static File logFile;
	public static Writer writer;
	public static PrintWriter printWriter;

	public static void Init(String fileName) {
		logFile = new File("logs/" + fileName);
		try {
			writer = new OutputStreamWriter(new FileOutputStream(logFile), "utf-8");
			printWriter = new PrintWriter(writer);
			Logger.Debug("Logger Successfully Initialized");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("How?");
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Could not open log file " + logFile);
		}
	}

	public static void Debug(String message) {
		if (ModConfig.General.debug.get())
			write(message, "DEBUG");
	}

	public static void Info(String message) {
		write(message, "INFO");
	}

	public static void Warning(String message) {
		write(message, "WARNING");
	}

	public static void Error(String message) {
		write(message, "ERROR");
	}

	private static void write(String message, String type){
		try {
			String[] splitMessage = message.split("\n");
			for (String m : splitMessage) {
				writer.write("[" + type + "] " + m + "\n");
			}
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}