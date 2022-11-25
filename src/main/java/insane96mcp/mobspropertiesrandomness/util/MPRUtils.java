package insane96mcp.mobspropertiesrandomness.util;

import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class MPRUtils {
	/**
	 * Checks if the entity passed matches the mob or the entity_tag
	 * @return true if the entity passed matches the mob or the mob in the entity_tag
	 */
	public static boolean matchesEntity(LivingEntity entity, MPRMob mob) {
		if (mob.entityTag != null) {
			return isEntityInTag(entity, mob.entityTag);
		}

		//noinspection ConstantConditions
		return entity.getType().getRegistryName().equals(mob.mobId);
	}

	//TODO Move to lib in 1.19
	public static boolean isEntityInTag(Entity entity, ResourceLocation tag) {
		TagKey<EntityType<?>> tagKey = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, tag);
		//noinspection ConstantConditions
		return ForgeRegistries.ENTITIES.tags().getTag(tagKey).contains(entity.getType());
	}
}
