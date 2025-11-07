package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifier;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class MPROnHitEvent extends MPREvent {
	@Nullable
	public MPRRange damageAmount;
	@Nullable
	public MPRRange damageModifier;
	@Nullable
	public MPRModifier.Operation damageModifierOperation;
	@Nullable
	public MPRRange healthLeft;
	public boolean flatHealthLeft;
	public MPRHurtData hurtData;

	public MPROnHitEvent(@Nullable MPRModifier.Operation damageModifierOperation, @Nullable MPRRange damageAmount, @Nullable MPRRange damageModifier, @Nullable MPRRange healthLeft, boolean flatHealthLeft, MPRHurtData hurtData, ResourceLocation id, Target target, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
		super(id, target, applyProperties, conditions);
		this.hurtData = hurtData;
		this.damageAmount = damageAmount;
		this.damageModifierOperation = damageModifierOperation;
		this.damageModifier = damageModifier;
		this.healthLeft = healthLeft;
		this.flatHealthLeft = flatHealthLeft;
	}

	public void hit(LivingDamageEvent event, LivingEntity living, @Nullable LivingEntity other, DamageSource source, float amount, boolean isDirectDamage) {
		if (!this.hurtData.shouldApply(source, isDirectDamage)
				|| living.isDeadOrDying())
			return;

		if (this.damageAmount != null
				&& (amount < this.damageAmount.getMin(living) || amount > this.damageAmount.getMax(living)))
			return;

		if (this.damageModifier != null) {
			if (this.damageModifierOperation == MPRModifier.Operation.ADD)
				event.setAmount((float) (event.getAmount() + this.damageModifier.getDoubleBetween(living)));
			else
				event.setAmount((float) (event.getAmount() * this.damageModifier.getDoubleBetween(living)));
		}

		if (this.healthLeft != null) {
			float healthLeft = living.getHealth() - event.getAmount();
			if (flatHealthLeft)
				healthLeft /= living.getMaxHealth();
			if (this.healthLeft.isBetween(living, healthLeft))
				return;
		}

		this.tryExecute(living, other);
	}

	public static void onHit(LivingDamageEvent event) {
		MPRDamagedEvent.onDamaged(event);
		MPRAttackEvent.onAttack(event);
	}
}
