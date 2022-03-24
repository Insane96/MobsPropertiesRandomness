package insane96mcp.mobspropertiesrandomness.json.util;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.io.File;
import java.util.List;

public class MPRCustomName implements IMPRObject {

	public MPRModifiableValue chance;
	public List<String> overrides;
	public List<String> prefixes;
	public List<String> suffixes;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (chance != null)
			chance.validate(file);
		if ((this.overrides == null || this.overrides.size() == 0) && (this.prefixes == null || this.prefixes.size() == 0) && (this.suffixes == null || this.suffixes.size() == 0))
			throw new InvalidJsonException("No overrides, prefixes or suffixes specified for Custom Name", file);
	}

	public void applyCustomName(LivingEntity entity, World world) {
		if (this.chance != null && world.random.nextDouble() >= this.chance.getValue(entity, world))
			return;

		String prefix = "";
		if (this.prefixes != null && this.prefixes.size() > 0)
			prefix = this.prefixes.get(entity.getRandom().nextInt(this.prefixes.size()));
		String suffix = "";
		if (this.suffixes != null && this.suffixes.size() > 0)
			suffix = this.suffixes.get(entity.getRandom().nextInt(this.suffixes.size()));

		ITextComponent iTextComponent;
		if (this.overrides != null && this.overrides.size() > 0)
			iTextComponent = new StringTextComponent(prefix + this.overrides.get(entity.getRandom().nextInt(this.overrides.size())) + suffix);
		else
			iTextComponent = new StringTextComponent(prefix).append(entity.getName()).append(new StringTextComponent(suffix));

		entity.setCustomName(iTextComponent);
	}

	@Override
	public String toString() {
		return String.format("CustomName{chance: %s, overrides: %s, prefixes: %s, suffixes: %s}", chance, overrides, this.prefixes, this.suffixes);
	}
}
