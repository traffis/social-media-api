package com.team3.twitterclone.dtos;

import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetResponseDto {
    private Long id;
    private UserResponseDto author;
    private Timestamp posted;
    private String content;
    private TweetResponseDto inReplyTo;
    private TweetResponseDto repostOf;

    // Ensure content is never null
    public String getContent() {
        return content != null ? content : "";
    }

    // Ensure author is never null
    public UserResponseDto getAuthor() {
        return author != null ? author : new UserResponseDto();
    }

}




//@Data
//public class TweetResponseDto {
//    private Long id;
//    private UserResponseDto author;
//    private Long posted; // Changed to Long to represent UNIX timestamp
//    private String content;
//    private TweetResponseDto inReplyTo;
//    private TweetResponseDto repostOf;
//}


