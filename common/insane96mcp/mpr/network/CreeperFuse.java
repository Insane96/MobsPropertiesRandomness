package insane96mcp.mpr.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CreeperFuse implements IMessage{

	public int id;
	
	public CreeperFuse() { }
	public CreeperFuse(int id) {
		this.id = id;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.id);
	}
	
	public static class Handler implements IMessageHandler<CreeperFuse, IMessage>{

		@Override
		public IMessage onMessage(CreeperFuse message, MessageContext ctx) {
			IThreadListener iThreadListener = (WorldServer) ctx.getServerHandler().player.world;
			iThreadListener.addScheduledTask(new Runnable() {
				
				@Override
				public void run() {
					EntityPlayerMP player = ctx.getServerHandler().player;
					World world = player.getEntityWorld();
					
					Entity entity = world.getEntityByID(message.id);
					
					int fuse = 30;
					boolean powered = false;
					
					if (entity instanceof EntityCreeper) {
						EntityCreeper creeper = (EntityCreeper) entity;
						NBTTagCompound nbt = new NBTTagCompound();
						creeper.writeEntityToNBT(nbt);
						fuse = nbt.getShort("Fuse");
						powered = nbt.getBoolean("powered");
					}
					
					PacketHandler.SendToClient(new CreeperFuseReply(message.id, fuse, powered), player);
				}
			});
			
			return null;
		}
		
	}

}
