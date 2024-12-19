package com.project.bricks.modules.templateEngine;


import com.project.bricks.exceptions.TemplateProcessingException;
import com.project.bricks.models.placeholders.Placeholder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateEngine {
    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);

    private final Map<String, String> partials = new HashMap<>();

    // Method to load a template from a file
    public String loadTemplate(String templatePath) {
        try {
            return Files.readString(Paths.get(templatePath));
        } catch (IOException ex) {
            logger.error("Error loading template: {}", ex.getMessage(), ex);
            throw new TemplateProcessingException("Failed to load template: " + templatePath, ex);
        }
    }

    // Registers a partial template that can be included in other templates
    public void registerPartial(String name, String content) {
        partials.put(name, content);
    }

    // Applies partial templates within the main template
    public String applyPartials(String templateContent) {
        Pattern pattern = Pattern.compile("\\{\\{>(\\w+)}}");
        Matcher matcher = pattern.matcher(templateContent);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String partialName = matcher.group(1);
            String replacement = partials.getOrDefault(partialName, "");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    // Applies conditional blocks based on placeholder values
    public String applyConditionals(String templateContent, List<Placeholder> placeholders) {
        Pattern pattern = Pattern.compile("\\{\\{#if (\\w+)}}(.*?)\\{\\{/if}}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(templateContent);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String conditionName = matcher.group(1);
            String content = matcher.group(2);

            String replacement = isConditionMet(conditionName, placeholders) ? content : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private boolean isConditionMet(String conditionName, List<Placeholder> placeholders) {
        return placeholders.stream()
                .filter(p -> p.getKey().equals(conditionName))
                .findFirst()
                .map(p -> {
                    Object generated = p.generate(null);
                    return generated != null && StringUtils.hasText(generated.toString());
                })
                .orElse(false);
    }

    // Applies placeholders to a template and generates final content
    public String applyPlaceholders(String templateContent, List<Placeholder> placeholders) {
        String processedContent = templateContent;
        for (Placeholder placeholder : placeholders) {
            Object resolvedValue = placeholder.generate(null);
            if (resolvedValue != null) {
                processedContent = processedContent.replace(
                        placeholder.getPrefix().toString(),
                        resolvedValue.toString()
                );
            }
        }
        return processedContent;
    }

    // Processes the entire template by applying partials, conditionals, and placeholders
    public String processTemplate(String templatePath, List<Placeholder> placeholders) {
        String templateContent = loadTemplate(templatePath);

        templateContent = applyPartials(templateContent);
        templateContent = applyConditionals(templateContent, placeholders);
        templateContent = applyPlaceholders(templateContent, placeholders);

        return templateContent;
    }

    // Method to save the processed content to a file
    public void saveToFile(String outputPath, String fileName, String content) {
        try {
            Path dirPath = Paths.get(outputPath);
            Files.createDirectories(dirPath);

            Path fullPath = dirPath.resolve(fileName);
            Files.writeString(fullPath, content);

            logger.info("File generated: {}", fullPath);
        } catch (IOException ex) {
            logger.error("Error saving file: {}", ex.getMessage(), ex);
            throw new TemplateProcessingException("Failed to save file: " + fileName, ex);
        }
    }
}
