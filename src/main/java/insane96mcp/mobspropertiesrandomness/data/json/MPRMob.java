package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRPresets;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import insane96mcp.mobspropertiesrandomness.util.MPRUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener.MPR_MOBS;

public class MPRMob extends MPRProperties implements IMPRObject {
	@SerializedName("mob_id")
	@JsonAdapter(ResourceLocation.Serializer.class)
	public ResourceLocation mobId;
	@SerializedName("entity_tag")
	@JsonAdapter(ResourceLocation.Serializer.class)
	public ResourceLocation entityTag;

	public MPRPresets presets;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
		if (this.mobId == null && this.entityTag == null)
			throw new JsonValidationException("Missing mob_id or entity_tag. " + this);
		else if (this.mobId != null && this.entityTag != null)
			Logger.info("mob_id and entity_tag are both present, mob_id will be ignored");

		if (this.mobId != null) {
			if (!ForgeRegistries.ENTITIES.containsKey(this.mobId))
				throw new JsonValidationException("Mob with ID " + this.mobId + " does not exist");
		}

		if (this.presets != null)
			this.presets.validate();
	}

	public static void apply(EntityJoinWorldEvent event) {
		if (MPR_MOBS.isEmpty())
			return;

		Entity entity = event.getEntity();
		Level world = event.getWorld();

		if (world.isClientSide)
			return;

		if (!(entity instanceof LivingEntity livingEntity))
			return;

		CompoundTag tags = livingEntity.getPersistentData();
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

		tags.putBoolean(Strings.Tags.PROCESSED, true);
	}
}
