package com.team3.twitterclone.controllers;

import com.team3.twitterclone.services.HashtagService;
import com.team3.twitterclone.services.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validate")
public class ValidateController {

    private final HashtagService hashtagService;
    private final ValidateService validateService;

    @Autowired
    public ValidateController(HashtagService hashtagService, ValidateService validateService) {
        this.hashtagService = hashtagService;
        this.validateService = validateService;
    }

    @GetMapping("/tag/exists/{label}")
    public boolean doesHashtagExist(@PathVariable String label) {
        return hashtagService.existsByLabel(label);
    }

    // Check if username exists (for 'exists' endpoint)
    @GetMapping("/username/exists/@{username}")
    public boolean doesUsernameExist(@PathVariable String username) {
        return validateService.isValidUsername(username);
    }

    // Check if username is available (for 'available' endpoint)
    @GetMapping("/username/available/@{username}")
    public boolean isUsernameAvailable(@PathVariable String username) {
        return !validateService.isValidUsername(username);
    }
}