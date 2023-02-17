package ru.practicum.notification;

public enum NotificationType {
    NEW("NEW"),

    CHANGE("CHANGE"),

    DELETE("DELETE");

    private String value;

    NotificationType(String value) {

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
