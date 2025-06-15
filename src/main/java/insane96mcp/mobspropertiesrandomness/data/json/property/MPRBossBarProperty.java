package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.util.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRBossBarProperty.Serializer.class)
public class MPRBossBarProperty extends MPRProperty {
    public static final ResourceLocation BOSS_BAR_VISIBILITY_RANGE = MPR.location("boss_bar_visibility_range");
    public static final ResourceLocation BOSS_BAR_ID = MPR.location("boss_bar_uuid");

    public BossEvent.BossBarColor color;
    public BossEvent.BossBarOverlay type;
    @SerializedName("darken_screen")
    public boolean darkenScreen;
    public int range;

    public MPRBossBarProperty(BossEvent.BossBarColor color, BossEvent.BossBarOverlay type, boolean darkenScreen, int range, List<MPRCondition> conditions) {
        super(conditions);
        this.color = color;
        this.type = type;
        this.darkenScreen = darkenScreen;
        this.range = range;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (living.getServer() == null
                || ModNBTData.contains(living, BOSS_BAR_ID))
            return false;
        MinecraftServer server = living.getServer();
        ResourceLocation bossBarId = MPR.location(living.getId() + "_" + living.getRandom().nextInt(Integer.MAX_VALUE));
        CustomBossEvent bossEvent = server.getCustomBossEvents().create(bossBarId, living.getDisplayName());
        bossEvent.setColor(this.color);
        bossEvent.setOverlay(this.type);
        bossEvent.setDarkenScreen(this.darkenScreen);
        ModNBTData.put(living, BOSS_BAR_ID, bossBarId.toString());
        ModNBTData.put(living, BOSS_BAR_VISIBILITY_RANGE, this.range);
        showBar(living, true);
        return true;
    }

    public static void removeBar(Entity entity) {
        if (entity.getServer() == null
                || !(entity instanceof LivingEntity living))
            return;
        CustomBossEvents customBossEvents = entity.getServer().getCustomBossEvents();
        CustomBossEvent bossEvent = getBarFromEntity(living);
        if (bossEvent != null) {
            bossEvent.removeAllPlayers();
            customBossEvents.remove(bossEvent);
        }
    }

    public static void removePlayer(Entity entity, Player player) {
        if (player.level().isClientSide
                || !(entity instanceof LivingEntity livingEntity)
                || !(player instanceof ServerPlayer serverPlayer))
            return;
        CustomBossEvent bossEvent = getBarFromEntity(livingEntity);
        if (bossEvent == null)
            return;
        bossEvent.removePlayer(serverPlayer);
    }

    public static void updateBar(LivingEntity living, @NotNull CustomBossEvent bossBar) {
        bossBar.setProgress(living.getHealth() / living.getMaxHealth());
    }

    public static void updateBar(Entity entity) {
        if (entity.level().isClientSide
                || !(entity instanceof LivingEntity livingEntity))
            return;
        CustomBossEvent bossEvent = getBarFromEntity(livingEntity);
        if (bossEvent == null)
            return;
        updateBar(livingEntity, bossEvent);
    }

    public static void showBar(Entity entity, boolean force) {
        if (!(entity instanceof LivingEntity living)
                || (entity.tickCount % 20 != entity.getId() % 20 && !force))
            return;

        CustomBossEvent bossBar = getBarFromEntity(living);
        if (bossBar == null)
            return;
        int range = ModNBTData.get(entity, BOSS_BAR_VISIBILITY_RANGE, Integer.class);
        bossBar.removeAllPlayers();
        entity.level().players()
                .stream()
                .filter(p -> p.distanceToSqr(entity) < range * range)
                .forEach(player -> bossBar.addPlayer((ServerPlayer) player));
        bossBar.setName(living.getDisplayName());
    }

    @Nullable
    private static CustomBossEvent getBarFromEntity(LivingEntity entity) {
        if (entity.getServer() == null)
            return null;
        String bossBarId = ModNBTData.get(entity, MPRBossBarProperty.BOSS_BAR_ID, String.class);
        if (bossBarId.isEmpty())
            return null;
        ResourceLocation bossBarIdLocation = ResourceLocation.parse(bossBarId);
        CustomBossEvents customBossEvents = entity.getServer().getCustomBossEvents();
        return customBossEvents.get(bossBarIdLocation);
    }

    public static class Serializer implements JsonDeserializer<MPRBossBarProperty>, JsonSerializer<MPRBossBarProperty> {
        @Override
        public MPRBossBarProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            BossEvent.BossBarColor color = GsonHelper.getAsObject(jObject, "color", context, BossEvent.BossBarColor.class);
            BossEvent.BossBarOverlay type = GsonHelper.getAsObject(jObject, "type", context, BossEvent.BossBarOverlay.class);
            boolean darkenScreen = GsonHelper.getAsBoolean(jObject, "darken_screen", false);
            int range = GsonHelper.getAsInt(jObject, "range", 48);
            return new MPRBossBarProperty(color, type, darkenScreen, range, MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRBossBarProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("color", context.serialize(src.color));
            jObject.add("type", context.serialize(src.type));
            jObject.addProperty("darken_screen", src.darkenScreen);
            jObject.addProperty("range", src.range);
            return src.endSerialization(jObject, context);
        }
    }
}
