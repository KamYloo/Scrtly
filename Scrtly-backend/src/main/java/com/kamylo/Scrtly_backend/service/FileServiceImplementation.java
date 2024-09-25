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
