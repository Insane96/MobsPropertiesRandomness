package net.insane96mcp.mobrandomness.events;

import java.util.Random;

import com.google.gson.Gson;

import net.insane96mcp.mobrandomness.events.mobs.RNGCreeper;
import net.insane96mcp.mobrandomness.events.mobs.RNGEntity;
import net.insane96mcp.mobrandomness.events.mobs.RNGGhast;
import net.insane96mcp.mobrandomness.events.mobs.RNGSkeleton;
import net.insane96mcp.mobrandomness.lib.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityJoinWorld { 
	
	@SubscribeEvent
	public static void EventEntityJoinWorld(EntityJoinWorldEvent event) {
		
		Entity entity = event.getEntity();
		World world = entity.getEntityWorld();
		Random random = world.rand;
		
		if (world.isRemote)
			return;
		
		float multiplier = 1.0f;
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
		
		RNGEntity.Attributes(living, SharedMonsterAttributes.MAX_HEALTH, Properties.Stats.health, multiplier, random);
		RNGEntity.Attributes(living, SharedMonsterAttributes.MOVEMENT_SPEED, Properties.Stats.movementSpeed, multiplier, random);
		RNGEntity.Attributes(living, SharedMonsterAttributes.FOLLOW_RANGE, Properties.Stats.followRange, multiplier, random);
		RNGEntity.Attributes(living, SharedMonsterAttributes.ATTACK_DAMAGE, Properties.Stats.attackDamage, multiplier, random);
		
		RNGEntity.Equipment(living, EntityEquipmentSlot.MAINHAND, Properties.Equipment.handEquipment, multiplier, random);
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

		tags.setByte("mobrandomizzation:check", (byte)1);
	}
}
