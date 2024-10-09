package com.team3.twitterclone.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Data
public class UserResponseDto {

    private String username;

    private ProfileDto profile;

    private Timestamp joined;

}
