package com.voxopus.chiwiserver.model.setup_session;

import java.util.Calendar;

import org.hibernate.annotations.CreationTimestamp;

import com.voxopus.chiwiserver.model.reviewer.Reviewer;
import com.voxopus.chiwiserver.model.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "reviewer_setup_sessions")
public class ReviewerSetupSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name="reviewer_id")
    Reviewer reviewer;

    @OneToOne
    @JoinColumn(name="user_id")
    User user;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Calendar dateStarted;

    @Temporal(TemporalType.TIMESTAMP)
    Calendar dateUsed;
    
    @OneToOne(mappedBy = "reviewerSetupSession", cascade = CascadeType.ALL, orphanRemoval = true)
    CreateFlashcardSession createFlashcardSession;

    @OneToOne(mappedBy = "reviewerSetupSession", cascade = CascadeType.ALL, orphanRemoval = true)
    SetupStep setupStep;

}
