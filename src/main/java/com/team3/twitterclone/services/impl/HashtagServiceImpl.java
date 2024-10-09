package com.team3.twitterclone.services.impl;

import com.team3.twitterclone.dtos.HashtagDto;
import com.team3.twitterclone.dtos.TweetRequestDto;
import com.team3.twitterclone.entities.Hashtag;
import com.team3.twitterclone.entities.Tweet;
import com.team3.twitterclone.exceptions.NotFoundException;
import com.team3.twitterclone.mappers.HashtagMapper;
import com.team3.twitterclone.repositories.HashtagRepository;
import com.team3.twitterclone.services.HashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HashtagServiceImpl implements HashtagService {

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private HashtagMapper hashtagMapper;

    // Finds or creates a hashtag by label
    @Override
    public HashtagDto findOrCreateHashtag(String label) {
        String normalizedLabel = label.toLowerCase();
        Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabelIgnoreCase(normalizedLabel);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        Hashtag hashtag = optionalHashtag.isPresent() ?
                updateExistingHashtag(optionalHashtag.get(), currentTime) :
                createNewHashtag(normalizedLabel, currentTime);

        Hashtag savedHashtag = hashtagRepository.save(hashtag);
        return hashtagMapper.entityToDto(savedHashtag);
    }

    // Helper method to update an existing hashtag's lastUsed timestamp
    private Hashtag updateExistingHashtag(Hashtag hashtag, Timestamp currentTime) {
        hashtag.setLastUsed(currentTime);
        return hashtag;
    }

    // Helper method to create a new hashtag
    private Hashtag createNewHashtag(String normalizedLabel, Timestamp currentTime) {
        Hashtag newHashtag = new Hashtag();
        newHashtag.setLabel(normalizedLabel);
        newHashtag.setFirstUsed(currentTime);
        newHashtag.setLastUsed(currentTime);
        return newHashtag;
    }

    // Checks if a hashtag exists by its label
    @Override
    public boolean existsByLabel(String label) {
        return hashtagRepository.findByLabelIgnoreCase(label).isPresent();
    }

    // Retrieves all hashtags as a list of DTOs
    @Override
    public List<HashtagDto> getAllHashtags() {
        List<Hashtag> hashtags = hashtagRepository.findAll();
        List<HashtagDto> hashtagDtos = new ArrayList<>();
        for (Hashtag hashtag : hashtags) {
            hashtagDtos.add(hashtagMapper.entityToDto(hashtag));
        }
        return hashtagDtos;
    }

    // Retrieves tweets associated with a specific hashtag (if needed)
    @Override
    public List<TweetRequestDto> getTweetsByHashtag(String label) {
        Optional<Hashtag> hashtagOptional = hashtagRepository.findByLabelIgnoreCase(label);
        List<TweetRequestDto> tweetDtos = new ArrayList<>();
        if (hashtagOptional.isPresent()) {
            Hashtag hashtag = hashtagOptional.get();
            for (Tweet tweet : hashtag.getTweets()) {
                tweetDtos.add(hashtagMapper.tweetToTweetRequestDto(tweet));
            }
        }
        return tweetDtos;
    }

    @Override
    public HashtagDto getHashtagByLabel(String label) {
        Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabelIgnoreCase(label);
        if (!optionalHashtag.isPresent()) {
            throw new NotFoundException("Hashtag not found");
        }
        Hashtag hashtag = optionalHashtag.get();
        return hashtagMapper.entityToDto(hashtag);
    }
}