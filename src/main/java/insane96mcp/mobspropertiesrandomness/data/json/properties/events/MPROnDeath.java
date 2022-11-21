package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import net.minecraft.world.entity.LivingEntity;

public class MPROnDeath extends MPREvent {

	public Target target;

	@SerializedName("damage_type")
	public DamageType damageType;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
		if (target == null)
			throw new JsonValidationException("Missing \"target\" for OnHit object: %s".formatted(this));
	}

	public void apply(LivingEntity entity, LivingEntity other, boolean isDirectDamage) {
		if (!super.shouldApply(entity))
			return;

		if (this.damageType != null && ((isDirectDamage && this.damageType == DamageType.INDIRECT) || (!isDirectDamage && this.damageType == DamageType.DIRECT)))
			return;

		if (this.target == Target.ENTITY) {
			this.tryPlaySound(entity);
		}
		else if (this.target == Target.OTHER) {
			this.tryPlaySound(other);
		}
	}

	@Override
	public String toString() {
		return String.format("OnDamage{%s, target: %s, damage_type: %s}", super.toString(), this.target, this.damageType);
	}
}
