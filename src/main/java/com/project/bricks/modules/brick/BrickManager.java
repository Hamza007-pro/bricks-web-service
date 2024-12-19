package com.project.bricks.modules.brick;

import com.project.bricks.models.Brick;
import com.project.bricks.modules.templateEngine.TemplateEngine;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BrickManager {
    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    private List<Brick> bricks;

    public BrickManager() {
        this.bricks = new ArrayList<Brick>();
    }

    public void addBrick(Brick brick) {
        if(bricks.contains(brick)) {
            logger.warn("Brick already exists in the list: {}", brick.getName());
            return;
        }
        bricks.add(brick);
        logger.info("Brick added successfully: {}", brick.getName());
    }

    public Brick getBrick(String name) {
        return bricks.stream()
                .filter(brick -> brick.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void updateBrick(String name,Brick updatedBrick) {
        var existingBrick = getBrick(name);
        if(existingBrick != null){
            existingBrick.setTemplatePath(updatedBrick.getTemplatePath());
            existingBrick.setOutputPath(updatedBrick.getOutputPath());
            existingBrick.setFileNameTemplate(updatedBrick.getFileNameTemplate());
            existingBrick.setDescription(updatedBrick.getDescription());
            existingBrick.setDependencies(updatedBrick.getDependencies());
            existingBrick.setPlaceholders(updatedBrick.getPlaceholders());
            existingBrick.setPreGenerationHooks(updatedBrick.getPreGenerationHooks());
            existingBrick.setPostGenerationHooks(updatedBrick.getPostGenerationHooks());

            logger.info("Brick updated successfully: {}", name);
        } else {
            logger.error("Brick not found: {}", name);
        }
    }

    public void deleteBrick(String name) {
        var existingBrick = getBrick(name);
        if(existingBrick != null){
            bricks.remove(existingBrick);
            logger.info("Brick deleted successfully: {}", name);
        } else {
            logger.error("Brick not found: {}", name);
        }
    }

    public List<Brick> listBricks() {
        if(bricks.isEmpty()) {
            logger.warn("No bricks found.");
        }
        logger.info("Listing all bricks.");
        for (Brick brick : bricks) {
            logger.info("Brick: {}", brick.getName() + " - " + brick.getDescription());
        }
        return bricks;
    }

    public void saveBricksToFile(String path) {
        logger.info("Saving bricks to file: {}", path);
        // Save bricks to file code
    }

    public void loadBricksFromFile(String path) {
        logger.info("Loading bricks from file: {}", path);
        // Load bricks from file code
    }
}
