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

		if ((!slot.override && !entity.getItemBySlot(equipmentSlotType).isEmpty())
			|| (slot.replaceOnly && entity.getItemBySlot(equipmentSlotType).isEmpty()))
			return;

		if (slot.chance != null && entity.level().random.nextFloat() >= slot.chance.getValue(entity))
			return;

		MPRItem choosenItem = slot.getRandomItem(entity);
		if (choosenItem == null)
			return;

		ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(choosenItem.id)), 1);

		if (choosenItem.nbt != null) {
			itemStack.setTag(choosenItem.getNBT());
		}

		if (choosenItem.enchantments != null) {
			for (MPREnchantment enchantment : choosenItem.enchantments) {
				enchantment.applyToStack(entity, itemStack);
			}
		}

		/*if (choosenItem.ticonModifiers != null) {
			for (MPRTiConModifier tiConModifier : choosenItem.ticonModifiers) {
				itemStack = tiConModifier.applyToStack(entity, level, itemStack);
			}
		}

		if (choosenItem.ticonMaterials != null) {
			itemStack = choosenItem.ticonMaterials.applyToStack(entity, level, itemStack);
		}*/

		if (choosenItem.attributes != null) {
			for (MPRItemAttribute itemAttribute : choosenItem.attributes) {
				itemAttribute.applyToStack(entity, itemStack, equipmentSlotType);
			}
		}

		entity.setItemSlot(equipmentSlotType, itemStack);

		//Drop Chance
		if (choosenItem.dropChance != null && entity instanceof Mob)
			((Mob) entity).setDropChance(equipmentSlotType, choosenItem.dropChance.getValue(entity));
	}

	@Override
	public String toString() {
		return String.format("Equipment{head: %s, chest: %s, legs: %s, feet: %s, main_hand: %s, off_hand: %s}", head, chest, legs, feet, mainHand, offHand);
	}
}
