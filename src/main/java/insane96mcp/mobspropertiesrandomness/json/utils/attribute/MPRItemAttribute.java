package insane96mcp.mobspropertiesrandomness.json.utils.attribute;

import insane96mcp.insanelib.utils.MCUtils;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;

public class MPRItemAttribute extends MPRAttribute implements IMPRObject {
	public EquipmentSlotType slot;

	@Override
	public void validate(File file) throws InvalidJsonException {
		super.validate(file);
	}

	public void applyToStack(LivingEntity entity, World world, ItemStack itemStack, EquipmentSlotType equipmentSlotType) {
		if (!this.shouldApply(entity, world))
			return;

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.id));
		AttributeModifier modifier = new AttributeModifier(this.modifierName, this.amount.getFloatBetween(entity, world), this.operation);
		EquipmentSlotType modifierSlot = this.slot == null ? equipmentSlotType : this.slot;
		MCUtils.addAttributeModifierToItemStack(itemStack, attribute, modifier, modifierSlot);

		//TODO Bug, doesn't work as getAttributes doesn't take into account item modifiers
		this.fixHealth(entity);
	}

	@Override
	public String toString() {
		return String.format("ItemAttribute{uuid: %s, id: %s, modifier_name: %s, amount: %s, operation: %s, world_whitelist: %s, slot: %s}", uuid, id, modifierName, amount, operation, worldWhitelist, slot);
	}
}
