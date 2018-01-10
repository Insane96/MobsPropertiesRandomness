package net.insane96mcp.mobrandomness.events.mobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.text.html.parser.Entity;

import org.lwjgl.Sys;

import net.insane96mcp.mobrandomness.events.mobs.utils.MobEquipment;
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

public class RNGEntity {

	private static List<MobEquipment> mobEquipments = new ArrayList<MobEquipment>();
	
	private static void LoadEquipment(String[] equipments, EntityEquipmentSlot slot) {
		String[] split;
		String mobName;
		float chance;
	
		for (String equipment : equipments) {
			if (equipment.equals(""))
				continue;
			
			try {
				split = equipment.split(",");
				mobName = split[0];
				chance = Float.parseFloat(split[1]);
			}
			catch (Exception e) {
				System.err.println("Failed to parse equipment \"" + equipment + "\": " + e.getMessage());
				continue;
			}
			
			boolean found = false;
			for (MobEquipment mobEquipment : mobEquipments) {
				if (mobName.equals(mobEquipment.mobName)) {
					for (int i = 2; i < split.length; i++) {
						String itemName = split[i];
						mobEquipment.AddEquipment(itemName, chance, slot, null);
						System.out.println("Added: " + mobEquipment);
					}
					found = true;
					break;
				}
			}
			if (!found) {
				MobEquipment newMobEquipment = new MobEquipment(mobName);
				for (int i = 2; i < split.length; i++) {
					String itemName = split[i];
					newMobEquipment.AddEquipment(itemName, chance, slot, null);
					mobEquipments.add(newMobEquipment);
					System.out.println("Created and added: " + newMobEquipment);
				}
				mobEquipments.add(newMobEquipment);
			}
			
			
		}
	}
	
	public static void Equipment(EntityLiving living, EntityEquipmentSlot equipmentSlot, String[] equipment, float multiplier, Random random){
		System.out.println("Checking slot: " + equipmentSlot);
		
		if (equipment.length == 0)
			return;
		
		mobEquipments = new ArrayList<MobEquipment>();
		
		if (mobEquipments.isEmpty()) {
			LoadEquipment(equipment, EntityEquipmentSlot.HEAD);
			LoadEquipment(equipment, EntityEquipmentSlot.CHEST);
			LoadEquipment(equipment, EntityEquipmentSlot.LEGS);
			LoadEquipment(equipment, EntityEquipmentSlot.FEET);
			LoadEquipment(equipment, EntityEquipmentSlot.MAINHAND);
		}
		
		ResourceLocation mobResourceLocation;
		for (MobEquipment mobEquipment : mobEquipments) {
			mobResourceLocation = new ResourceLocation(mobEquipment.mobName);
			if (EntityList.isMatchingName(living, mobResourceLocation)) {
				ItemStack itemStack = mobEquipment.GetRandomItem(random, equipmentSlot);
				System.out.println("Itemstack: " + itemStack);
				if (itemStack.equals(ItemStack.EMPTY))
					break;

				living.setItemStackToSlot(equipmentSlot, itemStack);
				
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
		String mobName;
		float chance;
		String id;
		int minAmplifier;
		int maxAmplifier;
		boolean ambientParticles;
		boolean showParticles;
		
		for (String potionEffect : potionEffects) {
			if (potionEffect.equals(""))
				continue;
			try {	
				mobName = potionEffect.split(",")[0];
				chance = Float.parseFloat(potionEffect.split(",")[1]);
				id = potionEffect.split(",")[2];
				minAmplifier = Integer.parseInt(potionEffect.split(",")[3]);
				maxAmplifier = Integer.parseInt(potionEffect.split(",")[4]);
				ambientParticles = Boolean.parseBoolean(potionEffect.split(",")[5]);
				showParticles = Boolean.parseBoolean(potionEffect.split(",")[6]);
			} catch (Exception e) {
				System.err.println("Failed to parse potion line \"" + potionEffect + "\": " + e.getMessage());
				continue;
			}
			
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
