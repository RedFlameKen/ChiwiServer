package com.voxopus.chiwiserver.model.reviewer;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.voxopus.chiwiserver.enums.ReviewChatType;
import com.voxopus.chiwiserver.model.review_session.ReviewSession;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="review_chats")
public class ReviewChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Enumerated(EnumType.STRING)
    private ReviewChatType type;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date time_sent;

    @ManyToOne
    @JoinColumn(name = "review_session_id")
    private ReviewSession reviewSession;
}
