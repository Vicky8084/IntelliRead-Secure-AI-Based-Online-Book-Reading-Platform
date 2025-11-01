package com.intelliRead.Online.Reading.Paltform.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileStorageUtil {

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("pdf", "txt");
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif");

    public static String sanitizeFileName(String filename) {
        return StringUtils.cleanPath(filename).replaceAll("[^a-zA-Z0-9\\.\\-\\_]", "_");
    }

    public static Path ensureDirectory(String dir) throws IOException {
        Path dirPath = Paths.get(dir).toAbsolutePath().normalize();
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        return dirPath;
    }

    public static String extension(String filename) {
        return FilenameUtils.getExtension(filename).toLowerCase();
    }

    public static String combine(String baseDir, String relativePath) {
        return Paths.get(baseDir, relativePath).toString();
    }

    // Validate book file type
    public static boolean isValidBookFile(String filename) {
        String ext = extension(filename);
        return ALLOWED_FILE_TYPES.contains(ext);
    }

    // Validate image file type
    public static boolean isValidImageFile(String filename) {
        String ext = extension(filename);
        return ALLOWED_IMAGE_TYPES.contains(ext);
    }

    // Get MIME type
    public static String getMimeType(String filename) {
        String ext = extension(filename);
        switch (ext) {
            case "pdf": return "application/pdf";
            case "txt": return "text/plain";
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            default: return "application/octet-stream";
        }
    }
}