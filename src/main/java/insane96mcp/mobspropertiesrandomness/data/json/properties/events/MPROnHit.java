package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRPotionEffect;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifier;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class MPROnHit extends MPREvent {

	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	public Target target;

	@SerializedName("damage_type")
	public DamageType damageType;

	@SerializedName("damage_amount")
	public MPRRange damageAmount = new MPRRange(0f, Float.MAX_VALUE);

	@SerializedName("damage_modifier_operation")
	public MPRModifier.Operation damageModifierOperation;
	@SerializedName("damage_modifier")
	public MPRModifiableValue damageModifier;

	@SerializedName("health_left")
	public Double healthLeft;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
		if (this.target == null)
			throw new JsonValidationException("Missing \"target\" for OnHit object: %s".formatted(this));

		if (this.potionEffects != null) {
			for (MPRPotionEffect potionEffect : this.potionEffects)
				potionEffect.validate();
		}

		if (this.damageModifier != null) {
			if (this.damageModifierOperation == null)
				throw new JsonValidationException("Missing 'damage_modifier_operation' for OnHit object: %s".formatted(this));
			else
				this.damageModifier.validate();
		}
	}

	public void apply(LivingEntity entity, LivingEntity other, boolean isDirectDamage, LivingDamageEvent event, boolean attacked) {
		if (!super.shouldApply(entity))
			return;

		if (this.damageType != null && ((isDirectDamage && this.damageType == DamageType.INDIRECT) || (!isDirectDamage && this.damageType == DamageType.DIRECT)))
			return;

		if (event.getAmount() < this.damageAmount.getMin(entity, entity.level) || event.getAmount() > this.damageAmount.getMax(entity, entity.level))
			return;

		if (this.damageModifier != null) {
			if (this.damageModifierOperation == MPRModifier.Operation.ADD)
				event.setAmount(event.getAmount() + this.damageModifier.getValue(entity, entity.level));
			else
				event.setAmount(event.getAmount() * this.damageModifier.getValue(entity, entity.level));
		}

		if (this.healthLeft != null && attacked) {
			float health = (entity.getHealth() - event.getAmount()) / entity.getMaxHealth();
			if (health > this.healthLeft || health <= 0f)
				return;
		}

		if (this.target == Target.ENTITY) {
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(entity, entity.level);
			}
			this.tryPlaySound(entity);
			this.tryExecuteFunction(entity);
		}
		else if (this.target == Target.OTHER) {
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(other, other.level);
			}
			this.tryPlaySound(other);
			this.tryExecuteFunction(other);
		}
	}

	@Override
	public String toString() {
		return String.format("OnHit{%s, potion_effects: %s, damage_modifier_operation: %s, damage_modifier: %s, target: %s, damage_type: %s, health_left: %s}", super.toString(), this.potionEffects, this.damageModifierOperation, this.damageModifier, this.target, this.damageType, this.healthLeft);
	}
}
