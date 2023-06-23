package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRBossBar;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.world.entity.LivingEntity;

public class MPRPreset extends MPRProperties implements IMPRObject {
	@JsonAdapter(ResourceLocation.Serializer.class)
	public transient ResourceLocation id;

	@SerializedName("boss_bar")
	public MPRBossBar bossBar;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
	}

	@Override
	public boolean apply(LivingEntity entity) {
		boolean ret = super.apply(entity);
		if (this.bossBar != null) {
			//noinspection ConstantConditions
			CustomBossEvent bossBar = this.bossBar.createBar(this.id.getPath(), entity, entity.getRandom(), entity.getServer());
			entity.getPersistentData().putString(Strings.Tags.BOSS_BAR_ID, bossBar.getTextId().toString());
		}
		return ret;
	}
}
