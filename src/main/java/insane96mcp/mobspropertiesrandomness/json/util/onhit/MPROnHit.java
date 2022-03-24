package insane96mcp.mobspropertiesrandomness.json.util.onhit;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.MPRPotionEffect;
import insane96mcp.mobspropertiesrandomness.json.util.MPRModifiableValue;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.List;

public class MPROnHit implements IMPRObject {

	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	public Target target;

	@SerializedName("damage_type")
	public DamageType damageType;

	public MPRModifiableValue chance;

	@SerializedName("health_left")
	public Double healthLeft;

	@SerializedName("play_sound")
	public String playSound;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (target == null)
			throw new InvalidJsonException("Missing target for OnHit object: " + this, file);

		if (potionEffects == null) {
			throw new InvalidJsonException("Missing potion_effects for OnHit object: " + this, file);
		}
		else {
			for (MPRPotionEffect potionEffect : this.potionEffects)
				potionEffect.validate(file);
		}

		if (this.chance != null)
			this.chance.validate(file);

		if (this.playSound != null) {
			ResourceLocation rl = ResourceLocation.tryParse(this.playSound);
			if (rl == null)
				throw new InvalidJsonException("Invalid resource location for On Hit playSound: " + this, file);
			if (ForgeRegistries.SOUND_EVENTS.getValue(rl) == null)
				throw new InvalidJsonException("Sound doesn not exist for On Hit playSound: " + this, file);
		}
	}

	public void apply(LivingEntity entity, LivingEntity other, float damage, boolean isDirectDamage) {
		if (this.damageType != null && ((isDirectDamage && this.damageType == DamageType.INDIRECT) || (!isDirectDamage && this.damageType == DamageType.DIRECT)))
			return;

		if (this.healthLeft != null) {
			float health = (entity.getHealth() - damage) / entity.getMaxHealth();
			if (health > this.healthLeft || health <= 0f)
				return;
		}

		if (this.chance != null && entity.getRandom().nextDouble() >= this.chance.getValue(entity, entity.level))
			return;

		SoundEvent sound = null;
		if (this.playSound != null)
			sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(this.playSound));

		if (this.target == Target.ENTITY) {
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(entity, entity.level);
			}
			if (sound != null)
				entity.level.playSound(null, entity, sound, SoundCategory.HOSTILE, 1.0f, 1f);
		}
		else if (this.target == Target.OTHER) {
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(other, other.level);
			}
			if (sound != null)
				other.level.playSound(null, other, sound, SoundCategory.HOSTILE, 1.0f, 1f);
		}
	}

	public enum Target {
		@SerializedName("entity")
		ENTITY,
		@SerializedName("other")
		OTHER
	}

	public enum DamageType {
		@SerializedName("direct")
		DIRECT,
		@SerializedName("indirect")
		INDIRECT
	}

	@Override
	public String toString() {
		return String.format("OnHit{potion_effects: %s, target: %s}", this.potionEffects, this.target);
	}
}
