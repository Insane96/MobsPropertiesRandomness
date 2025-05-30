package insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.equipment;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MPRSlot implements IMPRObject {

	@SerializedName("keep_spawned")
	public boolean keepSpawned;
	@SerializedName("replace_only")
	public boolean replaceOnly;
	@Nullable
	public List<MPRItemOld> items;
	public String nbt;
	private transient CompoundTag _nbt;

	@Override
	public void validate() throws JsonValidationException {
		if (this.replaceOnly && this.keepSpawned)
		{
			Logger.debug("keep_spawned has been set to false since replace_only is true. " + this);
			this.keepSpawned = false;
		}

        if (this.items != null) {
			List<MPRItemOld> invalid = new ArrayList<>();
            for (MPRItemOld item : this.items) {
                item.validate();
				if (!item.isValid())
					invalid.add(item);
            }
			invalid.forEach(this.items::remove);
        }

		if (this.nbt != null) {
			try {
				this._nbt = TagParser.parseTag(this.nbt);
			}
			catch (CommandSyntaxException e) {
				throw new JsonValidationException("Invalid nbt for Slot (%s): %s".formatted(e.getMessage(), this.nbt));
			}
		}
	}

	public CompoundTag getNBT() {
		return this._nbt.copy();
	}
}
