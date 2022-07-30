package insane96mcp.mobspropertiesrandomness.json.properties.onhit;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.properties.MPRPotionEffect;
import insane96mcp.mobspropertiesrandomness.json.util.modifiable.MPRModifiableValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

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
	public void validate() throws JsonValidationException {
		if (target == null)
			throw new JsonValidationException("Missing \"target\" for OnHit object: %s".formatted(this));

		if (potionEffects == null) {
			throw new JsonValidationException("Missing \"potion_effects\" for OnHit object: %s".formatted(this));
		}
		else {
			for (MPRPotionEffect potionEffect : this.potionEffects)
				potionEffect.validate();
		}

		if (this.chance != null)
			this.chance.validate();

		if (this.playSound != null) {
			ResourceLocation rl = ResourceLocation.tryParse(this.playSound);
			if (rl == null)
				throw new JsonValidationException("Invalid resource location for On Hit playSound: " + this);
			if (ForgeRegistries.SOUND_EVENTS.getValue(rl) == null)
				throw new JsonValidationException("Sound doesn not exist for On Hit playSound: " + this);
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
				entity.level.playSound(null, entity, sound, SoundSource.HOSTILE, 1.0f, 1f);
		}
		else if (this.target == Target.OTHER) {
			for (MPRPotionEffect potionEffect : this.potionEffects) {
				potionEffect.apply(other, other.level);
			}
			if (sound != null)
				other.level.playSound(null, other, sound, SoundSource.HOSTILE, 1.0f, 1f);
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
