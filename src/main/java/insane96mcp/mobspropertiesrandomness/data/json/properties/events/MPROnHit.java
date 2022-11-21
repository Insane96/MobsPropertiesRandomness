package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRPotionEffect;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class MPROnHit extends MPREvent {

	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	@SerializedName("damage_modifier_operation")
	public MPRModifier.Operation damageModifierOperation;
	@SerializedName("damage_modifier")
	public MPRModifiableValue damageModifier;

	public Target target;

	@SerializedName("damage_type")
	public DamageType damageType;

	@SerializedName("health_left")
	public Double healthLeft;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
		if (target == null)
			throw new JsonValidationException("Missing \"target\" for OnHit object: %s".formatted(this));

		if (potionEffects == null) {
			throw new JsonValidationException("Missing \"potion_effects\" for OnHit object: %s".formatted(this));
		}
		else {
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
		}
		else if (this.target == Target.OTHER) {
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(other, other.level);
			}
			this.tryPlaySound(other);
		}
	}

	@Override
	public String toString() {
		return String.format("OnHit{%s, potion_effects: %s, damage_modifier_operation: %s, damage_modifier: %s, target: %s, damage_type: %s, health_left: %s}", super.toString(), this.potionEffects, this.damageModifierOperation, this.damageModifier, this.target, this.damageType, this.healthLeft);
	}
}
