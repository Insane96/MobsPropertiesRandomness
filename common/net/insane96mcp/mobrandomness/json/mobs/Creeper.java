package net.insane96mcp.mobrandomness.json.mobs;

import java.io.File;
import java.util.Random;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mobrandomness.exceptions.InvalidJsonException;
import net.insane96mcp.mobrandomness.json.Mob;
import net.insane96mcp.mobrandomness.json.utils.Chance;
import net.insane96mcp.mobrandomness.json.utils.RangeMinMax;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class Creeper {
	public RangeMinMax fuse;
	@SerializedName("explosion_radius")
	public RangeMinMax explosionRadius;
	@SerializedName("powered_chance")
	public Chance poweredChance;
	
	@Override
	public String toString() {
		return String.format("Creeper{fuse: %s, explosionRadius: %s, poweredChance: %s}", fuse, explosionRadius, poweredChance);
	}
	
	public void Validate(final File file) throws InvalidJsonException{
		if (poweredChance != null)
			poweredChance.Validate(file);
	}
	
	public static void Apply(Entity entity, World world, Random random) {		
		if (world.isRemote)
			return;
		
		if (!(entity instanceof EntityCreeper)) 
			return;
		
		EntityCreeper entityCreeper = (EntityCreeper)entity;
		
		for (Mob mob : Mob.mobs) {
			if (mob.creeper == null)
				continue;
			
			if (EntityList.isMatchingName(entityCreeper, new ResourceLocation(mob.id))) {
				Creeper creeper = mob.creeper;
				
				NBTTagCompound compound = new NBTTagCompound();
				
				//Fuse
				if (mob.creeper.fuse != null) {
					int minFuse = (int) mob.creeper.fuse.min;
					int maxFuse = (int) mob.creeper.fuse.max;
					int fuse = MathHelper.getInt(random, minFuse, maxFuse);
					compound.setShort("Fuse", (short)fuse);
				}
				
				//Explosion Radius
				if (mob.creeper.explosionRadius != null) {
					int minExplosionRadius = (int) mob.creeper.explosionRadius.min;
					int maxExplosionRadius = (int) mob.creeper.explosionRadius.max;
					int explosionRadius = MathHelper.getInt(random, minExplosionRadius, maxExplosionRadius);
					compound.setByte("ExplosionRadius", (byte) explosionRadius);
				}
				
				//Power It
				if (creeper.poweredChance.affectedByDifficulty) {
					float chance = creeper.poweredChance.amount;
					if (creeper.poweredChance.isLocalDifficulty) {
						chance *= world.getDifficultyForLocation(entityCreeper.getPosition()).getAdditionalDifficulty() * creeper.poweredChance.multiplier;
					}
					else {
						EnumDifficulty difficulty = world.getDifficulty();
						if (difficulty.equals(EnumDifficulty.EASY))
							chance *= 0.5f;
						else if (difficulty.equals(EnumDifficulty.HARD))
							chance *= 2.0f;
						
						chance *= creeper.poweredChance.multiplier;
					}
					
					if (random.nextFloat() < chance / 100f)
						compound.setBoolean("powered", true);
				}
				else if (random.nextFloat() < creeper.poweredChance.amount / 100f) {
					compound.setBoolean("powered", true);
				}
				
				entityCreeper.readEntityFromNBT(compound);
			}
		}
	}
}
