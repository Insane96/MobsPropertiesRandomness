package insane96mcp.mpr.json.mobs;

import java.io.File;
import java.util.Random;

import com.google.gson.annotations.SerializedName;

import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.json.JsonMob;
import insane96mcp.mpr.json.utils.JsonRangeMinMax;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class JsonGhast implements IJsonObject{
	@SerializedName("explosion_power")
	public JsonRangeMinMax explosionPower;
	
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
		
		for (JsonMob mob : JsonMob.mobs) {
			if (mob.ghast == null)
				continue;
			
			if (EntityList.isMatchingName(entityGhast, new ResourceLocation(mob.mobId))) {
				
				//Explosion Power
				if (compound.getInteger("ExplosionPower") == 1) {
					int minExplosionPower = (int) mob.ghast.explosionPower.GetMin();
					int maxExplosionPower = (int) mob.ghast.explosionPower.GetMax();
					int explosionPower = MathHelper.getInt(random, minExplosionPower, maxExplosionPower);
					compound.setInteger("ExplosionPower", explosionPower);
				}
				
				entityGhast.readEntityFromNBT(compound);
			}
		}
	}

	@Override
	public void Validate(File file) throws InvalidJsonException {
		if (explosionPower == null)
			throw new InvalidJsonException("Missing explosion_power for " + this, file);
		else 
			explosionPower.Validate(file);
		
	}
}
