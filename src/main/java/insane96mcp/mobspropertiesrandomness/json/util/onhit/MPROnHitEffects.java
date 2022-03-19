package insane96mcp.mobspropertiesrandomness.json.util.onhit;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.entity.LivingEntity;

import java.io.File;
import java.util.List;

public class MPROnHitEffects implements IMPRObject {

	@SerializedName("on_attack")
	public List<MPROnHit> onAttack;

	@SerializedName("on_attacked")
	public List<MPROnHit> onAttacked;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.onAttack != null)
			for (MPROnHit onHit : this.onAttack)
				onHit.validate(file);

		if (this.onAttacked != null)
			for (MPROnHit onHit : this.onAttacked)
				onHit.validate(file);
	}

	public void addToNBT(LivingEntity entity) {
		entity.getPersistentData().putString(Strings.Tags.ON_HIT_EFFECTS, new Gson().toJson(this));
	}

	public void applyOnAttack(LivingEntity entity, LivingEntity other, float damage, boolean isDirectDamage) {
		if (this.onAttack != null)
			for (MPROnHit onHit : this.onAttack)
				onHit.apply(entity, other, damage, isDirectDamage);
	}

	public void applyOnAttacked(LivingEntity entity, LivingEntity other, float damage, boolean isDirectDamage) {
		if (this.onAttacked != null)
			for (MPROnHit onHit : this.onAttacked)
				onHit.apply(entity, other, damage, isDirectDamage);
	}

	@Override
	public String toString() {
		return String.format("OnHitEffects{on_attack: %s, on_attacked: %s}", this.onAttack, this.onAttacked);
	}
}
