package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifier;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class MPROnHitLegacy extends MPREventLegacy {
	@SerializedName("direct_indirect")
	public MPRHurtData.DirectIndirect directIndirect;
	@SerializedName("damage_type")
	public String damageType;
	private transient ResourceKey<DamageType> _damageType;
	private transient TagKey<DamageType> _damageTypeTag;

	@SerializedName("damage_amount")
	public MPRRange damageAmount = new MPRRange(0d);

	@SerializedName("damage_modifier_operation")
	public MPRModifier.Operation damageModifierOperation;
	@SerializedName("damage_modifier")
	public MPRModifiableValue damageModifier;
	@SerializedName("health_left")
	public MPRRange healthLeft;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
		if (this.target == null)
			throw new JsonValidationException("Missing \"target\" for OnHit object: %s".formatted(this));

		if (this.damageType != null) {
			if (this.damageType.startsWith("#")) {
				ResourceLocation rl = ResourceLocation.tryParse(this.damageType.substring(1));
				if (rl == null)
					throw new JsonValidationException("Invalid damage type tag %s for OnHit object: %s".formatted(this.damageType, this));
				this._damageTypeTag = TagKey.create(Registries.DAMAGE_TYPE, rl);
			}
			else {
				ResourceLocation rl = ResourceLocation.tryParse(this.damageType);
				if (rl == null)
					throw new JsonValidationException("Invalid damage type %s for OnHit object: %s".formatted(this.damageType, this));
				this._damageType = ResourceKey.create(Registries.DAMAGE_TYPE, rl);
			}
		}

		if (this.damageModifier != null) {
			if (this.damageModifierOperation == null)
				throw new JsonValidationException("Missing 'damage_modifier_operation' for OnHit object: %s".formatted(this));
		}
	}

	public void apply(LivingEntity entity, LivingEntity other, LivingDamageEvent event, boolean attacked) {
		if (!super.shouldApply(entity)
				|| event.getEntity().isDeadOrDying())
			return;

		boolean isDirectDamage = event.getSource().getDirectEntity() == event.getSource().getEntity();
		if (this.directIndirect != null
				&& ((isDirectDamage && this.directIndirect == MPRHurtData.DirectIndirect.INDIRECT) || (!isDirectDamage && this.directIndirect == MPRHurtData.DirectIndirect.DIRECT)))
			return;

		if ((this._damageType != null && !event.getSource().is(this._damageType))
				|| (this._damageTypeTag != null && !event.getSource().is(this._damageTypeTag)))
			return;

		if (event.getAmount() < this.damageAmount.getMin(entity)
				|| event.getAmount() > this.damageAmount.getMax(entity))
			return;

		if (this.damageModifier != null) {
			if (this.damageModifierOperation == MPRModifier.Operation.ADD)
				event.setAmount((float) (event.getAmount() + this.damageModifier.getValue(entity)));
			else
				event.setAmount((float) (event.getAmount() * this.damageModifier.getValue(entity)));
		}

		if (this.healthLeft != null && attacked) {
			float health = (entity.getHealth() - event.getAmount()) / entity.getMaxHealth();
			if (health < this.healthLeft.getMin(entity) || health > this.healthLeft.getMax(entity))
				return;
		}

		LivingEntity target = this.target == Target.THIS ? entity : other;
		if (target == null)
			return;
		this.tryApply(target);
	}
}
