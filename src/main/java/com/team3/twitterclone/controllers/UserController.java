package com.team3.twitterclone.controllers;

import com.team3.twitterclone.dtos.CredentialsDto;
import com.team3.twitterclone.dtos.TweetResponseDto;
import com.team3.twitterclone.dtos.UserRequestDto;
import com.team3.twitterclone.dtos.UserResponseDto;
import com.team3.twitterclone.services.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.team3.twitterclone.exceptions.NotFoundException;



import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Data

public class UserController {

    private final UserService userService;
    private static final Logger log = (Logger) LoggerFactory.getLogger(UserController.class);




    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @GetMapping("/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getUserByUsername(@PathVariable("username") String username) {
        return userService.getUserByUsername(username);
    }

    @PatchMapping("/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto updateUser(@RequestBody UserRequestDto userRequestDto, @PathVariable("username") String username) {
        return userService.updateUser(userRequestDto, username);
    }

    @DeleteMapping("/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto deleteUser(@RequestBody CredentialsDto credentialsDto, @PathVariable("username") String username) {
        return userService.deleteUser(credentialsDto, username);
    }

    @PostMapping("/@{username}/follow")
    @ResponseStatus(HttpStatus.OK)
    public void followUser(@RequestBody CredentialsDto credentialsDto, @PathVariable("username") String username) {
        userService.followUser(credentialsDto, username);
    }

    @PostMapping("/@{username}/unfollow")
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUser(@RequestBody CredentialsDto credentialsDto, @PathVariable("username") String username) {
        userService.unfollowUser(credentialsDto, username);
    }

    @GetMapping("/@{username}/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getUserFollowers(@PathVariable("username") String username) {
        return userService.getUserFollowers(username);
    }

    @GetMapping("/@{username}/following")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getUserFollowing(@PathVariable("username") String username) {
        return userService.getUserFollowing(username);
    }

    @GetMapping("/@{username}/feed")
    public List<TweetResponseDto> getUserFeed(@PathVariable("username") String username) {
        return userService.getUserFeed(username);
    }


    @GetMapping("/@{username}/tweets")
    public ResponseEntity<List<TweetResponseDto>> getUserTweets(@PathVariable String username) {

        try {
            log.info("Fetching tweets for user: " + username);
            List<TweetResponseDto> tweets = userService.getUserTweets(username);
            log.info("Successfully retrieved tweets for user: " + username);
            return ResponseEntity.ok(tweets);
        } catch (NotFoundException e) {
            log.error("User not found: " + username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error getting tweets for user: " + username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/@{username}/mentions")
    public List<TweetResponseDto> getUserMentions(@PathVariable String username) {
        return userService.getUserMentions(username);
    }






}
