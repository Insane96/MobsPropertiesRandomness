package insane96mcp.mpr.network;

import insane96mcp.mpr.MobsPropertiesRandomness;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class CreeperFuse {
    private int id;

    public CreeperFuse(int id) {
        this.id = id;
    }

    public static CreeperFuse decode(final PacketBuffer buffer) {
        int id = buffer.readInt();

        return new CreeperFuse(id);
    }

    public static void encode(final CreeperFuse message, final PacketBuffer buffer) {
        buffer.writeInt(message.id);
    }

    public static void handle(final CreeperFuse message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            World world = player.getEntityWorld();

            Entity entity = world.getEntityByID(message.id);

            int fuse = 30;
            boolean powered = false;

            if (entity instanceof CreeperEntity) {
                CreeperEntity creeper = (CreeperEntity) entity;
                CompoundNBT nbt = new CompoundNBT();
                creeper.deserializeNBT(nbt);
                fuse = nbt.getShort("Fuse");
                powered = nbt.getBoolean("powered");
            }

            MobsPropertiesRandomness.network.send(PacketDistributor.PLAYER.with(() -> player), new CreeperFuseReply(message.id, fuse, powered));
        });
    }

}
