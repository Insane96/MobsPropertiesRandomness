package insane96mcp.mobspropertiesrandomness.feature;

import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.insanelib.core.feature.Feature;
import insane96mcp.insanelib.core.feature.LoadFeature;
import insane96mcp.insanelib.core.feature.config.Config;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.MPRAttributeModifier;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRBossBarProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRDamageImmunityProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPREffectImmunityProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import static insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener.MPR_MOBS;
import static insane96mcp.mobspropertiesrandomness.data.MPRMobsPresetReloadListener.PRESETS;

@LoadFeature(canBeDisabled = false)
public class MPRBase extends Feature {
	public static final ResourceLocation PROCESSED = MPR.id("processed");
	public static final ResourceLocation PRESET = MPR.id("preset");

	@Config(description = "If true creeper lingering clouds size changes based off their explosion radius.")
	public static Boolean betterCreeperLingering = true;
	@Config(description = "If true, warnings will be displayed in chat when reloading data packs. Useful when developing a data pack.")
	public static Boolean warningsChat = false;
	@Config(description = "If true, the full stack trace will be logged when a mob/preset/etc fails to load. Should be enabled when the dev asks to, to trace bugs.")
	public static Boolean logStacktraceOnError = false;

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

		if (eventPriority.equals(EventPriority.HIGHEST))
			tryApplyPresetFromNBT(livingEntity);

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

	public static void tryApplyPresetFromNBT(LivingEntity living) {
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
	public void onLivingDamage(LivingDamageEvent.Pre event) {
		MPROnHitEvent.onHit(event);
	}

	@SubscribeEvent
	public void onLivingAttack(LivingIncomingDamageEvent event) {
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
			event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
	}

	@SubscribeEvent
	public void onLivingTick(EntityTickEvent.Pre event) {
		if (event.getEntity().level().isClientSide
				|| !(event.getEntity() instanceof LivingEntity livingEntity))
			return;
		MPRBossBarProperty.showBar(event.getEntity(), false);
		MPRBossBarProperty.updateBar(event.getEntity());
		MPRTickEvent.tickEvents(livingEntity);
	}

	@SubscribeEvent
	public void onStopTracking(PlayerEvent.StopTracking event) {
		MPRBossBarProperty.removePlayer(event.getTarget(), event.getEntity());
	}

	@SubscribeEvent
	public void onItemAttributeModifierEvent(ItemAttributeModifierEvent event) {

	}

	public static boolean isBetterCreeperLingeringEnabled() {
		return Feature.isEnabled(MPRBase.class) && betterCreeperLingering;
	}
}
