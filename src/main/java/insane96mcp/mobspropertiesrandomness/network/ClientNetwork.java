package insane96mcp.mobspropertiesrandomness.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

@OnlyIn(Dist.CLIENT)
public class ClientNetwork {
	public static void handleCreeperFuseSyncMessage(int id, short fuse) {
		ThreadTaskExecutor<Runnable> executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.CLIENT);
		executor.tell(new TickDelayedTask(0, () -> {
			World world = Minecraft.getInstance().level;
			Entity entity = world.getEntity(id);
			if (entity instanceof CreeperEntity) {
				CreeperEntity creeper = (CreeperEntity) entity;
				CompoundNBT nbt = new CompoundNBT();
				creeper.addAdditionalSaveData(nbt);
				nbt.putShort("Fuse", fuse);
				creeper.readAdditionalSaveData(nbt);
			}
		}));
	}
}