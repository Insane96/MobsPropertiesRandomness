/*
 * 
 * Copy-pasted "some" code from https://github.com/CraftTweaker/CraftTweaker/blob/1.12/CraftTweaker2-MC1120-Main/src/main/java/crafttweaker/mc1120/logger/MCLogger.java
 *
 */

package net.insane96mcp.mpr.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

public class Logger {
	public static File logFile;
	public static Writer writer;
	public static PrintWriter printWriter;
	
	public static void Init(String filePath) {
		logFile = new File(filePath);
		try {
			writer = new OutputStreamWriter(new FileOutputStream(logFile), "utf-8");
			printWriter = new PrintWriter(writer);
		}  catch(UnsupportedEncodingException ex) {
            throw new RuntimeException("How?");
        } catch(FileNotFoundException ex) {
            throw new RuntimeException("Could not open log file " + logFile);
        }
	}
	
	public static void Debug(String message) {
		if (Properties.config.debug) {
			try {
				writer.write("[" + Loader.instance().getLoaderState() + "][" + FMLCommonHandler.instance().getEffectiveSide() + "][DEBUG] " + message + "\n");
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static void Info(String message) {
		try {
			writer.write("[" + Loader.instance().getLoaderState() + "][" + FMLCommonHandler.instance().getEffectiveSide() + "][INFO] " + message + "\n");
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void Warning(String message) {
		try {
			writer.write("[" + Loader.instance().getLoaderState() + "][" + FMLCommonHandler.instance().getEffectiveSide() + "][WARNING] " + message + "\n");
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void Error(String message) {
		try {
			writer.write("[" + Loader.instance().getLoaderState() + "][" + FMLCommonHandler.instance().getEffectiveSide() + "][ERROR] " + message + "\n");
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
