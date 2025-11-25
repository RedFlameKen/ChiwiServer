package com.voxopus.chiwiserver.model.review_session;

import com.voxopus.chiwiserver.enums.QuizState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quiz_sessions")
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 1024)
    String answer;

    @Enumerated(EnumType.STRING)
    QuizState state;

    @OneToOne
    @JoinColumn(name="review_session_id")
    ReviewSession reviewSession;

    // @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    // boolean isAnswering;
    
}
