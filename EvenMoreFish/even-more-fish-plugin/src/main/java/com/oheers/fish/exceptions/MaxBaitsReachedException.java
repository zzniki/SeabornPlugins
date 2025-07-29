package com.oheers.fish.exceptions;

import com.oheers.fish.baits.ApplicationResult;
import com.oheers.fish.messages.ConfigMessage;
import org.jetbrains.annotations.NotNull;

public class MaxBaitsReachedException extends ConfigMessageException {
    //BAITS_MAXED_ON_ROD
    private final ApplicationResult recoveryResult;

    public MaxBaitsReachedException(@NotNull String errorMessage, @NotNull ApplicationResult recoveryResult) {
        super(errorMessage, ConfigMessage.BAITS_MAXED_ON_ROD);
        this.recoveryResult = recoveryResult;
    }

    public @NotNull ApplicationResult getRecoveryResult() {
        return recoveryResult;
    }
}
