package com.example.bookhub.service;

import com.example.bookhub.FileNameGenerator;
import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class FileService {
    private static final String UPLOAD_DIR = "uploads/";

    private final FileNameGenerator fileNameGenerator;

    public String saveImage(MultipartFile imageFile, String title) throws IOException {
        String uniqueFileName = fileNameGenerator.generateUniqueFileName(title);

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        File outputFile = uploadPath.resolve(uniqueFileName).toFile();

        Thumbnails.of(imageFile.getInputStream())
                .size(300, 300)
                .outputFormat("jpg")
                .toFile(outputFile);

        return uniqueFileName;
    }

    public void cleanUploadsFolder() {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (Files.exists(uploadPath)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(uploadPath)) {
                for (Path filePath : directoryStream) {
                    if (Files.isRegularFile(filePath) && !filePath.getFileName().toString().startsWith("example_book")) {
                        Files.delete(filePath);
                    }
                }
            } catch (IOException e) {
                System.err.println("Błąd podczas czyszczenia folderu uploads: " + e.getMessage());
            }
        }
    }

    public boolean isImageFileValid(MultipartFile imageFile, BindingResult result) {
        String contentType = imageFile.getContentType();
        long maxFileSizeInBytes = 1024 * 1024; // 1 MB

        if (!isSupportedContentType(contentType)) {
            result.rejectValue("image", "error.image", "Nieobsługiwany format pliku. Dozwolone formaty to JPG i PNG");
            return false;
        } else if (imageFile.getSize() > maxFileSizeInBytes) {
            result.rejectValue("image", "error.image", "Plik jest za duży. Maksymalny rozmiar to 1 MB");
            return false;
        }
        return true;
    }

    private boolean isSupportedContentType(String contentType) {
        return "image/jpeg".equals(contentType) || "image/png".equals(contentType);
    }
}
