package net.insane96mcp.mobrandomness.events.mobs.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class MobEquipment {
	public String mobName;
	public List<RNGEquipment> headEquipment;
	public List<RNGEquipment> chestEquipment;
	public List<RNGEquipment> legsEquipment;
	public List<RNGEquipment> feetEquipment;
	public List<RNGEquipment> handEquipment;
	
	@Override
	public String toString() {
		return String.format("mobName: %s, headEquipmentCount: %d, chestEquipmentCount: %d, legsEquipmentCount: %d, feetEquipmentCount: %d, handEquipmentCount: %d", this.mobName, headEquipment.size(), chestEquipment.size(), legsEquipment.size(), feetEquipment.size(), handEquipment.size());
	}
	
	public MobEquipment(String mobName) {
		this.mobName = mobName;
		headEquipment = new ArrayList<RNGEquipment>();
		chestEquipment = new ArrayList<RNGEquipment>();
		legsEquipment = new ArrayList<RNGEquipment>();
		feetEquipment = new ArrayList<RNGEquipment>();
		handEquipment = new ArrayList<RNGEquipment>();
	}
	
	public void AddEquipment(String itemName, float chance, EntityEquipmentSlot slot, List<RNGEnchantment> enchantments) {
		RNGEquipment equipment = new RNGEquipment(itemName, chance);
		
		switch (slot) {
		case HEAD:
			headEquipment.add(equipment);
			break;
		case CHEST:
			chestEquipment.add(equipment);
			break;
		case LEGS:
			legsEquipment.add(equipment);
			break;
		case FEET:
			feetEquipment.add(equipment);
			break;
		case MAINHAND:
			handEquipment.add(equipment);
			break;
		}
	}
	
	public ItemStack GetRandomItem(Random random, EntityEquipmentSlot slot) {
		switch (slot) {
		case HEAD:
			if (headEquipment.size() == 0)
				break;
			return headEquipment.get(random.nextInt(headEquipment.size())).GetItemEnchanted(random);
		case CHEST:
			if (chestEquipment.size() == 0)
				break;
			return chestEquipment.get(random.nextInt(chestEquipment.size())).GetItemEnchanted(random);
		case LEGS:
			if (legsEquipment.size() == 0)
				break;
			return legsEquipment.get(random.nextInt(legsEquipment.size())).GetItemEnchanted(random);
		case FEET:
			if (feetEquipment.size() == 0)
				break;
			return feetEquipment.get(random.nextInt(feetEquipment.size())).GetItemEnchanted(random);
		case MAINHAND:
			if (handEquipment.size() == 0)
				break;
			return handEquipment.get(random.nextInt(handEquipment.size())).GetItemEnchanted(random);
		}
		return ItemStack.EMPTY;
	}
	
	public class RNGEquipment {
		public String itemName;
		public List<RNGEnchantment> enchantmentList;
		
		public float chance;
		
		public RNGEquipment(String itemName, float chance) {
			this.itemName = itemName;
			
			enchantmentList = new ArrayList<RNGEnchantment>();
			
			this.chance = chance;
		}
		
		public void AddEnchantment(float chance, String enchantmentId, short minLevel, short maxLevel) {
			RNGEnchantment rngEnchantment = new RNGEnchantment(chance, enchantmentId, minLevel, maxLevel);
			enchantmentList.add(rngEnchantment);
		}
		
		public ItemStack GetItemEnchanted(Random random) {
			ItemStack itemStack = new ItemStack(Item.getByNameOrId(itemName), 1);
			if (enchantmentList.isEmpty())
				return itemStack;
			for (RNGEnchantment enchantment : enchantmentList) {
				enchantment.EnchantRandomly(random, itemStack);
			}
			return itemStack;
		}
		
		@Override
		public String toString() {
			return String.format("itemName: %s, enchantmentCount: %d", this.itemName, this.enchantmentList.size());
		}
	}
	
	public class RNGEnchantment{
		public String enchantmentId;
		public short minLevel;
		public short maxLevel;
		
		public float chance;
		
		public RNGEnchantment(float chance, String enchantmentId, short minLevel, short maxLevel) {
			this.enchantmentId = enchantmentId;
			this.minLevel = minLevel;
			this.maxLevel = maxLevel;
			
			this.chance = chance;
		}
		
		public void EnchantRandomly(Random random, ItemStack itemStack) {
			if (random.nextFloat() < this.chance / 100f)
				itemStack.addEnchantment(GetEnchantment(), MathHelper.getInt(random, this.minLevel, this.maxLevel - 1));
		}
		
		private Enchantment GetEnchantment() {
			return Enchantment.getEnchantmentByLocation(this.enchantmentId);
		}
	}
}
