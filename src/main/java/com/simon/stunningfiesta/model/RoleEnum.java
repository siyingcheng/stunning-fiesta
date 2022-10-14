package com.simon.stunningfiesta.model;

public enum RoleEnum {
    USER("ROLE_USER"),
    MANAGER("USER_MANAGER"),
    ADMIN("ADMIN");

    private final String name;

    RoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
