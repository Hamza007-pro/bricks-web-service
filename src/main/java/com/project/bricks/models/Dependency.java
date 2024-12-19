package com.project.bricks.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Data
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

    public boolean isMet(){
        if(validationFunction != null){
            return validationFunction.apply(true);
        }
        throw new UnsupportedOperationException("Dependency must have a validation function for evaluation");
    }
}
