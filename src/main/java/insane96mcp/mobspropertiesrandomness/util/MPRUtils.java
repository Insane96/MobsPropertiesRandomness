package insane96mcp.mobspropertiesrandomness.util;

import insane96mcp.mobspropertiesrandomness.data.MPRGroupReloadListener;
import insane96mcp.mobspropertiesrandomness.json.MPRGroup;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class MPRUtils {
	/**
	 * Checks if the entity passed matches the mob, or the namespace if the id is "modid:*", or the group
	 * @return true if the entity passed matches the mob, or the namespace if the id is "modid:*", or the mob in the group
	 */
	public static boolean matchesEntity(LivingEntity entity, MPRMob mob) {
		if (mob.group != null) {
			MPRGroup group = MPRGroupReloadListener.MPR_GROUPS.stream().filter(g -> g.name.equals(mob.group)).findFirst().orElse(null);
			for (String mobId : group.mobs) {
				if (mobId.endsWith("*")) {
					String modDomain = mobId.split(":")[0];
					ResourceLocation location = entity.getType().getRegistryName();
					if (location.getNamespace().equals(modDomain))
						return true;
				}
				if (entity.getType().getRegistryName().equals(new ResourceLocation(mobId)))
					return true;
			}
			return false;
		}
		else if (mob.mobId.endsWith("*")) {
			String modDomain = mob.mobId.split(":")[0];
			ResourceLocation location = entity.getType().getRegistryName();
			if (location.getNamespace().equals(modDomain)) {
				return true;
			}
		}

		return entity.getType().getRegistryName().equals(new ResourceLocation(mob.mobId));
	}
}
