package com.team3.twitterclone.mappers;

import com.team3.twitterclone.dtos.ProfileDto;
import com.team3.twitterclone.entities.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    Profile profileDtoToProfile(ProfileDto profileDto);

}
