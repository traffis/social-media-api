package com.team3.twitterclone.services.impl;


import com.team3.twitterclone.repositories.HashtagRepository;
import com.team3.twitterclone.repositories.UserRepository;
import com.team3.twitterclone.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    // Check if username is valid (exists in the system)
    @Override
    public boolean isValidUsername(String username) {
        return userRepository.findByCredentialsUsernameAndDeletedFalse(username).isPresent();
    }

    // Check if hashtag is valid (exists in the system)
    @Override
    public boolean isValidHashtag(String label) {
        return hashtagRepository.findByLabelIgnoreCase(label).isPresent();
    }
}
