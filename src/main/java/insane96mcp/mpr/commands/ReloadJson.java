package insane96mcp.mpr.commands;

import com.mojang.brigadier.CommandDispatcher;
import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.json.Group;
import insane96mcp.mpr.json.Mob;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class ReloadJson {

	private ReloadJson() {

	}

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(
				Commands.literal("mpr:reloadjson")
						.requires(source -> source.hasPermissionLevel(3))
						.executes(context -> reloadJson(context.getSource()))
		);
	}

	private static int reloadJson(CommandSource source) {
		source.sendFeedback(new TranslationTextComponent(MobsPropertiesRandomness.RESOURCE_PREFIX + "reload_json"), true);
		boolean correctlyReloaded = Group.loadGroups() && Mob.loadJsons();
		if (!correctlyReloaded) {
			source.sendFeedback(new TranslationTextComponent(MobsPropertiesRandomness.RESOURCE_PREFIX + "json_reloaded_error"), true);
			return 0;
		} else {
			source.sendFeedback(new TranslationTextComponent(MobsPropertiesRandomness.RESOURCE_PREFIX + "json_reloaded"), true);
			return 1;
		}
	}
}
