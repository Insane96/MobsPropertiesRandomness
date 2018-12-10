package net.insane96mcp.mobrandomness.events;

import java.util.Random;
import java.util.UUID;

import net.insane96mcp.mobrandomness.json.Attribute;
import net.insane96mcp.mobrandomness.json.Mob;
import net.insane96mcp.mobrandomness.json.PotionEffect;
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
		byte isAlreadyChecked = tags.getByte("mobrandomizzation:check");

		if (isAlreadyChecked == 1)
			return;
		
		boolean shouldNotBeProcessed = tags.getBoolean("mobsrandomizzation:preventProcessing");
		
		if (shouldNotBeProcessed)
			return;

		ApplyPotionEffects(entity, world, random);
		ApplyModifiers(entity, world, random);
		
		tags.setByte("mobrandomizzation:check", (byte)1);
		
		/*float multiplier = 1.0f;
		if (Properties.difficultyWise)
			multiplier = world.getDifficulty() == EnumDifficulty.EASY ? Properties.difficultyMultiplierEasy : 
				world.getDifficulty() == EnumDifficulty.NORMAL ? Properties.difficultyMultiplierNormal : 
				world.getDifficulty() == EnumDifficulty.HARD ? Properties.difficultyMultiplierHard : 1.0f;
		
		float localMultiplier = 1.0f;
		if (Properties.localDifficultyWise)
			localMultiplier = world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty() * Properties.localDifficultyMultiplier;
		
		multiplier *= localMultiplier;
		
	
		if (!(entity instanceof EntityLiving)) 
			return;
		
		EntityLiving living = (EntityLiving)entity;
		
		NBTTagCompound tags = living.getEntityData();
		byte isAlreadyChecked = tags.getByte("mobrandomizzation:check");

		if (isAlreadyChecked == 1)
			return;
		
		boolean shouldNotBeProcessed = tags.getBoolean("mobsrandomizzation:preventProcessing");
		
		if (shouldNotBeProcessed)
			return;
		
		RNGEntity.Attributes(living, SharedMonsterAttributes.MAX_HEALTH, Properties.Stats.health, multiplier, random);
		RNGEntity.Attributes(living, SharedMonsterAttributes.MOVEMENT_SPEED, Properties.Stats.movementSpeed, multiplier, random);
		RNGEntity.Attributes(living, SharedMonsterAttributes.FOLLOW_RANGE, Properties.Stats.followRange, multiplier, random);
		RNGEntity.Attributes(living, SharedMonsterAttributes.ATTACK_DAMAGE, Properties.Stats.attackDamage, multiplier, random);
		RNGEntity.Attributes(living, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, Properties.Stats.knockbackResistance, multiplier, random);

		RNGEntity.Equipment(living, EntityEquipmentSlot.MAINHAND, Properties.Equipment.handEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.OFFHAND, Properties.Equipment.offHandEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.HEAD, Properties.Equipment.headEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.CHEST, Properties.Equipment.chestEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.LEGS, Properties.Equipment.legsEquipment, multiplier, random);
		RNGEntity.Equipment(living, EntityEquipmentSlot.FEET, Properties.Equipment.feetEquipment, multiplier, random);
		
		RNGEntity.PotionEffects(living, Properties.Stats.potionEffects, random);
		
		RNGCreeper.Fuse(living, random);
		RNGCreeper.ExplosionRadius(living, random);
		RNGCreeper.Powered(living, multiplier, random);
		
		RNGGhast.ExplosionPower(living, random);
		
		RNGSkeleton.TippedArrow(living, multiplier, random);
		
		RNGPigZombie.Aggro(living, multiplier, random);

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
					
					float amount = MathHelper.nextFloat(random, min, max);
					if (!attribute.isFlat) {
						amount /= 100f;
						amount += 1f;
					}

					AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),"mobspropertiesrandomness:" + attribute.id, amount, attribute.isFlat ? 0 : 1);
					IAttributeInstance attributeInstance = entityLiving.getAttributeMap().getAttributeInstanceByName(attribute.id);
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
					if (potionEffect.chance > 0.0f && random.nextFloat() > potionEffect.chance / 100f)
						continue;
					
					float chance = potionEffect.chanceWithDifficulty.chance;
					if (potionEffect.chanceWithDifficulty.isLocalDifficulty) {
						chance *= world.getDifficultyForLocation(entityLiving.getPosition()).getAdditionalDifficulty() * potionEffect.chanceWithDifficulty.multiplier;
					}
					else {
						EnumDifficulty difficulty = world.getDifficulty();
						if (difficulty.equals(EnumDifficulty.EASY))
							chance *= 0.5f;
						else if (difficulty.equals(EnumDifficulty.HARD))
							chance *= 2.0f;
						
						chance *= potionEffect.chanceWithDifficulty.multiplier;
					}
					
					if (random.nextFloat() > chance / 100f)
						continue;

					int minAmplifier = (int)potionEffect.amplifier.min;
					int maxAmplifier = (int)potionEffect.amplifier.max;
					
					Potion potion = Potion.getPotionFromResourceLocation(potionEffect.id);
					net.minecraft.potion.PotionEffect effect = new net.minecraft.potion.PotionEffect(potion, 100000, MathHelper.getInt(random, minAmplifier, maxAmplifier), potionEffect.ambient, !potionEffect.hideParticles);
					entityLiving.addPotionEffect(effect);
				}
			}
		}
	}
}
