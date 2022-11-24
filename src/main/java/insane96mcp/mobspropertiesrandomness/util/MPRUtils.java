package insane96mcp.mobspropertiesrandomness.util;

import insane96mcp.mobspropertiesrandomness.data.MPRGroupReloadListener;
import insane96mcp.mobspropertiesrandomness.data.json.MPRGroup;
import insane96mcp.mobspropertiesrandomness.data.json.MPRMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class MPRUtils {
	/**
	 * Checks if the entity passed matches the mob or the group
	 * @return true if the entity passed matches the mob or the mob in the group
	 */
	public static boolean matchesEntity(LivingEntity entity, MPRMob mob) {
		if (mob.group != null) {
			MPRGroup group = MPRGroupReloadListener.MPR_GROUPS.stream().filter(g -> g.id.equals(mob.group)).findFirst().orElse(MPRGroup.EMPTY);
			for (String mobId : group.mobs) {
				//noinspection ConstantConditions
				if (entity.getType().getRegistryName().equals(new ResourceLocation(mobId)))
					return true;
			}
			return false;
		}

		//noinspection ConstantConditions
		return entity.getType().getRegistryName().equals(mob.mobId);
	}
}
