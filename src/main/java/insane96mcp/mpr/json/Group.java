package insane96mcp.mpr.json;

import com.google.gson.Gson;
import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.utils.Logger;
import insane96mcp.mpr.utils.Utils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Group implements IJsonObject{
	public static ArrayList<Group> groups = new ArrayList<>();
	
	public static boolean doesGroupExist(String name) {
		for (Group group : groups) {
			if (group.name.equals(name))
				return true;
		}
		return false;
	}
	
	public String name;
	public List<String> mobs;
	
	@Override
	public String toString() {
		return String.format("Group{name: %s, mobs: %s}", name, mobs);
	}
	
	public static boolean loadGroups() {
		File groupsFolder = new File(MobsPropertiesRandomness.configPath + "groups");
		if (!groupsFolder.exists())
			groupsFolder.mkdir();
		
		//Empty the list with the loaded groups
		groups.clear();
		
		//if has failed loading to display the message in chat
		boolean correctlyReloaded = true;
		
		Gson gson = new Gson();

		ArrayList<File> gropusFiles = Utils.listFilesForFolder(groupsFolder);
		
		for (File file : gropusFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_"))
				continue;
			
			try {
				Logger.debug("Reading file " + file.getName());
				FileReader fileReader = new FileReader(file);
				Group group = gson.fromJson(fileReader, Group.class);
				Logger.debug(group.toString());
				group.validate(file);
				groups.add(group);
			} catch (Exception e) {
				correctlyReloaded = false;
				Logger.error("Failed to parse file with name " + file.getName());
				Logger.error(e.toString());
				e.printStackTrace();
			}
		}
		if (correctlyReloaded)
			Logger.info("Reloaded All Groups");
		else
			Logger.info("Reloaded All Groups with errors");
		return correctlyReloaded;
	}
	
	@Override
	public void validate(File file) throws InvalidJsonException {
		if (name == null) {
			throw new InvalidJsonException("Missing group name", file);
		}
		
		if (doesGroupExist(name)) {
			throw new InvalidJsonException("Group name " + name + " already exist", file);
		}
		
		if (mobs == null || mobs.isEmpty()) {
			throw new InvalidJsonException("Group " + this.name + " is missing or has no mobs in the list", file);
		}
	}
}
