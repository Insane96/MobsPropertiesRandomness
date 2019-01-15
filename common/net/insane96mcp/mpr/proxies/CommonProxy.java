package net.insane96mcp.mpr.proxies;

import net.insane96mcp.mpr.json.Group;
import net.insane96mcp.mpr.json.Mob;
import net.insane96mcp.mpr.network.PacketHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void PreInit(FMLPreInitializationEvent event) {
		PacketHandler.Init();
	}
	
	public void Init(FMLInitializationEvent event) {

	}
	
	public void PostInit(FMLPostInitializationEvent event) {
		//TODO move this to PlayerJoinedWorld or LoggedIn
		Group.LoadGroups();
		Mob.LoadJsons();
	}
}
