package insane96mcp.mobspropertiesrandomness.module.base.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.MPRAttributeModifier;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRBossBarProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPREffectImmunityProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRScalePehkuiProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.MPRDeathEvent;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.MPROnHitEvent;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.MPRTickEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener.MPR_MOBS;

@LoadFeature(module = MPR.RESOURCE_PREFIX + "base", canBeDisabled = false)
public class MPRBase extends Feature {
	public static final String PROCESSED = MPR.RESOURCE_PREFIX + "processed";
	public static final String PRESET = MPR.RESOURCE_PREFIX + "preset";
	/*@Config
        @Label(name = "TiCon Attack", description = "If true mob attacks with Tinker tools will use the Tinker attack method, making mobs able to use some TiCon modifiers.")
        public static Boolean ticonAttack = true;*/
	@Config(description = "If true creeper lingering clouds size changes based off their explosion radius.")
	public static Boolean betterCreeperLingering = true;
	@Config
	public static Boolean verboseLog = false;

	public MPRBase(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static void postActualHurt(LivingEntity living, DamageSource source, float amount) {

	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide)
			return;

		if (!(event.getEntity() instanceof LivingEntity livingEntity))
			return;
		//TODO
		/*if (livingEntity.getPersistentData().contains(PRESET)) {
			ResourceLocation rl = ResourceLocation.tryParse(livingEntity.getPersistentData().getString(PRESET));
			if (rl != null) {
				Optional<MPRPresetLegacy> preset = MPR_PRESETS.stream().filter(p -> p.id.equals(rl)).findFirst();
				preset.ifPresent(mprWeightedPreset -> mprWeightedPreset.apply(livingEntity));
			}
		}*/

		CompoundTag tags = livingEntity.getPersistentData();
		if (tags.getBoolean(MPRBase.PROCESSED))
			return;

		if (MPR_MOBS.isEmpty())
			return;

		for (MPRMob mprMob : MPR_MOBS)
			mprMob.tryApply(livingEntity);

		tags.putBoolean(MPRBase.PROCESSED, true);
		MPRAttributeModifier.fixHealth(livingEntity);
	}

	@SubscribeEvent
	public void onLivingDamage(LivingDamageEvent event) {
		MPROnHitEvent.onHit(event);
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		MPRDeathEvent.onDeath(event);
	}

	@SubscribeEvent
	public void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
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
