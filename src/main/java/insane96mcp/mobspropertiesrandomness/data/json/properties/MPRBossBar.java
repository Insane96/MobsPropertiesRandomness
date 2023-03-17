package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;

public class MPRBossBar implements IMPRObject {

    public static final String BOSS_BAR_VISIBILITY_RANGE = MobsPropertiesRandomness.RESOURCE_PREFIX + "boss_bar_visibility_range";
    public String color;
    public String type;
    @SerializedName("darken_screen")
    public boolean darkenScreen;
    public int range = 48;

    @Override
    public void validate() throws JsonValidationException {
        if (color == null)
            color = "white";
        if (type == null)
            type = "progress";
    }

    public CustomBossEvent createBar(String id, LivingEntity entity, RandomSource random, MinecraftServer server) {
        ResourceLocation bossBarId = new ResourceLocation(MobsPropertiesRandomness.MOD_ID, id + "_" + random.nextInt(Integer.MAX_VALUE));
        CustomBossEvent bossEvent = server.getCustomBossEvents().create(bossBarId, entity.getDisplayName());
        bossEvent.setColor(BossEvent.BossBarColor.byName(this.color));
        bossEvent.setOverlay(BossEvent.BossBarOverlay.byName(this.type));
        bossEvent.setDarkenScreen(this.darkenScreen);
        entity.getPersistentData().putInt(BOSS_BAR_VISIBILITY_RANGE, this.range);
        return bossEvent;
    }

    @Override
    public String toString() {
        return "MPRBossBar{color: '%s', type: %s, darken_screen: %s, range: %d}".formatted(this.color, this.type, this.darkenScreen, this.range);
    }
}
