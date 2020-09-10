package com.example.demo.exception;

public class DocStorageException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DocStorageException(String message) {
		super(message);
	}

	public DocStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
