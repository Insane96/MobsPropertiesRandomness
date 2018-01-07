package net.insane96mcp.mobrandomness.events.mobs;

import java.util.Random;

import net.insane96mcp.mobrandomness.lib.Properties;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class EventEntity {

	public static void Equipment(EntityLiving living, EntityEquipmentSlot equipmentSlot, String[] equipment, float multiplier, Random random){
		ResourceLocation mob_name;
		float chance;
		
		if (equipment.length == 0)
			return;
		
		for (String mob_modifier : equipment) {
			String[] split = mob_modifier.split(",");
			mob_name = new ResourceLocation(split[0]);
			if (EntityList.isMatchingName(living, mob_name)) {
				chance = Float.parseFloat(split[1]) * multiplier;
				
				if (random.nextFloat() > chance / 100f)
					break;
				
				if (!living.getItemStackFromSlot(equipmentSlot).isEmpty())
					break;
				
				ItemStack[] items = new ItemStack[split.length - 2];
				
				for (int i = 2; i < split.length; i++) {
					items[i - 2] = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(split[i])));
				}
				
				int item = MathHelper.getInt(random, 0, items.length - 1);
				
				living.setItemStackToSlot(equipmentSlot, items[item]);
				
				break;
			}
		}
	}
	
	public static void Attributes(EntityLiving living, IAttribute attribute, String[] stats, float multiplier, Random random) {
		ResourceLocation mob_name;
		float min_increase, max_increase;
		
		if (stats.length == 0)
			return;
		
		for (String mob_modifier : stats) {
			mob_name = new ResourceLocation(mob_modifier.split(",")[0]);
			if (EntityList.isMatchingName(living, mob_name)) {
				min_increase = Float.parseFloat(mob_modifier.split(",")[1]) * multiplier;
				max_increase = Float.parseFloat(mob_modifier.split(",")[2]) * multiplier;
				
				double attack_damage = living.getEntityAttribute(attribute).getBaseValue();
				float increase = MathHelper.nextFloat(random, min_increase, max_increase);
				
				if (Properties.Stats.valuesAsPercentage)
					attack_damage += attack_damage * increase / 100f;
				else
					attack_damage += increase;
				
				living.getEntityAttribute(attribute).setBaseValue(attack_damage);
				
				break;
			}
		}
	}

}
