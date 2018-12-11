package net.insane96mcp.mobrandomness.proxies;

import java.io.File;

import net.insane96mcp.mobrandomness.MobsPropertiesRandomness;
import net.insane96mcp.mobrandomness.events.EntityJoinWorld;
import net.insane96mcp.mobrandomness.json.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void PreInit(FMLPreInitializationEvent event) {

	}
	
	public void Init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(EntityJoinWorld.class);
	}
	
	public void PostInit(FMLPostInitializationEvent event) {
		File jsonFolder = new File(MobsPropertiesRandomness.configPath + "json");
		if (!jsonFolder.exists())
			jsonFolder.mkdir();
		Mob.LoadJsons();
	}
}
