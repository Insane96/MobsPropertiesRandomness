package insane96mcp.mobspropertiesrandomness.module.base.feature;

import com.google.gson.Gson;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.json.util.onhit.MPROnHitEffects;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Base", description = "Base feature of the mod")
public class BaseFeature extends Feature {
	private final ForgeConfigSpec.ConfigValue<Boolean> debugConfig;

	public boolean debug = false;

	public BaseFeature(Module module) {
		super(Config.builder, module, true, false);
		Config.builder.comment(this.getDescription()).push(this.getName());
		this.debugConfig = Config.builder
				.comment("If true, all the loaded JSONs will be logged in the mobspropertiesrandomness.log file.")
				.define("Debug", this.debug);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.debug = this.debugConfig.get();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		MPRMob.apply(event);
	}

	@SubscribeEvent
	public void onSpawnFromSpawner(LivingSpawnEvent.CheckSpawn event) {
		if (event.getSpawnReason() == MobSpawnType.SPAWNER)
			event.getEntityLiving().getPersistentData().putBoolean(Strings.Tags.SPAWNED_FROM_SPAWNER, true);
		if (event.getSpawnReason() == MobSpawnType.STRUCTURE)
			event.getEntityLiving().getPersistentData().putBoolean(Strings.Tags.SPAWNED_FROM_STRUCTURE, true);
	}

	@SubscribeEvent
	public void onExperienceDrop(LivingExperienceDropEvent event) {
		if (event.getEntityLiving().getPersistentData().contains(Strings.Tags.EXPERIENCE_MULTIPLIER))
			event.setDroppedExperience((int) (event.getDroppedExperience() * event.getEntityLiving().getPersistentData().getDouble(Strings.Tags.EXPERIENCE_MULTIPLIER)));
	}

	@SubscribeEvent
	public void onLivingAttack(LivingDamageEvent event) {
		if (!(event.getSource().getEntity() instanceof LivingEntity))
			return;

		onAttack(event);
		onAttacked(event);
	}

	private void onAttack(LivingDamageEvent event) {
		LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
		if (!attacker.getPersistentData().contains(Strings.Tags.ON_HIT_EFFECTS))
			return;

		//TODO Validate the MPROnHitEffects as if invalid can crash the game
		MPROnHitEffects onHitEffects = new Gson().fromJson(attacker.getPersistentData().getString(Strings.Tags.ON_HIT_EFFECTS), MPROnHitEffects.class);

		onHitEffects.applyOnAttack(attacker, event.getEntityLiving(), event.getAmount(), event.getSource().getDirectEntity() == event.getSource().getEntity());
	}

	private void onAttacked(LivingDamageEvent event) {
		LivingEntity attacked = event.getEntityLiving();
		if (!attacked.getPersistentData().contains(Strings.Tags.ON_HIT_EFFECTS))
			return;

		//TODO Validate the MPROnHitEffects as if invalid can crash the game
		MPROnHitEffects onHitEffects = new Gson().fromJson(attacked.getPersistentData().getString(Strings.Tags.ON_HIT_EFFECTS), MPROnHitEffects.class);

		onHitEffects.applyOnAttacked(attacked, (LivingEntity) event.getSource().getEntity(), event.getAmount(), event.getSource().getDirectEntity() == event.getSource().getEntity());
	}
}
