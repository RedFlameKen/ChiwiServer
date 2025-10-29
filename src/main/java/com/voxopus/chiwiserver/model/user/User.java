package com.voxopus.chiwiserver.model.user;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.voxopus.chiwiserver.model.reviewer.ReviewSession;
import com.voxopus.chiwiserver.model.reviewer.Reviewer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String salt;

    @Column
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date date_created;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reviewer> reviewers;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ReviewSession reviewSession;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private AuthToken authToken;
    
}
