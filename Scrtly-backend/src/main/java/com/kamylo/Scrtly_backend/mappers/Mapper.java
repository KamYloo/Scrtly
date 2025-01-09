package com.kamylo.Scrtly_backend.mappers;

public interface Mapper<A,B> {
    B mapTo(A a);

    A mapFrom(B b);
}
