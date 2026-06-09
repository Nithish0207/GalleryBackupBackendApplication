package com.nithish.gallery.gallerybackup.service;

import com.nithish.gallery.gallerybackup.model.MediaBackup;
import com.nithish.gallery.gallerybackup.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MediaService {

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public MediaBackup uploadMedia(String userEmail, MultipartFile file) throws IOException {
        String folder = userEmail.split("@")[0];
        String fileUrl = cloudinaryService.uploadFile(file, folder);
        String publicId = cloudinaryService.getPublicId(fileUrl);

        String fileType = file.getContentType().contains("video") ? "video" : "image";

        MediaBackup media = new MediaBackup();
        media.setUserEmail(userEmail);
        media.setFileName(file.getOriginalFilename());
        media.setFileType(fileType);
        media.setFileSize(file.getSize());
        media.setCloudinaryUrl(fileUrl);
        media.setCloudinaryPublicId(publicId);
        media.setUploadedAt(LocalDateTime.now());
        media.setMediaDate(LocalDateTime.now());

        return mediaRepository.save(media);
    }

    public List<MediaBackup> getAllMedia() {
        return mediaRepository.findAllByOrderByUploadedAtDesc();
    }

    public List<MediaBackup> getMediaByUser(String userEmail) {
        return mediaRepository.findByUserEmailAndIsVisibleTrue(userEmail);
    }

    public void deleteMedia(Long id) {
        MediaBackup media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        try {
            cloudinaryService.deleteFile(media.getCloudinaryPublicId());
        } catch (IOException e) {
            System.err.println("Failed to delete from Cloudinary: " + e.getMessage());
        }

        mediaRepository.delete(media);
    }

    public void hideMedia(Long id) {
        MediaBackup media = mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));
        media.setVisible(false);
        mediaRepository.save(media);
    }
}