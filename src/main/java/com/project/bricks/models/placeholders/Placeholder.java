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
