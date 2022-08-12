package insane96mcp.mobspropertiesrandomness.module.base.feature;

import com.google.gson.Gson;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.json.properties.onhit.MPROnHitEffects;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Base")
public class BaseFeature extends Feature {
	private final ForgeConfigSpec.BooleanValue tiConAttackConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> debugConfig;

	public boolean ticonAttack = true;
	public boolean debug = false;

	public BaseFeature(Module module) {
		super(Config.builder, module, true, false);
		//this.pushConfig(Config.builder);
		this.tiConAttackConfig = Config.builder
				.comment("If true mob attacks with Tinker tools will use the Tinker attack method. Might have side effects")
				.define("TiCon Attack", this.ticonAttack);
		this.debugConfig = Config.builder
				.comment("If true, all the loaded JSONs will be logged in the mobspropertiesrandomness.log file.")
				.define("Debug", this.debug);
		//Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.ticonAttack = this.tiConAttackConfig.get();
		this.debug = this.debugConfig.get();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		MPRMob.apply(event);
	}

	@SubscribeEvent
	public void onLivingDamage(LivingDamageEvent event) {
		if (!(event.getSource().getEntity() instanceof LivingEntity))
			return;

		onAttack(event);
		onAttacked(event);
	}

	private void onAttack(LivingDamageEvent event) {
		LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
		if (attacker == null)
			return;

		if (!attacker.getPersistentData().contains(Strings.Tags.ON_HIT_EFFECTS))
			return;

		//TODO Validate the MPROnHitEffects as if invalid can crash the game
		MPROnHitEffects onHitEffects = new Gson().fromJson(attacker.getPersistentData().getString(Strings.Tags.ON_HIT_EFFECTS), MPROnHitEffects.class);

		onHitEffects.applyOnAttack(attacker, event.getEntityLiving(), event.getAmount(), event.getSource().getDirectEntity() == event.getSource().getEntity());
	}

	private void onAttacked(LivingDamageEvent event) {
		LivingEntity attacked = event.getEntityLiving();
		if (attacked == null)
			return;

		if (!attacked.getPersistentData().contains(Strings.Tags.ON_HIT_EFFECTS))
			return;

		//TODO Validate the MPROnHitEffects as if invalid can crash the game
		MPROnHitEffects onHitEffects = new Gson().fromJson(attacked.getPersistentData().getString(Strings.Tags.ON_HIT_EFFECTS), MPROnHitEffects.class);

		onHitEffects.applyOnAttacked(attacked, (LivingEntity) event.getSource().getEntity(), event.getAmount(), event.getSource().getDirectEntity() == event.getSource().getEntity());
	}
}
