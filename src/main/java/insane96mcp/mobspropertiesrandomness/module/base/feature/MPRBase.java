package insane96mcp.mobspropertiesrandomness.module.base.feature;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.LogHelper;
import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRBossBar;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPREvents;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPROnDeath;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPROnHit;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPROnTick;
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

@Label(name = "Base")
@LoadFeature(module = MobsPropertiesRandomness.RESOURCE_PREFIX + "base", canBeDisabled = false)
public class MPRBase extends Feature {
	public static final String PROCESSED = MobsPropertiesRandomness.RESOURCE_PREFIX + "processed";
	/*@Config
        @Label(name = "TiCon Attack", description = "If true mob attacks with Tinker tools will use the Tinker attack method, making mobs able to use some TiCon modifiers.")
        public static Boolean ticonAttack = true;*/
	@Config
	@Label(name = "Better Creeper Lingering", description = "If true creeper lingering clouds size changes based off creeper explosion radius.")
	public static Boolean betterCreeperLingering = true;
	@Config
	@Label(name = "Verbose Log")
	public static Boolean verboseLog = false;

	public MPRBase(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEntityJoinWorld(EntityJoinLevelEvent event) {
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
		removeBossBar(event);
	}

	@SubscribeEvent
	public void onApplyEffect(MobEffectEvent.Applicable event) {
		if (event.getEntity().level().isClientSide
				|| !event.getEntity().getPersistentData().contains(MobsPropertiesRandomness.RESOURCE_PREFIX + "effect_immunity"))
			return;

		ListTag listTag = event.getEntity().getPersistentData().getList(MobsPropertiesRandomness.RESOURCE_PREFIX + "effect_immunity", Tag.TAG_STRING);
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
		checkOnTick(event.getEntity());
		showBossBar(event.getEntity());
		updateBossBar(event.getEntity());
	}

	public void onDeathEvent(LivingDeathEvent event) {
		CompoundTag compoundTag = event.getEntity().getPersistentData();
		if (compoundTag.contains(MPRBossBar.BOSS_BAR_ID)) {
			CustomBossEvents customBossEvents = event.getEntity().getServer().getCustomBossEvents();
			CustomBossEvent bossEvent = customBossEvents.get(new ResourceLocation(compoundTag.getString(MPRBossBar.BOSS_BAR_ID)));
			if (bossEvent != null) {
				bossEvent.removeAllPlayers();
				customBossEvents.remove(bossEvent);
			}
		}

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

	public void removeBossBar(LivingDeathEvent event) {
		if (event.getEntity().level().isClientSide
				|| !(event.getSource().getEntity() instanceof LivingEntity livingEntity)
				|| !(event.getEntity() instanceof ServerPlayer player))
			return;
		CustomBossEvent bossEvent = getBarFromEntity(livingEntity);
		if (bossEvent == null)
			return;
		bossEvent.removePlayer(player);
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
			LogHelper.warn("[%s] Failed to find boss bar with id %s", MobsPropertiesRandomness.MOD_ID, entity.getPersistentData().getString(MPRBossBar.BOSS_BAR_ID));
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
		if (entity.tickCount % 20 != 0)
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
			mprOnHit.apply(attacker, event.getEntity(), event.getSource().getDirectEntity() == event.getSource().getEntity(), event, false);
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
			mprOnHit.apply(attacked, (LivingEntity) event.getSource().getEntity(), event.getSource().getDirectEntity() == event.getSource().getEntity(), event, true);
		}
	}

	public static boolean isBetterCreeperLingeringActivated() {
		return Feature.isEnabled(MPRBase.class) && betterCreeperLingering;
	}
}
