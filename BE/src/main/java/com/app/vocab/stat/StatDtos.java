package com.app.vocab.stat;

import java.time.Instant;

public final class StatDtos {
    private StatDtos() {}

    public record WrongWordView(
        String termNorm,
        int wrongCount,
        int correctCount,
        int correctSinceWrong,
        boolean isActiveWrong,
        Instant lastWrongAt
    ) {}
}
