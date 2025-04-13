package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.service.impl.FileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class FileServiceImplTest {

    private final String BASE_DIR = "C:\\images\\";
    private final String CDN_BASE = "http://cdn.com/";
    private final String DIRECTORY = "subdir/";

    private FileServiceImpl getService() {
        FileServiceImpl service = new FileServiceImpl();
        ReflectionTestUtils.setField(service, "imageDir", BASE_DIR);
        ReflectionTestUtils.setField(service, "cdnBaseUrl", CDN_BASE);
        return service;
    }
    
    @Test
    public void testSaveFileSuccess() throws IOException {
        FileServiceImpl service = getService();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        String originalFilename = "test.jpg";
        InputStream stream = new ByteArrayInputStream("dummy".getBytes());
        Mockito.when(file.getOriginalFilename()).thenReturn(originalFilename);
        Mockito.when(file.getInputStream()).thenReturn(stream);

        Path folderPath = Path.of(BASE_DIR + DIRECTORY);

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(eq(folderPath))).thenReturn(false);
            filesMock.when(() -> Files.createDirectories(eq(folderPath))).thenReturn(folderPath);
            filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class), eq(StandardCopyOption.REPLACE_EXISTING)))
                     .thenReturn(100L);

            String url = service.saveFile(file, DIRECTORY);
            assertTrue(url.startsWith(CDN_BASE + DIRECTORY));
            filesMock.verify(() -> Files.createDirectories(eq(folderPath)));
            filesMock.verify(() -> Files.copy(any(InputStream.class), any(Path.class), eq(StandardCopyOption.REPLACE_EXISTING)));
        }
    }
    
    @Test
    public void testSaveFileIOException() throws IOException {
        FileServiceImpl service = getService();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn("test.jpg");
        Mockito.when(file.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        Path folderPath = Path.of(BASE_DIR + DIRECTORY);
        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(eq(folderPath))).thenReturn(true);
            CustomException ex = assertThrows(CustomException.class, () -> service.saveFile(file, DIRECTORY));
            assertEquals(BusinessErrorCodes.IMAGE_FETCH_FAILED, ex.getErrorCode());
        }
    }
    
    @Test
    public void testUpdateFileSuccess() throws IOException {
        FileServiceImpl service = getService();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        String originalFilename = "update.jpg";
        InputStream stream = new ByteArrayInputStream("update".getBytes());
        Mockito.when(file.getOriginalFilename()).thenReturn(originalFilename);
        Mockito.when(file.getInputStream()).thenReturn(stream);

        String existingFileUrl = CDN_BASE + DIRECTORY + "oldfile.jpg";
        String relativePath = DIRECTORY + "oldfile.jpg";
        Path folderPath = Path.of(BASE_DIR + DIRECTORY);
        Path oldFilePath = Path.of(BASE_DIR + relativePath);

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(eq(folderPath))).thenReturn(false);
            filesMock.when(() -> Files.createDirectories(eq(folderPath))).thenReturn(folderPath);
            filesMock.when(() -> Files.exists(eq(oldFilePath))).thenReturn(true);
            filesMock.when(() -> Files.delete(eq(oldFilePath))).thenAnswer(invocation -> null);
            filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class), eq(StandardCopyOption.REPLACE_EXISTING)))
                     .thenReturn(100L);

            String url = service.updateFile(file, existingFileUrl, DIRECTORY);
            assertTrue(url.startsWith(CDN_BASE + DIRECTORY));
            filesMock.verify(() -> Files.delete(eq(oldFilePath)));
        }
    }
    
    @Test
    public void testUpdateFileIOException() throws IOException {
        FileServiceImpl service = getService();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getOriginalFilename()).thenReturn("update.jpg");
        Mockito.when(file.getInputStream()).thenReturn(new ByteArrayInputStream("update".getBytes()));

        String existingFileUrl = CDN_BASE + DIRECTORY + "oldfile.jpg";
        Path folderPath = Path.of(BASE_DIR + DIRECTORY);
        Path oldFilePath = Path.of(BASE_DIR + DIRECTORY + "oldfile.jpg");

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(eq(folderPath))).thenReturn(true);
            filesMock.when(() -> Files.exists(eq(oldFilePath))).thenReturn(true);
            filesMock.when(() -> Files.delete(eq(oldFilePath))).thenAnswer(invocation -> null);
            filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class), eq(StandardCopyOption.REPLACE_EXISTING)))
                     .thenThrow(new IOException("Simulated copy error"));
            
            CustomException ex = assertThrows(CustomException.class, () -> service.updateFile(file, existingFileUrl, DIRECTORY));
            assertEquals(BusinessErrorCodes.IMAGE_NOT_FOUND, ex.getErrorCode());
        }
    }
    
    @Test
    public void testDeleteFileSuccess() throws IOException {
        FileServiceImpl service = getService();
        String fileUrl = CDN_BASE + DIRECTORY + "delete.jpg";
        Path filePath = Path.of(BASE_DIR + DIRECTORY + "delete.jpg");

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(eq(filePath))).thenReturn(true);
            filesMock.when(() -> Files.delete(eq(filePath))).thenAnswer(invocation -> null);
            
            assertDoesNotThrow(() -> service.deleteFile(fileUrl));
            filesMock.verify(() -> Files.delete(eq(filePath)));
        }
    }
    
    @Test
    public void testDeleteFileIOException() throws IOException {
        FileServiceImpl service = getService();
        String fileUrl = CDN_BASE + DIRECTORY + "delete.jpg";
        Path filePath = Path.of(BASE_DIR + DIRECTORY + "delete.jpg");

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(eq(filePath))).thenReturn(true);
            filesMock.when(() -> Files.delete(eq(filePath))).thenThrow(new IOException("Simulated delete error"));
            
            CustomException ex = assertThrows(CustomException.class, () -> service.deleteFile(fileUrl));
            assertEquals(BusinessErrorCodes.IMAGE_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Test
    public void testUpdateFileNoExistingFileDeletion() throws IOException {
        FileServiceImpl service = getService();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        String originalFilename = "update.jpg";
        InputStream stream = new ByteArrayInputStream("update".getBytes());
        Mockito.when(file.getOriginalFilename()).thenReturn(originalFilename);
        Mockito.when(file.getInputStream()).thenReturn(stream);

        String existingFileName = "otherDir/oldfile.jpg";
        Path folderPath = Path.of(BASE_DIR + DIRECTORY);
        Path filePath = Path.of(BASE_DIR + existingFileName);

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(eq(folderPath))).thenReturn(false);
            filesMock.when(() -> Files.createDirectories(eq(folderPath))).thenReturn(folderPath);
            filesMock.when(() -> Files.exists(eq(filePath))).thenReturn(false);
            filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class), eq(StandardCopyOption.REPLACE_EXISTING)))
                     .thenReturn(100L);
            
            String url = service.updateFile(file, existingFileName, DIRECTORY);
            assertTrue(url.startsWith(CDN_BASE + DIRECTORY));
            filesMock.verify(() -> Files.delete(any(Path.class)), Mockito.never());
        }
    }

    @Test
    public void testUpdateFileNullExistingFileName() throws IOException {
        FileServiceImpl service = getService();
        MultipartFile file = Mockito.mock(MultipartFile.class);
        String originalFilename = "update.jpg";
        InputStream stream = new ByteArrayInputStream("update".getBytes());
        Mockito.when(file.getOriginalFilename()).thenReturn(originalFilename);
        Mockito.when(file.getInputStream()).thenReturn(stream);

        String existingFileName = null;
        Path folderPath = Path.of(BASE_DIR + DIRECTORY);
        Path filePath = Path.of(BASE_DIR + "null");

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(eq(folderPath))).thenReturn(false);
            filesMock.when(() -> Files.createDirectories(eq(folderPath))).thenReturn(folderPath);
            filesMock.when(() -> Files.exists(eq(filePath))).thenReturn(false);
            filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class), eq(StandardCopyOption.REPLACE_EXISTING)))
                     .thenReturn(100L);
            
            String url = service.updateFile(file, existingFileName, DIRECTORY);
            assertTrue(url.startsWith(CDN_BASE + DIRECTORY));
        }
    }
}
