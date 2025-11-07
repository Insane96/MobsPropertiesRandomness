package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.world.entity.LivingEntity;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRSeasonCondition.Serializer.class)
public class MPRSeasonCondition extends MPRCondition {
	public List<Season.SubSeason> seasons;

	public MPRSeasonCondition(List<Season.SubSeason> seasons, boolean inverted) {
		super(inverted);
		this.seasons = seasons;
	}

	@Override
	protected boolean conditionCheck(LivingEntity living) {
        return this.seasons.contains(SeasonHelper.getSeasonState(living.level()).getSubSeason());
	}

	public static class Serializer implements JsonDeserializer<MPRSeasonCondition>, JsonSerializer<MPRSeasonCondition> {
		@Override
		public MPRSeasonCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			List<Season.SubSeason> values = SerializerUtils.deserializeList(jObject, "seasons", context, Season.SubSeason.class);
			if (values.isEmpty())
				throw new JsonParseException("No seasons specified for season Condition");
			return new MPRSeasonCondition(values, MPRCondition.deserializeInverted(jObject));
		}

		@Override
		public JsonElement serialize(MPRSeasonCondition src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("seasons", context.serialize(src.seasons));
			return src.endSerialization(jObject);
		}
	}
}
