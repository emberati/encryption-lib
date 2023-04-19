package com.emb.crypto;

public interface Encoder<T> {
    T encode(T data);
    T decode(T data);
}
