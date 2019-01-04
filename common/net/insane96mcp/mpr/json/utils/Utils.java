package net.insane96mcp.mpr.json.utils;

import java.util.List;
import java.util.Random;

import net.insane96mcp.mpr.json.Mob;
import net.insane96mcp.mpr.lib.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class Utils {
	public static boolean doesDimensionMatch(Entity entity, List<Integer> dimensions) {
		if (dimensions.isEmpty())
			return true;
		else {
			DimensionType entityDimension = entity.world.provider.getDimensionType();
			for (Integer dimension : dimensions) {
				DimensionType potionDimension = DimensionType.getById(dimension.intValue());
				if (entityDimension.equals(potionDimension)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Checks if the entity passed matches the mob, or the namespace if the mob_id is "modid:*"
	 * @return true if the entity matches the mob_id or it's in the mod namespace
	 */
	public static boolean MatchesEntity(EntityLiving entity, World world, Random random, Mob mob) {
		if (mob.id.endsWith("*")) {
			String[] splitId = mob.id.split(":");
			if (splitId.length != 2) {
				Logger.Warning("Failed to parse mod domain from " + mob);
			}
			ResourceLocation location = EntityList.getKey(entity);
			if (location.getNamespace().toString().equals(splitId[0])) {
				return true;
			}
		}
		
		if (EntityList.isMatchingName(entity, new ResourceLocation(mob.id)))
			return true;
		
		return false;
		
	}
}
