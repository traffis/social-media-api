package com.team3.twitterclone.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetRequestDto {
    private String content;
    private CredentialsDto credentials;
}

