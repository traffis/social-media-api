package com.team3.twitterclone.mappers;

import com.team3.twitterclone.dtos.CredentialsDto;
import com.team3.twitterclone.entities.Credentials;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {

    Credentials credentialsDtoToCredentials(CredentialsDto credentialsDto);

    CredentialsDto credentialsToCredentialsDto(Credentials credentials);

}
