package net.insane96mcp.mobrandomness.lib;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	public static Configuration config;
	public static boolean DEBUG = false;
	
	public static int LoadIntProperty(String category, String key, String description, int defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		property.setComment(description + " (default: " + defaultValue + ")");
		
		if (DEBUG)
			System.out.println("Loaded Int " + key + ": " + defaultValue);
		
		return property.getInt();
	}
	
	public static int[] LoadIntListProperty(String category, String key, String description, int[] defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		description += " (default: [";
		for (int i = 0; i < property.getIntList().length; i++) {
			description += property.getIntList()[i];
			if (i != property.getIntList().length - 1)
				description += ", ";
		}
		description += "])";
		
		if (DEBUG)
			System.out.println("Loaded Int[] " + key + ": " + defaultValue);
		
		property.setComment(description);
		
		return property.getIntList();
	}
	
	public static double LoadDoubleProperty(String category, String key, String description, double defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		property.setComment(description + " (default: " + defaultValue + ")");

		if (DEBUG)
			System.out.println("Loaded Double " + key + ": " + defaultValue);
		
		return property.getDouble();
	}
	
	public static float LoadFloatProperty(String category, String key, String description, float defaultValue) {
		return (float)LoadDoubleProperty(category, key, description, defaultValue);
	}
	
	public static String LoadStringProperty(String category, String key, String description, String defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		property.setComment(description + " (default: " + defaultValue + ")");
		
		if (DEBUG)
			System.out.println("Loaded String " + key + ": " + defaultValue);
		
		return property.getString();
	}
	
	public static String[] LoadStringListProperty(String category, String key, String description, String[] defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		description += " (default: [";
		for (int i = 0; i < property.getStringList().length; i++) {
			description += property.getStringList()[i];
			if (i != property.getStringList().length - 1)
				description += ", ";
		}
		description += "])";

		if (DEBUG)
			System.out.println("Loaded String[] " + key + ": " + defaultValue);
		
		property.setComment(description);
		
		return property.getStringList();
	}
	
	public static boolean LoadBoolProperty(String category, String key, String description, boolean defaultValue) {
		Property property = Config.config.get(category, key, defaultValue);
		property.setComment(description + " (default: " + defaultValue + ")");
		
		if (DEBUG)
			System.out.println("Loaded bool " + key + ": " + defaultValue);
		
		return property.getBoolean();
	}
	
	public static void SetCategoryComment(String category, String comment) {
		Config.config.setCategoryComment(category, comment);
	}
	
	/**
	 * Must be called in Mod PreInit before everything and after
	 * Config.config = new Configuration(event.getSuggestedConfigurationFile());
	 */
	public static void SyncConfig() {
		try {
			if (DEBUG)
				System.out.println("Trying To Load Config");
			Config.config.load();
			if (DEBUG)
				System.out.println("Config Loaded");
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	/**
	 * It's recommended to be called in Mod PostInit after everything
	 */
	public static void SaveConfig() {
		if (DEBUG)
			System.out.println("Trying To Save Config");
		Config.config.save();
		if (DEBUG)
			System.out.println("Saved Config");
	}
}
