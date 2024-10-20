package com.kamylo.Scrtly_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImplementation {
    public String saveFile(MultipartFile image, String directory) {
        try {

            Path folderPath = Paths.get("src/main/resources/static" + directory);

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving image file.", e);
        }
    }

    public String updateFile(MultipartFile newImage, String existingFileName, String directory) {
        try {
            // Ścieżka do katalogu
            Path folderPath = Paths.get("src/main/resources/static" + directory);

            // Sprawdzenie czy katalog istnieje
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String imagePath = "src/main/resources/static" + existingFileName;
            Path filePath = Paths.get(imagePath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // Generowanie nowej nazwy pliku
            String newFileName = UUID.randomUUID().toString() + "_" + newImage.getOriginalFilename();
            Path newFilePath = folderPath.resolve(newFileName);

            // Zapisanie nowego pliku
            Files.copy(newImage.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Error updating image file.", e);
        }
    }


    public void deleteFile(String directory) {
        try {

            String imagePath = "src/main/resources/static" + directory;
            Path filePath = Paths.get(imagePath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error deleting image file.", e);
        }
    }
}
