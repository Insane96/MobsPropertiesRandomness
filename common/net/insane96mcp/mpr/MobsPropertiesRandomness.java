package net.insane96mcp.mpr;

import java.util.Random;

import org.apache.logging.log4j.Logger;

import net.insane96mcp.mpr.commands.ReloadJson;
import net.insane96mcp.mpr.lib.Properties;
import net.insane96mcp.mpr.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = MobsPropertiesRandomness.MOD_ID, name = MobsPropertiesRandomness.MOD_NAME, version = MobsPropertiesRandomness.VERSION, acceptedMinecraftVersions = MobsPropertiesRandomness.MINECRAFT_VERSIONS)
public class MobsPropertiesRandomness {
	
	public static final String MOD_ID = "mpr";
	public static final String MOD_NAME = "Mobs Properties Randomness";
	public static final String VERSION = "2.0.3";
	public static final String RESOURCE_PREFIX = MOD_ID.toLowerCase() + ":";
	public static final String MINECRAFT_VERSIONS = "[1.12,1.12.2]";

	public static Random random = new Random();
	
	@Instance(MOD_ID)
	public static MobsPropertiesRandomness instance;
	
	@SidedProxy(clientSide = "net.insane96mcp.mpr.proxies.ClientProxy")
	public static CommonProxy proxy;
	public static Logger logger;
	
	public static String configPath;
	
	public static void Debug(String string) {
		if (Properties.config.debug)
			logger.info(string);
	}
	
	public static void Warning(String string) {
		logger.warn(string);
	}
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		proxy.PreInit(event);
		configPath = event.getModConfigurationDirectory().getAbsolutePath() + "/MobsPropertiesRandomness/";
		logger = event.getModLog();
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		proxy.Init(event);
	}
	
	@EventHandler
	public void PostInit(FMLPostInitializationEvent event) {
		proxy.PostInit(event);
	}
	
	@EventHandler 
	public void Start(FMLServerStartingEvent event){
		event.registerServerCommand(new ReloadJson());
	}
}
