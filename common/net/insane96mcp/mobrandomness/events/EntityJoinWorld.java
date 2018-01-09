package net.insane96mcp.mobrandomness.events;

import java.util.Random;

import net.insane96mcp.mobrandomness.events.mobs.EventCreeper;
import net.insane96mcp.mobrandomness.events.mobs.EventEntity;
import net.insane96mcp.mobrandomness.events.mobs.EventGhast;
import net.insane96mcp.mobrandomness.events.mobs.EventSkeleton;
import net.insane96mcp.mobrandomness.lib.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

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
		
		EventEntity.Attributes(living, SharedMonsterAttributes.MAX_HEALTH, Properties.Stats.health, multiplier, random);
		EventEntity.Attributes(living, SharedMonsterAttributes.MOVEMENT_SPEED, Properties.Stats.movementSpeed, multiplier, random);
		EventEntity.Attributes(living, SharedMonsterAttributes.FOLLOW_RANGE, Properties.Stats.followRange, multiplier, random);
		EventEntity.Attributes(living, SharedMonsterAttributes.ATTACK_DAMAGE, Properties.Stats.attackDamage, multiplier, random);
		
		EventEntity.Equipment(living, EntityEquipmentSlot.MAINHAND, Properties.Equipment.handEquipment, multiplier, random);
		EventEntity.Equipment(living, EntityEquipmentSlot.HEAD, Properties.Equipment.helmetEquipment, multiplier, random);
		EventEntity.Equipment(living, EntityEquipmentSlot.CHEST, Properties.Equipment.chestplateEquipment, multiplier, random);
		EventEntity.Equipment(living, EntityEquipmentSlot.LEGS, Properties.Equipment.leggingsEquipment, multiplier, random);
		EventEntity.Equipment(living, EntityEquipmentSlot.FEET, Properties.Equipment.bootsEquipment, multiplier, random);
		
		EventEntity.PotionEffects(living, Properties.Stats.potionEffects, random);
		
		EventCreeper.Fuse(living, random);
		EventCreeper.ExplosionRadius(living, random);
		EventCreeper.Powered(living, multiplier, random);
		
		EventGhast.ExplosionPower(living, random);
		
		EventSkeleton.TippedArrow(living, multiplier, random);

		tags.setByte("mobrandomizzation:check", (byte)1);
	}
}
