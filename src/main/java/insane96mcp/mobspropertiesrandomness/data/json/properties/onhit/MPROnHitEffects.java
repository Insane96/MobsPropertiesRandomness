package insane96mcp.mobspropertiesrandomness.data.json.properties.onhit;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class MPROnHitEffects implements IMPRObject {

	@SerializedName("on_attack")
	public List<MPROnHit> onAttack;

	@SerializedName("on_attacked")
	public List<MPROnHit> onAttacked;

	@Override
	public void validate() throws JsonValidationException {
		if (this.onAttack != null)
			for (MPROnHit onHit : this.onAttack)
				onHit.validate();

		if (this.onAttacked != null)
			for (MPROnHit onHit : this.onAttacked)
				onHit.validate();
	}

	public void addToNBT(LivingEntity entity) {
		entity.getPersistentData().putString(Strings.Tags.ON_HIT_EFFECTS, new Gson().toJson(this));
	}

	public void applyOnAttack(LivingEntity entity, LivingEntity other, boolean isDirectDamage, LivingDamageEvent event) {
		if (this.onAttack != null)
			for (MPROnHit onHit : this.onAttack)
				onHit.apply(entity, other, isDirectDamage, event, false);
	}

	public void applyOnAttacked(LivingEntity entity, LivingEntity other, boolean isDirectDamage, LivingDamageEvent event) {
		if (this.onAttacked != null)
			for (MPROnHit onHit : this.onAttacked)
				onHit.apply(entity, other, isDirectDamage, event, true);
	}

	@Override
	public String toString() {
		return String.format("OnHitEffects{on_attack: %s, on_attacked: %s}", this.onAttack, this.onAttacked);
	}
}
