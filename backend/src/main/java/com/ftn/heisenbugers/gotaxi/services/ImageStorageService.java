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

    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf('.'));
            }

            //unic name
            String filename = UUID.randomUUID() + ext;
            Path target = uploadPath.resolve(filename);

            file.transferTo(target.toFile());

            return "/uploads/avatars/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile image", e);
        }
    }
}
