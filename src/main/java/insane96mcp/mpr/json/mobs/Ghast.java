package insane96mcp.mpr.json.mobs;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.json.Mob;
import insane96mcp.mpr.json.utils.RangeMinMax;
import insane96mcp.mpr.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.Random;

public class Ghast implements IJsonObject{
	@SerializedName("explosion_power")
	public RangeMinMax explosionPower;
	
	@Override
	public String toString() {
		return String.format("Ghast{explosionPower: %s}", explosionPower);
	}
	
	public static void apply(Entity entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		if (!(entity instanceof GhastEntity))
			return;

		GhastEntity entityGhast = (GhastEntity)entity;
		

		CompoundNBT compound = new CompoundNBT();
		entityGhast.writeAdditional(compound);
		
		for (Mob mob : Mob.mobs) {
			if (mob.ghast == null)
				continue;
			
			if (ForgeRegistries.ENTITIES.getKey(entityGhast.getType()).toString().equals(mob.mobId)) {
				
				//Explosion Power
				if (compound.getInt("ExplosionPower") == 1) {
					int minExplosionPower = (int) mob.ghast.explosionPower.getMin();
					int maxExplosionPower = (int) mob.ghast.explosionPower.getMax();
					int explosionPower = Utils.getRandomInt(random, minExplosionPower, maxExplosionPower);
					compound.putInt("ExplosionPower", explosionPower);
				}
				
				entityGhast.readAdditional(compound);
			}
		}
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (explosionPower == null)
			throw new InvalidJsonException("Missing explosion_power for " + this, file);
		else 
			explosionPower.validate(file);
		
	}
}
