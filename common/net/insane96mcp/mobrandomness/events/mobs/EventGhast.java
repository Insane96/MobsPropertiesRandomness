package net.insane96mcp.mobrandomness.events.mobs;

import java.util.Random;

import net.insane96mcp.mobrandomness.lib.Properties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class EventGhast {
	public static void ExplosionPower(EntityLiving living, Random random) {
		if (!(living instanceof EntityGhast))
			return;
		
		EntityGhast ghast = (EntityGhast)living;
		
		NBTTagCompound tags = new NBTTagCompound();
		ghast.writeEntityToNBT(tags);
		int explosionPower = MathHelper.getInt(random, Properties.Ghast.explosionPowerMin, Properties.Ghast.explosionPowerMax - 1);
		tags.setInteger("ExplosionPower", explosionPower);
		ghast.readEntityFromNBT(tags);
	}
}
