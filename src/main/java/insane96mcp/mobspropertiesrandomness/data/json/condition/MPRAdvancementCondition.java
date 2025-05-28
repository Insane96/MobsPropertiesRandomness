package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRAdvancementCondition.Serializer.class)
public class MPRAdvancementCondition extends MPRCondition {
	public List<ResourceLocation> advancements;
	public PlayerMode player;

	public MPRAdvancementCondition(List<ResourceLocation> advancements, PlayerMode player, boolean inverted) {
		super(inverted);
		this.advancements = advancements;
		this.player = player;
	}

	@Override
	protected boolean conditionCheck(LivingEntity livingEntity) {
		List<ServerPlayer> players = new ArrayList<>();
		if (this.player == PlayerMode.NEAREST) {
			ServerPlayer nearestPlayer = (ServerPlayer) livingEntity.level().getNearestPlayer(livingEntity, 128d);
			if (nearestPlayer == null)
				return false;
			players.add(nearestPlayer);
		}
		else {
			players = livingEntity.level().getEntitiesOfClass(ServerPlayer.class, livingEntity.getBoundingBox().inflate(128d));
			if (players.isEmpty())
				return false;
		}

		for (ServerPlayer player : players) {
			boolean allAdvancementDone = true;
			for (ResourceLocation adv : this.advancements) {
				if (!MCUtils.isAdvancementDone(player, adv))
					allAdvancementDone = false;
			}
			if (allAdvancementDone)
				return true;
		}
		return false;
	}

	public static class Serializer implements JsonDeserializer<MPRAdvancementCondition>, JsonSerializer<MPRAdvancementCondition> {
		@Override
		public MPRAdvancementCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			JsonArray aAdvancements = jObject.getAsJsonArray("advancements");
			if (aAdvancements == null)
				throw new JsonParseException("Missing advancements array");
			Type listType = new TypeToken<List<String>>() {}.getType();
			List<String> values = context.deserialize(aAdvancements, listType);
			List<ResourceLocation> advancementsList = new ArrayList<>();
			for (String adv : values) {
				ResourceLocation advancement = ResourceLocation.tryParse(adv);
				if (advancement == null)
					throw new JsonParseException("Invalid advancement: " + adv);
				advancementsList.add(advancement);
			}
			PlayerMode playerMode = PlayerMode.NEAREST;
			if (jObject.has("player"))
				playerMode = context.deserialize(jObject.get("player"), PlayerMode.class);
			return new MPRAdvancementCondition(advancementsList, playerMode, MPRCondition.deserializeInverted(jObject));
		}

		@Override
		public JsonElement serialize(MPRAdvancementCondition src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("advancements", context.serialize(src.advancements));
			jObject.add("player", context.serialize(src.player));
			return src.endSerialization(jObject);
		}
	}

	public enum PlayerMode {
		@SerializedName("nearest")
		NEAREST,
		@SerializedName("any")
		ANY
	}
}
