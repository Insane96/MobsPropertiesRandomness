package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class MPRAdvancement implements IMPRObject {

	public List<String> advancements;
	@SerializedName("player_mode")
	public PlayerMode playerMode;

	public MPRAdvancement() {
		this.playerMode = PlayerMode.NEAREST;
	}

	@Override
	public void validate() throws JsonValidationException {
		if (this.advancements == null || this.advancements.size() == 0) {
			throw new JsonValidationException(String.format("Missing or empty advancements list. %s", this));
		}
		else {
			for (String s : this.advancements) {
				if (ResourceLocation.tryParse(s) == null) {
					throw new JsonValidationException(String.format("Invalid advancement %s in advancements list. %s", s, this));
				}
			}
		}
	}

	public boolean conditionApplies(LivingEntity livingEntity) {
		List<ServerPlayer> players = new ArrayList<>();
		if (this.playerMode == PlayerMode.NEAREST)
			players.add((ServerPlayer) livingEntity.level().getNearestPlayer(livingEntity, 128d));
		else
			players = livingEntity.level().getEntitiesOfClass(ServerPlayer.class, livingEntity.getBoundingBox().inflate(128d));

		for (ServerPlayer player : players) {
			boolean allAdvancementDone = true;
			for (String adv : this.advancements) {
				if (!MCUtils.isAdvancementDone(player, new ResourceLocation(adv))) {
					allAdvancementDone = false;
				}
			}
			if (allAdvancementDone)
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("Advancement{advancements: %s, player_mode: %s}", advancements, playerMode);
	}

	public enum PlayerMode {
		@SerializedName("nearest")
		NEAREST,
		@SerializedName("any")
		ANY
	}
}
