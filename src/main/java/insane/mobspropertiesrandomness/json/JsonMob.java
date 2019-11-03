package insane.mobspropertiesrandomness.json;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import insane.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane.mobspropertiesrandomness.exceptions.InvalidJsonException;
import insane.mobspropertiesrandomness.setup.Logger;
import insane.mobspropertiesrandomness.utils.FilesUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.FileUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JsonMob implements IJsonObject {

	public static List<JsonMob> mobs = new ArrayList<JsonMob>();

	@SerializedName("mob_id")
	public String mobId;
	//TODO Groups
	//public String group;

	@SerializedName("potion_effects")
	public List<JsonPotionEffect> potionEffects;

	@Override
	public String toString() {
		return String.format("Mob{mob_id: %s, potion_effects: %s}", mobId, potionEffects);
	}

	public static void apply(EntityJoinWorldEvent event) {
		if (JsonMob.mobs.isEmpty())
			return;

		Entity entity = event.getEntity();
		World world = event.getWorld();
		Random random = world.rand;

		//JsonCreeper.FixAreaEffectClouds(entity);

		if (!(entity instanceof LivingEntity))
			return;

		LivingEntity entityLiving = (LivingEntity) entity;

		CompoundNBT tags = entityLiving.getPersistentData();
		boolean isAlreadyChecked = tags.getBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked");

		if (isAlreadyChecked)
			return;

		boolean shouldNotBeProcessed = tags.getBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "prevent_processing");

		if (shouldNotBeProcessed)
			return;

		JsonPotionEffect.apply(entityLiving, world, random);
		//JsonAttribute.Apply(entityLiving, world, random);
		//JsonEquipment.Apply(entityLiving, world, random);

		//JsonCreeper.Apply(entityLiving, world, random);
		//JsonGhast.Apply(entityLiving, world, random);

		tags.putBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked", true);

	}

	@Override
	public void validate() throws InvalidJsonException {
		//TODO Groups
		if (mobId == null)
			throw new InvalidJsonException("Missing mob_id or group for %s", this.toString());

		if (mobId != null) {
			String[] splitId = mobId.split(":");
			if (splitId.length != 2)
				throw new InvalidJsonException("Invalid mob_id '%s'", mobId);

			ResourceLocation resourceLocation = new ResourceLocation(mobId);
			if (!ForgeRegistries.ENTITIES.containsKey(resourceLocation) && !mobId.endsWith("*"))
				throw new InvalidJsonException("mob_id '%s' does not exist", mobId);
		}

		if (potionEffects == null)
			potionEffects = new ArrayList<JsonPotionEffect>();
		for (JsonPotionEffect potionEffect : potionEffects) {
			potionEffect.validate();
		}
	}

	/*
		Reloads all the JSONs in the mod's config folder. Returns an empty string if no error was thrown
	 */
	public static String loadJsons() {
		//check if json folder exist, if not, create it
		File jsonFolder = new File(Paths.get("config", MobsPropertiesRandomness.MOD_ID, "json").toString());
		if (!jsonFolder.exists())
			jsonFolder.mkdir();

		//Empty the list with the loaded jsons
		mobs.clear();

		//if has failed loading to display the message in chat
		String outputResult = "";

		Gson gson = new Gson();

		//config/mobspropertiesrandomness/json
		ArrayList<File> jsonFiles = FilesUtils.ListFilesForFolder(jsonFolder);

		for (File file : jsonFiles) {
			//Ignore files that start with underscore '_'
			if (file.getName().startsWith("_"))
				continue;

			try {
				Logger.Debug("Reading file " + file.getName());
				FileReader fileReader = new FileReader(file);
				JsonMob mob = gson.fromJson(fileReader, JsonMob.class);
				Logger.Debug(mob.toString());
				mob.validate();
				mobs.add(mob);
			} catch (Exception e) {
				Logger.Error("Failed to parse file with name " + file.getName());
				Logger.Error(e.getMessage());
				outputResult = "Failed to parse file '" + file.getName() + "': " + e.getMessage();
				e.printStackTrace();
			}
		}

		Logger.Info("Reloaded JSONs");
		return outputResult;
	}
}
