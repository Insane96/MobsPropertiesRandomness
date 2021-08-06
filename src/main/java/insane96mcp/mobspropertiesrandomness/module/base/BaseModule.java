package insane96mcp.mobspropertiesrandomness.module.base;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.mobspropertiesrandomness.module.base.feature.BaseFeature;
import insane96mcp.mobspropertiesrandomness.setup.Config;

@Label(name = "Base")
public class BaseModule extends Module {
	public BaseFeature base;

	public BaseModule() {
		super(Config.builder, true, false);
		pushConfig(Config.builder);
		base = new BaseFeature(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		base.loadConfig();
	}
}
