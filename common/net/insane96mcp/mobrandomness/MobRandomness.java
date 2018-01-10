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

@Mod(modid = MobRandomness.MOD_ID, name = MobRandomness.MOD_NAME, version = MobRandomness.VERSION)
public class MobRandomness {
	
	public static final String MOD_ID = "mobrandomness";
	public static final String MOD_NAME = "Mob Properties Randomness";
	public static final String VERSION = "1.0.1";
	public static final String RESOURCE_PREFIX = MOD_ID.toLowerCase() + ":";

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
