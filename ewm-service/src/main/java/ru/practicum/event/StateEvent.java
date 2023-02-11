package ru.practicum.event;

public enum StateEvent {
    PENDING("PENDING"),

    PUBLISHED("PUBLISHED"),

    CANCELED("CANCELED");

    private String value;

    StateEvent(String value) {

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
