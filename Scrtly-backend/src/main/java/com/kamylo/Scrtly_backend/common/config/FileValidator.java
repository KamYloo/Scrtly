package com.kamylo.Scrtly_backend.common.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FileValidator implements ConstraintValidator<FileConstraint, MultipartFile> {
    private Set<String> allowed;
    private int maxSizeKb;

    @Override
    public void initialize(FileConstraint constraintAnnotation) {
        allowed = Arrays.stream(constraintAnnotation.allowed()).collect(Collectors.toSet());
        maxSizeKb = constraintAnnotation.maxSizeKb();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return true;
        if (file.getSize() > maxSizeKb * 1024L) return false;
        String contentType = file.getContentType();
        return contentType != null && (allowed.isEmpty() || allowed.contains(contentType));
    }
}
