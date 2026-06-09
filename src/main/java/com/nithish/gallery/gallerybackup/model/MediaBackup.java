package com.nithish.gallery.gallerybackup.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "media_backup")
public class MediaBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType; // "image" or "video"

    private Long fileSize;

    @Column(nullable = false)
    private String cloudinaryUrl;

    private String cloudinaryPublicId;

    private LocalDateTime mediaDate;

    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    private boolean isVisible = true;
}