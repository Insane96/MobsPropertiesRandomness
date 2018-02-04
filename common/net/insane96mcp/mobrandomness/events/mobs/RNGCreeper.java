package net.insane96mcp.mobrandomness.events.mobs;

import java.util.Random;

import net.insane96mcp.mobrandomness.lib.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class RNGCreeper {
	public static void Fuse(EntityLiving living, Random random) {
		if (!(living instanceof EntityCreeper))
			return;
		
		EntityCreeper creeper = (EntityCreeper)living;
		
		NBTTagCompound tags = new NBTTagCompound();
		creeper.writeEntityToNBT(tags);
		int fuse = MathHelper.getInt(random, Properties.Creeper.fuseMin, Properties.Creeper.fuseMax - 1);
		tags.setShort("Fuse", (short)fuse);
		creeper.readEntityFromNBT(tags);
	}
	
	public static void ExplosionRadius(EntityLiving living, Random random) {
		if (!(living instanceof EntityCreeper))
			return;
		
		EntityCreeper creeper = (EntityCreeper)living;
		
		NBTTagCompound tags = new NBTTagCompound();
		creeper.writeEntityToNBT(tags);
		int explRadius = MathHelper.getInt(random, Properties.Creeper.explosionRadiusMin, Properties.Creeper.explosionRadiusMax - 1);
		tags.setByte("ExplosionRadius", (byte)explRadius);
		creeper.readEntityFromNBT(tags);
	}

	public static void Powered(EntityLiving living, float multiplier, Random random) {
		if (!(living instanceof EntityCreeper))
			return;
		
		float chance = Properties.Creeper.poweredChance * multiplier;
		if (random.nextFloat() > chance / 100f)
			return;
		
		EntityCreeper creeper = (EntityCreeper)living;
		
		NBTTagCompound tags = new NBTTagCompound();
		creeper.writeEntityToNBT(tags);
		tags.setByte("powered", (byte)1);
		creeper.readEntityFromNBT(tags);
	}
}
