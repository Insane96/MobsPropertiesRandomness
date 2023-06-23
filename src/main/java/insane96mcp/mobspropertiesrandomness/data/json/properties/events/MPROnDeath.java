package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class MPROnDeath extends MPREvent {

	public Target target;

	@SerializedName("damage_type")
	public DamageType damageType;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
		if (this.target == null)
			throw new JsonValidationException("Missing \"target\" for OnDeath object: %s".formatted(this));
	}

	public void apply(LivingEntity entity, @Nullable LivingEntity other, boolean isDirectDamage) {
		if (!super.shouldApply(entity))
			return;

		if (this.damageType != null && ((isDirectDamage && this.damageType == DamageType.INDIRECT) || (!isDirectDamage && this.damageType == DamageType.DIRECT)))
			return;

		if (this.target == Target.ENTITY) {
			this.tryApply(entity);
		}
		else if (this.target == Target.OTHER && other != null) {
			this.tryApply(other);
		}
	}

	@Override
	public String toString() {
		return String.format("OnDamage{%s, target: %s, damage_type: %s}", super.toString(), this.target, this.damageType);
	}
}
