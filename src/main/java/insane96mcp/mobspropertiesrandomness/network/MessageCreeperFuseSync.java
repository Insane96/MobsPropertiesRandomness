package insane96mcp.mobspropertiesrandomness.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
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
			ThreadTaskExecutor<Runnable> executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.CLIENT);
			executor.enqueue(new TickDelayedTask(0, () -> {
				World world = Minecraft.getInstance().world;
				Entity entity = world.getEntityByID(message.id);
				if (entity instanceof CreeperEntity) {
					CreeperEntity creeper = (CreeperEntity) entity;
					CompoundNBT nbt = new CompoundNBT();
					creeper.writeAdditional(nbt);
					nbt.putShort("Fuse", message.fuse);
					creeper.readAdditional(nbt);
				}
			}));

		});
		ctx.get().setPacketHandled(true);
	}
}
