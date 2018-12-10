package net.insane96mcp.mobrandomness;

import java.util.Random;

import org.apache.logging.log4j.Logger;

import net.insane96mcp.mobrandomness.commands.ReloadJson;
import net.insane96mcp.mobrandomness.lib.Properties;
import net.insane96mcp.mobrandomness.proxies.CommonProxy;
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
	
	public static final String MOD_ID = "mobspropertiesrandomness";
	public static final String MOD_NAME = "Mobs Properties Randomness";
	public static final String VERSION = "2.0.0";
	public static final String RESOURCE_PREFIX = MOD_ID.toLowerCase() + ":";
	public static final String MINECRAFT_VERSIONS = "[1.12,1.12.2]";

	public static Random random = new Random();
	
	@Instance(MOD_ID)
	public static MobsPropertiesRandomness instance;
	
	@SidedProxy(clientSide = "net.insane96mcp.mobrandomness.proxies.ClientProxy", serverSide = "net.insane96mcp.mobrandomness.proxies.ServerProxy")
	public static CommonProxy proxy;
	public static Logger logger;
	
	public static String configPath;
	
	public static void Debug(String string) {
		if (Properties.config.debug)
			logger.info(string);
	}
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		proxy.PreInit(event);
		configPath = event.getModConfigurationDirectory().getAbsolutePath() + "/" + MOD_ID + "/";
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
