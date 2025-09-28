package com.mttk.orche.addon.agent;

public enum MessageRole {

    USER("user"),
    SYSTEM("system"),
    ASSISTANT("assistant"),
    TOOL("tool");

    private final String value;

    MessageRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}