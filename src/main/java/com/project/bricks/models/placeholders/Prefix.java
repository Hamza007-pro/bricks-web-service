package com.project.bricks.models.placeholders;

import lombok.Data;

import java.lang.reflect.Array;
import java.util.List;

@Data
public class Prefix {

    private String key;
    public String[] params;
    private String leftSideIndicator;
    private String rightSideIndicator;

    public Prefix(String value) {
        this.key = "key";
        this.leftSideIndicator = "{{";
        this.rightSideIndicator = "}}";
        this.params = new String[0];
        Extract(value);
    }

    private void Extract(String value) {
        if(value.startsWith(leftSideIndicator) && value.endsWith(rightSideIndicator)) {
            value = value.replace(leftSideIndicator, "").replace(rightSideIndicator, "");
            var parts = value.split(":");
            key = parts[0];
            if (parts.length == 2) {
                params = parts[1].split(",");
            }
        } else {
            throw new IllegalArgumentException("Invalid prefix format");
        }
    }

    @Override
    public String toString() {
        return leftSideIndicator + key + rightSideIndicator;
    }
}
