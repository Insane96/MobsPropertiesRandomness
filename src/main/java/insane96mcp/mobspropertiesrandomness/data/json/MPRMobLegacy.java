package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRPresets;
import insane96mcp.mobspropertiesrandomness.module.base.feature.MPRBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import static insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener.MPR_MOBS;

public class MPRMobLegacy extends MPRPropertiesLegacy implements IMPRObject {
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

		if (!(entity instanceof LivingEntity livingEntity))
			return;

		/*for (MPRMobLegacy mprMob : MPR_MOBS) {
			if (!mprMob.target.matchesEntity(livingEntity))
				continue;
			Logger.debug("Applying MPRMob " + mprMob + " to " + livingEntity);
			if (mprMob.presets == null)
				mprMob.apply(livingEntity);
			else if (!livingEntity.getPersistentData().contains(MPRBase.PRESET)) {
				switch (mprMob.presets.mode) {
					case EXCLUSIVE:
						if (!mprMob.presets.apply(livingEntity))
							mprMob.apply(livingEntity);
						break;
					case BEFORE:
						mprMob.presets.apply(livingEntity);
						mprMob.apply(livingEntity);
						break;
					case AFTER:
						mprMob.apply(livingEntity);
						mprMob.presets.apply(livingEntity);
						break;
				}
			}
		}*/

		livingEntity.getPersistentData().remove(MPRBase.PRESET);
	}
}
