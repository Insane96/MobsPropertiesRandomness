package insane96mcp.mpr.network;

import insane96mcp.mpr.MobsPropertiesRandomness;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetwork {
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MobsPropertiesRandomness.MOD_ID, "network");

    public static final String NETWORK_VERSION = new ResourceLocation(MobsPropertiesRandomness.MOD_ID, "1").toString();

    private static int messageId = 0;

    public static SimpleChannel getNetworkChannel() {
        final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .clientAcceptedVersions(version -> true)
                .serverAcceptedVersions(version -> true)
                .networkProtocolVersion(() -> NETWORK_VERSION)
                .simpleChannel();

        channel.messageBuilder(CreeperFuse.class, ++messageId)
                .decoder(CreeperFuse::decode)
                .encoder(CreeperFuse::encode)
                .consumer(CreeperFuse::handle)
                .add();

        channel.messageBuilder(CreeperFuseReply.class, ++messageId)
                .decoder(CreeperFuseReply::decode)
                .encoder(CreeperFuseReply::encode)
                .consumer(CreeperFuseReply::handle)
                .add();

        return channel;
    }
}
