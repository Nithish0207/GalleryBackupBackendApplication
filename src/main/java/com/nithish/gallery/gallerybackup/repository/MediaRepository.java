package com.nithish.gallery.gallerybackup.repository;


import com.nithish.gallery.gallerybackup.model.MediaBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<MediaBackup, Long> {
    List<MediaBackup> findByUserEmail(String userEmail);
    List<MediaBackup> findAllByIsVisibleTrue();
    List<MediaBackup> findByUserEmailAndIsVisibleTrue(String userEmail);
    List<MediaBackup> findAllByOrderByUploadedAtDesc();
}