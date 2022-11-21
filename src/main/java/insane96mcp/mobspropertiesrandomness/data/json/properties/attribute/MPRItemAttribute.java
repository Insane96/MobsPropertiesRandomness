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
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class MPRItemAttribute extends MPRAttribute implements IMPRObject {
	public EquipmentSlot slot;

	@Override
	public void validate() throws JsonValidationException {
		super.validate();
	}

	public void applyToStack(LivingEntity entity, Level world, ItemStack itemStack, EquipmentSlot equipmentSlotType) {
		if (!this.shouldApply(entity, world))
			return;

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.id));
		AttributeModifier modifier = new AttributeModifier(this.modifierName, this.amount.getFloat(entity, world), this.operation.get());
		EquipmentSlot modifierSlot = this.slot == null ? equipmentSlotType : this.slot;
		MCUtils.addAttributeModifierToItemStack(itemStack, attribute, modifier, modifierSlot);

		//TODO Bug, doesn't work as getAttributes doesn't take into account item modifiers
		this.fixHealth(entity);
	}

	@Override
	public String toString() {
		return String.format("ItemAttribute{uuid: %s, id: %s, modifier_name: %s, amount: %s, operation: %s, world_whitelist: %s, slot: %s}", uuid, id, modifierName, amount, operation, worldWhitelist, slot);
	}
}
