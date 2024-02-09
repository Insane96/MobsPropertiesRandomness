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

	@SerializedName("damage_type")
	public DamageType damageType;

	@SerializedName("damage_amount")
	public MPRRange damageAmount = new MPRRange(0f, Float.MAX_VALUE);

	@SerializedName("damage_modifier_operation")
	public MPRModifier.Operation damageModifierOperation;
	@SerializedName("damage_modifier")
	public MPRModifiableValue damageModifier;
	@SerializedName("set_fire")
	public MPRRange setFire;
	@SerializedName("additive_fire")
	public boolean additiveFire;
	@SerializedName("set_freeze")
	public MPRRange setFreeze;
	@SerializedName("additive_freeze")
	public boolean additiveFreeze;
	@SerializedName("health_left")
	public MPRRange healthLeft = new MPRRange(0f, 1f);

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
		if (this.setFire != null)
			this.setFire.validate();
		if (this.setFreeze != null)
			this.setFreeze.validate();
	}

	public void apply(LivingEntity entity, LivingEntity other, boolean isDirectDamage, LivingDamageEvent event, boolean attacked) {
		if (!super.shouldApply(entity) || event.getEntity().isDeadOrDying())
			return;

		if (this.damageType != null && ((isDirectDamage && this.damageType == DamageType.INDIRECT) || (!isDirectDamage && this.damageType == DamageType.DIRECT)))
			return;

		if (event.getAmount() < this.damageAmount.getMin(entity) || event.getAmount() > this.damageAmount.getMax(entity))
			return;

		if (this.damageModifier != null) {
			if (this.damageModifierOperation == MPRModifier.Operation.ADD)
				event.setAmount(event.getAmount() + this.damageModifier.getValue(entity));
			else
				event.setAmount(event.getAmount() * this.damageModifier.getValue(entity));
		}

		if (this.healthLeft != null && attacked) {
			float health = (entity.getHealth() - event.getAmount()) / entity.getMaxHealth();
			if (health < this.damageAmount.getMin(entity) || event.getAmount() > this.damageAmount.getMax(entity))
				return;
		}

		LivingEntity target = this.target == Target.THIS ? entity : other;
		if (target == null)
			return;
		if (this.potionEffects != null) {
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(target);
			}
		}
		if (this.setFire != null)
			if (!this.additiveFire)
				target.setSecondsOnFire(this.setFire.getIntBetween(target));
			else
				target.setSecondsOnFire(target.getRemainingFireTicks() / 20 + this.setFire.getIntBetween(target));
		if (this.setFreeze != null) {
			if (!this.additiveFreeze)
				target.setTicksFrozen(this.setFreeze.getIntBetween(target) * 20);
			else
				target.setTicksFrozen(target.getTicksFrozen() + this.setFreeze.getIntBetween(target) * 20);
		}
		this.tryApply(target);
	}

	@Override
	public String toString() {
		return String.format("OnHit{%s, potion_effects: %s, damage_modifier_operation: %s, damage_modifier: %s, target: %s, damage_type: %s, health_left: %s}", super.toString(), this.potionEffects, this.damageModifierOperation, this.damageModifier, this.target, this.damageType, this.healthLeft);
	}
}
