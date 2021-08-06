package insane96mcp.mobspropertiesrandomness.json;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;

import java.io.File;

public interface IMPRObject {
	public void validate(final File file) throws InvalidJsonException;
}
