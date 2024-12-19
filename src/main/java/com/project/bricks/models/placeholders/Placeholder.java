package com.project.bricks.models.placeholders;

import com.project.bricks.models.Transformer;
import lombok.Data;

import java.util.function.Function;
import java.util.function.Predicate;

@Data
public class Placeholder implements IPlaceholder {

    private String key;
    private String name;
    private String description;
    private Object defaultValue;
    private boolean isRequired;
    private Object value;
    private Prefix prefix;
    private Function<Object, Object> extendedTransform;
    private Predicate<Object> Validate;

    public Placeholder(String key, String name, String description, Object defaultValue, boolean isRequired) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.isRequired = isRequired;
        this.prefix = new Prefix("{{" + key + "}}");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public void setPrefix(Prefix prefix) {
        this.prefix = prefix;
    }

    public Function<Object, Object> getExtendedTransform() {
        return extendedTransform;
    }

    public void setExtendedTransform(Function<Object, Object> extendedTransform) {
        this.extendedTransform = extendedTransform;
    }

    public Predicate<Object> getValidate() {
        return Validate;
    }

    public void setValidate(Predicate<Object> validate) {
        Validate = validate;
    }

    protected Object transform(Object value, String[] options) {
        // Apply predefined transformations
        Object transformedValue = Transformer.transform(value.toString(), options);

        // Apply custom transformation if defined
        if (extendedTransform != null) {
            transformedValue = extendedTransform.apply(transformedValue);
        }

        return transformedValue;
    }

    @Override
    public Object generate(Object value) {
        var effectiveValue = value != null ? value : defaultValue;

        if(Validate != null){
           if(!Validate.test(effectiveValue)){
               throw new IllegalArgumentException("Invalid value for placeholder");
           }
        }
        this.value = transform(effectiveValue, prefix.getParams());

        return this.value;
    }
}
