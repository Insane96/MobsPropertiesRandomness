package net.insane96mcp.mobrandomness.events.mobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.insane96mcp.mobrandomness.events.mobs.utils.MobPotionEffect;
import net.insane96mcp.mobrandomness.events.mobs.utils.MobPotionEffect.RNGPotionEffect;
import net.insane96mcp.mobrandomness.lib.Properties;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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
	
	private static List<MobPotionEffect> mobPotionEffects = new ArrayList<MobPotionEffect>();
	
	private static void LoadPotionEffects(String[] potionEffects) {		
		if (potionEffects.length == 0)
			return;
		
		for (String potionEffect : potionEffects) {
			String mobName = potionEffect.split(",")[0];
			float chance = Float.parseFloat(potionEffect.split(",")[1]);
			String id = potionEffect.split(",")[2];
			int minAmplifier = Integer.parseInt(potionEffect.split(",")[3]);
			int maxAmplifier = Integer.parseInt(potionEffect.split(",")[4]);
			boolean ambientParticles = Boolean.parseBoolean(potionEffect.split(",")[5]);
			boolean showParticles = Boolean.parseBoolean(potionEffect.split(",")[6]);
			
			boolean found = false;
			for (MobPotionEffect mobPotionEffect : mobPotionEffects) {
				if (mobName.equals(mobPotionEffect.mobName)) {
					mobPotionEffect.AddPotionEffect(chance, id, minAmplifier, maxAmplifier, showParticles, ambientParticles);
					found = true;
					break;
				}
			}
			if (!found) {
				MobPotionEffect newMobPotionEffect = new MobPotionEffect(mobName);
				newMobPotionEffect.AddPotionEffect(chance, id, minAmplifier, maxAmplifier, showParticles, ambientParticles);
				mobPotionEffects.add(newMobPotionEffect);
			}
		}
	}

	public static void PotionEffects(EntityLiving living, String[] potionEffects, Random random) {
		if (potionEffects.length == 0)
			return;
		
		if (mobPotionEffects.isEmpty())
			LoadPotionEffects(potionEffects);
		
		ResourceLocation mobResourceLocation;
		for (MobPotionEffect mobPotionEffect : mobPotionEffects) {
			mobResourceLocation = new ResourceLocation(mobPotionEffect.mobName);
			if (EntityList.isMatchingName(living, mobResourceLocation)) {
				for (RNGPotionEffect rngPotionEffect : mobPotionEffect.potionEffects) {
					if (random.nextFloat() > rngPotionEffect.chance / 100f)
						continue;
					Potion potion = Potion.getPotionFromResourceLocation(rngPotionEffect.id);
					PotionEffect potionEffect = new PotionEffect(potion, 100000, MathHelper.getInt(random, rngPotionEffect.minAmplifier, rngPotionEffect.maxAmplifier - 1), rngPotionEffect.ambientParticles, rngPotionEffect.showParticles);
					living.addPotionEffect(potionEffect);
				}
				break;
			}
		}
	}
}
