package com.voxopus.chiwiserver.model.setup_session;

import com.voxopus.chiwiserver.enums.CreateFlashcardState;

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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "create_flashcard_sessions")
public class CreateFlashcardSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 1024)
    String question;

    @Column(length = 1024)
    String answer;

    @Enumerated(EnumType.STRING)
    CreateFlashcardState state;

    @OneToOne
    @JoinColumn(name="reviewer_setup_session_id")
    ReviewerSetupSession reviewerSetupSession;

}
