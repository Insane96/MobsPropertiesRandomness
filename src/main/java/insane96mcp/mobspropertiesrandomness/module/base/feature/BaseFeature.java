package insane96mcp.mobspropertiesrandomness.module.base.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.entity.SpawnReason;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
				.comment("If true, everything will be logged in the mobspropertiesrandomness.log file.")
				.define("Debug", this.debug);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.debug = this.debugConfig.get();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		MPRMob.apply(event);
	}

	@SubscribeEvent
	public void onSpawnFromSpawner(LivingSpawnEvent.CheckSpawn event) {
		if (event.getSpawnReason() == SpawnReason.SPAWNER)
			event.getEntityLiving().getPersistentData().putBoolean(Strings.Tags.SPAWNED_FROM_SPAWNER, true);
	}

	/*@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntitySize(EntityEvent.Size event) {
		if (event.getEntity() instanceof CreeperEntity) {
			event.setNewSize(new EntitySize(0.3f, 0.85f, false));
			event.setNewEyeHeight(event.getNewEyeHeight() / 2f);
			CreeperEntity creeper = (CreeperEntity) event.getEntity();
			creeper
		}
	}*/
}
