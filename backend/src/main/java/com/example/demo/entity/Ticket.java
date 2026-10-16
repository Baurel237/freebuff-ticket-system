package com.example.demo.entity;

import com.example.demo.enums.StatutTicket;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "tickets",
        uniqueConstraints = @UniqueConstraint(columnNames = "titre"))
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTicket statut;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    protected Ticket() {
    }

    public Ticket(String titre, String description) {
        this.titre = titre;
        this.description = description;
        this.statut = StatutTicket.OUVERT;
        this.dateCreation = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutTicket getStatut() {
        return statut;
    }

    public void setStatut(StatutTicket statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
