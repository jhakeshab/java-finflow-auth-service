// UserSuspendedException.java
package com.finflow.auth.exception;

public class UserSuspendedException extends RuntimeException {
    public UserSuspendedException(String message) {
        super(message);
    }
}