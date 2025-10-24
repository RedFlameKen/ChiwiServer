package com.voxopus.chiwiserver.model.user;

import java.util.Date;
import java.util.List;

import com.voxopus.chiwiserver.model.reviewer.Reviewer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String salt_iv;

    private Date date_created;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reviewer> reviewers;
    
}
