package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.properties.attribute.MPRItemAttribute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MPREquipment implements IMPRObject {

	public MPRSlot head;
	public MPRSlot chest;
	public MPRSlot legs;
	public MPRSlot feet;
	@SerializedName("main_hand")
	public MPRSlot mainHand;
	@SerializedName("off_hand")
	public MPRSlot offHand;
	
	@Override
	public void validate() throws JsonValidationException {
		if (head != null)
			head.validate();
		if (chest != null)
			chest.validate();
		if (legs != null)
			legs.validate();
		if (feet != null)
			feet.validate();
		if (mainHand != null)
			mainHand.validate();
		if (offHand != null)
			offHand.validate();
	}

	public void apply(LivingEntity entity) {
		applyEquipmentToSlot(entity, this.head, EquipmentSlot.HEAD);
		applyEquipmentToSlot(entity, this.chest, EquipmentSlot.CHEST);
		applyEquipmentToSlot(entity, this.legs, EquipmentSlot.LEGS);
		applyEquipmentToSlot(entity, this.feet, EquipmentSlot.FEET);
		applyEquipmentToSlot(entity, this.mainHand, EquipmentSlot.MAINHAND);
		applyEquipmentToSlot(entity, this.offHand, EquipmentSlot.OFFHAND);
	}

	private void applyEquipmentToSlot(LivingEntity entity, MPRSlot slot, EquipmentSlot equipmentSlotType) {
		if (slot == null)
			return;

		if ((slot.keepSpawned && !entity.getItemBySlot(equipmentSlotType).isEmpty())
			|| (slot.replaceOnly && entity.getItemBySlot(equipmentSlotType).isEmpty()))
			return;

		if (slot.chance != null && entity.level().random.nextFloat() >= slot.chance.getValue(entity))
			return;

		MPRItem chosenItem = slot.getRandomItem(entity);
		if (chosenItem == null)
			return;
		//noinspection DataFlowIssue
		ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(chosenItem.id)), chosenItem.count);

		if (slot.nbt != null) {
			itemStack.setTag(slot.getNBT());
		}
		if (chosenItem.nbt != null) {
			itemStack.setTag(chosenItem.getNBT());
		}

		if (slot.enchantments != null) {
			for (MPREnchantment enchantment : slot.enchantments) {
				enchantment.applyToStack(entity, itemStack);
			}
		}
		if (chosenItem.enchantments != null) {
			for (MPREnchantment enchantment : chosenItem.enchantments) {
				enchantment.applyToStack(entity, itemStack);
			}
		}

		/*if (chosenItem.ticonModifiers != null) {
			for (MPRTiConModifier tiConModifier : chosenItem.ticonModifiers) {
				itemStack = tiConModifier.applyToStack(entity, level, itemStack);
			}
		}

		if (chosenItem.ticonMaterials != null) {
			itemStack = chosenItem.ticonMaterials.applyToStack(entity, level, itemStack);
		}*/

		if (slot.attributes != null) {
			for (MPRItemAttribute itemAttribute : slot.attributes) {
				itemAttribute.applyToStack(entity, itemStack, equipmentSlotType);
			}
		}
		if (chosenItem.attributes != null) {
			for (MPRItemAttribute itemAttribute : chosenItem.attributes) {
				itemAttribute.applyToStack(entity, itemStack, equipmentSlotType);
			}
		}

		entity.setItemSlot(equipmentSlotType, itemStack);

		//Drop Chance
		if (slot.dropChance != null && entity instanceof Mob)
			((Mob) entity).setDropChance(equipmentSlotType, slot.dropChance.getValue(entity));
		if (chosenItem.dropChance != null && entity instanceof Mob)
			((Mob) entity).setDropChance(equipmentSlotType, chosenItem.dropChance.getValue(entity));
	}

	@Override
	public String toString() {
		return String.format("Equipment{head: %s, chest: %s, legs: %s, feet: %s, main_hand: %s, off_hand: %s}", head, chest, legs, feet, mainHand, offHand);
	}
}
