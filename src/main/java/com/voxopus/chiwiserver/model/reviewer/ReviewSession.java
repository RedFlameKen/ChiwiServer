package com.voxopus.chiwiserver.model.reviewer;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.voxopus.chiwiserver.model.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "review_sessions")
public class ReviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reviewer_id")
    private Reviewer reviewer;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="time_started")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date timeStarted;

    @OneToMany(mappedBy = "reviewSession", cascade = CascadeType.ALL)
    private List<ReviewChat> chats;

}
