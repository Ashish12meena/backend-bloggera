package com.example.Blogera_demo.utility;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActionType {
    LIKE(0), COMMENT(1), FOLLOW(2);

    private final int value;

    ActionType(int value) {
        this.value = value;
    }

    @JsonValue // Ensures this integer is stored in MongoDB instead of the Enum nam
    public int getValue() {
        return value;
    }

    @JsonCreator // Converts JSON integer back to Enum when reading from MongoDB
    public static ActionType fromValue(int value) {
        for (ActionType type : ActionType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid action type: " + value);
    }

}
