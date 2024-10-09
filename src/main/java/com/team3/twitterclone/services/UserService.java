package com.team3.twitterclone.services;

import com.team3.twitterclone.dtos.CredentialsDto;
import com.team3.twitterclone.dtos.TweetResponseDto;
import com.team3.twitterclone.dtos.UserRequestDto;
import com.team3.twitterclone.dtos.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto getUserByUsername(String username);

    UserResponseDto updateUser(UserRequestDto userRequestDto, String username);

    UserResponseDto deleteUser(CredentialsDto credentialsDto, String username);

    void followUser(CredentialsDto credentialsDto, String username);

    void unfollowUser(CredentialsDto credentialsDto, String username);

    List<TweetResponseDto> getUserFeed(String username);

    List<TweetResponseDto> getUserTweets(String username);

    List<TweetResponseDto> getUserMentions(String username);

    List<UserResponseDto> getUserFollowers(String username);

    List<UserResponseDto> getUserFollowing(String username);

}
