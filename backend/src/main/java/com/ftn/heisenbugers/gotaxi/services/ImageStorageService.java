package com.ftn.heisenbugers.gotaxi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {
    @Value("${app.upload.dir}")
    private String uploadDir;
    @Value("${app.upload.url-prefix:/uploads/avatars/}")
    private String urlPrefix;

    private Path resolveUploadPath() {
        if (uploadDir == null || uploadDir.isBlank()) {
            String baseDir = System.getProperty("user.dir");
            Path path = Paths.get(baseDir, "uploads", "avatars")
                    .toAbsolutePath()
                    .normalize();
            System.out.println("[ImageStorageService] Using default upload dir: " + path);
            return path;
        } else {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            System.out.println("[ImageStorageService] Using configured upload dir: " + path);
            return path;
        }
    }

    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path uploadPath = resolveUploadPath();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf('.'));
            }

            String filename = UUID.randomUUID() + ext;
            Path target = uploadPath.resolve(filename);

            System.out.println("[ImageStorageService] Saving profile image to: " + target);

            file.transferTo(target.toFile());
            return urlPrefix + filename;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save profile image", e);
        }
    }
}
