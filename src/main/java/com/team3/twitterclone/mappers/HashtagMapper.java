package com.team3.twitterclone.mappers;

import com.team3.twitterclone.dtos.HashtagDto;
import com.team3.twitterclone.dtos.TweetRequestDto;
import com.team3.twitterclone.entities.Hashtag;
import com.team3.twitterclone.entities.Tweet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

    Hashtag dtoToEntity(HashtagDto hashtagDto);

    HashtagDto entityToDto(Hashtag hashtag);

    List<HashtagDto> entitiesToDtos(List<Hashtag> hashtags);

    TweetRequestDto tweetToTweetRequestDto(Tweet tweet);
}

