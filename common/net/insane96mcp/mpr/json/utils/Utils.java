package net.insane96mcp.mpr.json.utils;

import java.util.List;
import java.util.Random;

import net.insane96mcp.mpr.json.Group;
import net.insane96mcp.mpr.json.Mob;
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
	 * Checks if the entity passed matches the mob, or the namespace if the id is "modid:*", or the group if the namespace is "group"
	 * @return true if the entity matches the mob_id or it's in the mod namespace
	 */
	public static boolean MatchesEntity(EntityLiving entity, World world, Random random, Mob mob) {
		if (mob.group != null) {
			for (Group group : Group.groups) {
				if (!group.name.equals(mob.group))
					continue;
				
				for (String mobId : group.mobs) {
					ResourceLocation location = new ResourceLocation(mobId);
					if (EntityList.isMatchingName(entity, location))
						return true;
				}
			}
			return false;
		}
		
		if (mob.mobId.endsWith("*")) {
			String modDomain = mob.mobId.split(":")[1];
			ResourceLocation location = EntityList.getKey(entity);
			if (location.getNamespace().toString().equals(modDomain)) {
				return true;
			}
		}
		
		if (EntityList.isMatchingName(entity, new ResourceLocation(mob.mobId)))
			return true;
		
		return false;
		
	}
}
