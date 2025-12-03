package com.voxopus.chiwiserver.model.review_session;

import com.voxopus.chiwiserver.enums.AnswerState;
import com.voxopus.chiwiserver.model.reviewer.Flashcard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "flashcard_queue_items")
public class FlashcardQueueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flashcardQueueItem;

    @ManyToOne
    @JoinColumn(name = "flashcard_id")
    private Flashcard flashcard;

    @ManyToOne
    @JoinColumn(name = "review_session_id")
    private ReviewSession reviewSession;

    private Long queuePosition;

    @Enumerated(EnumType.STRING)
    private AnswerState answerState;

    private String submittedAnswer;

    // @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
    // private boolean isCorrect;
    
}
