package insane96mcp.mobspropertiesrandomness.data.json.mobspecificproperties;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;

public class MPRGhast implements IMPRObject {
	@SerializedName("explosion_power")
	public MPRRange explosionPower;

	@Override
	public void validate() throws JsonValidationException {
		if (explosionPower != null)
			explosionPower.validate();
	}

	public void apply(LivingEntity entity, Level world) {
		if (!(entity instanceof Ghast ghast))
			return;

		CompoundTag compound = new CompoundTag();
		ghast.addAdditionalSaveData(compound);

		//Explosion Radius
		if (this.explosionPower != null && compound.getByte("ExplosionPower") == 1) {
			int power = this.explosionPower.getInt(ghast, world);
			compound.putInt("ExplosionPower", power);
		}

		ghast.readAdditionalSaveData(compound);
	}


	@Override
	public String toString() {
		return String.format("Ghast{explosion_power: %s}", explosionPower);
	}
}
