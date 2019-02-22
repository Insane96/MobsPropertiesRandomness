package insane96mcp.mpr.json;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.mobs.Creeper;
import insane96mcp.mpr.json.mobs.Ghast;
import insane96mcp.mpr.lib.Logger;
import insane96mcp.mpr.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class Mob implements IJsonObject{
	public static List<Mob> mobs = new ArrayList<Mob>();
	
	@SerializedName("mob_id")
	public String mobId;
	public String group;
	
	@SerializedName("potion_effects")
	public List<JPotionEffect> potionEffects;
	
	public List<Attribute> attributes;
	
	public Equipment equipment;
	
	public Creeper creeper;
	public Ghast ghast;
	
	@Override
	public String toString() {
		return String.format("Mob{id: %s, group: %s, potionEffects: %s, attributes: %s, equipment: %s, creeper: %s, ghast: %s}", mobId, group, potionEffects, attributes, equipment, creeper, ghast);
	}
	
	public static boolean LoadJsons() {
		//check if json folder exist, if not, create it
		File jsonFolder = new File(MobsPropertiesRandomness.configPath + "json");
		if (!jsonFolder.exists())
			jsonFolder.mkdir();
		
		//Empty the list with the loaded jsons
		mobs.clear();
		
		//if has failed loading to display the message in chat
		boolean correctlyReloaded = true;
		
		Gson gson = new Gson();
		
		//config/mobspropertiesrandomness/json
		ArrayList<File> jsonFiles = Utils.ListFilesForFolder(jsonFolder);
		
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
		if (mobId == null && group == null)
			throw new InvalidJsonException("Missing mob_id or group for " + this.toString(), file);
		else if (mobId != null && group != null)
			Logger.Debug("mob_id and group are both present, mob_id will be ignored");
		
		if (mobId != null) {
			String[] splitId = mobId.split(":");
			if (splitId.length != 2)
				throw new InvalidJsonException("Invalid mob_id " + mobId, file);

			ResourceLocation resourceLocation = new ResourceLocation(mobId);
			if (!EntityList.isRegistered(resourceLocation) && !mobId.endsWith("*"))
				throw new InvalidJsonException("mob_id " + mobId + " does not exist", file);
		}
		
		if (group != null) {
			if (!Group.DoesGroupExist(group))
				throw new InvalidJsonException("group " + group + " does not exist", file);
		}
		
		if (potionEffects == null)
			potionEffects = new ArrayList<JPotionEffect>();
		for (JPotionEffect potionEffect : potionEffects) {
			potionEffect.Validate(file);
		}
		
		if (attributes == null)
			attributes = new ArrayList<Attribute>();
		for (Attribute attribute : attributes) {
			attribute.Validate(file);
		}
		
		if (equipment == null)
			equipment = new Equipment();
		equipment.Validate(file);
		
		//Mob specific validations
		if (creeper != null)
			creeper.Validate(file);
		
		if (ghast != null)
			ghast.Validate(file);
	}

	public static void Apply(EntityJoinWorldEvent event) {
		if (Mob.mobs.isEmpty())
			return;
		
		Entity entity = event.getEntity();
		World world = event.getWorld();
		Random random = world.rand;
		
		Creeper.FixAreaEffectClouds(entity);
		
		if (!(entity instanceof EntityLiving)) 
			return;
		
		EntityLiving entityLiving = (EntityLiving)entity;
		
		NBTTagCompound tags = entityLiving.getEntityData();
		boolean isAlreadyChecked = tags.getBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked");

		if (isAlreadyChecked)
			return;
		
		boolean shouldNotBeProcessed = tags.getBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "prevent_processing");
		
		if (shouldNotBeProcessed)
			return;
		
		JPotionEffect.Apply(entityLiving, world, random);
		Attribute.Apply(entityLiving, world, random);
		Equipment.Apply(entityLiving, world, random);
		
		Creeper.Apply(entityLiving, world, random);
		Ghast.Apply(entityLiving, world, random);
		
		tags.setBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked", true);
	}
}
