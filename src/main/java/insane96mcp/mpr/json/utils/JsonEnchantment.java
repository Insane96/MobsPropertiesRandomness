package insane96mcp.mpr.json.utils;

import com.google.common.collect.Lists;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.utils.Logger;
import insane96mcp.mpr.utils.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.List;
import java.util.Random;

public class JsonEnchantment implements IJsonObject{

	public String id;
	public RangeMinMax level;
	public Chance chance;
	
	@Override
	public String toString() {
		return String.format("Enchantment{id: %s, level: %s, chance: %s}", id, level, chance);
	}

	public void validate(final File file) throws InvalidJsonException{
		if (id == null)
			throw new InvalidJsonException("Missing Enchantment ID for " + this, file);
		else if (!id.equals("random") && !ForgeRegistries.ENCHANTMENTS.containsKey(new ResourceLocation(id)))
			Logger.warning("Failed to find enchantment with id " + id);
		
		if (level != null)
			level.validate(file);
		else {
			Logger.debug("Missing Enchantment Level for " + this + ". Will default to 1");
			level = new RangeMinMax(1, 1);
		}
		
		if (chance != null) 
			chance.validate(file);
		else
			throw new InvalidJsonException("Missing chance for " + this, file);
		
	}
	
	public static void apply(MobEntity entity, World world, Random random, JsonItem item, ItemStack itemStack) {
		for (JsonEnchantment jEnchantment : item.enchantments) {
			if (!jEnchantment.chance.chanceMatches(entity, world, random))
				continue;

			if (jEnchantment.id.equals("random")) {
	            List<Enchantment> list = Lists.newArrayList();

	            for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()) {
	                if (itemStack.getItem() == Items.ENCHANTED_BOOK || enchantment.canApply(itemStack)) {
	                    list.add(enchantment);
	                }
	            }

	            if (list.isEmpty()) {
	                Logger.warning("Couldn't find a compatible enchantment for " + item);
	                continue;
	            }

	            else {
		            Enchantment choosenEnch = list.get(random.nextInt(list.size()));
		            int level = Utils.getRandomInt(random, choosenEnch.getMinLevel(), choosenEnch.getMaxLevel());
	            	if (itemStack.getItem() == Items.ENCHANTED_BOOK)
	                    EnchantedBookItem.addEnchantment(itemStack, new EnchantmentData(choosenEnch, level));
	                else
	                	itemStack.addEnchantment(choosenEnch, level);
	            }
			}
			else {
				int level = Utils.getRandomInt(random, (int)jEnchantment.level.getMin(), (int)jEnchantment.level.getMax());
				itemStack.addEnchantment(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(jEnchantment.id)), level);
			}
		}
	}
	
}
