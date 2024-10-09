package com.team3.twitterclone.services;


import com.team3.twitterclone.dtos.*;

import java.util.List;


public interface TweetService {
    List<TweetResponseDto> getTweetReplies(Long id);
    TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);
    TweetResponseDto getTweetById(Long id);
    List<TweetResponseDto> getAllTweets();
    TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto);
    void restoreTweet(Long id, CredentialsDto credentialsDto);
    void likeTweet(Long id, CredentialsDto credentialsDto);
    TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetRequestDto);
    TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto);
    List<TweetResponseDto> getUserFeed(String username);
    List<TweetResponseDto> getUserTweets(String username);
    List<TweetResponseDto> getUserMentions(String username);
    List<HashtagDto> getTweetTags(Long id);
    List<UserResponseDto> getTweetMentions(Long tweetId);
    ContextDto getTweetContext(Long tweetId);
}
