package net.insane96mcp.mpr.json.mobs;

import java.util.Random;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.json.Mob;
import net.insane96mcp.mpr.json.utils.RangeMinMax;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Ghast {
	@SerializedName("explosion_power")
	public RangeMinMax explosionPower;
	
	@Override
	public String toString() {
		return String.format("Ghast{explosionPower: %s}", explosionPower);
	}
	
	public static void Apply(Entity entity, World world, Random random) {		
		if (world.isRemote)
			return;
		
		if (!(entity instanceof EntityGhast)) 
			return;
		
		EntityGhast entityGhast = (EntityGhast)entity;
		

		NBTTagCompound compound = new NBTTagCompound();
		entityGhast.writeEntityToNBT(compound);
		
		for (Mob mob : Mob.mobs) {
			if (mob.ghast == null)
				continue;
			
			if (EntityList.isMatchingName(entityGhast, new ResourceLocation(mob.id))) {
				
				//Explosion Power
				if (compound.getInteger("ExplosionPower") == 1) {
					int minExplosionPower = (int) mob.ghast.explosionPower.min;
					int maxExplosionPower = (int) mob.ghast.explosionPower.max;
					int explosionPower = MathHelper.getInt(random, minExplosionPower, maxExplosionPower);
					compound.setInteger("ExplosionPower", explosionPower);
				}
				
				entityGhast.readEntityFromNBT(compound);
			}
		}
	}
}
