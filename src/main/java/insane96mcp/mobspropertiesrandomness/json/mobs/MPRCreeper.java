package insane96mcp.mobspropertiesrandomness.json.mobs;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.io.File;

public class MPRCreeper implements IMPRObject, IMPRAppliable {
	public MPRRange fuse;
	@SerializedName("explosion_radius")
	public MPRRange explosionRadius;
	@SerializedName("powered_chance")
	public MPRModifiableValue poweredChance;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (fuse != null)
			fuse.validate(file);

		if (explosionRadius != null)
			explosionRadius.validate(file);

		if (poweredChance != null)
			poweredChance.validate(file);
	}

	@Override
	public void apply(MobEntity entity, World world) {
		if (!(entity instanceof CreeperEntity))
			return;

		CreeperEntity creeper = (CreeperEntity) entity;

		if (world.isRemote) {
			//TODO Fix creeper fuse animation clientside
			//PacketHandler.SendToServer(new CreeperFuse(entityCreeper.getEntityId()));
			return;
		}

		CompoundNBT compound = new CompoundNBT();
		creeper.writeAdditional(compound);

		//Fuse
		if (this.fuse != null && compound.getShort("Fuse") == 30) {
			int minFuse = (int) this.fuse.getMin(creeper, world);
			int maxFuse = (int) this.fuse.getMax(creeper, world);
			int fuse = RandomHelper.getInt(world.rand, minFuse, maxFuse + 1);
			compound.putShort("Fuse", (short)fuse);
		}

		//Explosion Radius
		if (this.explosionRadius != null && compound.getByte("ExplosionRadius") == 3) {
			int minExplosionRadius = (int) this.explosionRadius.getMin(creeper, world);
			int maxExplosionRadius = (int) this.explosionRadius.getMax(creeper, world);
			int explosionRadius = RandomHelper.getInt(world.rand, minExplosionRadius, maxExplosionRadius + 1);
			compound.putByte("ExplosionRadius", (byte) explosionRadius);
		}

		//Power It
		if(this.poweredChance != null && world.rand.nextFloat() < this.poweredChance.getValue(creeper, world))
			compound.putBoolean("powered", true);

		creeper.readAdditional(compound);
	}


	@Override
	public String toString() {
		return String.format("Creeper{fuse: %s, explosion_radius: %s, powered_chance: %s}", fuse, explosionRadius, poweredChance);
	}
}