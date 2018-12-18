package net.insane96mcp.mpr.proxies;

import net.insane96mcp.mpr.json.Mob;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void PreInit(FMLPreInitializationEvent event) {

	}
	
	public void Init(FMLInitializationEvent event) {

	}
	
	public void PostInit(FMLPostInitializationEvent event) {
		Mob.LoadJsons();
	}
}
