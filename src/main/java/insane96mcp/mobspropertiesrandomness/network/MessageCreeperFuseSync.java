package insane96mcp.mobspropertiesrandomness.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageCreeperFuseSync {

	int id;
	short fuse;

	public MessageCreeperFuseSync(int id, short fuse) {
		this.id = id;
		this.fuse = fuse;
	}

	public static void encode(MessageCreeperFuseSync pkt, FriendlyByteBuf buf) {
		buf.writeInt(pkt.id);
		buf.writeShort(pkt.fuse);
	}

	public static MessageCreeperFuseSync decode(FriendlyByteBuf buf) {
		return new MessageCreeperFuseSync(buf.readInt(), buf.readShort());
	}

	public static void handle(final MessageCreeperFuseSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ClientNetwork.handleCreeperFuseSyncMessage(message.id, message.fuse));
		ctx.get().setPacketHandled(true);
	}
}