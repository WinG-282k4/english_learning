package com.app.vocab.config;

import org.springframework.stereotype.Component;

/**
 * Utility class for AES-GCM encryption/decryption of sensitive data such as API keys.
 */
@Component
public class CryptoUtil {

    private final AppProperties appProperties;

    public CryptoUtil(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Encrypts plaintext using AES-GCM.
     */
    public String encrypt(String plainText) {
        // TODO: implement AES-GCM encryption
        return plainText;
    }

    /**
     * Decrypts ciphertext using AES-GCM.
     */
    public String decrypt(String cipherText) {
        // TODO: implement AES-GCM decryption
        return cipherText;
    }

    /**
     * Masks an API key for safe display in frontend (e.g., AIza...x4f2).
     */
    public String maskKey(String rawKey) {
        // TODO: implement masking logic
        return rawKey;
    }
}
