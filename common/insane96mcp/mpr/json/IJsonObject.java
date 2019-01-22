package insane96mcp.mpr.json;

import java.io.File;

import insane96mcp.mpr.exceptions.InvalidJsonException;

public interface IJsonObject {
	public void Validate(final File file) throws InvalidJsonException;
}
