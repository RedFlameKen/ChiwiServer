package com.voxopus.chiwiserver.model.reviewer;

import java.util.Date;
import java.util.List;

import com.voxopus.chiwiserver.enums.FlashcardType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="flashcard")
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String question;

    private FlashcardType type;

    private Date date_created;

    private Date date_modified;

    @ManyToOne
    @JoinColumn(name="reviewer_id")
    private Reviewer reviewer;

    @OneToMany(mappedBy = "flashcard", cascade = CascadeType.ALL)
    private List<Answer> answers;

}
