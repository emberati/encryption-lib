package com.emb.main;

public interface Encoder<T> {
    T encrypt(T data);
    T decrypt(T data);
}
