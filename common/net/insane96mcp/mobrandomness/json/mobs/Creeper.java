package net.insane96mcp.mobrandomness.json.mobs;

import java.io.File;
import java.util.Random;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mobrandomness.MobsPropertiesRandomness;
import net.insane96mcp.mobrandomness.exceptions.InvalidJsonException;
import net.insane96mcp.mobrandomness.json.ChanceWithDifficulty;
import net.insane96mcp.mobrandomness.json.Mob;
import net.insane96mcp.mobrandomness.json.RangeMinMax;
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
	public float poweredChance;
	@SerializedName("powered_chance_with_difficulty")
	public ChanceWithDifficulty poweredChanceDifficulty;
	
	@Override
	public String toString() {
		return String.format("Creeper{fuse: %s, explosionRadius: %s, poweredChance: %f, poweredChanceWithDifficulty: %s}", fuse, explosionRadius, poweredChance, poweredChanceDifficulty);
	}
	
	public void Validate(final File file) throws InvalidJsonException{
		if (poweredChance > 0.0f && poweredChanceDifficulty != null) {
			MobsPropertiesRandomness.Debug("chance and chance_with_difficulty are both present, chance is set to 0 and will be ignored for " + this.toString());
			poweredChance = 0.0f;
		}
		if (poweredChanceDifficulty != null)
			poweredChanceDifficulty.Validate(file);
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
				
				//Powered
				if (random.nextFloat() < creeper.poweredChance / 100f) {
					compound.setBoolean("powered", true);
				}
				else if (creeper.poweredChance == 0.0f){
					float chance = creeper.poweredChanceDifficulty.chance;
					if (creeper.poweredChanceDifficulty.isLocalDifficulty) {
						chance *= world.getDifficultyForLocation(entityCreeper.getPosition()).getAdditionalDifficulty() * creeper.poweredChanceDifficulty.multiplier;
					}
					else {
						EnumDifficulty difficulty = world.getDifficulty();
						if (difficulty.equals(EnumDifficulty.EASY))
							chance *= 0.5f;
						else if (difficulty.equals(EnumDifficulty.HARD))
							chance *= 2.0f;
						
						chance *= creeper.poweredChanceDifficulty.multiplier;
					}
					
					if (random.nextFloat() < chance / 100f)
						compound.setBoolean("powered", true);
				}
				
				entityCreeper.readEntityFromNBT(compound);
			}
		}
	}
}
