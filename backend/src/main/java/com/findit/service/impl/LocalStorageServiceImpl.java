package com.findit.service.impl;

import com.findit.entity.ItemImage;
import com.findit.exception.StorageException;
import com.findit.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class LocalStorageServiceImpl implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageServiceImpl.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:5242880}")
    private long maxFileSize;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
            log.info("Initialized file storage location at: {}", this.rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage directory", e);
        }
    }

    @Override
    public ItemImage storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Failed to store empty file");
        }

        if (file.getSize() > maxFileSize) {
            throw new StorageException("File size exceeds maximum allowed limit of " + (maxFileSize / (1024 * 1024)) + " MB");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "unknown.jpg"));
        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new StorageException("Invalid file type: " + contentType + ". Allowed types: JPG, PNG, WEBP");
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new StorageException("Invalid file extension: ." + extension + ". Allowed extensions: jpg, jpeg, png, webp");
        }

        String storedFileName = UUID.randomUUID().toString() + "." + extension.toLowerCase();
        Path destinationFile = this.rootLocation.resolve(storedFileName).normalize();

        if (!destinationFile.getParent().equals(this.rootLocation)) {
            throw new StorageException("Cannot store file outside current directory");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfully stored image: {} as {}", originalFilename, storedFileName);

            return ItemImage.builder()
                    .fileName(storedFileName)
                    .originalFileName(originalFilename)
                    .contentType(contentType)
                    .fileSize(file.getSize())
                    .storagePath(destinationFile.toString())
                    .primary(false)
                    .build();
        } catch (IOException e) {
            throw new StorageException("Failed to store file: " + originalFilename, e);
        }
    }

    @Override
    public List<ItemImage> storeImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemImage> images = new ArrayList<>();
        boolean first = true;
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                ItemImage img = storeImage(file);
                if (first) {
                    img.setPrimary(true);
                    first = false;
                }
                images.add(img);
            }
        }
        return images;
    }

    @Override
    public void deleteImage(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return;
        }
        try {
            Path file = rootLocation.resolve(fileName).normalize();
            if (file.getParent().equals(this.rootLocation)) {
                Files.deleteIfExists(file);
                log.info("Deleted image file: {}", fileName);
            }
        } catch (IOException e) {
            log.warn("Failed to delete image: {}", fileName, e);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1);
    }
}
