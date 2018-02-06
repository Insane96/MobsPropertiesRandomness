package net.insane96mcp.mobrandomness;

import java.util.Random;

import net.insane96mcp.mobrandomness.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MobRandomness.MOD_ID, name = MobRandomness.MOD_NAME, version = MobRandomness.VERSION, acceptedMinecraftVersions = MobRandomness.MINECRAFT_VERSIONS)
public class MobRandomness {
	
	public static final String MOD_ID = "mobspropertiesrandomness";
	public static final String MOD_NAME = "Mobs Properties Randomness";
	public static final String VERSION = "1.3.0";
	public static final String RESOURCE_PREFIX = MOD_ID.toLowerCase() + ":";
	public static final String MINECRAFT_VERSIONS = "[1.11,1.11.2]";

	public static Random random = new Random();
	
	@Instance(MOD_ID)
	public static MobRandomness instance;
	
	@SidedProxy(clientSide = "net.insane96mcp.mobrandomness.proxies.ClientProxy", serverSide = "net.insane96mcp.mobrandomness.proxies.ServerProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		proxy.PreInit(event);
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		proxy.Init(event);
	}
	
	@EventHandler
	public void PostInit(FMLPostInitializationEvent event) {
		proxy.PostInit(event);
	}
}
