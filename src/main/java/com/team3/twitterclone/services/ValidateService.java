package com.team3.twitterclone.services;

public interface ValidateService {
    boolean isValidUsername(String username);
    boolean isValidHashtag(String label);
}