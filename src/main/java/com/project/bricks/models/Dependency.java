package com.project.bricks.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;


public class Dependency {

    private String Id;
    private String name;
    private String description;
    private boolean isRequired;
    @Transient
    public Function<Boolean,Boolean> validationFunction;

    public Dependency(String name, String description, boolean isRequired, Function<Boolean,Boolean> validationFunction) {
        this.name = name;
        this.description = description;
        this.isRequired = isRequired;
        this.validationFunction = validationFunction;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public Function<Boolean, Boolean> getValidationFunction() {
        return validationFunction;
    }

    public void setValidationFunction(Function<Boolean, Boolean> validationFunction) {
        this.validationFunction = validationFunction;
    }

    public boolean isMet(){
        if(validationFunction != null){
            return validationFunction.apply(true);
        }
        throw new UnsupportedOperationException("Dependency must have a validation function for evaluation");
    }
}
