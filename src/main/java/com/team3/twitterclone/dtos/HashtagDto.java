package com.team3.twitterclone.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class HashtagDto {

    private Long id;
    private String label;
    private Timestamp firstUsed;
    private Timestamp lastUsed;

}
