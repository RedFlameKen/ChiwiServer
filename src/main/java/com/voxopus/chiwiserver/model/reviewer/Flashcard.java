package com.voxopus.chiwiserver.model.reviewer;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.voxopus.chiwiserver.enums.FlashcardType;
import com.voxopus.chiwiserver.model.review_session.FlashcardQueueItem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Entity
@Builder
@Table(name="flashcards")
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String question;

    @Enumerated(EnumType.STRING)
    private FlashcardType type;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date date_created;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date date_modified;

    @ManyToOne
    @JoinColumn(name="reviewer_id")
    private Reviewer reviewer;

    @OneToMany(mappedBy = "flashcard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @OneToMany(mappedBy = "flashcard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlashcardQueueItem> flashcardQueueItems;

}
