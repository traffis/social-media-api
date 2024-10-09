package com.team3.twitterclone.repositories;

import com.team3.twitterclone.entities.Tweet;
import com.team3.twitterclone.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;



@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    List<Tweet> findByIdInAndDeletedFalse(List<Long> ids);
    List<Tweet> findByAuthorAndDeletedFalseOrderByPostedDesc(User author);
    List<Tweet> findByDeletedFalseOrderByPostedDesc();
    List<Tweet> findByAuthorInAndDeletedFalseOrderByPostedDesc(List<User> authors);
    Optional<Tweet> findByIdAndDeletedFalse(Long id);
    List<Tweet> findByMentionedUsersContainingAndDeletedFalseOrderByPostedDesc(User user);

}
