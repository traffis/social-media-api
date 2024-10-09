package com.team3.twitterclone.services.impl;

import com.team3.twitterclone.dtos.CredentialsDto;
import com.team3.twitterclone.dtos.TweetResponseDto;
import com.team3.twitterclone.dtos.UserRequestDto;
import com.team3.twitterclone.dtos.UserResponseDto;
import com.team3.twitterclone.entities.Credentials;
import com.team3.twitterclone.entities.Profile;
import com.team3.twitterclone.entities.Tweet;
import com.team3.twitterclone.entities.User;
import com.team3.twitterclone.exceptions.BadRequestException;
import com.team3.twitterclone.exceptions.NotAuthorizedException;
import com.team3.twitterclone.exceptions.NotFoundException;
import com.team3.twitterclone.mappers.CredentialsMapper;
import com.team3.twitterclone.mappers.TweetMapper;
import com.team3.twitterclone.mappers.UserMapper;
import com.team3.twitterclone.repositories.UserRepository;
import com.team3.twitterclone.services.TweetService;
import com.team3.twitterclone.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CredentialsMapper credentialsMapper;
    private final TweetService tweetService;
    private final TweetMapper tweetMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse());
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        validateUserRequest(userRequestDto);
        User userToSave = userMapper.requestDtoToEntity(userRequestDto);

        Optional<User> optionalExistingUser = userRepository
                .findByCredentialsUsername(userToSave.getCredentials().getUsername());

        if (optionalExistingUser.isPresent()) {
            User existingUser = optionalExistingUser.get();

            if (existingUser.getCredentials().equals(userToSave.getCredentials()) && existingUser.isDeleted()) {
                existingUser.setDeleted(false);
                userRepository.saveAndFlush(existingUser);

                restoreUserTweets(existingUser);

                return userMapper.entityToDto(existingUser);
            } else {
                throw new BadRequestException("Username already exists.");
            }
        }

        return userMapper.entityToDto(userRepository.saveAndFlush(userToSave));
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException(String.format("User '%s' not found.", username)));

        return userMapper.entityToDto(user);
    }

    @Override
    public UserResponseDto updateUser(UserRequestDto userRequestDto, String username) {
        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException(String.format("User '%s' not found.", username)));

        User userRequest = userMapper.requestDtoToEntity(userRequestDto);

        if (!user.getCredentials().equals(userRequest.getCredentials())) {
            throw new NotAuthorizedException(String.format("Credentials do not match for user '%s'.", username));
        }

        if (userRequest.getProfile() == null) {
            throw new BadRequestException("No profile given to update user.");
        }

        updateProfile(user, userRequest.getProfile());
        return userMapper.entityToDto(userRepository.saveAndFlush(user));
    }

    @Override
    public UserResponseDto deleteUser(CredentialsDto credentialsDto, String username) {
        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException(String.format("User '%s' not found.", username)));

        Credentials credentialsRequest = credentialsMapper.credentialsDtoToCredentials(credentialsDto);

        if (!user.getCredentials().equals(credentialsRequest)) {
            throw new NotAuthorizedException(String.format("Credentials do not match for user '%s'.", username));
        }

        deleteUserTweets(user);

        user.setDeleted(true);
        return userMapper.entityToDto(userRepository.saveAndFlush(user));
    }

    @Override
    public void followUser(CredentialsDto credentialsDto, String username) {
        Credentials credentials = credentialsMapper.credentialsDtoToCredentials(credentialsDto);

        User activeUser = userRepository.findByCredentialsAndDeletedFalse(credentials)
                .orElseThrow(() -> new NotFoundException("User with given credentials does not exist."));

        User userToFollow = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot follow user '%s' because they do not exist.", username)));

        if (activeUser.getFollowing().contains(userToFollow)) {
            throw new BadRequestException(String.format("You are already following user '%s'.", username));
        }

        userToFollow.getFollowers().add(activeUser);
        userRepository.saveAndFlush(userToFollow);
    }

    @Override
    public void unfollowUser(CredentialsDto credentialsDto, String username) {
        Credentials credentials = credentialsMapper.credentialsDtoToCredentials(credentialsDto);

        User activeUser = userRepository.findByCredentialsAndDeletedFalse(credentials)
                .orElseThrow(() -> new NotFoundException("User with given credentials does not exist."));

        User userToUnfollow = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException(String.format("Cannot unfollow user '%s' because they do not exist.", username)));

        if (!activeUser.getFollowing().contains(userToUnfollow)) {
            throw new BadRequestException(String.format("You are not currently following user '%s'.", username));
        }

        userToUnfollow.getFollowers().remove(activeUser);
        userRepository.saveAndFlush(userToUnfollow);
    }

    @Override
    public List<TweetResponseDto> getUserFeed(String username) {
        return tweetService.getUserFeed(username);
    }

    @Override
    public List<TweetResponseDto> getUserTweets(String username) {
        return tweetService.getUserTweets(username);
    }

    @Override
    public List<TweetResponseDto> getUserMentions(String username) {
        return tweetService.getUserMentions(username);
    }

    @Override
    public List<UserResponseDto> getUserFollowers(String username) {
        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException(String.format("User '%s' not found.", username)));

        return userMapper.entitiesToDtos(user.getFollowers().stream().toList());
    }

    @Override
    public List<UserResponseDto> getUserFollowing(String username) {
        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException(String.format("User '%s' not found.", username)));

        return userMapper.entitiesToDtos(user.getFollowing().stream().toList());
    }

    /********************************** HELPER METHODS **********************************/

    private void validateUserRequest(UserRequestDto userRequestDto) {
        if (userRequestDto == null || userRequestDto.getCredentials() == null || userRequestDto.getProfile() == null) {
            throw new BadRequestException("Credentials and profile are required.");
        }

        String username = userRequestDto.getCredentials().getUsername();
        String password = userRequestDto.getCredentials().getPassword();
        String email = userRequestDto.getProfile().getEmail();

        if (username == null) {
            throw new BadRequestException("Username field is required.");
        }

        if (password == null) {
            throw new BadRequestException("Password field is required.");
        }

        if (email == null) {
            throw new BadRequestException("Email field is required.");
        }
    }

    private void updateProfile(User user, Profile newProfile) {
        Profile profileToUpdate = user.getProfile();

        profileToUpdate.setEmail(Optional.ofNullable(newProfile.getEmail()).orElse(profileToUpdate.getEmail()));
        profileToUpdate.setPhone(Optional.ofNullable(newProfile.getPhone()).orElse(profileToUpdate.getPhone()));
        profileToUpdate.setFirstName(Optional.ofNullable(newProfile.getFirstName()).orElse(profileToUpdate.getFirstName()));
        profileToUpdate.setLastName(Optional.ofNullable(newProfile.getLastName()).orElse(profileToUpdate.getLastName()));

        user.setProfile(profileToUpdate);
    }

    private void restoreUserTweets(User user) {
        List<Tweet> existingUserTweets = tweetMapper.dtosToEntities(
                tweetService.getUserTweets(user.getCredentials().getUsername()));

        CredentialsDto existingUserCredentialsDto = credentialsMapper
                .credentialsToCredentialsDto(user.getCredentials());

        existingUserTweets.forEach(tweet -> tweetService.restoreTweet(tweet.getId(), existingUserCredentialsDto));
    }

    private void deleteUserTweets(User user) {
        List<Tweet> userTweets = tweetMapper.dtosToEntities(
                tweetService.getUserTweets(user.getCredentials().getUsername()));

        CredentialsDto existingUserCredentialsDto = credentialsMapper
                .credentialsToCredentialsDto(user.getCredentials());

        userTweets.forEach(tweet -> tweetService.deleteTweet(tweet.getId(), existingUserCredentialsDto));
    }
}