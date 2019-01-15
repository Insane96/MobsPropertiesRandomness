package net.insane96mcp.mpr.json.mobs;

import java.io.File;
import java.util.Random;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.json.IJsonObject;
import net.insane96mcp.mpr.json.Mob;
import net.insane96mcp.mpr.json.utils.Chance;
import net.insane96mcp.mpr.json.utils.RangeMinMax;
import net.insane96mcp.mpr.network.CreeperFuse;
import net.insane96mcp.mpr.network.PacketHandler;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Creeper implements IJsonObject {
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
	
	public static void Apply(EntityLiving entity, World world, Random random) {
		
		if (!(entity instanceof EntityCreeper)) 
			return;
		
		EntityCreeper entityCreeper = (EntityCreeper)entity;
		
		if (world.isRemote) {
			//Fix creeper fuse animation clientside
			PacketHandler.SendToServer(new CreeperFuse(entityCreeper.getEntityId()));
			
			return;
		}
		
		for (Mob mob : Mob.mobs) {
			if (mob.creeper == null)
				continue;
			
			if (EntityList.isMatchingName(entityCreeper, new ResourceLocation(mob.mobId))) {
				Creeper creeper = mob.creeper;
				
				NBTTagCompound compound = new NBTTagCompound();
				entityCreeper.writeEntityToNBT(compound);
				
				//Fuse
				if (mob.creeper.fuse != null && compound.getShort("Fuse") == 30) {
					int minFuse = (int) mob.creeper.fuse.min;
					int maxFuse = (int) mob.creeper.fuse.max;
					int fuse = MathHelper.getInt(random, minFuse, maxFuse);
					compound.setShort("Fuse", (short)fuse);
				}
				
				//Explosion Radius
				if (mob.creeper.explosionRadius != null && compound.getByte("ExplosionRadius") == 30) {
					int minExplosionRadius = (int) mob.creeper.explosionRadius.min;
					int maxExplosionRadius = (int) mob.creeper.explosionRadius.max;
					int explosionRadius = MathHelper.getInt(random, minExplosionRadius, maxExplosionRadius);
					compound.setByte("ExplosionRadius", (byte) explosionRadius);
				}
				
				//Power It
				if(creeper.poweredChance.ChanceMatches(entity, world, random))
					compound.setBoolean("powered", true);
				
				entityCreeper.readEntityFromNBT(compound);
			}
		}
	}
}
