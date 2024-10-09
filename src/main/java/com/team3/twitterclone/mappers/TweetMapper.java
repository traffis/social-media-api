package com.team3.twitterclone.mappers;
import org.mapstruct.*;
import com.team3.twitterclone.entities.Tweet;
import com.team3.twitterclone.dtos.TweetResponseDto;
import com.team3.twitterclone.dtos.TweetRequestDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TweetMapper {

    TweetResponseDto entityToDto(Tweet tweet);

    Tweet dtoToEntity(TweetRequestDto tweetRequestDto);

    List<Tweet> dtosToEntities(List<TweetResponseDto> tweetResponseDtos);

}
