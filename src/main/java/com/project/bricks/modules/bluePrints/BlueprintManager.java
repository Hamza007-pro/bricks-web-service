package com.project.bricks.modules.bluePrints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bricks.models.Blueprint;
import jakarta.persistence.criteria.From;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BlueprintManager {

    private final List<Blueprint> blueprints;
    private final String blueprintDirectory;
    private final ObjectMapper objectMapper;

    public BlueprintManager(String blueprintDirectory, ObjectMapper objectMapper) {
        this.blueprintDirectory = blueprintDirectory;
        this.blueprints = new ArrayList<>();
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<Void> loadAllBlueprintsAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                Path directory = Paths.get(blueprintDirectory);
                if (!Files.exists(directory)) {
                    Files.createDirectory(directory);
                }

                Files.list(directory)
                        .forEach(path -> {
                            try {
                                Blueprint blueprint = loadBlueprintAsync(path.toString()).join();
                                if (blueprint != null) {
                                    blueprints.add(blueprint);
                                }
                            } catch (Exception e) {
                                System.out.println("Error loading blueprint from " + path + ": " + e.getMessage());
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException("Error loading blueprints: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Blueprint> loadBlueprintAsync(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (filePath.endsWith(".json")) {
                    return loadFromJsonAsync(filePath).join();
                } else if (filePath.endsWith(".xml")) {
                    return loadFromXml(filePath);
                }
            } catch (Exception ex) {
                System.out.println("Error loading blueprint from " + filePath + ": " + ex.getMessage());
            }
            return null;
        });
    }

    public List<Blueprint> listBlueprints() {
        return new ArrayList<>(blueprints);
    }

    public Blueprint getBlueprintById(String id) {
        return blueprints.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public CompletableFuture<Void> exportBlueprintAsync(Blueprint blueprint, String format) {
        return CompletableFuture.runAsync(() -> {
            String filePath = Paths.get(blueprintDirectory, blueprint.getName() + "." + format).toString();
            try {
                if (format.toLowerCase().equals("json")) {
                    saveToJsonAsync(blueprint, filePath).join();
                } else if (format.toLowerCase().equals("xml")) {
                    saveToXml(blueprint, filePath);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error exporting blueprint: " + e.getMessage());
            }
        });
    }

    public boolean deleteBlueprint(String id) {
        return blueprints.removeIf(b -> b.getId().equals(id));
    }

    private CompletableFuture<Blueprint> loadFromJsonAsync(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonContent = Files.readString(Paths.get(filePath));
                return objectMapper.readValue(jsonContent, Blueprint.class);
            } catch (IOException e) {
                throw new RuntimeException("Error loading JSON: " + e.getMessage());
            }
        });
    }

    private CompletableFuture<Void> saveToJsonAsync(Blueprint blueprint, String filePath) {
        return CompletableFuture.runAsync(() -> {
            try {
                objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValue(new File(filePath), blueprint);
            } catch (IOException e) {
                throw new RuntimeException("Error saving JSON: " + e.getMessage());
            }
        });
    }

    private Blueprint loadFromXml(String filePath) {
        try {
            JAXBContext context = JAXBContext.newInstance(Blueprint.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            try (FileReader reader = new FileReader(filePath)) {
                return (Blueprint) unmarshaller.unmarshal(reader);
            }
        } catch (JAXBException | IOException e) {
            throw new RuntimeException("Error loading XML: " + e.getMessage());
        }
    }

    private void saveToXml(Blueprint blueprint, String filePath) {
        try {
            JAXBContext context = JAXBContext.newInstance(Blueprint.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            try (FileWriter writer = new FileWriter(filePath)) {
                marshaller.marshal(blueprint, writer);
            }
        } catch (JAXBException | IOException e) {
            throw new RuntimeException("Error saving XML: " + e.getMessage());
        }
    }
}
