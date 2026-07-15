package insane96mcp.mobspropertiesrandomness.util;

import insane96mcp.mobspropertiesrandomness.feature.MPRBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.LinkedHashMap;
import java.util.Map;

public class MPRChatNotifier {
	private static final Map<String, String> PENDING_ISSUES = new LinkedHashMap<>();

	public static void reportIssues(String source, int errorCount, int warningCount) {
		if (!MPRBase.warningsChat)
			warningCount = 0;

		if (errorCount <= 0 && warningCount <= 0) {
			PENDING_ISSUES.remove(source);
			return;
		}

		StringBuilder sb = new StringBuilder();
		if (errorCount > 0)
			sb.append(errorCount).append(" error(s)");
		if (warningCount > 0) {
			if (!sb.isEmpty())
				sb.append(" and ");
			sb.append(warningCount).append(" warning(s)");
		}
		sb.append(" loading ").append(source).append(", check logs/MobsPropertiesRandomness.log for details.");

		String message = sb.toString();
		PENDING_ISSUES.put(source, message);
		notifyOnlineOps(message);
	}

	public static void notifyJoiningOp(ServerPlayer player) {
		if (!player.hasPermissions(2))
			return;

		for (String message : PENDING_ISSUES.values())
			player.sendSystemMessage(format(message));
	}

	private static void notifyOnlineOps(String message) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null)
			return;

		Component component = format(message);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.hasPermissions(2))
				player.sendSystemMessage(component);
		}
	}

	private static Component format(String message) {
		return Component.literal("[MobsPropertiesRandomness] " + message).withStyle(ChatFormatting.RED);
	}
}
