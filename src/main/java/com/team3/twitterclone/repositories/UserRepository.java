package com.team3.twitterclone.repositories;

import com.team3.twitterclone.entities.Credentials;
import com.team3.twitterclone.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByCredentialsUsername(String username);

    Optional<User> findByCredentialsUsernameAndDeletedFalse(String username);

    Optional<User> findByCredentialsAndDeletedFalse(Credentials credentials);

    List<User> findAllByDeletedFalse();
}
