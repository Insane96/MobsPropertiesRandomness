package insane96mcp.mobspropertiesrandomness.network;

import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MobsPropertiesRandomness.MOD_ID, "simple_channel"))
			.clientAcceptedVersions(s -> true)
			.serverAcceptedVersions(s -> true)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void init() {
		CHANNEL.registerMessage(1, MessageCreeperFuseSync.class, MessageCreeperFuseSync::encode, MessageCreeperFuseSync::decode, MessageCreeperFuseSync::handle);
		MinecraftForge.EVENT_BUS.register(new NetworkHandler());
	}
}
