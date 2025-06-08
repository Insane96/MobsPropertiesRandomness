package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class MPROnDeathLegacy extends MPREventLegacy {
	@SerializedName("damage_type")
	public DirectIndirect directIndirect;
	@SerializedName("set_freeze")
	public MPRRange setFreeze;
	@SerializedName("additive_freeze")
	public boolean additiveFreeze;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
		if (this.target == null)
			throw new JsonValidationException("Missing \"target\" for OnDeath object: %s".formatted(this));
	}

	public void apply(LivingEntity entity, @Nullable LivingEntity other, boolean isDirectDamage) {
		if (!super.shouldApply(entity))
			return;

		if (this.directIndirect != null && ((isDirectDamage && this.directIndirect == DirectIndirect.INDIRECT) || (!isDirectDamage && this.directIndirect == DirectIndirect.DIRECT)))
			return;

		LivingEntity target = this.target == Target.THIS ? entity : other;
		if (target == null)
			return;
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
		return String.format("OnDamage{%s, target: %s, damage_type: %s}", super.toString(), this.target, this.directIndirect);
	}
}
