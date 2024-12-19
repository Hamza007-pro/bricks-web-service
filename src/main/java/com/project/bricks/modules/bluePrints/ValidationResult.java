package com.project.bricks.modules.bluePrints;

import java.util.List;

import com.project.bricks.modules.templateEngine.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ValidationResult {
    private boolean isValid;
    private List<String> errors;
    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);

    public ValidationResult(boolean isValid, List<String> errors) {
        this.isValid = isValid;
        this.errors = errors;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void displayErrors() {
       if (isValid){
           logger.info("Blueprint is valid.");
       }
         else {
              logger.error("Blueprint validation failed with the following errors:");
              for (String error : errors) {
                logger.error(error);
              }
         }
    }
}
