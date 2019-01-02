package net.insane96mcp.mpr.lib;

import net.insane96mcp.mpr.MobsPropertiesRandomness;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = MobsPropertiesRandomness.MOD_ID, name = "/MobsPropertiesRandomness/MobsPropertiesRandomness", category = "")
public class Properties {

	@Name("Config")
	public static final ConfigOptions config = new ConfigOptions();
	
	public static class ConfigOptions {
		@Name("Debug")
		@Comment("Enable debug info in log file, useful when configuring the mod by adding and modifying JSONs")
		public boolean debug = true;
		
		@Name("Difficulty")
		@Comment("Change all the difficulty multiplier settings here")
		public Difficulty difficulty = new Difficulty();
		
		public static class Difficulty {
			@Name("Easy Multiplier")
			@Comment("Values affected by difficulty will be multiplied by this value in Easy Difficulty")
			public float easyMultiplier = 0.5f;
			
			@Name("Normal Multiplier")
			@Comment("Values affected by difficulty will be multiplied by this value in Normal Difficulty")
			public float normalMultiplier = 1.0f;
			
			@Name("Hard Multiplier")
			@Comment("Values affected by difficulty will be multiplied by this value in Hard Difficulty")
			public float hardMultiplier = 2.0f;
		}
	}	
	
	@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID)
	private static class EventHandler{
		@SubscribeEvent
	    public static void onConfigChangedEvent(OnConfigChangedEvent event)
	    {
	        if (event.getModID().equals(MobsPropertiesRandomness.MOD_ID))
	        {
	            ConfigManager.sync(MobsPropertiesRandomness.MOD_ID, Type.INSTANCE);
	        }
	    }
	}
}