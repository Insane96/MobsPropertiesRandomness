package insane96mcp.mobspropertiesrandomness.util;

import insane96mcp.mobspropertiesrandomness.data.MPRGroupReloadListener;
import insane96mcp.mobspropertiesrandomness.json.MPRGroup;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class MPRUtils {
	/**
	 * Checks if the entity passed matches the mob or the group
	 * @return true if the entity passed matches the mob or the mob in the group
	 */
	public static boolean matchesEntity(LivingEntity entity, MPRMob mob) {
		if (mob.group != null) {
			MPRGroup group = MPRGroupReloadListener.MPR_GROUPS.stream().filter(g -> g.name.equals(mob.group)).findFirst().orElse(null);
			for (String mobId : group.mobs) {
				if (entity.getType().getRegistryName().equals(new ResourceLocation(mobId)))
					return true;
			}
			return false;
		}

		return entity.getType().getRegistryName().equals(new ResourceLocation(mob.mobId));
	}
}
