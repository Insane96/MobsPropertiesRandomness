package insane96mcp.mobspropertiesrandomness.module.base.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Base", description = "Base feature of the mod")
public class BaseFeature extends Feature {
	private final ForgeConfigSpec.ConfigValue<Boolean> debugConfig;

	public boolean debug = true;

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

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		MPRMob.apply(event);
	}
}
