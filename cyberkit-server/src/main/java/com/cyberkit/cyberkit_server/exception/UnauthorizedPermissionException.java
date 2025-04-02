package com.cyberkit.cyberkit_server.exception;

public class UnauthorizedPermissionException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public UnauthorizedPermissionException(String message) {
        super(message);
    }
}
