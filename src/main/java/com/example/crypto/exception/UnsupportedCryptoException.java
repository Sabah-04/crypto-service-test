package com.example.crypto.exception;


public class UnsupportedCryptoException extends RuntimeException {
    public UnsupportedCryptoException(String symbol) {
        super("Crypto symbol not supported: " + symbol);
    }
}
