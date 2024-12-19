package com.project.bricks.models;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Transformer {
    public static Object transform(Object value, String[] options) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Only string values are supported for transformations.");
        }

        String strValue = (String) value;

        for (String option : options) {
            switch (option.toLowerCase()) {
                case "upper":
                    strValue = strValue.toUpperCase();
                    break;

                case "lower":
                    strValue = strValue.toLowerCase();
                    break;

                case "camel":
                    strValue = toCamelCase(strValue);
                    break;

                case "pascal":
                    strValue = toPascalCase(strValue);
                    break;

                case "snake":
                    strValue = toSnakeCase(strValue);
                    break;

                case "kebab":
                    strValue = toKebabCase(strValue);
                    break;

                case "capitalize":
                    strValue = capitalizeWords(strValue);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid transformation option: " + option);
            }
        }

        return strValue;
    }
    private static String toCamelCase(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String[] words = splitWords(value);
        StringBuilder sb = new StringBuilder(words[0].toLowerCase());

        for (int i = 1; i < words.length; i++) {
            sb.append(capitalize(words[i].toLowerCase()));
        }

        return sb.toString();
    }

    private static String toPascalCase(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String[] words = splitWords(value);
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            sb.append(capitalize(word.toLowerCase()));
        }

        return sb.toString();
    }

    private static String toSnakeCase(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String[] words = splitWords(value);
        return String.join("_", Arrays.stream(words)
                .map(String::toLowerCase)
                .collect(Collectors.toList()));
    }

    private static String toKebabCase(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String[] words = splitWords(value);
        return String.join("-", Arrays.stream(words)
                .map(String::toLowerCase)
                .collect(Collectors.toList()));
    }

    private static String capitalizeWords(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        String[] words = value.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(capitalize(word));
        }

        return result.toString();
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) +
                (str.length() > 1 ? str.substring(1).toLowerCase() : "");
    }

    private static String[] splitWords(String value) {
        // Split on camelCase, spaces, underscores, and hyphens
        return value.split("(?<!^)(?=[A-Z])|[\\s_-]+");
    }
}
