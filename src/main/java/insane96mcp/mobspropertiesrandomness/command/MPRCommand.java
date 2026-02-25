package insane96mcp.mobspropertiesrandomness.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener;
import insane96mcp.mobspropertiesrandomness.feature.MPRBase;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MPRCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));

    public static final SuggestionProvider<CommandSourceStack> PRESETS = SuggestionProviders.register(
            MPR.location("summonable_presets"),
            (context, builder)
                    -> SharedSuggestionProvider.suggestResource(MPRPresetReloadListener.PRESETS.keySet().stream(), builder)
    );

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext pContext) {
        pDispatcher.register(Commands.literal("mpr").requires((p_138819_)
                -> p_138819_.hasPermission(2)).then(Commands.literal("summon")
        .then(Commands.argument("preset", ResourceLocationArgument.id()).suggests(PRESETS)
        .then(Commands.argument("entity", ResourceArgument.resource(pContext, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                .executes((ctx)
                        -> spawnEntity(ctx.getSource(), ResourceLocationArgument.getId(ctx, "preset"), ResourceArgument.getSummonableEntityType(ctx, "entity"), ctx.getSource().getPosition(), new CompoundTag(), true))
        .then(Commands.argument("pos", Vec3Argument.vec3())
                .executes((ctx)
                        -> spawnEntity(ctx.getSource(), ResourceLocationArgument.getId(ctx, "preset"), ResourceArgument.getSummonableEntityType(ctx, "entity"), Vec3Argument.getVec3(ctx, "pos"), new CompoundTag(), true))
        .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                .executes((ctx)
                        -> spawnEntity(ctx.getSource(), ResourceLocationArgument.getId(ctx, "preset"), ResourceArgument.getSummonableEntityType(ctx, "entity"), Vec3Argument.getVec3(ctx, "pos"), CompoundTagArgument.getCompoundTag(ctx, "nbt"), false))))))));
    }

    public static Entity createEntity(CommandSourceStack source, ResourceLocation preset, Holder.Reference<EntityType<?>> pType, Vec3 pPos, CompoundTag pTag, boolean pRandomizeProperties) throws CommandSyntaxException {
        BlockPos blockpos = BlockPos.containing(pPos);
        if (!Level.isInSpawnableBounds(blockpos)) {
            throw INVALID_POSITION.create();
        } else {
            CompoundTag compoundtag = pTag.copy();
            compoundtag.putString("id", pType.key().location().toString());
            ServerLevel serverlevel = source.getLevel();
            Entity entity = EntityType.loadEntityRecursive(compoundtag, serverlevel, (p_138828_) -> {
                p_138828_.moveTo(pPos.x, pPos.y, pPos.z, p_138828_.getYRot(), p_138828_.getXRot());
                return p_138828_;
            });
            if (entity == null) {
                throw ERROR_FAILED.create();
            } else {
                if (pRandomizeProperties && entity instanceof Mob mob) {
                    //noinspection deprecation,OverrideOnly
                    mob.finalizeSpawn(source.getLevel(), source.getLevel().getCurrentDifficultyAt(BlockPos.containing(source.getPosition())), MobSpawnType.COMMAND, null);
                }

                ModNBTData.put(entity, MPRBase.PRESET, preset.toString());
                if (!serverlevel.tryAddFreshEntityWithPassengers(entity)) {
                    throw ERROR_DUPLICATE_UUID.create();
                } else {
                    return entity;
                }
            }
        }
    }

    private static int spawnEntity(CommandSourceStack pSource, ResourceLocation preset, Holder.Reference<EntityType<?>> pType, Vec3 pPos, CompoundTag pTag, boolean pRandomizeProperties) throws CommandSyntaxException {
        Entity entity = createEntity(pSource, preset, pType, pPos, pTag, pRandomizeProperties);
        pSource.sendSuccess(() -> Component.translatable("commands.summon.success", entity.getDisplayName()), true);
        return 1;
    }
}
