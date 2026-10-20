package com.app.vocab.setting;

import jakarta.validation.constraints.NotBlank;

public final class SettingDtos {
    private SettingDtos() {}

    public record Item(
        @NotBlank String key,
        @NotBlank String value
    ) {}
}
