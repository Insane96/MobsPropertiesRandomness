package insane96mcp.mobspropertiesrandomness.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class MPRChatNotifier {
	public static void notifyOps(String message) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null)
			return;

		Component component = Component.literal("[MobsPropertiesRandomness] " + message).withStyle(ChatFormatting.RED);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.hasPermissions(2))
				player.sendSystemMessage(component);
		}
	}
}
