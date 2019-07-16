package insane96mcp.mpr.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CreeperFuseReply {
	
	public int id;
	public short fuse;
	public boolean powered;

	public CreeperFuseReply(int id, int fuse, boolean powered) {
		this.id = id;
		this.fuse = (short) fuse;
		this.powered = powered;
	}


	public static CreeperFuseReply decode(final PacketBuffer buffer) {
		int id = buffer.readInt();
		short fuse = buffer.readShort();
		boolean powered = buffer.readBoolean();

		return new CreeperFuseReply(id, fuse, powered);
	}

	public static void encode(final CreeperFuseReply message, final PacketBuffer buffer) {
		buffer.writeInt(message.id);
		buffer.writeShort(message.fuse);
		buffer.writeBoolean(message.powered);
	}

	public static void handle(final CreeperFuseReply message, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			World world = Minecraft.getInstance().world;
			Entity entity = world.getEntityByID(message.id);
			if (entity instanceof CreeperEntity) {
				CreeperEntity creeper = (CreeperEntity) entity;
				CompoundNBT compound = new CompoundNBT();
				compound.putShort("Fuse", message.fuse);
				compound.putBoolean("powered", message.powered);
				creeper.deserializeNBT(compound);
			}
		});
	}
}
