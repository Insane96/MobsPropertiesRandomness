package net.insane96mcp.mobrandomness.json;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mobrandomness.MobsPropertiesRandomness;

public class Mob {
	public static List<Mob> mobs = new ArrayList<Mob>();
	
	@SerializedName("mob_id")
	public String id;
	
	@SerializedName("potion_effects")
	public List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	
	public List<Attribute> attributes = new ArrayList<Attribute>();
	
	@Override
	public String toString() {
		return String.format("Mob{id: %s, potionEffects: %s, attributes: %s}", id, potionEffects, attributes);
	}
	
	public static boolean LoadJsons() {
		mobs.clear();
		
		boolean correctlyReloaded = true;
		
		Gson gson = new Gson();
		
		File jsonPath = new File(MobsPropertiesRandomness.configPath + "json");
		ArrayList<File> jsonFiles = ListFilesForFolder(jsonPath);
		
		for (File file : jsonFiles) {
			if (file.getName().startsWith("_"))
				continue;
			try {
				MobsPropertiesRandomness.Debug("Reading file " + file.getName());
				FileReader fileReader = new FileReader(file);
				Mob mob = gson.fromJson(fileReader, Mob.class);
				MobsPropertiesRandomness.Debug(mob.toString());
				mob.Validate(file);
				mobs.add(mob);
			} catch (Exception e) {
				correctlyReloaded = false;
				MobsPropertiesRandomness.logger.error("Failed to parse file with name " + file.getName());
				e.printStackTrace();
			}
		}
		
		return correctlyReloaded;
	}

	public void Validate(final File file) throws InvalidJsonException{
		if (id == null)
			throw new InvalidJsonException("Missing Mob Id for " + this.toString(), file);
		for (PotionEffect potionEffect : potionEffects) {
			potionEffect.Validate(file);
		}
		for (Attribute attribute : attributes) {
			attribute.Validate(file);
		}
	}
	
	private static ArrayList<File> ListFilesForFolder(final File folder) {
	    ArrayList<File> list = new ArrayList<File>();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	ListFilesForFolder(fileEntry);
	        } else {
	            list.add(fileEntry);
	        }
	    }
	    return list;
	}
}
