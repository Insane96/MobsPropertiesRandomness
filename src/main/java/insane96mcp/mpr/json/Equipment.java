package insane96mcp.mpr.json;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.utils.*;
import insane96mcp.mpr.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.Random;

public class Equipment implements IJsonObject{

	public Slot head;
	public Slot chest;
	public Slot legs;
	public Slot feets;
	@SerializedName("main_hand")
	public Slot mainHand;
	@SerializedName("off_hand")
	public Slot offHand;
	
	@Override
	public String toString() {
		return String.format("Equipment{head: %s, chest: %s, legs: %s, feets: %s, mainHand: %s, offHand: %s}", head, chest, legs, feets, mainHand, offHand);
	}

	public void validate(final File file) throws InvalidJsonException{
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

	public static void apply(MobEntity entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (JsonUtils.matchesEntity(entity, world, random, mob)) {
				applyEquipmentToSlot(entity, world, random, mob.equipment.head, EquipmentSlotType.HEAD);
				applyEquipmentToSlot(entity, world, random, mob.equipment.chest, EquipmentSlotType.CHEST);
				applyEquipmentToSlot(entity, world, random, mob.equipment.legs, EquipmentSlotType.LEGS);
				applyEquipmentToSlot(entity, world, random, mob.equipment.feets, EquipmentSlotType.FEET);
				applyEquipmentToSlot(entity, world, random, mob.equipment.mainHand, EquipmentSlotType.MAINHAND);
				applyEquipmentToSlot(entity, world, random, mob.equipment.offHand, EquipmentSlotType.OFFHAND);
			}
		}
	}
	
	private static void applyEquipmentToSlot(MobEntity entity, World world, Random random, Slot slot, EquipmentSlotType entityEquipmentSlot) {
		if (slot == null)
			return;
		
		if (!slot.overrideVanilla && !entity.getItemStackFromSlot(entityEquipmentSlot).isEmpty())
			return;
		
		if (slot.replaceOnly && entity.getItemStackFromSlot(entityEquipmentSlot).isEmpty())
			return;
		
		if (!slot.chance.chanceMatches(entity, world, random))
			return;

		JsonItem choosenItem = slot.getRandomItem(world, entity.getPosition());
		if (choosenItem == null)
			return;

		ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(choosenItem.id)), 1);

		CompoundNBT tag = new CompoundNBT();
		
		if (choosenItem.nbt != null) {
			try {
				tag = JsonToNBT.getTagFromJson(choosenItem.nbt);
			} catch (CommandSyntaxException e) {
				Logger.error("Failed to parse NBT for " + choosenItem);
				e.printStackTrace();
			}
		}

		CompoundNBT tagCompound = new CompoundNBT();
		tagCompound.put("tag", tag);
		
		itemStack.deserializeNBT(tagCompound);
			
		JsonEnchantment.apply(entity, world, random, choosenItem, itemStack);
		
		entity.setItemStackToSlot(entityEquipmentSlot, itemStack);
	
		for (ItemAttribute itemAttribute : choosenItem.attributes) {
			float amount = MathHelper.nextFloat(random, itemAttribute.amount.getMin(), itemAttribute.amount.getMax()) / 100f;
			//TODO Change this into a better operation and maybe remove the uuid
			AttributeModifier modifier = new AttributeModifier(itemAttribute.id, itemAttribute.modifier, (double) amount, AttributeModifier.Operation.byId(itemAttribute.operation.ordinal()));
			EquipmentSlotType modifierSlot = itemAttribute.slot == null ? entityEquipmentSlot : itemAttribute.slot;
			itemStack.addAttributeModifier(itemAttribute.attributeName, modifier, modifierSlot);
		}
		
		//Drop Chance
		entity.setDropChance(entityEquipmentSlot, choosenItem.dropChance / 100f);
		
	}
}
