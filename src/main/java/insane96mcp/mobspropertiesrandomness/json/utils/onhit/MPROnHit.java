package insane96mcp.mobspropertiesrandomness.json.utils.onhit;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.MPRPotionEffect;
import net.minecraft.entity.LivingEntity;

import java.io.File;
import java.util.List;

public class MPROnHit implements IMPRObject {

	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	public Target target;

	@SerializedName("damage_type")
	public DamageType damageType;

	@SerializedName("health_left")
	public Double healthLeft;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (target == null)
			throw new InvalidJsonException("Missing target for OnHit object: " + this, file);

		if (potionEffects == null)
			throw new InvalidJsonException("Missing potion_effects for OnHit object: " + this, file);
		else
			for (MPRPotionEffect potionEffect : this.potionEffects)
				potionEffect.validate(file);
	}

	public void apply(LivingEntity entity, LivingEntity other, float damage, boolean isDirectDamage) {
		if (this.damageType != null && ((isDirectDamage && this.damageType == DamageType.INDIRECT) || (!isDirectDamage && this.damageType == DamageType.DIRECT)))
			return;
		if (this.target == Target.ENTITY) {
			if (this.healthLeft != null) {
				float health = (entity.getHealth() - damage) / entity.getMaxHealth();
				if (health > this.healthLeft || health <= 0f)
					return;
			}

			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(entity, entity.level);
			}
		}
		else if (this.target == Target.OTHER) {
			if (this.healthLeft != null) {
				float health = (other.getHealth() - damage) / other.getMaxHealth();
				if (health > this.healthLeft || health <= 0f)
					return;
			}

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

	public enum DamageType {
		@SerializedName("direct")
		DIRECT,
		@SerializedName("indirect")
		INDIRECT
	}

	@Override
	public String toString() {
		return String.format("OnHit{potion_effects: %s, target: %s}", this.potionEffects, this.target);
	}
}
