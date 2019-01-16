package net.insane96mcp.mpr.json;

import java.io.File;
import java.util.Random;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.json.utils.Enchantment;
import net.insane96mcp.mpr.json.utils.Item;
import net.insane96mcp.mpr.json.utils.ItemAttribute;
import net.insane96mcp.mpr.json.utils.Slot;
import net.insane96mcp.mpr.json.utils.Utils;
import net.insane96mcp.mpr.lib.Logger;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

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

	public void Validate(final File file) throws InvalidJsonException{
		if (head != null)
			head.Validate(file);
		if (chest != null)
			chest.Validate(file);
		if (legs != null)
			legs.Validate(file);
		if (feets != null)
			feets.Validate(file);
		if (mainHand != null)
			mainHand.Validate(file);
		if (offHand != null)
			offHand.Validate(file);
	}

	public static void Apply(EntityLiving entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (Utils.MatchesEntity(entity, world, random, mob)) {
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.head, EntityEquipmentSlot.HEAD);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.chest, EntityEquipmentSlot.CHEST);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.legs, EntityEquipmentSlot.LEGS);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.feets, EntityEquipmentSlot.FEET);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.mainHand, EntityEquipmentSlot.MAINHAND);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.offHand, EntityEquipmentSlot.OFFHAND);
			}
		}
	}
	
	private static void ApplyEquipmentToSlot(EntityLiving entity, World world, Random random, Slot slot, EntityEquipmentSlot entityEquipmentSlot) {
		if (slot == null)
			return;
		
		if (!slot.overrideVanilla && !entity.getItemStackFromSlot(entityEquipmentSlot).isEmpty())
			return;
		
		if (slot.replaceOnly && entity.getItemStackFromSlot(entityEquipmentSlot).isEmpty())
			return;
		
		if (!slot.chance.ChanceMatches(entity, world, random))
			return;

		Item choosenItem = slot.GetRandomItem(world);

		ItemStack itemStack = new ItemStack(net.minecraft.item.Item.getByNameOrId(choosenItem.id), 1, choosenItem.data);

		NBTTagCompound tag = new NBTTagCompound();
		
		if (choosenItem.nbt != null) {
			try {
				tag = JsonToNBT.getTagFromJson(choosenItem.nbt);
			} catch (NBTException e) {
				Logger.Error("Failed to parse NBT for " + choosenItem);
				e.printStackTrace();
			}
		}
		
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setTag("tag", tag);
		
		itemStack.deserializeNBT(tagCompound);
		
		for (Enchantment enchantment : choosenItem.enchantments) {
			if (!enchantment.chance.ChanceMatches(entity, world, random))
				continue;
			int level = MathHelper.getInt(random, (int)enchantment.level.min, (int)enchantment.level.max);
			itemStack.addEnchantment(net.minecraft.enchantment.Enchantment.getEnchantmentByLocation(enchantment.id), level);
		}
		
		entity.setItemStackToSlot(entityEquipmentSlot, itemStack);
	
		for (ItemAttribute itemAttribute : choosenItem.attributes) {
			float amount = MathHelper.nextFloat(random, itemAttribute.amount.min, itemAttribute.amount.max) / 100f;
			AttributeModifier modifier = new AttributeModifier(itemAttribute.id, itemAttribute.modifier, amount, itemAttribute.operation.ordinal());
			EntityEquipmentSlot modifierSlot = itemAttribute.slot == null ? entityEquipmentSlot : itemAttribute.slot;
			itemStack.addAttributeModifier(itemAttribute.attributeName, modifier, modifierSlot);
		}
		
		//Drop Chance
		entity.setDropChance(entityEquipmentSlot, choosenItem.dropChance / 100f);
		
	}
}
