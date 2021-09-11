package insane96mcp.mobspropertiesrandomness.json.mobs;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.io.File;

public class MPRGhast implements IMPRObject, IMPRAppliable {
	@SerializedName("explosion_power")
	public MPRRange explosionPower;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (explosionPower != null)
			explosionPower.validate(file);
	}

	@Override
	public void apply(MobEntity entity, World world) {
		if (!(entity instanceof GhastEntity))
			return;

		GhastEntity ghast = (GhastEntity) entity;

		CompoundNBT compound = new CompoundNBT();
		ghast.writeAdditional(compound);

		//Explosion Radius
		if (this.explosionPower != null && compound.getByte("ExplosionPower") == 1) {
			int power = RandomHelper.getInt(world.rand, (int) this.explosionPower.getMin(ghast, world), (int) this.explosionPower.getMax(ghast, world) + 1);
			compound.putInt("ExplosionPower", power);
		}

		ghast.readAdditional(compound);
	}


	@Override
	public String toString() {
		return String.format("Ghast{explosion_power: %s}", explosionPower);
	}
}
