package insane96mcp.mobspropertiesrandomness.data.json.properties.attribute;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MPRItemAttribute extends MPRAttribute implements IMPRObject {
	public EquipmentSlot slot;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
	}

	public void applyToStack(LivingEntity entity, ItemStack itemStack, EquipmentSlot equipmentSlotType) {
		if (!this.shouldApply(entity))
			return;

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.id));
		AttributeModifier modifier = new AttributeModifier(this.modifierName, this.amount.getFloatBetween(entity), this.operation.get());
		EquipmentSlot modifierSlot = this.slot == null ? equipmentSlotType : this.slot;
		MCUtils.addAttributeModifierToItemStack(itemStack, attribute, modifier, modifierSlot);

		//TODO Bug, doesn't work as getAttributes doesn't take into account item modifiers
		this.fixHealth(entity);
	}

	@Override
	public String toString() {
		return String.format("ItemAttribute{uuid: %s, id: %s, modifier_name: %s, amount: %s, operation: %s, conditions: %s, slot: %s}", this.uuid, this.id, this.modifierName, this.amount, this.operation, this.conditions, this.slot);
	}
}
