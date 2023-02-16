package ru.practicum.notification;

public enum NotificationMethod {
    EMAIL("EMAIL"),

    PORTAL("PORTAL");

    private String value;

    NotificationMethod(String value) {

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
