package net.insane96mcp.mpr.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CreeperFuseReply implements IMessage {
	
	public short fuse;
	public int id;
	
	public CreeperFuseReply() { this(30, -1); }
	public CreeperFuseReply(int fuse, int id) {
		this.fuse = (short) fuse;
		this.id = id;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.fuse = buf.readShort();
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(this.fuse);
		buf.writeInt(this.id);
	}

	public static class Handler implements IMessageHandler<CreeperFuseReply, IMessage> {

		@Override
		public IMessage onMessage(CreeperFuseReply message, MessageContext ctx) {
			IThreadListener iThreadListener = Minecraft.getMinecraft();
			iThreadListener.addScheduledTask(new Runnable() {
				
				@Override
				public void run() {
					World world = Minecraft.getMinecraft().world;
					Entity entity = world.getEntityByID(message.id);
					if (entity instanceof EntityCreeper) {
						EntityCreeper creeper = (EntityCreeper) entity;
						NBTTagCompound compound = new NBTTagCompound();
						compound.setShort("Fuse", message.fuse);
						creeper.readEntityFromNBT(compound);
					}
				}
			});
			return null;
		}
		
	}
	
}
