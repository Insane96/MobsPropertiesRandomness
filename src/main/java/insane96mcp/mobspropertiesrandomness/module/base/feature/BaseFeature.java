package insane96mcp.mobspropertiesrandomness.module.base.feature;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMobOld;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPROnDeath;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPROnHit;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPROnTick;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Base")
public class BaseFeature extends Feature {
	private final ForgeConfigSpec.BooleanValue tiConAttackConfig;
	private final ForgeConfigSpec.BooleanValue betterCreeperLingeringConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> debugConfig;

	public boolean ticonAttack = true;
	public boolean betterCreeperLingering = true;
	public boolean debug = false;

	public BaseFeature(Module module) {
		super(Config.builder, module, true, false);
		//this.pushConfig(Config.builder);
		this.tiConAttackConfig = Config.builder
				.comment("If true mob attacks with Tinker tools will use the Tinker attack method. Might have side effects")
				.define("TiCon Attack", this.ticonAttack);
		this.betterCreeperLingeringConfig = Config.builder
				.comment("If true creeper lingering clouds size changes based off creeper explosion radius")
				.define("Better Creeper Lingering", this.betterCreeperLingering);
		this.debugConfig = Config.builder
				.comment("If true, all the loaded JSONs will be logged in the mobspropertiesrandomness.log file.")
				.define("Debug", this.debug);
		//Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.ticonAttack = this.tiConAttackConfig.get();
		this.betterCreeperLingering = this.betterCreeperLingeringConfig.get();
		this.debug = this.debugConfig.get();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		MPRMobOld.apply(event);
	}

	@SubscribeEvent
	public void onLivingDamage(LivingDamageEvent event) {
		if (!(event.getSource().getEntity() instanceof LivingEntity))
			return;

		onAttack(event);
		onAttacked(event);
	}

	public static final java.lang.reflect.Type MPR_ON_DEATH_LIST_TYPE = new TypeToken<ArrayList<MPROnDeath>>(){}.getType();
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		if (!event.getEntityLiving().getPersistentData().contains(Strings.Tags.ON_DEATH))
			return;

		LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
		List<MPROnDeath> onDeaths = new Gson().fromJson(event.getEntityLiving().getPersistentData().getString(Strings.Tags.ON_DEATH), MPR_ON_DEATH_LIST_TYPE);
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
			onDeath.apply(event.getEntityLiving(), attacker, event.getSource().getDirectEntity() == event.getSource().getEntity());
		}
	}

	public static final java.lang.reflect.Type MPR_ON_TICK_LIST_TYPE = new TypeToken<ArrayList<MPROnTick>>(){}.getType();
	@SubscribeEvent
	public void onLivingTick(LivingEvent.LivingUpdateEvent event) {
		if (!event.getEntityLiving().getPersistentData().contains(Strings.Tags.ON_TICK))
			return;

		List<MPROnTick> onTicks = new Gson().fromJson(event.getEntityLiving().getPersistentData().getString(Strings.Tags.ON_TICK), MPR_ON_TICK_LIST_TYPE);
		if (onTicks == null)
			return;

		for (MPROnTick onTick : onTicks) {
			//Does it impact performance?
			try {
				onTick.validate();
			} catch (JsonValidationException e) {
				Logger.error("Failed to validate MPROnTick: " + e);
				continue;
			}
			onTick.apply(event.getEntityLiving());
		}
	}

	public static final java.lang.reflect.Type MPR_ON_HIT_LIST_TYPE = new TypeToken<ArrayList<MPROnHit>>(){}.getType();
	private void onAttack(LivingDamageEvent event) {
		LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
		if (attacker == null
				|| !attacker.getPersistentData().contains(Strings.Tags.ON_ATTACK))
			return;

		List<MPROnHit> onHitEffects = new Gson().fromJson(attacker.getPersistentData().getString(Strings.Tags.ON_ATTACK), MPR_ON_HIT_LIST_TYPE);
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
			mprOnHit.apply(attacker, event.getEntityLiving(), event.getSource().getDirectEntity() == event.getSource().getEntity(), event, false);
		}
	}

	private void onAttacked(LivingDamageEvent event) {
		LivingEntity attacked = event.getEntityLiving();
		if (attacked == null
				|| !attacked.getPersistentData().contains(Strings.Tags.ON_ATTACKED))
			return;

		List<MPROnHit> onHitEffects = new Gson().fromJson(attacked.getPersistentData().getString(Strings.Tags.ON_ATTACKED), MPR_ON_HIT_LIST_TYPE);
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

	public boolean isBetterCreeperLingeringActivated() {
		return this.isEnabled() && this.betterCreeperLingering;
	}
}
