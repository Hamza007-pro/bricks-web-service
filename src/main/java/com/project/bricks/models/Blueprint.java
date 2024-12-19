package com.project.bricks.models;

import com.project.bricks.exceptions.BlueprintGenerationException;
import com.project.bricks.models.placeholders.Placeholder;
import com.project.bricks.modules.templateEngine.TemplateEngine;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class Blueprint {

    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);

    private String Id;
    private String name;
    private String description;
    private List<Brick> bricks;
    private Map<String,String> configuration;
    private List<Placeholder> placeholders;
    private List<Dependency> dependencies;
    private List<Hook> preGenerationHooks;
    private List<Hook> postGenerationHooks;
    private String outputDirectoryStructure;

    public Blueprint(String name, String description, String outputDirectoryStructure) {
        this.name = name;
        this.description = description;
        this.bricks = new ArrayList<Brick>();
        this.placeholders = new ArrayList<Placeholder>();
        this.dependencies = new ArrayList<Dependency>();
        this.preGenerationHooks = new ArrayList<Hook>();
        this.postGenerationHooks = new ArrayList<Hook>();
        this.outputDirectoryStructure = outputDirectoryStructure;
    }

    public boolean validateDependencies() {
        for (Dependency dependency : dependencies) {
            if(dependency.isRequired() && !CheckDependencyMet(dependency)){
                logger.error("Missing required dependency: {}", dependency.getName());
                return false;
            }
        }
        logger.info("All dependencies met.");
        return true;
    }

    private boolean CheckDependencyMet(Dependency dependency) {
        return dependency.getValidationFunction() != null ? dependency.getValidationFunction().apply(false) : true;
    }

    public CompletableFuture<Void> runPreGenerationHooksAsync(){
        logger.warn("Running pre-generation hooks for Blueprint: {}", name);

        preGenerationHooks.sort((h1, h2) -> Integer.compare(h1.getExecutionOrder(), h2.getExecutionOrder()));

        CompletableFuture<Void> allHooks = CompletableFuture.completedFuture(null);
        for (Hook hook : preGenerationHooks) {
            if (hook.getAction() != null) {
                logger.info("Running pre-generation hook: {}", hook.getName());
                allHooks = allHooks.thenCompose(aVoid -> hook.executeAsync());
            }
        }
        return allHooks;
    }

    public CompletableFuture<Void> runPostGenerationHooksAsync(){
        logger.warn("Running post-generation hooks for Blueprint: {}", name);

        postGenerationHooks.sort((h1, h2) -> Integer.compare(h1.getExecutionOrder(), h2.getExecutionOrder()));

        CompletableFuture<Void> allHooks = CompletableFuture.completedFuture(null);
        for (Hook hook : postGenerationHooks) {
            if (hook.getAction() != null) {
                logger.info("Running post-generation hook: {}", hook.getName());
                allHooks = allHooks.thenCompose(aVoid -> hook.executeAsync());
            }
        }
        return allHooks;
    }

    public CompletableFuture<Void> executeAsync() {
        logger.info("Executing Blueprint: " + name);

        if (!validateDependencies()) {
            logger.error("Cannot proceed: Unmet dependencies.");
            return CompletableFuture.completedFuture(null);
        }

        return runPreGenerationHooksAsync()
                .thenCompose(v -> {
                    logger.warn("Generating code for bricks...");
                    List<CompletableFuture<Void>> brickFutures = new ArrayList<>();
                    int totalBricks = bricks.size();

                    for (int i = 0; i < bricks.size(); i++) {
                        Brick brick = bricks.get(i);
                        int index = i + 1;

                        CompletableFuture<Void> brickFuture = brick.generateAsync()
                                .thenRun(() -> {
                                    logger.info("Successfully generated: " + brick.getName());
                                    float progress = (100f / totalBricks) * index;
                                    logger.info("Progress: " + progress + "%");
                                })
                                .exceptionally(ex -> {
                                    logger.error("Failed to generate: " + brick.getName() +
                                            ". Error: " + ex.getMessage());
                                    return null;
                                });

                        brickFutures.add(brickFuture);
                    }

                    return CompletableFuture.allOf(brickFutures.toArray(new CompletableFuture[0]));
                })
                .thenCompose(v -> runPostGenerationHooksAsync())
                .thenRun(() -> logger.info("Blueprint " + name + " execution completed."));
    }



}
