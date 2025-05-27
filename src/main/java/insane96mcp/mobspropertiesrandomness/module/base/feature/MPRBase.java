package insane96mcp.mobspropertiesrandomness.module.base.feature;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.LogHelper;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.data.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRBossBar;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPREvents;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPROnDeath;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPROnHit;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPROnTick;
import insane96mcp.mobspropertiesrandomness.data.json.properties.mods.pehuki.MPRScalePehkui;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.MPR_PRESETS;

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

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide)
			return;

		if (!(event.getEntity() instanceof LivingEntity livingEntity))
			return;
		if (livingEntity.getPersistentData().contains(PRESET)) {
			ResourceLocation rl = ResourceLocation.tryParse(livingEntity.getPersistentData().getString(PRESET));
			if (rl != null) {
				Optional<MPRPreset> preset = MPR_PRESETS.stream().filter(p -> p.id.equals(rl)).findFirst();
				preset.ifPresent(mprWeightedPreset -> mprWeightedPreset.apply(livingEntity));
			}
		}
		MPRMob.apply(event);

	}

	@SubscribeEvent
	public void onLivingDamage(LivingDamageEvent event) {
		onAttack(event);
		onAttacked(event);
	}

	public static final java.lang.reflect.Type MPR_ON_DEATH_LIST_TYPE = new TypeToken<ArrayList<MPROnDeath>>(){}.getType();
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		onDeathEvent(event);
		removePlayerFromBossBar(event);
	}

	@SubscribeEvent
	public void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
		if (!(event.getEntity() instanceof LivingEntity livingEntity)
				|| livingEntity.getServer() == null)
			return;

		CustomBossEvent bossEvent = getBarFromEntity(livingEntity);
		if (bossEvent == null)
			return;
		livingEntity.getServer().getCustomBossEvents().remove(bossEvent);
	}

	@SubscribeEvent
	public void onApplyEffect(MobEffectEvent.Applicable event) {
		if (event.getEntity().level().isClientSide
				|| !event.getEntity().getPersistentData().contains(MPR.RESOURCE_PREFIX + "effect_immunity"))
			return;

		ListTag listTag = event.getEntity().getPersistentData().getList(MPR.RESOURCE_PREFIX + "effect_immunity", Tag.TAG_STRING);
		for (int i = 0; i < listTag.size(); ++i) {
			String s = listTag.getString(i);
			if (ForgeRegistries.MOB_EFFECTS.getKey(event.getEffectInstance().getEffect()).toString().equals(s)) {
				event.setResult(Event.Result.DENY);
				break;
			}
		}
	}

	public static final java.lang.reflect.Type MPR_ON_TICK_LIST_TYPE = new TypeToken<ArrayList<MPROnTick>>(){}.getType();
	@SubscribeEvent
	public void onLivingTick(LivingEvent.LivingTickEvent event) {
		if (event.getEntity().level().isClientSide)
			return;
		tryApplyPehkui(event.getEntity());
		checkOnTick(event.getEntity());
		showBossBar(event.getEntity());
		updateBossBar(event.getEntity());
	}

	public void tryApplyPehkui(LivingEntity entity) {
		if (entity.tickCount == 1)
			MPRScalePehkui.applyScheduled(entity);
	}

	public void onDeathEvent(LivingDeathEvent event) {
		removeBossBar(event.getEntity());

		CompoundTag compoundTag = event.getEntity().getPersistentData();
        if (!compoundTag.contains(MPREvents.ON_DEATH))
			return;

		LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
		List<MPROnDeath> onDeaths = new Gson().fromJson(compoundTag.getString(MPREvents.ON_DEATH), MPR_ON_DEATH_LIST_TYPE);
		if (onDeaths == null)
			return;

		for (MPROnDeath onDeath : onDeaths) {
			//Does it impact performance?
			try {
				onDeath.validate();
			} catch (JsonValidationException e) {
				Logger.error("Failed to validate MPROnDeath: " + e);
				continue;
			}
			onDeath.apply(event.getEntity(), attacker, event.getSource().getDirectEntity() == event.getSource().getEntity());
		}
	}

	public void removePlayerFromBossBar(LivingDeathEvent event) {
		if (event.getEntity().level().isClientSide
				|| !(event.getSource().getEntity() instanceof LivingEntity livingEntity)
				|| !(event.getEntity() instanceof ServerPlayer player))
			return;
		CustomBossEvent bossEvent = getBarFromEntity(livingEntity);
		if (bossEvent == null)
			return;
		bossEvent.removePlayer(player);
	}

	public void removeBossBar(LivingEntity entity) {
		if (entity.getServer() == null)
			return;
		CompoundTag compoundTag = entity.getPersistentData();
		if (!compoundTag.contains(MPRBossBar.BOSS_BAR_ID))
			return;
		CustomBossEvents customBossEvents = entity.getServer().getCustomBossEvents();
		CustomBossEvent bossEvent = customBossEvents.get(ResourceLocation.parse(compoundTag.getString(MPRBossBar.BOSS_BAR_ID)));
		if (bossEvent != null) {
			bossEvent.removeAllPlayers();
			customBossEvents.remove(bossEvent);
		}
	}

	@SubscribeEvent
	public void onStopTracking(PlayerEvent.StopTracking event) {
		if (event.getEntity().level().isClientSide
				|| !(event.getTarget() instanceof LivingEntity livingEntity)
				|| !(event.getEntity() instanceof ServerPlayer player))
			return;
		CustomBossEvent bossEvent = getBarFromEntity(livingEntity);
		if (bossEvent == null)
			return;
		bossEvent.removePlayer(player);
	}

	@Nullable
	private CustomBossEvent getBarFromEntity(LivingEntity entity) {
		CompoundTag persistentData = entity.getPersistentData();
		if (!persistentData.contains(MPRBossBar.BOSS_BAR_ID))
			return null;
		ResourceLocation bossbarId = ResourceLocation.tryParse(persistentData.getString(MPRBossBar.BOSS_BAR_ID));
		if (bossbarId == null) {
			LogHelper.warn("[%s] Failed to find boss bar with id %s", MPR.MOD_ID, entity.getPersistentData().getString(MPRBossBar.BOSS_BAR_ID));
			return null;
		}
		//noinspection ConstantConditions
		return entity.getServer().getCustomBossEvents().get(bossbarId);
	}

	private void updateBossBar(LivingEntity entity) {
		if (entity.isDeadOrDying())
			return;

		CustomBossEvent bossBar = getBarFromEntity(entity);
		if (bossBar == null)
			return;
		bossBar.setProgress(entity.getHealth() / entity.getMaxHealth());
	}

	private void showBossBar(LivingEntity entity) {
		if (entity.tickCount % 20 != entity.getId() % 20)
			return;

		CustomBossEvent bossBar = getBarFromEntity(entity);
		if (bossBar == null)
			return;
		int range = entity.getPersistentData().getInt(MPRBossBar.BOSS_BAR_VISIBILITY_RANGE);
		bossBar.removeAllPlayers();
		entity.level().players()
				.stream()
				.filter(p -> p.distanceToSqr(entity) < range * range)
				.forEach(player -> bossBar.addPlayer((ServerPlayer) player));
	}

	private void checkOnTick(LivingEntity entity) {
		if (entity.isDeadOrDying())
			return;
		CompoundTag persistentData = entity.getPersistentData();
		if (!persistentData.contains(MPREvents.ON_TICK))
			return;

		List<MPROnTick> onTicks = new Gson().fromJson(persistentData.getString(MPREvents.ON_TICK), MPR_ON_TICK_LIST_TYPE);
		if (onTicks == null)
			return;

		for (MPROnTick onTick : onTicks) {
			//Does it impact performance?
			try {
				onTick.validate();
			}
			catch (JsonValidationException e) {
				Logger.error("Failed to validate MPROnTick: " + e);
				continue;
			}
			onTick.apply(entity);
		}
	}

	public static final java.lang.reflect.Type MPR_ON_HIT_LIST_TYPE = new TypeToken<ArrayList<MPROnHit>>(){}.getType();
	private void onAttack(LivingDamageEvent event) {
		if (!(event.getSource().getEntity() instanceof LivingEntity attacker)
				|| !attacker.getPersistentData().contains(MPREvents.ON_ATTACK))
			return;

		List<MPROnHit> onHitEffects = new Gson().fromJson(attacker.getPersistentData().getString(MPREvents.ON_ATTACK), MPR_ON_HIT_LIST_TYPE);
		if (onHitEffects == null)
			return;

		for (MPROnHit mprOnHit : onHitEffects) {
			//Does it impact performance?
			try {
				mprOnHit.validate();
			} catch (JsonValidationException e) {
				Logger.error("Failed to validate MPROnHit: " + e);
				continue;
			}
			mprOnHit.apply(attacker, event.getEntity(), event, false);
		}
	}

	private void onAttacked(LivingDamageEvent event) {
		LivingEntity attacked = event.getEntity();
		if (!attacked.getPersistentData().contains(MPREvents.ON_DAMAGED))
			return;

		List<MPROnHit> onHitEffects = new Gson().fromJson(attacked.getPersistentData().getString(MPREvents.ON_DAMAGED), MPR_ON_HIT_LIST_TYPE);
		if (onHitEffects == null)
			return;

		for (MPROnHit mprOnHit : onHitEffects) {
			//Does it impact performance?
			try {
				mprOnHit.validate();
			} catch (JsonValidationException e) {
				Logger.error("Failed to validate MPROnHit: " + e);
				continue;
			}
			mprOnHit.apply(attacked, (LivingEntity) event.getSource().getEntity(), event, true);
		}
	}

	public static boolean isBetterCreeperLingeringActivated() {
		return Feature.isEnabled(MPRBase.class) && betterCreeperLingering;
	}
}
