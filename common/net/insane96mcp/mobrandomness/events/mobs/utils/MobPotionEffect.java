package net.insane96mcp.mobrandomness.events.mobs.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.potion.PotionEffect;

public class MobPotionEffect {
	public String mobName;
	public List<RNGPotionEffect> potionEffects;
	
	public MobPotionEffect(String mobName) {
		this.mobName = mobName;
		potionEffects = new ArrayList<RNGPotionEffect>();
	}
	
	public void AddPotionEffect(float chance, String id, int minAmplifier, int maxAmplifier, boolean showParticles, boolean ambientParticles) {
		RNGPotionEffect advancedPotionEffect = new RNGPotionEffect(chance, id, minAmplifier, maxAmplifier, showParticles, ambientParticles);
		potionEffects.add(advancedPotionEffect);
	}
	
	public class RNGPotionEffect {
		public String id;
		public int minAmplifier;
		public int maxAmplifier;
		public boolean showParticles;
		public boolean ambientParticles;
	
		public float chance;
		
		public RNGPotionEffect(float chance, String id, int minAmplifier, int maxAmplifier, boolean showParticles, boolean ambientParticles) {
			this.chance = chance;
			
			this.id = id;
			this.minAmplifier = minAmplifier;
			this.maxAmplifier = maxAmplifier;
			this.showParticles = showParticles;
			this.ambientParticles = ambientParticles;
		}
	}
}
