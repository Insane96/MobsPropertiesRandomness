package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.util.MPRPresets;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import insane96mcp.mobspropertiesrandomness.util.MPRUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;

import static insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener.MPR_MOBS;

public class MPRMob extends MPRProperties implements IMPRObject {
	@SerializedName("mob_id")
	public String mobId;
	public String group;

	public MPRPresets presets;

	@Override
	public void validate(File file) throws InvalidJsonException {
		super.validate(file);
		if (this.mobId == null && this.group == null)
			throw new InvalidJsonException("Missing mob_id or group. " + this, file);
		else if (this.mobId != null && this.group != null)
			Logger.info("mob_id and group are both present, mob_id will be ignored");

		if (this.mobId != null) {
			String[] splitId = this.mobId.split(":");
			if (splitId.length != 2)
				throw new InvalidJsonException("Invalid mob_id " + this.mobId, file);

			ResourceLocation resourceLocation = new ResourceLocation(this.mobId);
			if (!ForgeRegistries.ENTITIES.containsKey(resourceLocation) && !this.mobId.endsWith("*"))
				throw new InvalidJsonException("Mob with ID " + this.mobId + " does not exist", file);
		}

		if (this.presets != null)
			this.presets.validate(file);
	}

	public static void apply(EntityJoinWorldEvent event) {
		if (MPR_MOBS.isEmpty())
			return;

		Entity entity = event.getEntity();
		World world = event.getWorld();

		if (world.isClientSide)
			return;

		if (!(entity instanceof LivingEntity))
			return;

		LivingEntity livingEntity = (LivingEntity) entity;

		CompoundNBT tags = livingEntity.getPersistentData();
		boolean isAlreadyChecked = tags.getBoolean(Strings.Tags.PROCESSED);
		if (isAlreadyChecked)
			return;

		for (MPRMob mprMob : MPR_MOBS) {
			if (!MPRUtils.matchesEntity(livingEntity, mprMob))
				continue;
			if (mprMob.presets == null)
				mprMob.apply(livingEntity, world);
			else {
				switch (mprMob.presets.mode) {
					case EXCLUSIVE:
						if (!mprMob.presets.apply(livingEntity, world))
							mprMob.apply(livingEntity, world);
						break;
					case BEFORE:
						mprMob.presets.apply(livingEntity, world);
						mprMob.apply(livingEntity, world);
						break;
					case AFTER:
						mprMob.apply(livingEntity, world);
						mprMob.presets.apply(livingEntity, world);
						break;
				}
			}
		}

		//LogHelper.info("" + mobEntity.getHealth() + " " + mobEntity.getMaxHealth());
		tags.putBoolean(Strings.Tags.PROCESSED, true);
	}

	@Override
	public String toString() {
		return String.format("Mob{id: %s, group: %s, conditions: %s, potion_effects: %s, attributes: %s, equipment: %s, creeper: %s, ghast: %s}", mobId, group, this.conditions, potionEffects, attributes, equipment, creeper, ghast);
	}
}
