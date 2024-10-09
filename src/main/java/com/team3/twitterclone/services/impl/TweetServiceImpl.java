package com.team3.twitterclone.services.impl;

import com.team3.twitterclone.dtos.*;
import com.team3.twitterclone.entities.Hashtag;
import com.team3.twitterclone.entities.Tweet;
import com.team3.twitterclone.entities.User;
import com.team3.twitterclone.exceptions.NotAuthorizedException;
import com.team3.twitterclone.exceptions.NotFoundException;
import com.team3.twitterclone.mappers.HashtagMapper;
import com.team3.twitterclone.mappers.TweetMapper;
import com.team3.twitterclone.mappers.UserMapper;
import com.team3.twitterclone.repositories.HashtagRepository;
import com.team3.twitterclone.repositories.TweetRepository;
import com.team3.twitterclone.repositories.UserRepository;
import com.team3.twitterclone.services.TweetService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Data
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;
    private final TweetMapper tweetMapper;
    private final UserMapper userMapper;

    @Override
    public List<TweetResponseDto> getUserMentions(String username) {
        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return tweetRepository.findByMentionedUsersContainingAndDeletedFalseOrderByPostedDesc(user)
                .stream()
                .map(tweetMapper::entityToDto)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<TweetResponseDto> getUserMentions(String username) {
//        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//
//        return tweetRepository.findByMentionedUsersContainingAndDeletedFalseOrderByPostedDesc(user)
//                .stream()
//                .map(tweetMapper::entityToDto)
//                .collect(Collectors.toList());
//    }


    @Override
    public List<TweetResponseDto> getAllTweets() {
        return tweetRepository.findByDeletedFalseOrderByPostedDesc().stream()
                .map(tweetMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<TweetResponseDto> getAllTweetsAlternative() {
        return tweetRepository.findByDeletedFalseOrderByPostedDesc().stream()
                .map(tweet -> tweetMapper.entityToDto(tweet))
                .collect(Collectors.toList());
    }

    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
        CredentialsDto credentials = tweetRequestDto.getCredentials();

        // Fetch the author (user) based on credentials
        User author = userRepository.findByCredentialsUsernameAndDeletedFalse(credentials.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!author.getCredentials().getPassword().equals(credentials.getPassword())) {
            throw new NotAuthorizedException("Invalid credentials");
        }

        // Create a new Tweet entity
        Tweet tweet = tweetMapper.dtoToEntity(tweetRequestDto);
        tweet.setAuthor(author);  // Set the author correctly here
        tweet.setPosted(new Timestamp(System.currentTimeMillis()));

        // Process mentions and hashtags (if any)
        processMentionsAndHashtags(tweet);

        Tweet savedTweet = tweetRepository.save(tweet);
        return tweetMapper.entityToDto(savedTweet);
    }

    @Override
    public TweetResponseDto getTweetById(Long id) {
        Tweet tweet = tweetRepository.findByIdAndDeletedFalse(id) //  findByIdAndDeletedFalse supposed to be in class TweetRepository
                .orElseThrow(() -> new NotFoundException("Tweet not found"));
        return tweetMapper.entityToDto(tweet);
    }

    @Override
    public TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto) {
        Tweet tweet = tweetRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found"));
        if (!tweet.getAuthor().getCredentials().equals(credentialsDto)) {
            throw new NotAuthorizedException("Invalid credentials");
        }
        tweet.setDeleted(true);
        Tweet deletedTweet = tweetRepository.save(tweet);
        return tweetMapper.entityToDto(deletedTweet);
    }

    @Override
    public void restoreTweet(Long id, CredentialsDto credentialsDto) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found"));
        if (!tweet.getAuthor().getCredentials().equals(credentialsDto)) {
            throw new NotAuthorizedException("Invalid credentials");
        }
        tweet.setDeleted(false);
        tweetRepository.saveAndFlush(tweet);
    }

    @Override
    public void likeTweet(Long id, CredentialsDto credentialsDto) {
        Tweet tweet = tweetRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found"));
        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(credentialsDto.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!user.getCredentials().equals(credentialsDto)) {
            throw new NotAuthorizedException("Invalid credentials");
        }
        tweet.getLikedByUsers().add(user);
        tweetRepository.save(tweet);
    }

    @Override
    public TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetRequestDto) {
        Tweet parentTweet = tweetRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Parent tweet not found"));
        CredentialsDto credentials = tweetRequestDto.getCredentials();
        User author = userRepository.findByCredentialsUsernameAndDeletedFalse(credentials.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!author.getCredentials().equals(credentials)) {
            throw new NotAuthorizedException("Invalid credentials");
        }
        Tweet reply = tweetMapper.dtoToEntity(tweetRequestDto);
        reply.setAuthor(author);
        reply.setInReplyTo(parentTweet);
        reply.setPosted(new Timestamp(System.currentTimeMillis()));
        processMentionsAndHashtags(reply);
        Tweet savedReply = tweetRepository.save(reply);
        return tweetMapper.entityToDto(savedReply);
    }

    @Override
    public TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto) {
        Tweet originalTweet = tweetRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Tweet to repost not found"));
        User author = userRepository.findByCredentialsUsernameAndDeletedFalse(credentialsDto.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!author.getCredentials().equals(credentialsDto)) {
            throw new NotAuthorizedException("Invalid credentials");
        }
        Tweet repost = new Tweet();
        repost.setAuthor(author);
        repost.setRepostOf(originalTweet);
        repost.setPosted(new Timestamp(System.currentTimeMillis()));
        Tweet savedRepost = tweetRepository.save(repost);
        return tweetMapper.entityToDto(savedRepost);
    }

    @Override
    public List<TweetResponseDto> getUserFeed(String username) {
        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<User> following = new ArrayList<>(user.getFollowing());
        following.add(user);
        return tweetRepository.findByAuthorInAndDeletedFalseOrderByPostedDesc(following).stream()
                .map(tweetMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TweetResponseDto> getUserTweets(String username) {
        User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return tweetRepository.findByAuthorAndDeletedFalseOrderByPostedDesc(user).stream()
                .map(tweetMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public List<TweetResponseDto> getTweetReplies(Long id) {
        Tweet tweet = tweetRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found"));
        return tweet.getReplies().stream()
                .filter(reply -> !reply.isDeleted())
                .map(tweetMapper::entityToDto)
                .collect(Collectors.toList());
    }


    private void processMentionsAndHashtags(Tweet tweet) {
        if (tweet.getContent() == null) return; // Avoid processing null content

        String content = tweet.getContent();

        // Process hashtags
        Pattern hashtagPattern = Pattern.compile("#(\\w+)");
        Matcher hashtagMatcher = hashtagPattern.matcher(content);
        while (hashtagMatcher.find()) {
            String label = hashtagMatcher.group(1).toLowerCase();
            Hashtag hashtag = hashtagRepository.findByLabelIgnoreCase(label)
                    .orElseGet(() -> {
                        Hashtag newHashtag = new Hashtag(label);
                        newHashtag.setFirstUsed(new Timestamp(System.currentTimeMillis()));
                        newHashtag.setLastUsed(new Timestamp(System.currentTimeMillis()));
                        return hashtagRepository.save(newHashtag);
                    });
            hashtag.setLastUsed(new Timestamp(System.currentTimeMillis()));
            hashtag.getTweets().add(tweet);
            tweet.getHashtags().add(hashtag);
            hashtagRepository.save(hashtag);
        }

        // Process mentions
        Pattern mentionPattern = Pattern.compile("@(\\w+)");
        Matcher mentionMatcher = mentionPattern.matcher(content);
        while (mentionMatcher.find()) {
            String mentionedUsername = mentionMatcher.group(1);
            User mentionedUser = userRepository.findByCredentialsUsernameAndDeletedFalse(mentionedUsername)
                    .orElse(null);
            if (mentionedUser != null) {
                tweet.getMentionedUsers().add(mentionedUser);
            }
        }
    }

    public List<HashtagDto> getTweetTags(Long id) {
        Tweet tweet = tweetRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found"));
        List<HashtagDto> hashtagDtos = new ArrayList<>();
        for (Hashtag hashtag : tweet.getHashtags()) {
            HashtagDto dto = hashtagMapper.entityToDto(hashtag);
            hashtagDtos.add(dto);
        }

        return hashtagDtos;
    }

    @Override
    public List<UserResponseDto> getTweetMentions(Long tweetId) {
        Tweet tweet = tweetRepository.findByIdAndDeletedFalse(tweetId)
                .orElseThrow(() -> new NotFoundException("Tweet not found"));
        return tweet.getMentionedUsers().stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ContextDto getTweetContext(Long tweetId) {
        Tweet target = tweetRepository.findByIdAndDeletedFalse(tweetId)
                .orElseThrow(() -> new NotFoundException("Tweet not found"));

        List<TweetResponseDto> before = new ArrayList<>();
        Tweet current = target.getInReplyTo();
        while (current != null && !current.isDeleted()) {
            before.add(0, tweetMapper.entityToDto(current));
            current = current.getInReplyTo();
        }

        List<TweetResponseDto> after = collectReplies(target);

        ContextDto context = new ContextDto();
        context.setTarget(tweetMapper.entityToDto(target));
        context.setBefore(before);
        context.setAfter(after);

        return context;
    }

    private List<TweetResponseDto> collectReplies(Tweet tweet) {
        List<TweetResponseDto> replies = new ArrayList<>();
        for (Tweet reply : tweet.getReplies()) {
            if (!reply.isDeleted()) {
                replies.add(tweetMapper.entityToDto(reply));
                replies.addAll(collectReplies(reply));
            }
        }
        return replies;
    }
}