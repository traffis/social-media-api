package com.team3.twitterclone.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotAuthorizedException extends RuntimeException{

    private static final long serialVersionUID = -1838968802955235936L;

    private String message;
}
