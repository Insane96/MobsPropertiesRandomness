package insane96mcp.mobspropertiesrandomness.json.utils.onhit;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.MPRPotionEffect;
import net.minecraft.entity.LivingEntity;

import java.io.File;
import java.util.List;

public class MPROnHit implements IMPRObject {

	//TODO Conditions such as mobs only attack and health remaining
	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	public Target target;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (target == null)
			throw new InvalidJsonException("Missing target for OnHit object: " + this, file);

		if (potionEffects == null)
			throw new InvalidJsonException("Missing potion_effects for OnHit object: " + this, file);
		else
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.validate(file);
			}
	}

	public void apply(LivingEntity entity, LivingEntity other) {
		if (this.target == Target.ENTITY) {
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(entity, entity.level);
			}
		}
		else if (this.target == Target.OTHER) {
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(other, other.level);
			}
		}
	}

	public enum Target {
		@SerializedName("entity")
		ENTITY,
		@SerializedName("other")
		OTHER
	}

	@Override
	public String toString() {
		return String.format("OnHit{potion_effects: %s, target: %s}", this.potionEffects, this.target);
	}
}
