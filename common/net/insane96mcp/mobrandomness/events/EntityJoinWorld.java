package net.insane96mcp.mobrandomness.events;

import java.util.Random;
import java.util.UUID;

import net.insane96mcp.mobrandomness.MobsPropertiesRandomness;
import net.insane96mcp.mobrandomness.json.Attribute;
import net.insane96mcp.mobrandomness.json.Mob;
import net.insane96mcp.mobrandomness.json.PotionEffect;
import net.insane96mcp.mobrandomness.json.mobs.Creeper;
import net.insane96mcp.mobrandomness.json.mobs.Ghast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
		
		ApplyPotionEffects(entity, world, random);
		ApplyModifiers(entity, world, random);
		
		Creeper.Apply(entity, world, random);
		Ghast.Apply(entityLiving, world, random);
		
		tags.setByte(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked", (byte)1);
		
		/*RNGEntity.Equipment(living, EntityEquipmentSlot.MAINHAND, Properties.Equipment.handEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.OFFHAND, Properties.Equipment.offHandEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.HEAD, Properties.Equipment.headEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.CHEST, Properties.Equipment.chestEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.LEGS, Properties.Equipment.legsEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.FEET, Properties.Equipment.feetEquipment, multiplier, random);
		
		RNGSkeleton.TippedArrow(living, multiplier, random);
		*/
	}
	
	private static void ApplyModifiers(Entity entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		if (!(entity instanceof EntityLiving)) 
			return;
		
		EntityLiving entityLiving = (EntityLiving)entity;
		
		for (Mob mob : Mob.mobs) {
			if (EntityList.isMatchingName(entityLiving, new ResourceLocation(mob.id))) {
				for (Attribute attribute : mob.attributes) {
					float min = attribute.modifier.min;
					float max = attribute.modifier.max;
					
					if (attribute.affectedByDifficulty) {
						
						EnumDifficulty difficulty = world.getDifficulty();
						
						if (!attribute.difficulty.isLocalDifficulty) {
							if (difficulty.equals(EnumDifficulty.EASY)) {
								if (!attribute.difficulty.affectsMaxOnly)
									min *= 0.5f;
								max *= 0.5f;
							}
							else if (difficulty.equals(EnumDifficulty.HARD)) {
								if (!attribute.difficulty.affectsMaxOnly)
									min *= 2.0f;
								max *= 2.0f;
							}
						}
						else {
							if (!attribute.difficulty.affectsMaxOnly)
								min *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty();
							max *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty();
						}
						max *= attribute.difficulty.multiplier;
					}
					
					IAttributeInstance attributeInstance = entityLiving.getAttributeMap().getAttributeInstanceByName(attribute.id);
					
					
					float amount = MathHelper.nextFloat(random, min, max);
					
					if (attribute.isFlat) {
						amount -= attributeInstance.getAttributeValue();
					}
					else {
						amount /= 100f;
						amount += 1f;
					}
					
					AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),"mobspropertiesrandomness:" + attribute.id, amount, attribute.isFlat ? 0 : 1);
					attributeInstance.applyModifier(modifier);
					
					//Health Fix
					if (attribute.id.equals("generic.maxHealth"))
						entityLiving.setHealth((float) attributeInstance.getAttributeValue());
				}
			}
		}
	}

	private static void ApplyPotionEffects(Entity entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		if (!(entity instanceof EntityLiving)) 
			return;
		
		EntityLiving entityLiving = (EntityLiving)entity;
		for (Mob mob : Mob.mobs) {
			if (EntityList.isMatchingName(entityLiving, new ResourceLocation(mob.id))) {
				for (PotionEffect potionEffect : mob.potionEffects) {
					float chance = potionEffect.chance.amount;
					if (potionEffect.chance.affectedByDifficulty) {
						if (potionEffect.chance.isLocalDifficulty) {
							chance *= world.getDifficultyForLocation(entityLiving.getPosition()).getAdditionalDifficulty() * potionEffect.chance.multiplier;
						}
						else {
							EnumDifficulty difficulty = world.getDifficulty();
							if (difficulty.equals(EnumDifficulty.EASY))
								chance *= 0.5f;
							else if (difficulty.equals(EnumDifficulty.HARD))
								chance *= 2.0f;
							
							chance *= potionEffect.chance.multiplier;
						}
					}
					
					if (random.nextFloat() > chance / 100f)
						continue;

					int minAmplifier = (int) potionEffect.amplifier.min;
					int maxAmplifier = (int) potionEffect.amplifier.max;
					
					Potion potion = Potion.getPotionFromResourceLocation(potionEffect.id);
					net.minecraft.potion.PotionEffect effect = new net.minecraft.potion.PotionEffect(potion, 1000000, MathHelper.getInt(random, minAmplifier, maxAmplifier), potionEffect.ambient, !potionEffect.hideParticles);
					entityLiving.addPotionEffect(effect);
				}
			}
		}
	}
}
