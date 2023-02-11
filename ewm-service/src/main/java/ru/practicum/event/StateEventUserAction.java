package ru.practicum.event;

public enum StateEventUserAction {
    SEND_TO_REVIEW("SEND_TO_REVIEW"),

    CANCEL_REVIEW("CANCEL_REVIEW");

    private String value;

    StateEventUserAction(String value) {

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
