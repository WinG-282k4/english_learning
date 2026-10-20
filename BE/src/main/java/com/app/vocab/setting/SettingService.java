package com.app.vocab.setting;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {

    private final AppSettingRepository appSettingRepository;

    public SettingService(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    public List<SettingDtos.Item> getAllSettings() {
        // TODO: retrieve all settings
        return null;
    }

    public SettingDtos.Item getSetting(String key) {
        // TODO: retrieve single setting
        return null;
    }

    public SettingDtos.Item updateSetting(String key, String value) {
        // TODO: update or insert setting
        return null;
    }
}
