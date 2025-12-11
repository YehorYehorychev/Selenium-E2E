package com.yehorychev.selenium.helpers;

/**
 * Unchecked exception indicating that an explicit wait exceeded the configured timeout.
 */
public class WaitTimeoutException extends RuntimeException {

    public WaitTimeoutException(String message) {
        super(message);
    }

    public WaitTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}

