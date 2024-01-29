package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class MPROnDeath extends MPREvent {
	@SerializedName("damage_type")
	public DamageType damageType;
	@SerializedName("set_fire")
	public MPRRange setFire;
	@SerializedName("additive_fire")
	public boolean additiveFire;
	@SerializedName("set_freeze")
	public MPRRange setFreeze;
	@SerializedName("additive_freeze")
	public boolean additiveFreeze;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
		if (this.target == null)
			throw new JsonValidationException("Missing \"target\" for OnDeath object: %s".formatted(this));
		if (this.setFire != null)
			this.setFire.validate();
		if (this.setFreeze != null)
			this.setFreeze.validate();
	}

	public void apply(LivingEntity entity, @Nullable LivingEntity other, boolean isDirectDamage) {
		if (!super.shouldApply(entity))
			return;

		if (this.damageType != null && ((isDirectDamage && this.damageType == DamageType.INDIRECT) || (!isDirectDamage && this.damageType == DamageType.DIRECT)))
			return;

		LivingEntity target = this.target == Target.THIS ? entity : other;
		if (target == null)
			return;
		if (this.target == Target.OTHER && this.setFire != null)
			if (!this.additiveFire)
				target.setSecondsOnFire(this.setFire.getIntBetween(target));
			else
				target.setSecondsOnFire(target.getRemainingFireTicks() / 20 + this.setFire.getIntBetween(target));
		if (this.target == Target.OTHER && this.setFreeze != null) {
			if (!this.additiveFreeze)
				target.setTicksFrozen(this.setFreeze.getIntBetween(target));
			else
				target.setTicksFrozen(target.getTicksFrozen() + this.setFreeze.getIntBetween(target));
		}
		this.tryApply(target);
	}

	@Override
	public String toString() {
		return String.format("OnDamage{%s, target: %s, damage_type: %s}", super.toString(), this.target, this.damageType);
	}
}
