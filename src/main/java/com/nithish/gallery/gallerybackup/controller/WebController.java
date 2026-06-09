package com.nithish.gallery.gallerybackup.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Admin dashboard page - allow public access
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Optional: Add user info to model
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "admin/dashboard";
    }

    // Admin login page
    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin/login";
    }
}