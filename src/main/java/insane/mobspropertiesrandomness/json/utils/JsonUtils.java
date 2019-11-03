package insane.mobspropertiesrandomness.json.utils;

import insane.mobspropertiesrandomness.json.JsonMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
			int entityDimension = entity.getEntityWorld().getDimension().getType().getId();
			for (Integer dimension : dimensions) {
				if (entityDimension == dimension) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Checks if the entity passed matches the mob, or the namespace if the id is "modid:*", or the group if the namespace is "group"
	 *
	 * @return true if the entity matches the mob_id or it's in the mod namespace
	 */
	public static boolean matchesEntity(LivingEntity entity, World world, Random random, JsonMob mob) {
		/*if (mob.group != null) {
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
		}*/

		if (mob.mobId.endsWith("*")) {
			String modDomain = mob.mobId.split(":")[0];
			ResourceLocation location = ForgeRegistries.ENTITIES.getKey(entity.getType());
			if (location.getNamespace().toString().equals(modDomain)) {
				return true;
			}
		}

		if (entity.getType().getRegistryName().toString().equals(mob.mobId))
			return true;

		return false;

	}

	public static boolean doesBiomeMatch(LivingEntity entity, List<Biome> biomes) {
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
