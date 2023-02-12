package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class MPRPreset extends MPRProperties implements IMPRObject {
	@JsonAdapter(ResourceLocation.Serializer.class)
	public transient ResourceLocation id;

	@SerializedName("show_boss_bar")
	public Boolean showBossBar;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();

		if (this.showBossBar == null)
			this.showBossBar = false;
	}

	@Override
	public boolean apply(LivingEntity livingEntity, Level level) {
		boolean ret = super.apply(livingEntity, level);
		if (this.showBossBar) {
			CustomBossEvents bossEvents = livingEntity.getServer().getCustomBossEvents();
			ResourceLocation bossBarId = new ResourceLocation(MobsPropertiesRandomness.MOD_ID, id.getPath() + "_" + livingEntity.getRandom().nextInt(Integer.MAX_VALUE));
			CustomBossEvent bossEvent = bossEvents.create(bossBarId, livingEntity.getDisplayName());
			bossEvent.setColor(BossEvent.BossBarColor.PINK);
			bossEvent.setOverlay(BossEvent.BossBarOverlay.NOTCHED_10);
			livingEntity.getPersistentData().putString(Strings.Tags.BOSS_BAR_ID, bossBarId.toString());
		}
		return ret;
	}
}
