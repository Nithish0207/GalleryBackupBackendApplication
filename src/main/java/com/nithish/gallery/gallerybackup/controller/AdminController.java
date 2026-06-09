package com.nithish.gallery.gallerybackup.controller;

import com.nithish.gallery.gallerybackup.dto.ApiResponse;
import com.nithish.gallery.gallerybackup.model.MediaBackup;
import com.nithish.gallery.gallerybackup.model.User;
import com.nithish.gallery.gallerybackup.service.MediaService;
import com.nithish.gallery.gallerybackup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserService userService;

    // Get ALL media from all users (ADMIN ONLY)
    @GetMapping("/media/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllMedia() {
        List<MediaBackup> allMedia = mediaService.getAllMedia();
        return ResponseEntity.ok(ApiResponse.success("All media fetched", allMedia));
    }

    // Get media by specific user (ADMIN ONLY)
    @GetMapping("/media/user/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMediaByUser(@PathVariable String email) {
        List<MediaBackup> mediaList = mediaService.getMediaByUser(email);
        return ResponseEntity.ok(ApiResponse.success(
                "Media for " + email + " fetched",
                mediaList
        ));
    }

    // Get all users (ADMIN ONLY)
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users fetched", users));
    }

    // Delete any media (ADMIN ONLY)
    @DeleteMapping("/media/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.ok(ApiResponse.success("Media deleted successfully", null));
    }

    // Hide any media (ADMIN ONLY)
    @PostMapping("/media/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> hideMedia(@PathVariable Long id) {
        mediaService.hideMedia(id);
        return ResponseEntity.ok(ApiResponse.success("Media hidden successfully", null));
    }
}