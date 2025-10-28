package com.voxopus.chiwiserver.model.user;

import java.util.Calendar;

import jakarta.persistence.Column;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="auth_tokens")
public class AuthToken {

    @Id
    @Column(unique = true)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Calendar expiration_date;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;
    
}
