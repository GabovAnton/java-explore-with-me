package ru.practicum.event;

public enum StateAdminAction {
    PUBLISH_EVENT("PUBLISH_EVENT"),

    REJECT_EVENT("REJECT_EVENT");

    private String value;

    StateAdminAction(String value) {

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
