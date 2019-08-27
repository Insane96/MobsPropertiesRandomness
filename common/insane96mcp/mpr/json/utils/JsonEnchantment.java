package insane96mcp.mpr.json.utils;

import java.io.File;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.lib.Logger;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class JsonEnchantment implements IJsonObject{

	public String id;
	public JsonRangeMinMax level;
	public JsonChance chance;
	
	@Override
	public String toString() {
		return String.format("Enchantment{id: %s, level: %s, chance: %s}", id, level, chance);
	}

	public void Validate(final File file) throws InvalidJsonException{
		if (id == null)
			throw new InvalidJsonException("Missing Enchantment ID for " + this, file);
		else if (id != "random" && Enchantment.getEnchantmentByLocation(id) == null)
			Logger.Warning("Failed to find enchantment with id " + id);
		
		if (level != null)
			level.Validate(file);
		else if (id != "random"){
			Logger.Debug("Missing Enchantment Level for " + this + ". Will default to 1");
			level = new JsonRangeMinMax(1, 1);
		}
		
		if (chance != null) 
			chance.Validate(file);
		else
			throw new InvalidJsonException("Missing chance for " + this, file);
		
	}
	
	public static void Apply(EntityLiving entity, World world, Random random, JsonItem item, ItemStack itemStack) {
		for (JsonEnchantment jEnchantment : item.enchantments) {
			if (!jEnchantment.chance.ChanceMatches(entity, world, random))
				continue;

			if (jEnchantment.id.equals("random")) {
	            List<Enchantment> list = Lists.<Enchantment>newArrayList();

	            for (Enchantment enchantment : Enchantment.REGISTRY) {
	                if (itemStack.getItem() == Items.ENCHANTED_BOOK || enchantment.canApply(itemStack)) {
	                    list.add(enchantment);
	                }
	            }

	            if (list.isEmpty()) {
	                Logger.Warning("Couldn't find a compatible enchantment for " + item);
	                continue;
	            }

	            else {
		            Enchantment choosenEnch = list.get(random.nextInt(list.size()));
		            int level = MathHelper.getInt(random, choosenEnch.getMinLevel(), choosenEnch.getMaxLevel());
	            	if (itemStack.getItem() == Items.ENCHANTED_BOOK)
	                    ItemEnchantedBook.addEnchantment(itemStack, new EnchantmentData(choosenEnch, level));
	                else
	                	itemStack.addEnchantment(choosenEnch, level);
	            }
			}
			else {
				int level = MathHelper.getInt(random, (int)jEnchantment.level.GetMin(), (int)jEnchantment.level.GetMax());
				itemStack.addEnchantment(Enchantment.getEnchantmentByLocation(jEnchantment.id), level);
			}
		}
	}
	
}
