package com.project.bricks.modules.fileGenerator;

import com.project.bricks.exceptions.FileGeneratorException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@Service
public class FileGenerator {
    private static final Logger logger = LoggerFactory.getLogger(FileGenerator.class);

    public void ensureDirectoryExists(String path) {
        try {
            Path dirPath = Paths.get(path);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                logger.info("Directory created: {}", path);
            }
        } catch (IOException ex) {
            logger.error("Error creating directory '{}': {}", path, ex.getMessage(), ex);
            throw new FileGeneratorException("Failed to create directory: " + path, ex);
        }
    }

    public void generateFile(String outputPath, String fileName, String content, boolean overwrite) {
        try {
            ensureDirectoryExists(outputPath);
            Path fullPath = Paths.get(outputPath, fileName);

            if (Files.exists(fullPath) && !overwrite) {
                logger.warn("File '{}' already exists at '{}'. Skipping generation.", fileName, outputPath);
                return;
            }

            Files.writeString(fullPath, content);
            logger.info("File generated: {}", fullPath);

        } catch (IOException ex) {
            logger.error("Error generating file '{}' at '{}': {}", fileName, outputPath, ex.getMessage(), ex);
            throw new FileGeneratorException(
                    String.format("Failed to generate file '%s' at '%s'", fileName, outputPath),
                    ex
            );
        }
    }

    public void generateFiles(List<FileGenerationRequest> files, boolean overwrite) {
        files.forEach(file ->
                generateFile(file.getOutputPath(), file.getFileName(), file.getContent(), overwrite)
        );
    }


}
