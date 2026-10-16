package com.example.demo.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration de l'API /api/tickets (H2 en mémoire, données jetables).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ---------------------------------------------------------------
    // Création
    // ---------------------------------------------------------------

    @Test
    void createTicket_valide_renvoie201_etStatutOuvert() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titre\":\"Imprimante bloquée\",\"description\":\"File d'attente figée\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titre").value("Imprimante bloquée"))
                .andExpect(jsonPath("$.description").value("File d'attente figée"))
                .andExpect(jsonPath("$.statut").value("OUVERT"))
                .andExpect(jsonPath("$.dateCreation").isNotEmpty());
    }

    @Test
    void createTicket_titreVide_renvoie400_avecDetails() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titre\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation échouée"))
                .andExpect(jsonPath("$.details.titre").value("Le titre est obligatoire"));
    }

    @Test
    void createTicket_titreTropLong_renvoie400() throws Exception {
        String titreTropLong = "x".repeat(101);
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titre\":\"" + titreTropLong + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.titre").value("Le titre ne peut pas dépasser 100 caractères"));
    }

    @Test
    void createTicket_titreDuplique_renvoie409() throws Exception {
        creerTicket("Ticket existant", null);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titre\":\"Ticket existant\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("déjà utilisé")));
    }

    @Test
    void createTicket_jsonMalForme_renvoie400() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{titre:"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("JSON")));
    }

    // ---------------------------------------------------------------
    // Lecture & filtrage
    // ---------------------------------------------------------------

    @Test
    void getAllTickets_renvoieLaListe() throws Exception {
        creerTicket("Ticket A", null);
        creerTicket("Ticket B", "Description B");

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
    }

    @Test
    void getTickets_filtrésParStatut() throws Exception {
        String idOuvert = creerTicket("Ouvert à filtrer", null);
        String idFerme = creerTicket("Fermé à filtrer", null);
        fermerTicket(idFerme);

        mockMvc.perform(get("/api/tickets").param("statut", "FERME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].statut", everyItem(is("FERME"))));

        mockMvc.perform(get("/api/tickets").param("statut", "OUVERT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.titre == 'Ouvert à filtrer')]").exists());
    }

    @Test
    void getTickets_statutInvalide_renvoie400() throws Exception {
        mockMvc.perform(get("/api/tickets").param("statut", "INCONNU"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("INCONNU")));
    }

    // ---------------------------------------------------------------
    // Fermeture
    // ---------------------------------------------------------------

    @Test
    void closeTicket_renvoie201_etStatutFerme() throws Exception {
        String id = creerTicket("À fermer", null);

        mockMvc.perform(patch("/api/tickets/" + id + "/fermer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Long.parseLong(id)))
                .andExpect(jsonPath("$.statut").value("FERME"));
    }

    @Test
    void closeTicket_dejaFerme_renvoie409() throws Exception {
        String id = creerTicket("Fermé deux fois", null);
        fermerTicket(id);

        mockMvc.perform(patch("/api/tickets/" + id + "/fermer"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("déjà fermé")));
    }

    @Test
    void closeTicket_inexistant_renvoie404() throws Exception {
        mockMvc.perform(patch("/api/tickets/999999/fermer"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("999999")));
    }

    @Test
    void closeTicket_idNonNumerique_renvoie400() throws Exception {
        mockMvc.perform(patch("/api/tickets/abc/fermer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("abc")));
    }

    @Test
    void getTicket_inconnu_listeVide() throws Exception {
        mockMvc.perform(get("/api/tickets").param("statut", "EN_COURS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ---------------------------------------------------------------
    // Utilitaires
    // ---------------------------------------------------------------

    private String creerTicket(String titre, String description) throws Exception {
        String body = description == null
                ? "{\"titre\":\"" + titre + "\"}"
                : "{\"titre\":\"" + titre + "\",\"description\":\"" + description + "\"}";
        MvcResult result = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        Object id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return String.valueOf(id);
    }

    private void fermerTicket(String id) throws Exception {
        mockMvc.perform(patch("/api/tickets/" + id + "/fermer"))
                .andExpect(status().isOk());
    }
}
