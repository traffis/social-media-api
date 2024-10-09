package com.team3.twitterclone.entities;// Imports
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.team3.twitterclone.entities.Hashtag;
import com.team3.twitterclone.entities.User;
import jakarta.persistence.*;
import java.util.List;

import jakarta.annotation.PostConstruct;


import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    private Timestamp posted; // Timestamp is a more precise version of Date, it used when we need to keep track of the date and time


    private boolean deleted = false; // Default value is false, we use this to keep track of whether a tweet has been deleted

    private String content; // Lob stands for Large Object, it is used to store large text data, we use to store the content of the tweet

    @ManyToOne
    @JoinColumn(name = "inReplyTo") // This is the foreign key column that will be used to join the Tweet to the Tweet it is replying to
    private Tweet inReplyTo;

    @ManyToOne
    @JoinColumn(name = "repostOf")
    private Tweet repostOf;

    @OneToMany(mappedBy = "inReplyTo")
	private List<Tweet> replies;

	@OneToMany(mappedBy = "repostOf")
	private List<Tweet> reposts;


//    POST tweets/{id}/like
//    Creates a "like" relationship between the tweet with the given id and the user whose credentials are provided by the request body. If the tweet is deleted or otherwise doesn't exist, or if the given credentials do not match an active user in the database, an error should be sent. Following successful completion of the operation, no response body is sent.
//    Request
//'Credentials'
    @ManyToMany(mappedBy = "likedTweets") // This is the field in the User class that maps to this field so that we can easily access the users who have liked this tweet
    private Set<User> likedByUsers = new HashSet<>(); // We use a Set to store the users who have liked this tweet because we don't want duplicates and we don't need to maintain order

    @ManyToMany
    @JoinTable(
            name = "tweet_hashtags",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private Set<Hashtag> hashtags = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "tweet_mentions",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> mentionedUsers = new HashSet<>();

    // Constructors
    public Tweet(User author, String content) {
        this.author = author;
        this.content = content;
        this.posted = new Timestamp(System.currentTimeMillis());
    }

    // Initialize 'posted' in the no-argument constructor so we don't have to worry about it being null
    @PostConstruct
    public void init() {
        if (this.posted == null) {
            this.posted = new Timestamp(System.currentTimeMillis());
        }
    }

    // Utility methods (if needed)
    public void addHashtag(Hashtag hashtag) {
        this.hashtags.add(hashtag);
        hashtag.getTweets().add(this);
    }
//
    public void removeHashtag(Hashtag hashtag) {
        this.hashtags.remove(hashtag);
        hashtag.getTweets().remove(this);
    }
}




