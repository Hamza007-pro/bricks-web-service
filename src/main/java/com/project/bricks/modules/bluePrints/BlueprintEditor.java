package com.project.bricks.modules.bluePrints;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bricks.models.Blueprint;
import com.project.bricks.models.Brick;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BlueprintEditor {

    private final ObjectMapper objectMapper;

    public BlueprintEditor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public ValidationResult validateBlueprint(String filePath) {
        try{
            if (filePath.endsWith(".json")) {
              return ValidateJson(filePath);
            }else if (filePath.endsWith(".xml")) {
              return ValidateXml(filePath);
            }else {
                return new ValidationResult(false, List.of("Unsupported file format. Only JSON and XML/XAML are supported."));
            }
        }catch (Exception e){
            return new ValidationResult(false, List.of("An error occurred while validating the blueprint: " + e.getMessage()));
        }
    }

    private ValidationResult ValidateJson(String filePath) {
        // Validate JSON
        var errors = new ArrayList<String>();
        try {
            var jsonContent = Files.readAllBytes(Paths.get(filePath));
            Blueprint blueprint = objectMapper.readValue(jsonContent, Blueprint.class);
            if (blueprint == null) {
                errors.add("Failed to parse JSON blueprint.");
            } else {
                validateBlueprintStructure(blueprint, errors);
            }
        }catch (Exception e){
            errors.add("Error reading JSON file: " + e.getMessage());
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    private ValidationResult ValidateXml(String filePath) {
        // Validate XML
        var errors = new ArrayList<String>();
        try {
            JAXBContext context = JAXBContext.newInstance(Blueprint.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Blueprint blueprint = (Blueprint) unmarshaller.unmarshal(new FileReader(filePath));

            if (blueprint == null) {
                errors.add("Failed to parse XML blueprint.");
            } else {
                validateBlueprintStructure(blueprint, errors);
            }
        } catch (JAXBException ex) {
            errors.add("XML syntax error: " + ex.getMessage());
        } catch (Exception ex) {
            errors.add("XML parsing error: " + ex.getMessage());
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }
    private void validateBlueprintStructure(Blueprint blueprint, List<String> errors) {
        if (isNullOrEmpty(blueprint.getName())) {
            errors.add("Blueprint 'Name' is required and cannot be empty.");
        }

        if (isNullOrEmpty(blueprint.getDescription())) {
            errors.add("Blueprint 'Description' is required and cannot be empty.");
        }

        if (blueprint.getBricks() == null || blueprint.getBricks().isEmpty()) {
            errors.add("Blueprint must contain at least one 'Brick'.");
        }

        if (blueprint.getBricks() != null) {
            for (Brick brick : blueprint.getBricks()) {
                if (isNullOrEmpty(brick.getName())) {
                    errors.add("Each Brick must have a 'Name'.");
                }

                if (isNullOrEmpty(brick.getTemplatePath())) {
                    errors.add("Brick '" + brick.getName() + "' must have a valid 'TemplatePath'.");
                }

                if (isNullOrEmpty(brick.getOutputPath())) {
                    errors.add("Brick '" + brick.getName() + "' must have a valid 'OutputPath'.");
                }
            }
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
