package com.nithish.gallery.gallerybackup.controller;

import com.nithish.gallery.gallerybackup.config.JwtService;
import com.nithish.gallery.gallerybackup.dto.ApiResponse;
import com.nithish.gallery.gallerybackup.model.MediaBackup;
import com.nithish.gallery.gallerybackup.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "*")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private JwtService jwtService;

    // Upload single file
    @PostMapping("/upload")
    public ResponseEntity<?> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {

        try {
            String jwt = token.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(jwt);

            MediaBackup media = mediaService.uploadMedia(userEmail, file);

            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", media));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Upload failed: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Upload multiple files
    @PostMapping("/upload-batch")
    public ResponseEntity<?> uploadBatch(
            @RequestParam("files") List<MultipartFile> files,
            @RequestHeader("Authorization") String token) {

        try {
            String jwt = token.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(jwt);

            List<MediaBackup> uploaded = files.stream()
                    .map(file -> {
                        try {
                            return mediaService.uploadMedia(userEmail, file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            return ResponseEntity.ok(ApiResponse.success(
                    uploaded.size() + " files uploaded successfully",
                    uploaded
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Upload failed: " + e.getMessage()));
        }
    }

    // Get all my media
    @GetMapping("/my-media")
    public ResponseEntity<?> getMyMedia(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(jwt);

            List<MediaBackup> mediaList = mediaService.getMediaByUser(userEmail);

            return ResponseEntity.ok(ApiResponse.success("Media fetched successfully", mediaList));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid token"));
        }
    }

    // Delete my media
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedia(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String userEmail = jwtService.extractUsername(jwt);

            MediaBackup media = mediaService.getMediaByUser(userEmail)
                    .stream()
                    .filter(m -> m.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Media not found"));

            mediaService.deleteMedia(id);

            return ResponseEntity.ok(ApiResponse.success("Media deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid token or media not found"));
        }
    }
}