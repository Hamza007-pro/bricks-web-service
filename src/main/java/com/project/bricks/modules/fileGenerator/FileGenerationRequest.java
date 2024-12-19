package com.project.bricks.modules.fileGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class FileGenerationRequest {
    private String outputPath;
    private String fileName;
    private String content;

    public FileGenerationRequest(String outputPath, String fileName, String content) {
        this.outputPath = outputPath;
        this.fileName = fileName;
        this.content = content;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
