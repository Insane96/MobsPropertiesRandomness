package insane96mcp.mpr.json.utils;

import java.util.List;
import java.util.Random;

import insane96mcp.mpr.json.JsonGroup;
import insane96mcp.mpr.json.JsonMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class JsonUtils {
	public static boolean doesDimensionMatch(Entity entity, List<Integer> dimensions) {
		if (dimensions.isEmpty())
			return true;
		else {
			int entityDimension = entity.world.provider.getDimension();
			for (Integer dimension : dimensions) {
				if (entityDimension == dimension.intValue()) {
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
	public static boolean matchesEntity(EntityLiving entity, World world, Random random, JsonMob mob) {
		if (mob.group != null) {
			for (JsonGroup group : JsonGroup.groups) {
				if (!group.name.equals(mob.group))
					continue;
				
				for (String mobId : group.mobs) {
					if (mobId.endsWith("*")) {
						String modDomain = mobId.split(":")[0];
						ResourceLocation location = EntityList.getKey(entity);
						if (location.getNamespace().toString().equals(modDomain))
							return true;
					}
					ResourceLocation location = new ResourceLocation(mobId);
					if (EntityList.isMatchingName(entity, location))
						return true;
				}
			}
			return false;
		}
		
		if (mob.mobId.endsWith("*")) {
			String modDomain = mob.mobId.split(":")[0];
			ResourceLocation location = EntityList.getKey(entity);
			if (location.getNamespace().toString().equals(modDomain)) {
				return true;
			}
		}
		
		if (EntityList.isMatchingName(entity, new ResourceLocation(mob.mobId)))
			return true;
		
		return false;
		
	}

	public static boolean doesBiomeMatch(EntityLiving entity, List<Biome> biomes) {
		if (biomes.isEmpty())
			return true;
		
		BlockPos pos = entity.getPosition();
		Biome entityBiome = entity.world.getChunk(pos).getBiome(pos, entity.world.getBiomeProvider());
		for (Biome biome : biomes) {
			if (biome.equals(entityBiome))
				return true;
		}
		return false;
	}
}
