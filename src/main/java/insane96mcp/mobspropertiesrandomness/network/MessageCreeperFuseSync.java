package insane96mcp.mobspropertiesrandomness.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageCreeperFuseSync {

	int id;
	short fuse;

	public MessageCreeperFuseSync(int id, short fuse) {
		this.id = id;
		this.fuse = fuse;
	}

	public static void encode(MessageCreeperFuseSync pkt, PacketBuffer buf) {
		buf.writeInt(pkt.id);
		buf.writeShort(pkt.fuse);
	}

	public static MessageCreeperFuseSync decode(PacketBuffer buf) {
		return new MessageCreeperFuseSync(buf.readInt(), buf.readShort());
	}

	public static void handle(final MessageCreeperFuseSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientNetwork.handleCreeperFuseSyncMessage(message.id, message.fuse);
		});
		ctx.get().setPacketHandled(true);
	}
}

//tp @p 83.639 89.42163 -461.082 67.0 26.2