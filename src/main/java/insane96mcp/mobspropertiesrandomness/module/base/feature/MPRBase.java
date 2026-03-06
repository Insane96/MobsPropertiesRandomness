package insane96mcp.mobspropertiesrandomness.module.base.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.util.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.MPRAttributeModifier;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRBossBarProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRDamageImmunityProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPREffectImmunityProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRScalePehkuiProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener.MPR_MOBS;
import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.PRESETS;

@LoadFeature(module = MPR.RESOURCE_PREFIX + "base", canBeDisabled = false)
public class MPRBase extends Feature {
	public static final ResourceLocation PROCESSED = MPR.location("processed");
	public static final ResourceLocation PRESET = MPR.location("preset");
	@Config(description = "If true creeper lingering clouds size changes based off their explosion radius.")
	public static Boolean betterCreeperLingering = true;
	@Config
	public static Boolean verboseLog = false;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinLevelLowest(EntityJoinLevelEvent event) {
		onEntityJoinLevel(event, EventPriority.LOWEST);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEntityJoinLevelLow(EntityJoinLevelEvent event) {
		onEntityJoinLevel(event, EventPriority.LOW);
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onEntityJoinLevelNormal(EntityJoinLevelEvent event) {
		onEntityJoinLevel(event, EventPriority.NORMAL);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onEntityJoinLevelHigh(EntityJoinLevelEvent event) {
		onEntityJoinLevel(event, EventPriority.HIGH);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityJoinLevelHighest(EntityJoinLevelEvent event) {
		onEntityJoinLevel(event, EventPriority.HIGHEST);
	}

	private static void onEntityJoinLevel(EntityJoinLevelEvent event, EventPriority eventPriority) {
		if (event.getLevel().isClientSide)
			return;

		if (!(event.getEntity() instanceof LivingEntity livingEntity))
			return;

		if (eventPriority == EventPriority.LOW)
			tryApplyPresetFromNbt(livingEntity);

		if (ModNBTData.get(livingEntity, PROCESSED, Boolean.class))
			return;

		if (MPR_MOBS.isEmpty())
			return;

		for (MPRMob mprMob : MPR_MOBS) {
			if (mprMob.eventPriority == eventPriority)
				mprMob.tryApply(livingEntity);
		}

		if (eventPriority == EventPriority.LOWEST)
			ModNBTData.put(livingEntity, PROCESSED, true);
		MPRAttributeModifier.fixHealth(livingEntity);
	}

	public static void tryApplyPresetFromNbt(LivingEntity living) {
        if (!ModNBTData.contains(living, PRESET))
            return;

        ResourceLocation rl = ResourceLocation.tryParse(ModNBTData.get(living, PRESET, String.class));
        if (rl == null)
            return;

        MPRProperties preset = PRESETS.get(rl);
        if (preset == null)
            return;

        preset.forceApply(living);
    }

	@SubscribeEvent
	public void onLivingDamage(LivingDamageEvent event) {
		MPROnHitEvent.onHit(event);
	}

	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event) {
		if (MPRDamageImmunityProperty.preventDamage(event.getEntity(), event.getSource()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		MPRDeathEvent.onDeath(event);
		MPRKillEvent.onKill(event);
	}

	@SubscribeEvent
	public void onTargetSwitch(LivingChangeTargetEvent event) {
		MPRChangeTargetEvent.onTargetChange(event);
	}

	@SubscribeEvent
	public void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity().getRemovalReason() != null)
		    MPRBossBarProperty.removeBar(event.getEntity());
	}

	@SubscribeEvent
	public void onApplyEffect(MobEffectEvent.Applicable event) {
		if (MPREffectImmunityProperty.shouldPreventEffect(event.getEntity(), event.getEffectInstance().getEffect()))
			event.setResult(Event.Result.DENY);
	}

	@SubscribeEvent
	public void onLivingTick(LivingEvent.LivingTickEvent event) {
		if (event.getEntity().level().isClientSide)
			return;
		tryApplyPehkui(event.getEntity());
		MPRBossBarProperty.showBar(event.getEntity(), true);
		MPRBossBarProperty.updateBar(event.getEntity());
		MPRTickEvent.tickEvents(event.getEntity());
	}

	public void tryApplyPehkui(LivingEntity entity) {
		if (entity.tickCount == 1)
			MPRScalePehkuiProperty.applyScheduled(entity);
	}

	@SubscribeEvent
	public void onStopTracking(PlayerEvent.StopTracking event) {
		MPRBossBarProperty.removePlayer(event.getTarget(), event.getEntity());
	}

	public static boolean isBetterCreeperLingeringEnabled() {
		return Feature.isEnabled(MPRBase.class) && betterCreeperLingering;
	}
}
