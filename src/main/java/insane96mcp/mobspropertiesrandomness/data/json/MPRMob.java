package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRPresets;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import insane96mcp.mobspropertiesrandomness.util.MPRUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener.MPR_MOBS;

public class MPRMob extends MPRProperties implements IMPRObject {
	@SerializedName("target")
	public IdTagMatcher target;

	public MPRPresets presets;

	public Integer priority = 0;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
		if (this.target == null)
			throw new JsonValidationException("Missing target entity. " + this);

		if (this.presets != null)
			this.presets.validate();
	}

	public static void apply(EntityJoinLevelEvent event) {
		if (MPR_MOBS.isEmpty())
			return;

		Entity entity = event.getEntity();
		Level level = event.getLevel();

		if (level.isClientSide)
			return;

		if (!(entity instanceof LivingEntity livingEntity))
			return;

		CompoundTag tags = livingEntity.getPersistentData();
		boolean isAlreadyChecked = tags.getBoolean(Strings.Tags.PROCESSED);
		if (isAlreadyChecked)
			return;

		for (MPRMob mprMob : MPR_MOBS) {
			if (!mprMob.target.matchesEntity(livingEntity))
				continue;
			if (mprMob.presets == null)
				mprMob.apply(livingEntity, level);
			else {
				switch (mprMob.presets.mode) {
					case EXCLUSIVE:
						if (!mprMob.presets.apply(livingEntity, level))
							mprMob.apply(livingEntity, level);
						break;
					case BEFORE:
						mprMob.presets.apply(livingEntity, level);
						mprMob.apply(livingEntity, level);
						break;
					case AFTER:
						mprMob.apply(livingEntity, level);
						mprMob.presets.apply(livingEntity, level);
						break;
				}
			}
		}

		tags.putBoolean(Strings.Tags.PROCESSED, true);
	}
}
