package com.voxopus.chiwiserver.model.setup_session;

import com.voxopus.chiwiserver.enums.SetupCommandType;

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
@Table(name="setup_steps")
public class SetupStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name="reviewer_setup_session_id")
    ReviewerSetupSession reviewerSetupSession;

    @Enumerated(EnumType.STRING)
    SetupCommandType commandType;
    
}
