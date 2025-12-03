package edu.uni.registration.util;

import java.util.NoSuchElementException;

/**
 * Generic Result type for success/failure handling.
 */
public final class Result<T> {

    private final T value;
    private final String error;

    private Result(T value, String error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> fail(String error) {
        return new Result<>(null, error);
    }

    public boolean isOk() {
        return error == null;
    }

    public boolean isFail() {
        return error != null;
    }

    public T get() {
        if (isFail()) {
            throw new NoSuchElementException("Result failed: " + error);
        }
        return value;
    }

    public String getError() {
        return error;
    }
}





