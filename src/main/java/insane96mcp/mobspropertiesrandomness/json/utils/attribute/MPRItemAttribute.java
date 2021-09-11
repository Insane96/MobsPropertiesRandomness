package insane96mcp.mobspropertiesrandomness.json.utils.attribute;

import insane96mcp.insanelib.utils.RandomHelper;
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
		if (world.isRemote)
			return;

		if (worldWhitelist != null && worldWhitelist.isWhitelisted(entity))
			return;

		float min = this.amount.getMin(entity, world);
		float max = this.amount.getMax(entity, world);

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.attributeId));
		ModifiableAttributeInstance attributeInstance = entity.getAttribute(attribute);
		if (attributeInstance == null) {
			Logger.warn("Attribute " + this.attributeId + " not found for the entity, skipping the attribute");
			return;
		}

		float amount = RandomHelper.getFloat(world.rand, min, max);

		AttributeModifier modifier = new AttributeModifier(this.modifierName, amount, this.operation);
		EquipmentSlotType modifierSlot = this.slot == null ? equipmentSlotType : this.slot;
		itemStack.addAttributeModifier(attribute, modifier, modifierSlot);
	}

	@Override
	public String toString() {
		return String.format("ItemAttribute{uuid: %s, attribute_id: %s, modifier_name: %s, amount: %s, operation: %s, world_whitelist: %s, slot: %s}", uuid, attributeId, modifierName, amount, operation, worldWhitelist, slot);
	}
}