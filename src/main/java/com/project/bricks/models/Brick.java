package com.project.bricks.models;

import com.project.bricks.exceptions.BrickGenerationException;
import com.project.bricks.models.placeholders.Placeholder;
import com.project.bricks.modules.templateEngine.TemplateEngine;
import lombok.Data;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Brick {

    private String id;
    private String name;
    private String templatePath;
    private List<Placeholder> placeholders;
    private String outputPath;
    private String fileNameTemplate;
    private String description;
    private List<Dependency> dependencies;
    private List<Hook> preGenerationHooks;
    private List<Hook> postGenerationHooks;
    private TemplateEngine templateEngine = null;
    private static final Logger logger = LoggerFactory.getLogger(Brick.class);

    public Brick(String name, String templatePath, String outputPath, String fileNameTemplate, String description) {
        this.templateEngine = templateEngine;
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.templatePath = templatePath;
        this.outputPath = outputPath;
        this.fileNameTemplate = fileNameTemplate;
        this.description = description;
        this.placeholders = new ArrayList<Placeholder>();
        this.dependencies = new ArrayList<Dependency>();
        this.preGenerationHooks = new ArrayList<Hook>();
        this.postGenerationHooks = new ArrayList<Hook>();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public List<Placeholder> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(List<Placeholder> placeholders) {
        this.placeholders = placeholders;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getFileNameTemplate() {
        return fileNameTemplate;
    }

    public void setFileNameTemplate(String fileNameTemplate) {
        this.fileNameTemplate = fileNameTemplate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public List<Hook> getPreGenerationHooks() {
        return preGenerationHooks;
    }

    public void setPreGenerationHooks(List<Hook> preGenerationHooks) {
        this.preGenerationHooks = preGenerationHooks;
    }

    public List<Hook> getPostGenerationHooks() {
        return postGenerationHooks;
    }

    public void setPostGenerationHooks(List<Hook> postGenerationHooks) {
        this.postGenerationHooks = postGenerationHooks;
    }

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    @Async
    public CompletableFuture<Void> generateAsync(){
        logger.info("Starting generation for Brick: {}", name);
        return CompletableFuture.runAsync(() -> {
            try {
                if (!validateDependencies()) {
                    logger.error("Cannot proceed: Unmet dependencies.");
                    return;
                }

                // Run pre-generation hooks
                runHooks(preGenerationHooks, "Pre-Generation")
                        .thenCompose(v -> processTemplate())
                        .thenCompose(v -> runHooks(postGenerationHooks, "Post-Generation"))
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                logger.error("Error during brick generation: {}", ex.getMessage(), ex);
                            } else {
                                logger.info("Generation for Brick {} completed successfully", name);
                            }
                        });

            } catch (Exception e) {
                logger.error("Unexpected error during brick generation: {}", e.getMessage(), e);
                throw new BrickGenerationException("Failed to generate brick: " + name, e);
            }
        });
    }
    private CompletableFuture<Void> processTemplate() {
        return CompletableFuture.runAsync(() -> {
            try {
                String templateContent = templateEngine.loadTemplate(templatePath);
                templateContent = templateEngine.applyPlaceholders(templateContent, placeholders);

                String dynamicOutputPath = buildDynamicPath(outputPath);
                String outputFileName = buildDynamicPath(fileNameTemplate);

                Files.createDirectories(Paths.get(dynamicOutputPath));

                templateEngine.saveToFile(dynamicOutputPath, outputFileName, templateContent);

                logger.info("Generated file: {}/{}", dynamicOutputPath, outputFileName);
            } catch (Exception e) {
                logger.error("Error processing template: {}", e.getMessage(), e);
                throw new BrickGenerationException("Template processing failed", e);
            }
        });
    }
    private String buildDynamicPath(String path) {
        return templateEngine.applyPlaceholders(path, placeholders);
    }
    private boolean validateDependencies() {
        for (Dependency dependency : dependencies) {
            if (dependency.isRequired() && !dependency.isMet()) {
                logger.error("Missing required dependency: {}", dependency.getName());
                return false;
            }
        }
        return true;
    }
    private CompletableFuture<Void> runHooks(List<Hook> hooks, String hookType) {
        logger.info("Running {} Hooks", hookType);

        return CompletableFuture.allOf(
                hooks.stream()
                        .map(hook -> {
                            logger.info("Running {} Hook: {}", hookType, hook.getName());
                            return hook.executeAsync();
                        })
                        .toArray(CompletableFuture[]::new)
        );
    }
}
