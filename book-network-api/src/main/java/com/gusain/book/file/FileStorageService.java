package com.gusain.book.file;

import jakarta.annotation.Nonnull;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${application.security.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull Integer userId
    ) {

        final String fileUploadSubPath = "users"+ File.separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull String fileUploadSubPath
    ) {

        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFoder = new File(finalUploadPath);
        if(!targetFoder.exists()) {
           boolean folderCreated =  targetFoder.mkdirs();
           if(!folderCreated) {
               log.warn("Failed to create folder {}", targetFoder.getAbsolutePath());
               return null;
           }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "."+fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to " + targetFilePath);
        }catch (IOException e) {
            log.error("Failed to write file {}", targetFilePath);
        }
        return targetFilePath;
    }

    private String getFileExtension(String fileName) {
        if(fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if(lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex).toLowerCase();
    }

}
