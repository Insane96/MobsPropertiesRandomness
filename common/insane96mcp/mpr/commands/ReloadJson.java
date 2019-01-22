package insane96mcp.mpr.commands;

import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.json.Group;
import insane96mcp.mpr.json.Mob;
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
		boolean correctlyReloaded = Group.LoadGroups() && Mob.LoadJsons();
		if (!correctlyReloaded) 
			sender.sendMessage(new TextComponentTranslation(MobsPropertiesRandomness.RESOURCE_PREFIX + "json_reloaded_error"));
		else
			sender.sendMessage(new TextComponentTranslation(MobsPropertiesRandomness.RESOURCE_PREFIX + "json_reloaded"));
	}

}
