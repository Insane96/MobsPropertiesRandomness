package insane96mcp.mobspropertiesrandomness.data.json.mobspecificproperties;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.setup.ILStrings;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.network.MessageCreeperFuseSync;
import insane96mcp.mobspropertiesrandomness.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;

public class MPRCreeper implements IMPRObject {
	public MPRRange fuse;
	@SerializedName("explosion_radius")
	public MPRRange explosionRadius;
	@SerializedName("powered_chance")
	public MPRModifiableValue poweredChance;
	@SerializedName("fire_chance")
	public MPRModifiableValue fireChance;

	@Override
	public void validate() throws JsonValidationException {
		if (this.fuse != null)
			this.fuse.validate();

		if (this.explosionRadius != null)
			this.explosionRadius.validate();

		if (this.poweredChance != null)
			this.poweredChance.validate();

		if (this.fireChance != null)
			this.fireChance.validate();
	}

	public void apply(LivingEntity entity, Level world) {
		if (!(entity instanceof Creeper creeper))
			return;

		CompoundTag compound = new CompoundTag();
		creeper.addAdditionalSaveData(compound);

		if (world.isClientSide)
			return;

		//Fuse
		int fuse = 30;
		if (this.fuse != null && compound.getShort("Fuse") == 30) {
			fuse = this.fuse.getInt(creeper, world);
			compound.putShort("Fuse", (short)fuse);
		}

		Object msg = new MessageCreeperFuseSync(creeper.getId(), (short) fuse);
		for (Player player : world.players()) {
			NetworkHandler.CHANNEL.sendTo(msg, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}

		//Explosion Radius
		if (this.explosionRadius != null && compound.getByte("ExplosionRadius") == 3) {
			int explosionRadius = this.explosionRadius.getInt(creeper, world);
			compound.putByte("ExplosionRadius", (byte) explosionRadius);
		}

		//Power It
		if(this.poweredChance != null && world.random.nextFloat() < this.poweredChance.getValue(creeper, world))
			compound.putBoolean("powered", true);

		//Causes fire on explosion
		if(this.fireChance != null && world.random.nextFloat() < this.fireChance.getValue(creeper, world))
			creeper.getPersistentData().putBoolean(ILStrings.Tags.EXPLOSION_CAUSES_FIRE, true);

		creeper.readAdditionalSaveData(compound);
	}


	@Override
	public String toString() {
		return String.format("Creeper{fuse: %s, explosion_radius: %s, powered_chance: %s}", fuse, explosionRadius, poweredChance);
	}
}
