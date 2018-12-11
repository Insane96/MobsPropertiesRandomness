package net.insane96mcp.mobrandomness.commands;

import net.insane96mcp.mobrandomness.json.Mob;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class ReloadJson extends CommandBase {

	@Override
	public String getName() {
		return "mpr:reloadjson";
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
		sender.sendMessage(new TextComponentTranslation("mobspropertiesrandomness.reload_json"));
		if (!Mob.LoadJsons()) {
			sender.sendMessage(new TextComponentTranslation("mobspropertiesrandomness.json_reload_error"));
		}
		sender.sendMessage(new TextComponentTranslation("mobspropertiesrandomness.json_count", String.valueOf(Mob.mobs.size())));
	}

}
