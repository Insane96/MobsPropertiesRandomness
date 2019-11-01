package insane96mcp.mpr.json;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.mobs.Creeper;
import insane96mcp.mpr.json.mobs.Ghast;
import insane96mcp.mpr.utils.Logger;
import insane96mcp.mpr.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mob implements IJsonObject{
	public static List<Mob> mobs = new ArrayList<>();
	
	@SerializedName("mob_id")
	public String mobId;
	public String group;
	
	@SerializedName("potion_effects")
	public List<PotionEffect> potionEffects;
	public List<Attribute> attributes;
	
	public Equipment equipment;
	
	public Creeper creeper;
	public Ghast ghast;
	
	@Override
	public String toString() {
		return String.format("Mob{id: %s, group: %s, potionEffects: %s, attributes: %s, equipment: %s, creeper: %s, ghast: %s}", mobId, group, potionEffects, attributes, equipment, creeper, ghast);
	}
	
	public static boolean loadJsons() {
		//Creates json folder if doesn't exist
		File jsonFolder = new File(MobsPropertiesRandomness.configPath + "json");
		jsonFolder.mkdirs();
		
		//Empty the list with the loaded jsons
		mobs.clear();
		
		//if has failed loading to display the message in chat
		boolean correctlyReloaded = true;
		
		Gson gson = new Gson();

		ArrayList<File> jsonFiles = Utils.listFilesForFolder(jsonFolder);
		
		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_"))
				continue;
			
			try {
				Logger.debug("Reading file " + file.getName());
				FileReader fileReader = new FileReader(file);
				Mob mob = gson.fromJson(fileReader, Mob.class);
				Logger.debug(mob.toString());
				mob.validate(file);
				mobs.add(mob);
			} catch (Exception e) {
				correctlyReloaded = false;
				Logger.error("Failed to parse file with name " + file.getName());
				Logger.error(e.toString());
				e.printStackTrace();
			}
		}
		if (correctlyReloaded)
			Logger.info("Reloaded All JSONs");
		else
			Logger.info("Reloaded All JSONs with errors");
		return correctlyReloaded;
	}

	public void validate(final File file) throws InvalidJsonException{
		if (mobId == null && group == null)
			throw new InvalidJsonException("Missing mob_id or group for " + this.toString(), file);
		else if (mobId != null && group != null)
			Logger.debug("mob_id and group are both present, mob_id will be ignored");
		
		if (mobId != null) {
			String[] splitId = mobId.split(":");
			if (splitId.length != 2)
				throw new InvalidJsonException("Invalid mob_id " + mobId, file);

			ResourceLocation resourceLocation = new ResourceLocation(mobId);
			if (!ForgeRegistries.ENTITIES.containsKey(resourceLocation) && !mobId.endsWith("*"))
				throw new InvalidJsonException("mob_id " + mobId + " does not exist", file);
		}
		
		if (group != null) {
			if (!Group.doesGroupExist(group))
				throw new InvalidJsonException("group " + group + " does not exist", file);
		}
		
		if (potionEffects == null)
			potionEffects = new ArrayList<>();
		for (PotionEffect potionEffect : potionEffects) {
			potionEffect.validate(file);
		}
		
		if (attributes == null)
			attributes = new ArrayList<>();
		for (Attribute attribute : attributes) {
			attribute.validate(file);
		}
		
		if (equipment == null)
			equipment = new Equipment();
		equipment.validate(file);
		
		//Mob specific validations
		if (creeper != null)
			creeper.validate(file);
		
		if (ghast != null)
			ghast.validate(file);
	}

	public static void apply(EntityJoinWorldEvent event) {

		if (Mob.mobs.isEmpty())
			return;
		
		Entity entity = event.getEntity();
		World world = event.getWorld();
		Random random = world.rand;
		
		Creeper.fixAreaEffectClouds(entity);
		
		if (!(entity instanceof MobEntity))
			return;

		MobEntity mobEntity = (MobEntity)entity;
		
		CompoundNBT tags = mobEntity.getEntityData();
		boolean isAlreadyChecked = tags.getBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked");

		if (isAlreadyChecked)
			return;
		
		boolean shouldNotBeProcessed = tags.getBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "prevent_processing");
		
		if (shouldNotBeProcessed)
			return;
		
		PotionEffect.apply(mobEntity, world, random);
		Attribute.apply(mobEntity, world, random);
		Equipment.apply(mobEntity, world, random);
		
		Creeper.apply(mobEntity, world, random);
		Ghast.apply(mobEntity, world, random);
		
		tags.putBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked", true);
	}
}
