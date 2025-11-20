package edu.uni.registration.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A generic Result type to handle success or failure without throwing exceptions.
 *
 * @param <T> The type of the value returned on success.
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
            throw new NoSuchElementException("No value present in failed result: " + error);
        }
        return value;
    }

    public String getError() {
        return error;
    }

    public Optional<T> toOptional() {
        return isOk() ? Optional.ofNullable(value) : Optional.empty();
    }

    public void ifOk(Consumer<T> consumer) {
        if (isOk()) {
            consumer.accept(value);
        }
    }
    
    public <U> Result<U> map(Function<T, U> mapper) {
        if (isFail()) {
            return Result.fail(error);
        }
        return Result.ok(mapper.apply(value));
    }
}

