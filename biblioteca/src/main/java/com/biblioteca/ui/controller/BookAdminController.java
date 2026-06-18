package com.biblioteca.ui.controller;

import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.domain.Location;
import com.biblioteca.service.InventoryService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class BookAdminController {
    private final InventoryService inventoryService;

    public BookAdminController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void registerBook(BookTitle bookTitle, BookCopy bookCopy, Location location) {
        inventoryService.registerBook(bookTitle, bookCopy, location);
    }

    public void updateBook(BookTitle bookTitle) {
        inventoryService.updateBook(bookTitle);
    }

    public void updateBookEntry(BookTitle bookTitle, BookCopy bookCopy, Location location) {
        inventoryService.updateBookEntry(bookTitle, bookCopy, location);
    }

    public void changeCopyStatus(Long copyId, CopyStatus status) {
        inventoryService.changeCopyStatus(copyId, status);
    }

    public String storeCoverImage(Path sourceFile, String isbn) {
        if (sourceFile == null) {
            return null;
        }

        String fileNameBase = (isbn == null || isbn.isBlank())
                ? "cover-" + UUID.randomUUID()
                : sanitizeFileName(isbn.trim());
        String extension = extractExtension(sourceFile.getFileName().toString());

        Path targetDirectory = Path.of(System.getProperty("user.home"), ".biblioteca", "portadas");
        Path targetFile = targetDirectory.resolve(fileNameBase + extension);

        try {
            Files.createDirectories(targetDirectory);
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            return targetFile.toAbsolutePath().toString();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo copiar la portada seleccionada.", exception);
        }
    }

    private String sanitizeFileName(String input) {
        return input.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return ".png";
        }
        return fileName.substring(dotIndex);
    }
}
