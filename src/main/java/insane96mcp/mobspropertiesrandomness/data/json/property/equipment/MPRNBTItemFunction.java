package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

//@JsonAdapter(MPRNBTItemFunction.Serializer.class)
public class MPRNBTItemFunction /*extends MPRItemFunction*/ {
    /*public MPRNbt nbt;

    public MPRNBTItemFunction(MPRNbt nbt, List<MPRCondition> conditions) {
        super(conditions);
        this.nbt = nbt;
    }

    @Override
    protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
        CompoundTag stackTag = stack.getOrCreateTag();
        MPRNbt.ResolvedPath resolved = this.nbt.resolveOrCreatePath(stackTag);
        this.nbt.writeValue(resolved.parent(), resolved.key(), living);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRNBTItemFunction>, JsonSerializer<MPRNBTItemFunction> {
        @Override
        public MPRNBTItemFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRNBTItemFunction(
                    context.deserialize(jObject, MPRNbt.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRNBTItemFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.nbt).getAsJsonObject();
            return src.endSerialization(jObject, context);
        }
    }*/
}
