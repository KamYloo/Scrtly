package com.kamylo.Scrtly_backend.common.service.impl;

import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${application.file.image-dir}")
    private String imageDir;

    @Value("${application.file.cdn}")
    private String cdnBaseUrl;

    @Override
    public String saveFile(MultipartFile file, String directory) {
        try {
            Path folderPath = Paths.get(imageDir + directory);

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return cdnBaseUrl+directory+fileName;
        } catch (IOException e) {
            throw new CustomException(BusinessErrorCodes.IMAGE_FETCH_FAILED);
        }
    }

    @Override
    public String updateFile(MultipartFile file, String existingFileName, String directory) {
        try {
            if (existingFileName != null && !existingFileName.isEmpty() && existingFileName.startsWith(cdnBaseUrl)) {
                existingFileName = existingFileName.replace(cdnBaseUrl, "");
            }

            Path folderPath = Paths.get(imageDir + directory);

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String imagePath = imageDir + existingFileName;
            Path filePath = Paths.get(imagePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            String newFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path newFilePath = folderPath.resolve(newFileName);

            Files.copy(file.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

            return cdnBaseUrl+directory+newFileName;
        } catch (IOException e) {
            throw new CustomException(BusinessErrorCodes.IMAGE_NOT_FOUND);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {

            if (filePath.startsWith(cdnBaseUrl)) {
                filePath = filePath.replace(cdnBaseUrl, "");
            }

            Path file = Paths.get(imageDir+filePath);
            if (Files.exists(file)) {
                Files.delete(file);
            }

        } catch (IOException e) {
            throw new CustomException(BusinessErrorCodes.IMAGE_NOT_FOUND);
        }
    }
}
