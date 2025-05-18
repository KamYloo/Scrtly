package com.kamylo.Scrtly_backend.service;

import java.util.concurrent.CompletableFuture;

public interface HlsService {
    CompletableFuture<String> generateHls(String inputFilePath, Long songId);
}
