package com.team3.twitterclone.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Credentials credentials;

    @Column(updatable = false)
    private Timestamp joined = new Timestamp(System.currentTimeMillis());

    private boolean deleted;

    @Embedded
    private Profile profile;

    @ManyToMany
    @JoinTable(
            name = "followers_following",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<User> following = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tweet> tweets = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    private Set<Tweet> likedTweets = new HashSet<>(); //  Tweets liked by this user. Used in UserService for retrieving user likes, and in TweetService when processing new tweets to update like relationships.

    @ManyToMany(mappedBy = "mentionedUsers")
    private Set<Tweet> mentionedTweets = new HashSet<>(); //  Tweets where this user is mentioned. Used in UserService for retrieving user mentions, and in TweetService when processing new tweets to update mention relationships.

    @OneToMany(mappedBy = "author")
    private Set<Tweet> authoredTweets = new HashSet<>(); //  Tweets authored by this user. Used in TweetService for retrieving user tweets.

    @ManyToMany(mappedBy = "mentionedUsers")
    private Set<Tweet> mentionedInTweets = new HashSet<>();

}
