package insane96mcp.mpr.json.utils;

import insane96mcp.mpr.json.Group;
import insane96mcp.mpr.json.Mob;
import insane96mcp.mpr.utils.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

public class JsonUtils {
	public static boolean doesDimensionMatch(Entity entity, List<Integer> dimensions) {
		if (dimensions.isEmpty())
			return true;
		else {
			DimensionType entityDimension = entity.world.getDimension().getType();
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
	public static boolean matchesEntity(MobEntity entity, World world, Random random, Mob mob) {
		if (mob.group != null) {
			for (Group group : Group.groups) {
				if (!group.name.equals(mob.group))
					continue;
				
				for (String mobId : group.mobs) {
					if (mobId.endsWith("*")) {
						String modDomain = mobId.split(":")[0];
						ResourceLocation location = ForgeRegistries.ENTITIES.getKey(entity.getType());
						if (location.getNamespace().equals(modDomain))
							return true;
					}
					ResourceLocation location = new ResourceLocation(mobId);
					if (ForgeRegistries.ENTITIES.getKey(entity.getType()).equals(location))
						return true;
				}
			}
			return false;
		}
		
		if (mob.mobId.endsWith("*")) {
			String modDomain = mob.mobId.split(":")[0];
			ResourceLocation location = ForgeRegistries.ENTITIES.getKey(entity.getType());
			if (location.getNamespace().equals(modDomain)) {
				return true;
			}
		}

		return ForgeRegistries.ENTITIES.getKey(entity.getType()).equals(new ResourceLocation(mob.mobId));
		
	}

	public static boolean doesBiomeMatch(MobEntity entity, List<Biome> biomes) {
		if (biomes.isEmpty())
			return true;
		
		BlockPos pos = entity.getPosition();
		Biome entityBiome = entity.world.getChunk(pos).getBiome(pos);
		for (Biome biome : biomes) {
			if (biome.equals(entityBiome))
				return true;
		}
		return false;
	}
}
