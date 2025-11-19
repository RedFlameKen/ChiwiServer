package com.voxopus.chiwiserver.model.setup_session;

import java.util.Calendar;

import org.hibernate.annotations.CreationTimestamp;

import com.voxopus.chiwiserver.model.reviewer.Reviewer;

import jakarta.persistence.Entity;
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
@Table(name = "review_setup_sessions")
public class ReviewerSetupSession {

    @Id
    Long id;

    @OneToOne
    @JoinColumn(name="reviewer_id")
    Reviewer reviewer;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Calendar dateStarted;

    @Temporal(TemporalType.TIMESTAMP)
    Calendar dateUsed;
    
}
