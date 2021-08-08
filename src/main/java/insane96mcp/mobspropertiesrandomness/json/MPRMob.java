package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRCreeper;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import insane96mcp.mobspropertiesrandomness.utils.MPRUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static insane96mcp.mobspropertiesrandomness.data.MPRReloadListener.MPR_MOBS;

public class MPRMob implements IMPRObject {
	@SerializedName("mob_id")
	public String mobId;
	public String group;

	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	public List<MPRAttribute> attributes;

	public MPREquipment equipment;

	public MPRCreeper creeper;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (mobId == null && group == null)
			throw new InvalidJsonException("Missing mob_id or group for " + this, file);
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

		/*if (group != null) {
			if (!JsonGroup.DoesGroupExist(group))
				throw new InvalidJsonException("group " + group + " does not exist", file);
		}*/

		if (potionEffects == null)
			potionEffects = new ArrayList<>();
		for (MPRPotionEffect potionEffect : potionEffects) {
			potionEffect.validate(file);
		}

		if (attributes == null)
			attributes = new ArrayList<>();
		for (MPRAttribute attribute : attributes) {
			attribute.validate(file);
		}

		if (equipment == null)
			equipment = new MPREquipment();
		equipment.validate(file);

		//Mob specific validations
		if (creeper != null)
			creeper.validate(file);

		/*if (ghast != null)
			ghast.Validate(file);*/
	}

	public static void apply(EntityJoinWorldEvent event) {
		if (MPR_MOBS.isEmpty())
			return;

		Entity entity = event.getEntity();
		World world = event.getWorld();

		if (!(entity instanceof MobEntity))
			return;

		MobEntity mobEntity = (MobEntity) entity;

		CompoundNBT tags = mobEntity.getPersistentData();
		boolean isAlreadyChecked = tags.getBoolean(Strings.Tags.PROCESSED);

		if (isAlreadyChecked)
			return;

		for (MPRMob mprMob : MPR_MOBS) {
			if (!MPRUtils.matchesEntity(mobEntity, mprMob))
				continue;
			for (MPRPotionEffect potionEffect : mprMob.potionEffects) {
				potionEffect.apply(mobEntity, world);
			}
			for (MPRAttribute attribute : mprMob.attributes) {
				attribute.apply(mobEntity, world);
			}
			mprMob.equipment.apply(mobEntity, world);

			if (mprMob.creeper != null)
				mprMob.creeper.apply(mobEntity, world);
		}
		//JsonAttribute.Apply(mobEntity, world, random);
		//JsonGhast.Apply(mobEntity, world, random);

		tags.putBoolean(Strings.Tags.PROCESSED, true);
	}

	@Override
	public String toString() {
		return String.format("Mob{id: %s, group: %s, potion_effects: %s, attributes: %s, equipment: %s, creeper: %s}", mobId, group, potionEffects, attributes, equipment, creeper);
		//return String.format("Mob{id: %s, group: %s, potionEffects: %s, attributes: %s, equipment: %s, creeper: %s, ghast: %s}", mobId, group, potionEffects, attributes, equipment, creeper, ghast);
	}
}
