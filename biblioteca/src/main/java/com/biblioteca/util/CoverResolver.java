package com.biblioteca.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class CoverResolver {

    private CoverResolver() {}

    private static final Path DATA_COVERS_DIR = Path.of(
            System.getProperty("user.home"), ".biblioteca", "covers");

    public static Path resolve(String coverPath) {
        if (coverPath == null || coverPath.isBlank()) {
            return null;
        }

        Path filePath = Path.of(coverPath);
        if (filePath.isAbsolute()) {
            return Files.exists(filePath) ? filePath : null;
        }

        String fileName = filePath.getFileName().toString();
        Path dataPath = DATA_COVERS_DIR.resolve(fileName);
        if (Files.exists(dataPath)) {
            return dataPath;
        }

        extractFromResources(fileName, dataPath);
        return Files.exists(dataPath) ? dataPath : null;
    }

    private static void extractFromResources(String fileName, Path target) {
        String resourcePath = "/covers/" + fileName;
        try (InputStream is = CoverResolver.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                return;
            }
            Files.createDirectories(target.getParent());
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
        }
    }
}
