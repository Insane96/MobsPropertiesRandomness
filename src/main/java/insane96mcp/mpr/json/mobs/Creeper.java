package insane96mcp.mpr.json.mobs;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.init.Reflection;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.json.Mob;
import insane96mcp.mpr.json.utils.Chance;
import insane96mcp.mpr.json.utils.RangeMinMax;
import insane96mcp.mpr.utils.Utils;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

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
	
	public void validate(final File file) throws InvalidJsonException{
		if (poweredChance != null)
			poweredChance.validate(file);
	}
	
	public static void apply(MobEntity entity, World world, Random random) {
		
		if (!(entity instanceof CreeperEntity))
			return;

		CreeperEntity entityCreeper = (CreeperEntity)entity;
		
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (mob.creeper == null)
				continue;

			if (ForgeRegistries.ENTITIES.getKey(entityCreeper.getType()).toString().equals(mob.mobId)) {
				Creeper creeper = mob.creeper;
				
				CompoundNBT compound = new CompoundNBT();
				entityCreeper.writeAdditional(compound);
				
				//Fuse
				if (mob.creeper.fuse != null && compound.getShort("Fuse") == 30) {
					int minFuse = (int) mob.creeper.fuse.getMin();
					int maxFuse = (int) mob.creeper.fuse.getMax();
					int fuse = Utils.getRandomInt(random, minFuse, maxFuse);
					compound.putShort("Fuse", (short)fuse);
				}
				
				//Explosion Radius
				if (mob.creeper.explosionRadius != null && compound.getByte("ExplosionRadius") == 30) {
					int minExplosionRadius = (int) mob.creeper.explosionRadius.getMin();
					int maxExplosionRadius = (int) mob.creeper.explosionRadius.getMax();
					int explosionRadius = Utils.getRandomInt(random, minExplosionRadius, maxExplosionRadius);
					compound.putByte("ExplosionRadius", (byte) explosionRadius);
				}
				
				//Power It
				if(creeper.poweredChance.chanceMatches(entity, world, random))
					compound.putBoolean("powered", true);
				
				entityCreeper.readAdditional(compound);
			}
		}
	}
	
	/**
	 * Fixes area effect clouds (not spawned by the player) that spawn with duration over 8 minutes setting them to 30 seconds
	 */
	public static void fixAreaEffectClouds(Entity entity) {
		if (!(entity instanceof AreaEffectCloudEntity))
			return;
		
		CompoundNBT tags = entity.getEntityData();
		boolean isAlreadyChecked = tags.getBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked");
		
		if (isAlreadyChecked)
			return;

		AreaEffectCloudEntity areaEffectCloud = (AreaEffectCloudEntity) entity;
		if (areaEffectCloud.getOwner() instanceof PlayerEntity) {
			tags.putBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked", true);
			return;
		}

		ArrayList<EffectInstance> effects = (ArrayList<EffectInstance>) Reflection.get(Reflection.AreaEffectCloud_effects, areaEffectCloud);
		ArrayList<EffectInstance> newEffects = new ArrayList<>();
		for (EffectInstance potionEffect : effects) {
			if (potionEffect.getDuration() > 9600) {
				EffectInstance newPotionEffect = new EffectInstance(potionEffect.getPotion(), 600, potionEffect.getAmplifier());
				newEffects.add(newPotionEffect);
				continue;
			}
			newEffects.add(potionEffect);
		}
		Reflection.set(Reflection.AreaEffectCloud_effects, areaEffectCloud, newEffects);
		tags.putBoolean(MobsPropertiesRandomness.RESOURCE_PREFIX + "checked", true);
	}
}
