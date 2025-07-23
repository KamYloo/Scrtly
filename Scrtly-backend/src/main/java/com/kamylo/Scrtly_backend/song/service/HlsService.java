package com.kamylo.Scrtly_backend.song.service;

public interface HlsService {
    String generateHls(String inputFilePath, Long songId);
    void deleteHlsFolder(Long songId);
}
