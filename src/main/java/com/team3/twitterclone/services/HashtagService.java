package com.team3.twitterclone.services;

import com.team3.twitterclone.dtos.HashtagDto;
import com.team3.twitterclone.dtos.TweetRequestDto;

import java.util.List;

public interface HashtagService {
    HashtagDto findOrCreateHashtag(String label);
    boolean existsByLabel(String label);
    List<HashtagDto> getAllHashtags();
    List<TweetRequestDto> getTweetsByHashtag(String label);
    HashtagDto getHashtagByLabel(String label);
}
