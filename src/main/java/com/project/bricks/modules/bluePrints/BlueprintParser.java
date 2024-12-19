package com.project.bricks.modules.bluePrints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.project.bricks.models.Blueprint;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BlueprintParser {
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;

    public BlueprintParser() {
        // Initialize JSON mapper
        this.jsonMapper = new ObjectMapper();

        // Initialize YAML mapper with CamelCase convention
        YAMLFactory yamlFactory = new YAMLFactory()
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        this.yamlMapper = new ObjectMapper(yamlFactory);
    }

    public Blueprint parseFromJson(String filePath) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
            return jsonMapper.readValue(jsonContent, Blueprint.class);
        } catch (Exception ex) {
            System.out.println("Error parsing JSON blueprint: " + ex.getMessage());
            return null;
        }
    }

    public Blueprint parseFromYaml(String filePath) {
        try {
            String yamlContent = new String(Files.readAllBytes(Paths.get(filePath)));
            return yamlMapper.readValue(yamlContent, Blueprint.class);
        } catch (Exception ex) {
            System.out.println("Error parsing YAML blueprint: " + ex.getMessage());
            return null;
        }
    }

    public void saveToJson(Blueprint blueprint, String filePath) {
        try {
            jsonMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filePath), blueprint);
            System.out.println("Blueprint saved to JSON file: " + filePath);
        } catch (Exception ex) {
            System.out.println("Error saving blueprint to JSON: " + ex.getMessage());
        }
    }

    public void saveToYaml(Blueprint blueprint, String filePath) {
        try {
            yamlMapper.writeValue(new File(filePath), blueprint);
            System.out.println("Blueprint saved to YAML file: " + filePath);
        } catch (Exception ex) {
            System.out.println("Error saving blueprint to YAML: " + ex.getMessage());
        }
    }
}
