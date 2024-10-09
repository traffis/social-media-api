package com.team3.twitterclone.controllers;

import com.team3.twitterclone.dtos.HashtagDto;
import com.team3.twitterclone.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class HashtagController {

    @Autowired
    private HashtagService hashtagService;

    @GetMapping
    public List<HashtagDto> getAllHashtags() {
        return hashtagService.getAllHashtags();
    }

    @GetMapping("/{label}")
    public HashtagDto getHashtagByLabel(@PathVariable String label) {
        return hashtagService.getHashtagByLabel(label);
    }

}