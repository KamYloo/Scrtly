package com.kamylo.Scrtly_backend.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveFile(MultipartFile file, String directory);
    String updateFile(MultipartFile file, String existingFileName, String directory);
    void deleteFile(String filePath);
}
