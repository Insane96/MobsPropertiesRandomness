package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

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

	@SerializedName("on_attack")
	public List<MPROnHitLegacy> onAttack;

	@SerializedName("on_damaged")
	public List<MPROnHitLegacy> onDamaged;

	@SerializedName("on_death")
	public List<MPROnDeathLegacy> onDeath;

	@Override
	public void validate() throws JsonValidationException {
		if (this.onAttack != null)
			for (MPROnHitLegacy onHit : this.onAttack)
				onHit.validate();

		if (this.onDamaged != null)
			for (MPROnHitLegacy onHit : this.onDamaged)
				onHit.validate();

		if (this.onDeath != null)
			for (MPROnDeathLegacy onDeath : this.onDeath)
				onDeath.validate();
	}

	public void addToNBT(LivingEntity entity) {
		if (this.onAttack != null)
			entity.getPersistentData().putString(ON_ATTACK, new Gson().toJson(this.onAttack));
		if (this.onDamaged != null)
			entity.getPersistentData().putString(ON_DAMAGED, new Gson().toJson(this.onDamaged));
		if (this.onDeath != null)
			entity.getPersistentData().putString(ON_DEATH, new Gson().toJson(this.onDeath));
	}
}
