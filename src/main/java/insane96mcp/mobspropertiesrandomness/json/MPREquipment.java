package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.util.MPREnchantment;
import insane96mcp.mobspropertiesrandomness.json.util.MPRItem;
import insane96mcp.mobspropertiesrandomness.json.util.MPRSlot;
import insane96mcp.mobspropertiesrandomness.json.util.attribute.MPRItemAttribute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

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
	public void validate() throws JsonValidationException {
		if (head != null)
			head.validate();
		if (chest != null)
			chest.validate();
		if (legs != null)
			legs.validate();
		if (feets != null)
			feets.validate();
		if (mainHand != null)
			mainHand.validate();
		if (offHand != null)
			offHand.validate();
	}

	@Override
	public void apply(LivingEntity entity, Level world) {
		if (world.isClientSide)
			return;

		applyEquipmentToSlot(entity, world, this.head, EquipmentSlot.HEAD);
		applyEquipmentToSlot(entity, world, this.chest, EquipmentSlot.CHEST);
		applyEquipmentToSlot(entity, world, this.legs, EquipmentSlot.LEGS);
		applyEquipmentToSlot(entity, world, this.feets, EquipmentSlot.FEET);
		applyEquipmentToSlot(entity, world, this.mainHand, EquipmentSlot.MAINHAND);
		applyEquipmentToSlot(entity, world, this.offHand, EquipmentSlot.OFFHAND);
	}

	private void applyEquipmentToSlot(LivingEntity entity, Level world, MPRSlot slot, EquipmentSlot equipmentSlotType) {
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
		if (choosenItem.dropChance != null && entity instanceof Mob)
			((Mob) entity).setDropChance(equipmentSlotType, choosenItem.dropChance.getValue(entity, world));

	}

	@Override
	public String toString() {
		return String.format("Equipment{head: %s, chest: %s, legs: %s, feets: %s, main_hand: %s, off_hand: %s}", head, chest, legs, feets, mainHand, offHand);
	}
}
