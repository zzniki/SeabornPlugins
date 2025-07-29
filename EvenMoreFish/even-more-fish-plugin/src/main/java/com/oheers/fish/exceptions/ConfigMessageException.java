package com.oheers.fish.exceptions;

import com.oheers.fish.messages.ConfigMessage;

public class ConfigMessageException extends Exception {
    private final ConfigMessage configMessage;

    public ConfigMessageException(ConfigMessage configMessage) {
        this.configMessage = configMessage;
    }

    public ConfigMessageException(String message, ConfigMessage configMessage) {
        super(message);
        this.configMessage = configMessage;
    }

    public ConfigMessage getConfigMessage() {
        return configMessage;
    }
}
