package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.utils.MPREnchantment;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRItem;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRSlot;
import insane96mcp.mobspropertiesrandomness.json.utils.attribute.MPRItemAttribute;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;

public class MPREquipment implements IMPRObject, IMPRAppliable {

	public MPRSlot head;
	public MPRSlot chest;
	public MPRSlot legs;
	public MPRSlot feets;
	@SerializedName("main_hand")
	public MPRSlot mainHand;
	@SerializedName("off_hand")
	public MPRSlot offHand;
	
	@Override
	public void validate(File file) throws InvalidJsonException {
		if (head != null)
			head.validate(file);
		if (chest != null)
			chest.validate(file);
		if (legs != null)
			legs.validate(file);
		if (feets != null)
			feets.validate(file);
		if (mainHand != null)
			mainHand.validate(file);
		if (offHand != null)
			offHand.validate(file);
	}

	@Override
	public void apply(MobEntity entity, World world) {
		if (world.isClientSide)
			return;

		applyEquipmentToSlot(entity, world, this.head, EquipmentSlotType.HEAD);
		applyEquipmentToSlot(entity, world, this.chest, EquipmentSlotType.CHEST);
		applyEquipmentToSlot(entity, world, this.legs, EquipmentSlotType.LEGS);
		applyEquipmentToSlot(entity, world, this.feets, EquipmentSlotType.FEET);
		applyEquipmentToSlot(entity, world, this.mainHand, EquipmentSlotType.MAINHAND);
		applyEquipmentToSlot(entity, world, this.offHand, EquipmentSlotType.OFFHAND);
	}

	private void applyEquipmentToSlot(MobEntity entity, World world, MPRSlot slot, EquipmentSlotType equipmentSlotType) {
		if (slot == null)
			return;

		if ((!slot.override && !entity.getItemBySlot(equipmentSlotType).isEmpty())
			|| (slot.replaceOnly && entity.getItemBySlot(equipmentSlotType).isEmpty()))
			return;

		if (slot.chance != null && world.random.nextFloat() >= slot.chance.getValue(entity, world))
			return;

		MPRItem choosenItem = slot.getRandomItem(entity, world);
		if (choosenItem == null)
			return;

		ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(choosenItem.id)), 1);

		if (choosenItem.nbt != null) {
			itemStack.deserializeNBT(choosenItem.getNBT());
		}

		if (choosenItem.enchantments != null) {
			for (MPREnchantment enchantment : choosenItem.enchantments) {
				enchantment.applyToStack(entity, world, itemStack);
			}
		}

		if (choosenItem.attributes != null) {
			for (MPRItemAttribute itemAttribute : choosenItem.attributes) {
				itemAttribute.applyToStack(entity, world, itemStack, equipmentSlotType);
			}
		}

		entity.setItemSlot(equipmentSlotType, itemStack);

		//Drop Chance
		if (choosenItem.dropChance != null)
			entity.setDropChance(equipmentSlotType, choosenItem.dropChance.getValue(entity, world));

	}

	@Override
	public String toString() {
		return String.format("Equipment{head: %s, chest: %s, legs: %s, feets: %s, main_hand: %s, off_hand: %s}", head, chest, legs, feets, mainHand, offHand);
	}
}
