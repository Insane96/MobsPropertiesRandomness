package insane96mcp.mobspropertiesrandomness.json.mob;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.setup.Strings;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.util.MPRRange;
import insane96mcp.mobspropertiesrandomness.network.MessageCreeperFuseSync;
import insane96mcp.mobspropertiesrandomness.network.NetworkHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;

import java.io.File;

public class MPRCreeper implements IMPRObject, IMPRAppliable {
	public MPRRange fuse;
	@SerializedName("explosion_radius")
	public MPRRange explosionRadius;
	@SerializedName("powered_chance")
	public MPRModifiableValue poweredChance;
	@SerializedName("fire_chance")
	public MPRModifiableValue fireChance;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (fuse != null)
			fuse.validate(file);

		if (explosionRadius != null)
			explosionRadius.validate(file);

		if (poweredChance != null)
			poweredChance.validate(file);

		if (fireChance != null)
			fireChance.validate(file);
	}

	@Override
	public void apply(LivingEntity entity, World world) {
		if (!(entity instanceof CreeperEntity))
			return;

		CreeperEntity creeper = (CreeperEntity) entity;

		CompoundNBT compound = new CompoundNBT();
		creeper.addAdditionalSaveData(compound);

		if (world.isClientSide)
			return;

		//Fuse
		int fuse = 30;
		if (this.fuse != null && compound.getShort("Fuse") == 30) {
			fuse = this.fuse.getIntBetween(creeper, world);
			compound.putShort("Fuse", (short)fuse);
		}

		Object msg = new MessageCreeperFuseSync(creeper.getId(), (short) fuse);
		for (PlayerEntity player : world.players()) {
			NetworkHandler.CHANNEL.sendTo(msg, ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}

		//Explosion Radius
		if (this.explosionRadius != null && compound.getByte("ExplosionRadius") == 3) {
			int explosionRadius = this.explosionRadius.getIntBetween(creeper, world);
			compound.putByte("ExplosionRadius", (byte) explosionRadius);
		}

		//Power It
		if(this.poweredChance != null && world.random.nextFloat() < this.poweredChance.getValue(creeper, world))
			compound.putBoolean("powered", true);

		//Causes fire on explosion
		if(this.fireChance != null && world.random.nextFloat() < this.fireChance.getValue(creeper, world))
			creeper.getPersistentData().putBoolean(Strings.Tags.EXPLOSION_CAUSES_FIRE, true);

		creeper.readAdditionalSaveData(compound);
	}


	@Override
	public String toString() {
		return String.format("Creeper{fuse: %s, explosion_radius: %s, powered_chance: %s}", fuse, explosionRadius, poweredChance);
	}
}
