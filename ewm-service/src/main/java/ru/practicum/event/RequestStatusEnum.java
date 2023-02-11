package ru.practicum.event;

public enum RequestStatusEnum {
    CONFIRMED("CONFIRMED"),

    REJECTED("REJECTED"),

    PENDING("PENDING"),

    CANCELED("CANCELED");

    private String value;

    RequestStatusEnum(String value) {

        this.value = value;
    }

    public String getValue() {

        return value;
    }

    @Override
    public String toString() {

        return String.valueOf(value);
    }
}
