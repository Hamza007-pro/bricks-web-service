package com.project.bricks.modules.fileGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileGenerationRequest {
    private String outputPath;
    private String fileName;
    private String content;

}
