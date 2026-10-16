-- ============================================================
-- Jeu de données de test — Module Ticket
-- ------------------------------------------------------------
-- Réinitialise la table "tickets" avec un jeu de données
-- réaliste couvrant les 3 statuts (OUVERT, EN_COURS, FERME).
--
-- Usage (PostgreSQL local via Docker) :
--   docker exec -i tickets-postgres psql -U postgres -d tickets_db < scripts/seed.sql
--
-- ATTENTION : cette commande écrase les tickets existants
-- (données de développement uniquement).
-- ============================================================

TRUNCATE TABLE tickets RESTART IDENTITY;

INSERT INTO tickets (titre, description, statut, date_creation) VALUES
    -- OUVERT
    ('Impossible de se connecter au compte',
     'L''utilisateur reçoit une erreur 500 lors de la connexion depuis hier soir. Le problème semble lié à la mise à jour du service d''authentification.',
     'OUVERT', now() - interval '10 minutes'),
    ('Le paiement par carte échoue à l''étape 3',
     'Plusieurs clients remontent un paiement refusé alors que leur banque confirme l''autorisation. À investiguer côté passerelle de paiement.',
     'OUVERT', now() - interval '3 hours'),
    ('Espace disque presque saturé sur le serveur de production',
     'Le disque est à 92 % de remplissage. Prévoir un nettoyage des logs et un agrandissement du volume.',
     'OUVERT', now() - interval '1 day'),
    ('Ajouter l''export CSV des tickets',
     'Feature request de l''équipe support : pouvoir exporter la liste des tickets filtrés au format CSV.',
     'OUVERT', now() - interval '2 days'),

    -- EN_COURS
    ('Lenteur du tableau de bord après la mise à jour',
     'Le chargement du tableau de bord passe de 2s à 12s environ. Requête SQL identifiée comme suspecte, optimisation en cours.',
     'EN_COURS', now() - interval '5 hours'),
    ('Emails de notification non reçus',
     'Les notifications ne partent plus depuis l''incident SMTP de lundi. Le connecteur a été redémarré, on surveille.',
     'EN_COURS', now() - interval '1 day'),
    ('Bug d''affichage sur mobile Safari',
     'Le menu déroulant se referme immédiatement sur iOS 18. Correction testée en préproduction.',
     'EN_COURS', now() - interval '3 days'),

    -- FERME
    ('Mot de passe réinitialisé impossible',
     'Le lien de réinitialisation expirait en 5 minutes au lieu de 30. Délai corrigé et redéployé.',
     'FERME', now() - interval '4 days'),
    ('Le bouton "Fermer le ticket" ne répondait pas',
     'Le gestionnaire d''événement était attaché deux fois après un re-render. Corrigé dans la version 1.2.1.',
     'FERME', now() - interval '6 days'),
    ('Documentation de l''API manquante',
     'La doc Swagger était inactive en production. Endpoint /v3/api-docs réactivé et vérifié.',
     'FERME', now() - interval '8 days');
