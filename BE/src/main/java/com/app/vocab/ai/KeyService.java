package com.app.vocab.ai;

import com.app.vocab.config.CryptoUtil;
import com.app.vocab.llm.LlmProviderType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyService {

    private final AiKeyRepository aiKeyRepository;
    private final CryptoUtil cryptoUtil;

    public KeyService(AiKeyRepository aiKeyRepository, CryptoUtil cryptoUtil) {
        this.aiKeyRepository = aiKeyRepository;
        this.cryptoUtil = cryptoUtil;
    }

    public List<AiDtos.KeyView> getAllKeys() {
        // TODO: retrieve keys, mask sensitive parts via cryptoUtil.maskKey()
        return null;
    }

    public AiDtos.KeyView saveKey(AiDtos.SaveKeyRequest request) {
        // TODO: encrypt raw key with cryptoUtil.encrypt() and save
        return null;
    }

    public AiDtos.KeyView updateKey(Long id, AiDtos.SaveKeyRequest request) {
        // TODO: update key details
        return null;
    }

    public void deleteKey(Long id) {
        // TODO: delete key
    }

    public String getDecryptedKey(LlmProviderType provider) {
        // TODO: fetch key and decrypt using cryptoUtil.decrypt()
        return null;
    }
}
