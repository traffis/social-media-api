package com.team3.twitterclone.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Data
public class Hashtag {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany(mappedBy = "hashtags")
    private Set<Tweet> tweets = new HashSet<>();


    @Column(nullable = false, unique = true)
    private String label;

    @Column(nullable = false)
    private Timestamp firstUsed;
    @Column(nullable = false)
    private Timestamp lastUsed;

    public Hashtag(String label) {
        this.label = label;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.firstUsed = now;
        this.lastUsed = now;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Timestamp getFirstUsed() {
        return firstUsed;
    }

    public void setFirstUsed(Timestamp firstUsed) {
        this.firstUsed = firstUsed;
    }

    public Timestamp getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Timestamp lastUsed) {
        this.lastUsed = lastUsed;
    }

}


