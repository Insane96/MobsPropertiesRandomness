package insane96mcp.mobspropertiesrandomness.json.mob;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.MPRRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;

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
	public void apply(LivingEntity entity, Level world) {
		if (!(entity instanceof Ghast))
			return;

		Ghast ghast = (Ghast) entity;

		CompoundTag compound = new CompoundTag();
		ghast.addAdditionalSaveData(compound);

		//Explosion Radius
		if (this.explosionPower != null && compound.getByte("ExplosionPower") == 1) {
			int power = this.explosionPower.getIntBetween(ghast, world);
			compound.putInt("ExplosionPower", power);
		}

		ghast.readAdditionalSaveData(compound);
	}


	@Override
	public String toString() {
		return String.format("Ghast{explosion_power: %s}", explosionPower);
	}
}
