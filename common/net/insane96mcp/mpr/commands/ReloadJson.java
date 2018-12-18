package net.insane96mcp.mpr.commands;

import net.insane96mcp.mpr.MobsPropertiesRandomness;
import net.insane96mcp.mpr.json.Mob;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class ReloadJson extends CommandBase {

	@Override
	public String getName() {
		return MobsPropertiesRandomness.RESOURCE_PREFIX + "reloadjson";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		sender.sendMessage(new TextComponentTranslation(MobsPropertiesRandomness.RESOURCE_PREFIX + "reload_json"));
		if (!Mob.LoadJsons()) {
			sender.sendMessage(new TextComponentTranslation(MobsPropertiesRandomness.RESOURCE_PREFIX + "json_reload_error"));
		}
		sender.sendMessage(new TextComponentTranslation(MobsPropertiesRandomness.RESOURCE_PREFIX + "json_count", String.valueOf(Mob.mobs.size())));
	}

}
