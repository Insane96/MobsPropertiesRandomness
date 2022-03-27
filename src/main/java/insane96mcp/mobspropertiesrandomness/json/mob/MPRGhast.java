package insane96mcp.mobspropertiesrandomness.json.mob;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.MPRRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;

public class MPRGhast implements IMPRObject, IMPRAppliable {
	@SerializedName("explosion_power")
	public MPRRange explosionPower;

	@Override
	public void validate() throws JsonValidationException {
		if (explosionPower != null)
			explosionPower.validate();
	}

	@Override
	public void apply(LivingEntity entity, Level world) {
		if (!(entity instanceof Ghast ghast))
			return;

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
