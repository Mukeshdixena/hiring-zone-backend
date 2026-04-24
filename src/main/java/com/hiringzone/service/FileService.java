package com.hiringzone.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    @Value("${hiringzone.upload-dir}")
    private String uploadDir;

    public String saveResume(MultipartFile file) throws IOException {
        Path root = Paths.get(uploadDir);
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
        
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), root.resolve(filename));
        
        return filename;
    }
}
