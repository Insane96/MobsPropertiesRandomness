package insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.equipment;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class MPRItemOld {

	public String id;
	private transient Item item;
	public MPRRange count;

	public String nbt;
	private transient CompoundTag _nbt;

	private transient boolean valid = true;

	public void validate() throws JsonValidationException {
		if (this.id == null)
			throw new JsonValidationException("Missing id. %s".formatted(this));

		this.item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(this.id));
		if (this.item == Items.AIR && !this.id.equals("minecraft:air")) {
			Logger.warn("Item not found %s: %s".formatted(this.id, this));
			this.valid = false;
			return;
		}

		if (this.count == null)
			this.count = new MPRRange(1f);

		if (this.nbt != null) {
			try {
				this._nbt = TagParser.parseTag(this.nbt);
			}
			catch (CommandSyntaxException e) {
				throw new JsonValidationException("Invalid nbt for Item (%s): %s".formatted(e.getMessage(), this.nbt));
			}
		}
	}

	public CompoundTag getNBT() {
		return this._nbt.copy();
	}

	public Item getItem() {
		return this.item;
	}

	public boolean isValid() {
		return this.valid;
	}
}
