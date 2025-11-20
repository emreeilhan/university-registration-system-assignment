package edu.uni.registration.util;

public interface Specification<T> {
    boolean isSatisfiedBy(T t);
}


