# FiniTask - Architecture commune

## Technologies
- Java 21
- Spring Boot
- Maven
- MySQL
- HTML, CSS, JavaScript

## Structure backend

finitask-backend/src/main/java/com/finitask/

- config/ : configuration generale
- security/ : authentification et autorisations
- entity/ : entites JPA
- repository/ : acces aux donnees
- service/ : logique metier
- controller/ : API REST
- dto/ : objets utilises pour les requetes et reponses
- enums/ : enumerations partagees

## Structure frontend

finitask-backend/src/main/resources/static/

- login.html : connexion
- register.html : inscription
- profil.html : profil utilisateur
- users.html : gestion des utilisateurs
- projects.html : projets
- teams.html : equipes
- tasks.html : taches
- index.html : tableau de bord
- css/style.css : styles communs
- js/api.js : appels aux API
- js/ui.js : composants d'interface
- js/pages.js : logique des pages
- js/auth.js : authentification
- js/main.js : initialisation

Les fichiers frontend seront ajoutes progressivement
par les responsables des modules.

## Repartition du travail

Sirine :
- Authentification
- Gestion des utilisateurs
- Profil et securite

Mariem :
- Gestion des equipes
- Gestion des projets

Yosr :
- Gestion des taches
- Tableau de bord

## Regles de collaboration

1. Utiliser les packages communs existants.
2. Ne pas creer un deuxieme projet Spring Boot.
3. Developper les fonctionnalites sur des branches personnelles.
4. Ne pas enregistrer de mots de passe dans Git.
5. Ne pas modifier les fichiers des autres sans coordination.
6. Integrer les fonctionnalites dans main apres verification.
