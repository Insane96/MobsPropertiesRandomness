package net.insane96mcp.mpr.events;

import java.util.Random;
import java.util.UUID;

import net.insane96mcp.mpr.MobsPropertiesRandomness;
import net.insane96mcp.mpr.json.Attribute;
import net.insane96mcp.mpr.json.Mob;
import net.insane96mcp.mpr.json.PotionEffect;
import net.insane96mcp.mpr.json.mobs.Creeper;
import net.insane96mcp.mpr.json.mobs.Ghast;
import net.insane96mcp.mpr.json.utils.Enchantment;
import net.insane96mcp.mpr.json.utils.Item;
import net.insane96mcp.mpr.json.utils.Slot;
import net.insane96mcp.mpr.lib.Logger;
import net.insane96mcp.mpr.lib.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID)
public class EntityJoinWorld { 
	
	@SubscribeEvent
	public static void EventEntityJoinWorld(EntityJoinWorldEvent event) {
		if (Mob.mobs.isEmpty())
			return;
		
		Entity entity = event.getEntity();
		World world = event.getWorld();
		Random random = world.rand;
		
		if (!(entity instanceof EntityLiving)) 
			return;
		
		EntityLiving entityLiving = (EntityLiving)entity;
		
		NBTTagCompound tags = entityLiving.getEntityData();
		byte isAlreadyChecked = tags.getByte(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked");

		if (isAlreadyChecked == 1)
			return;
		
		boolean shouldNotBeProcessed = tags.getBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "prevent_processing");
		
		if (shouldNotBeProcessed)
			return;
		
		ApplyPotionEffects(entityLiving, world, random);
		ApplyAttributeModifiers(entityLiving, world, random);
		ApplyEquipment(entityLiving, world, random);
		
		Creeper.Apply(entityLiving, world, random);
		Ghast.Apply(entityLiving, world, random);
		
		tags.setByte(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked", (byte)1);
	}
	
	private static boolean MatchesEntity(EntityLiving entity, World world, Random random, Mob mob) {
		if (mob.id.endsWith("*")) {
			String[] splitId = mob.id.split(":");
			if (splitId.length != 2) {
				Logger.Warning("Failed to parse mod domain from " + mob);
			}
			ResourceLocation location = EntityList.getKey(entity);
			if (location.getNamespace().toString().equals(splitId[0])) {
				return true;
			}
		}
		
		if (EntityList.isMatchingName(entity, new ResourceLocation(mob.id)))
			return true;
		
		return false;
		
	}
	
	private static void ApplyAttributeModifiers(EntityLiving entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (MatchesEntity(entity, world, random, mob)) {
				for (Attribute attribute : mob.attributes) {
					float min = attribute.modifier.min;
					float max = attribute.modifier.max;
					
					if (attribute.affectedByDifficulty) {
						
						EnumDifficulty difficulty = world.getDifficulty();
						
						if (!attribute.difficulty.isLocalDifficulty) {
							if (difficulty.equals(EnumDifficulty.EASY)) {
								if (!attribute.difficulty.affectsMaxOnly)
									min *= Properties.config.difficulty.easyMultiplier;
								max *= Properties.config.difficulty.easyMultiplier;
							}
							else if (difficulty.equals(EnumDifficulty.NORMAL)) {
								if (!attribute.difficulty.affectsMaxOnly)
									min *= Properties.config.difficulty.normalMultiplier;
								max *= Properties.config.difficulty.normalMultiplier;
							}
							else if (difficulty.equals(EnumDifficulty.HARD)) {
								if (!attribute.difficulty.affectsMaxOnly)
									min *= Properties.config.difficulty.hardMultiplier;
								max *= Properties.config.difficulty.hardMultiplier;
							}
						}
						else {
							if (!attribute.difficulty.affectsMaxOnly)
								min *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty();
							max *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty();
						}
						max *= attribute.difficulty.multiplier;
					}
					
					IAttributeInstance attributeInstance = entity.getAttributeMap().getAttributeInstanceByName(attribute.id);
					if (attributeInstance == null) {
						Logger.Warning("Attribute " + attribute.id + " not found for the entity, skipping the attribute");
						continue;
					}
					
					float amount = MathHelper.nextFloat(random, min, max);
					
					if (attribute.isFlat) {
						amount -= attributeInstance.getAttributeValue();
					}
					else {
						amount /= 100f;
					}
					
					AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), MobsPropertiesRandomness.RESOURCE_PREFIX + attribute.id, amount, attribute.isFlat ? 0 : 1);
					attributeInstance.applyModifier(modifier);
					
					//Health Fix
					if (attribute.id.equals("generic.maxHealth"))
						entity.setHealth((float) attributeInstance.getAttributeValue());
				}
				break;
			}
		}
	}

	private static void ApplyPotionEffects(EntityLiving entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (MatchesEntity(entity, world, random, mob)) {
				for (PotionEffect potionEffect : mob.potionEffects) {
					if (!potionEffect.chance.ChanceMatches(entity, world, random))
						continue;
					
					int minAmplifier = (int) potionEffect.amplifier.min;
					int maxAmplifier = (int) potionEffect.amplifier.max;
					
					Potion potion = Potion.getPotionFromResourceLocation(potionEffect.id);
					net.minecraft.potion.PotionEffect effect = new net.minecraft.potion.PotionEffect(potion, 1000000, MathHelper.getInt(random, minAmplifier, maxAmplifier), potionEffect.ambient, !potionEffect.hideParticles);
					entity.addPotionEffect(effect);
				}
				break;
			}
		}
	}

	private static void ApplyEquipment(EntityLiving entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (MatchesEntity(entity, world, random, mob)) {
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.head, EntityEquipmentSlot.HEAD);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.chest, EntityEquipmentSlot.CHEST);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.legs, EntityEquipmentSlot.LEGS);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.feets, EntityEquipmentSlot.FEET);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.mainHand, EntityEquipmentSlot.MAINHAND);
				ApplyEquipmentToSlot(entity, world, random, mob.equipment.offHand, EntityEquipmentSlot.OFFHAND);
				break;
			}
		}
	}
	
	private static void ApplyEquipmentToSlot(EntityLiving entity, World world, Random random, Slot slot, EntityEquipmentSlot entityEquipmentSlot) {
		if (slot == null)
			return;
		
		if (!slot.overrideVanilla && !entity.getItemStackFromSlot(entityEquipmentSlot).isEmpty())
			return;
		
		if (!slot.chance.ChanceMatches(entity, world, random))
			return;

		Item choosenItem = WeightedRandom.getRandomItem(random, slot.items);

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
		
		//Drop Chance
		entity.setDropChance(entityEquipmentSlot, choosenItem.dropChance / 100f);
		
	}
}
