package net.insane96mcp.mpr.json;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.MobsPropertiesRandomness;
import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.json.mobs.Creeper;
import net.insane96mcp.mpr.json.mobs.Ghast;
import net.insane96mcp.mpr.lib.Logger;

public class Mob {
	public static List<Mob> mobs = new ArrayList<Mob>();
	
	@SerializedName("mob_id")
	public String id;
	
	@SerializedName("potion_effects")
	public List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	
	public List<Attribute> attributes = new ArrayList<Attribute>();
	
	public Equipment equipment = new Equipment();
	
	public Creeper creeper;
	public Ghast ghast;
	
	@Override
	public String toString() {
		return String.format("Mob{id: %s, potionEffects: %s, attributes: %s, equipment: %s, creeper: %s, ghast: %s}", id, potionEffects, attributes, equipment, creeper, ghast);
	}
	
	public static boolean LoadJsons() {
		//check if json folder exist, if not, create it
		File jsonFolder = new File(MobsPropertiesRandomness.configPath + "json");
		if (!jsonFolder.exists()) {
			jsonFolder.mkdir();
			return true;
		}
		
		//Empty the list with the loaded jsons
		mobs.clear();
		
		//if has failed loading to display the message in chat
		boolean correctlyReloaded = true;
		
		Gson gson = new Gson();
		
		//config/mobspropertiesrandomness/json
		File jsonPath = new File(MobsPropertiesRandomness.configPath + "json");
		ArrayList<File> jsonFiles = ListFilesForFolder(jsonPath);
		
		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_"))
				continue;
			
			try {
				Logger.Debug("Reading file " + file.getName());
				FileReader fileReader = new FileReader(file);
				Mob mob = gson.fromJson(fileReader, Mob.class);
				Logger.Debug(mob.toString());
				mob.Validate(file);
				mobs.add(mob);
			} catch (Exception e) {
				correctlyReloaded = false;
				Logger.Error("Failed to parse file with name " + file.getName());
				Logger.Error(e.toString());
				e.printStackTrace();
			}
		}
		if (correctlyReloaded)
			Logger.Info("Reloaded All JSONs");
		else
			Logger.Info("Reloaded All JSONs with errors");
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
		equipment.Validate(file);
		
		//Mob specific validations
		if (creeper != null)
			creeper.Validate(file);
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
