package insane96mcp.mobspropertiesrandomness.json.utils.attribute;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
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

	public void applyToStack(MobEntity entity, World world, ItemStack itemStack, EquipmentSlotType equipmentSlotType) {
		if (!this.shouldApply(entity, world))
			return;

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.id));
		ModifiableAttributeInstance attributeInstance = entity.getAttribute(attribute);
		if (attributeInstance == null) {
			Logger.warn("Attribute " + this.id + " not found for the entity, skipping the attribute");
			return;
		}

		AttributeModifier modifier = new AttributeModifier(this.modifierName, this.amount.getFloatBetween(entity, world), this.operation);
		EquipmentSlotType modifierSlot = this.slot == null ? equipmentSlotType : this.slot;
		itemStack.addAttributeModifier(attribute, modifier, modifierSlot);

		this.fixHealth(entity);
	}

	@Override
	public String toString() {
		return String.format("ItemAttribute{uuid: %s, id: %s, modifier_name: %s, amount: %s, operation: %s, world_whitelist: %s, slot: %s}", uuid, id, modifierName, amount, operation, worldWhitelist, slot);
	}
}
