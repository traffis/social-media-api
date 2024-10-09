package com.team3.twitterclone.mappers;

import com.team3.twitterclone.dtos.UserRequestDto;
import com.team3.twitterclone.dtos.UserResponseDto;
import com.team3.twitterclone.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, CredentialsMapper.class})
public interface UserMapper {

    @Mapping(target = "username", source = "credentials.username")
    UserResponseDto entityToDto(User entity);

    User requestDtoToEntity(UserRequestDto userRequestDto);

    List<UserResponseDto> entitiesToDtos(List<User> entities);




}
