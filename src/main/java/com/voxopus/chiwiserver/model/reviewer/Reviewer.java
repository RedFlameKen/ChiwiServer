package com.voxopus.chiwiserver.model.reviewer;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.voxopus.chiwiserver.model.setup_session.ReviewerSetupSession;
import com.voxopus.chiwiserver.model.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "reviewers")
public class Reviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String subject;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date date_created;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date date_modified;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flashcard> flashcards;

    @OneToOne(mappedBy = "reviewer")
    private ReviewSession reviewSession;
    
    @OneToOne(mappedBy = "reviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewerSetupSession reviewerSetupSession;
    
}
