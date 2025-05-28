package insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.events;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class MPREvents implements IMPRObject {

	public static final String ON_ATTACK = MPR.RESOURCE_PREFIX + "on_attack";
	public static final String ON_DAMAGED = MPR.RESOURCE_PREFIX + "on_damaged";
	public static final String ON_DEATH = MPR.RESOURCE_PREFIX + "on_death";
	public static final String ON_TICK = MPR.RESOURCE_PREFIX + "on_tick";
	@SerializedName("on_attack")
	public List<MPROnHit> onAttack;

	@SerializedName("on_damaged")
	public List<MPROnHit> onDamaged;

	@SerializedName("on_death")
	public List<MPROnDeath> onDeath;

	@SerializedName("on_tick")
	public List<MPROnTick> onTick;

	@Override
	public void validate() throws JsonValidationException {
		if (this.onAttack != null)
			for (MPROnHit onHit : this.onAttack)
				onHit.validate();

		if (this.onDamaged != null)
			for (MPROnHit onHit : this.onDamaged)
				onHit.validate();

		if (this.onDeath != null)
			for (MPROnDeath onDeath : this.onDeath)
				onDeath.validate();

		if (this.onTick != null)
			for (MPROnTick onTick : this.onTick)
				onTick.validate();
	}

	public void addToNBT(LivingEntity entity) {
		if (this.onAttack != null)
			entity.getPersistentData().putString(ON_ATTACK, new Gson().toJson(this.onAttack));
		if (this.onDamaged != null)
			entity.getPersistentData().putString(ON_DAMAGED, new Gson().toJson(this.onDamaged));
		if (this.onDeath != null)
			entity.getPersistentData().putString(ON_DEATH, new Gson().toJson(this.onDeath));
		if (this.onTick != null)
			entity.getPersistentData().putString(ON_TICK, new Gson().toJson(this.onTick));
	}

	@Override
	public String toString() {
		return String.format("OnHitEffects{on_attack: %s, on_attacked: %s, on_death: %s, on_tick: %s}", this.onAttack, this.onDamaged, this.onDeath, this.onTick);
	}
}
