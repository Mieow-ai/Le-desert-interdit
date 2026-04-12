# Le Désert Interdit - Version Java

## 1- Description
Ce projet est une adaptation numérique du jeu de carte 'Le désert Interdit'. Nous étions deux à réaliser ce projet. Le principe du jeu est le suivant : Vous êtes une équipe de joueurs qui a survécu à un crash
, vous vous retrouvez alors perdu dans le desert avec une quantité d'eau limitée. Vous devez survivre, trouver des indices et des pièces d'équipement pour réparer votre support. Il y aussi un danger permanent, la tempête.

## 2- Fonctionnalités implémentées
* **Interface Graphique complète (Java Swing) :** Menu de lancement, plateau dynamique, panneaux de joueurs et gestionnaire d'événements.
* **Jeu Coopératif (2 à 6 joueurs) :** Implémentation des 6 rôles originaux avec leurs pouvoirs spécifiques :
    * *Archéologue, Alpiniste, Explorateur, Météorologue, Navigatrice, Porteuse d'eau.*
* **Mécaniques du jeu de société :**
    * Gestion de la tempête de sable (déplacement de l'œil, ensablement des tuiles).
    * Système de réserve d'eau et partage entre joueurs.
    * Découverte des indices et apparition des 4 pièces (Boîte, Hélice, Cristal, Navigation).
    * Utilisation de cartes Équipements (Jetpack, Blaster, Terrascope, etc.).
* **Audio :** Intégration de musiques de fond et de bruitages d'interaction.

## 3- Architecture Technique
Ce projet respecte strictement le patron de conception **MVC (Modèle-Vue-Contrôleur)** et utilise le patron **Observer/Observable** pour la communication entre la logique et l'interface.

### Arborescence du projet
* **`modele/`** : Cœur de la logique métier.
    * Gestion de la grille et de la tempête (`Plateau`).
    * Polymorphisme pour les cases (`Zone`, `ZoneCrash`, `ZoneTunnel`, etc.) et les rôles (`Joueur`, `Alpiniste`, etc.).
    * Gestion des pioches et défausses (`Paquet`, `CarteTempete`, `CarteEquipement`).
* **`vue/`** : Interface graphique (Swing).
    * Fenêtre principale (`FenetreJeu`) et vues spécifiques (`VuePlateau`, `VueJoueur`, `VueCommande`, etc.).
    * Classe `Theme` centralisant la charte graphique.
    * Gestion des assets audio via la classe `Son`.
* **`controleur/`** : Lien entre le modèle et la vue.
    * La classe `Controleur` gère les interactions utilisateur (clics souris, boutons) via une machine à états (`enum action`).

## 5- Prérequis et Installation

### Prérequis
* **Java Development Kit (JDK) :** Version 8 si possible
* **Ressources (Assets) :** Il faut s'assurer que les images (`desert.png`, `tempete.png`, etc.) et les sons (`.wav`) sont situés dans la racine du projet.

### Lancement

Lancement automatique avec IntelliJ

