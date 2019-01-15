package net.insane96mcp.mpr.json;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import net.insane96mcp.mpr.MobsPropertiesRandomness;
import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.lib.Logger;
import net.insane96mcp.mpr.utils.Utils;

public class Group implements IJsonObject{
	public static ArrayList<Group> groups = new ArrayList<Group>();
	
	public static boolean DoesGroupExist(String name) {
		for (Group group : groups) {
			if (group.name.equals(name))
				return true;
		}
		return false;
	}
	
	public String name;
	public List<String> mobs = new ArrayList<String>();
	
	@Override
	public String toString() {
		return String.format("Group{name: %s, mobs: %s}", name, mobs);
	}
	
	public static boolean LoadGroups() {
		//Empty the list with the loaded groups
		groups.clear();
		
		//if has failed loading to display the message in chat
		boolean correctlyReloaded = true;
		
		Gson gson = new Gson();
		
		//config/mobspropertiesrandomness/json
		File groupsPath = new File(MobsPropertiesRandomness.configPath + "groups");
		ArrayList<File> gropusFiles = Utils.ListFilesForFolder(groupsPath);
		
		for (File file : gropusFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_"))
				continue;
			
			try {
				Logger.Debug("Reading file " + file.getName());
				FileReader fileReader = new FileReader(file);
				Group group = gson.fromJson(fileReader, Group.class);
				Logger.Debug(group.toString());
				group.Validate(file);
				groups.add(group);
			} catch (Exception e) {
				correctlyReloaded = false;
				Logger.Error("Failed to parse file with name " + file.getName());
				Logger.Error(e.toString());
				e.printStackTrace();
			}
		}
		if (correctlyReloaded)
			Logger.Info("Reloaded All Groups");
		else
			Logger.Info("Reloaded All Groups with errors");
		return correctlyReloaded;
	}
	
	@Override
	public void Validate(File file) throws InvalidJsonException {
		if (name == null) {
			throw new InvalidJsonException("Missing group name", file);
		}
		
		if (DoesGroupExist(name)) {
			throw new InvalidJsonException("Group name " + name + " already exist", file);
		}
		
		if (mobs.isEmpty()) {
			throw new InvalidJsonException("Group " + this.name + " has no mobs in the list", file);
		}
	}
}
