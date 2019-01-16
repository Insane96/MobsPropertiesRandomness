package net.insane96mcp.mpr.proxies;

import net.insane96mcp.mpr.lib.Reflection;
import net.insane96mcp.mpr.network.PacketHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void PreInit(FMLPreInitializationEvent event) {
		PacketHandler.Init();
		Reflection.Init();
	}
	
	public void Init(FMLInitializationEvent event) {

	}
	
	public void PostInit(FMLPostInitializationEvent event) {

	}
}
