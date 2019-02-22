package insane96mcp.mpr.json.utils;

import java.io.File;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.lib.Logger;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Enchantment implements IJsonObject{

	public String id;
	public RangeMinMax level;
	public Chance chance;
	
	@Override
	public String toString() {
		return String.format("Enchantment{id: %s, level: %s, chance: %s}", id, level, chance);
	}

	public void Validate(final File file) throws InvalidJsonException{
		if (id == null)
			throw new InvalidJsonException("Missing Enchantment ID for " + this, file);
		else if (id != "random" && net.minecraft.enchantment.Enchantment.getEnchantmentByLocation(id) == null)
			Logger.Warning("Failed to find enchantment with id " + id);
		
		if (level != null)
			level.Validate(file);
		else {
			Logger.Debug("Missing Enchantment Level for " + this + ". Will default to 1");
			level = new RangeMinMax(1, 1);
		}
		
		if (chance != null) 
			chance.Validate(file);
		else
			throw new InvalidJsonException("Missing chance for " + this, file);
		
	}
	
	public static void Apply(EntityLiving entity, World world, Random random, Item item, ItemStack itemStack) {
		for (Enchantment enchantment : item.enchantments) {
			if (!enchantment.chance.ChanceMatches(entity, world, random))
				continue;

			if (enchantment.id.equals("random")) {
	            List<net.minecraft.enchantment.Enchantment> list = Lists.<net.minecraft.enchantment.Enchantment>newArrayList();

	            for (net.minecraft.enchantment.Enchantment ench : net.minecraft.enchantment.Enchantment.REGISTRY) {
	                if (itemStack.getItem() == Items.ENCHANTED_BOOK || ench.canApply(itemStack)) {
	                    list.add(ench);
	                }
	            }

	            if (list.isEmpty()) {
	                Logger.Warning("Couldn't find a compatible enchantment for " + item);
	                continue;
	            }

	            else {
		            net.minecraft.enchantment.Enchantment choosen = list.get(random.nextInt(list.size()));
		            int level = MathHelper.getInt(random, choosen.getMinLevel(), choosen.getMaxLevel());
	            	if (itemStack.getItem() == Items.ENCHANTED_BOOK)
	                    ItemEnchantedBook.addEnchantment(itemStack, new EnchantmentData(choosen, level));
	                else
	                	itemStack.addEnchantment(choosen, level);
	            }
			}
			else {
				int level = MathHelper.getInt(random, (int)enchantment.level.GetMin(), (int)enchantment.level.GetMax());
				itemStack.addEnchantment(net.minecraft.enchantment.Enchantment.getEnchantmentByLocation(enchantment.id), level);
			}
		}
	}
	
}
