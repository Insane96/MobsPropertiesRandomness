package insane96mcp.mobspropertiesrandomness.utils;

import insane96mcp.mobspropertiesrandomness.data.MPRGroupReloadListener;
import insane96mcp.mobspropertiesrandomness.json.MPRGroup;
import insane96mcp.mobspropertiesrandomness.json.MPRMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class MPRUtils {
	/**
	 * Checks if the entity passed matches the mob, or the namespace if the id is "modid:*", or the group
	 * @return true if the entity passed matches the mob, or the namespace if the id is "modid:*", or the mob in the group
	 */
	public static boolean matchesEntity(MobEntity entity, MPRMob mob) {
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

	/**
	 * Checks if nbt1 tags are all present and match nbt2
	 * @param nbt1
	 * @param nbt2
	 * @return
	 */
	public static boolean compareNBT(CompoundNBT nbt1, CompoundNBT nbt2) {
		for (String key : nbt1.keySet()) {
			if (!nbt2.contains(key))
				return false;

			if (nbt1.get(key) instanceof CompoundNBT && nbt2.get(key) instanceof CompoundNBT) {
				if (!compareNBT(nbt1.getCompound(key), nbt2.getCompound(key)))
					return false;
			}
			else if (!nbt1.get(key).equals(nbt2.get(key)))
				return false;
		}
		return true;
	}
}
