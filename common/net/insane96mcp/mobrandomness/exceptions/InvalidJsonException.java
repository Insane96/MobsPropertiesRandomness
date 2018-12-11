package net.insane96mcp.mobrandomness.exceptions;

import java.io.File;

public class InvalidJsonException extends Exception {
	private String message;
	private File file;
	
	public InvalidJsonException(String message, File file) {
		this.message = message;
		this.file = file;
	}
	
    public String getMessage()
    {
        return "Invalid JSON parsed (File: " + file.getName() + "): " + this.message;
    }
}
