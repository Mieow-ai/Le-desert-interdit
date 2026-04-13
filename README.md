# Le Désert Interdit - Projet POGL 
## Membres : Simon Corvoysier, Sacha Donato

# Rapport POGL

## 1. Les parties du sujet que nous avons traitées

Nous avons implémenté la totalité du sujet et des règles du jeu de société:
* **Interface Graphique  avec Java swing :** Menu de lancement, plateau, vues des joueurs avec boutons spéciaux, vues des paquets de carte, actions de la tempête effectuées avec un délai visuel, apparition des objets à ramasser
* ** Multi-joueurs (Obligatoirement 2 à 6 joueurs) :** Implémentation des 6 rôles originaux avec leurs pouvoirs spécifiques :
  * *Archéologue, Alpiniste, Explorateur, Météorologue, Navigatrice, Porteuse d'eau.*
* **Mécaniques du jeu de société :**
  * Gestion de la tempête de sable (déplacement de l'œil, ensablement des tuiles).
  * Système de réserve d'eau et partage entre joueurs.
  * Découverte des indices et apparition des 4 pièces (Boîte, Hélice, Cristal, Navigation).
  * Utilisation de cartes Équipements (Jetpack, Blaster, Terrascope, Bouclier Solaire, Accélérateur de temps et Réserve d’eau).
* **Audio :** Intégration de musiques de fond et de bruitages d'interaction.



## 2. Vos choix d’architectures.
### A. Architecture Globale : Le Modèle-Vue-Contrôleur (MVC)
L'intégralité du projet est strictement séparée en trois composantes principales :
* Le Modèle : Le modèle (déplacement, gestion du sable, de l'eau, actions des joueurs) de manière totalement indépendante de tout affichage graphique.
* La Vue  : C'est l'interface graphique en Java Swing. Elle ne fait qu'afficher l'état actuel du modèle. Toutes ses classes redessinent les composants (via le gestionnaire paintComponent ou le GridBagLayout) en lisant les données du Modèle.
* Le Contrôleur : Il gère les actions de l'utilisateur (clics de souris ou boutons). C'est lui qui interprète ces événements, met à jour le Modèle en appelant ses méthodes, puis ordonne le rafraîchissement de la Vue.
* On a une entorse dans le modèle MVC pour les fenêtres de dialogue, on a été obligé de les faire dans la Vue et non dans le Contrôleur car sinon on aurait eu trop de booléen à traiter.

### B. Mise à jour dynamique : Pattern Observer / Observable
Pour maintenir le Modèle indépendant de la Vue, nous avons implémenté le patron Observer / Observable vu en cours. Le Modèle n'a aucune référence directe vers l'interface graphique. À la place, les objets majeurs (comme le Plateau) héritent de notre classe Observable. La VuePlateau (et les autres vues) s'y abonnent en tant qu'Observer. Dès qu'une mécanique altère l'état du jeu (ex: une tempête s'abat ou un joueur creuse), le plateau notifie ses observateurs, déclenchant ainsi un redessinage automatique de l'interface.

### C. L'Héritage et Polymorphisme abstrait
L'organisation des classes internes implémentent des fonctions polymorphes.
* Modélisation des Rôles : Nous avons créé une classe abstraite Joueur qui regroupe les actions standard (marcher, boire, creuser). Chaque rôle spécifique (Alpiniste, Explorateur...) hérite de cette classe et redéfinit (override) ses méthodes. Par exemple, l'explorateur redéfinit getDirections() pour ajouter les diagonales, modifiant ses déplacements sans altérer la boucle principale.
* Modélisation du Terrain : De la même manière, la classe abstraite Zone dicte le comportement de base d'une tuile (contenir des joueurs, stocker du sable). Les sous-classes (ZoneEau, ZoneTunnel, ZoneIndice) surchargent principalement la méthode actionReveler(), déclenchant automatiquement leurs effets uniques (donner l'eau coordonnée, devenir un relais de téléportation) sans intervention lourde du contrôleur.

### D. Pattern Factory
Afin de centraliser et sécuriser l'instanciation des objets polymorphes, nous utilisons des méthodes fabriques statiques (Zone.creerZone(...) et Joueur.creerJoueur(...)). Lors de la génération aléatoire du board ou du lobby, le système passe simplement un Enum du composant souhaité, et la fabrique renvoie la bonne sous-classe configurée.

### E. Machine à états dans le Contrôleur
Pour partager de façon claire les rôles d'interaction, la classe Controleur s'appuie sur une structure d'états (souvent gérée via Enum). Cela permet au jeu de contextualiser le clic du joueur : si l'état est DEPLACEMENT, un clic sur une case invoque la méthode setZone(), mais si l'état est CREUSER, ce même clic invoquera creuserZone().

### F. Arborescence du projet
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

## 3. Les problèmes qui sont présents et que nous n'avons pas pu éliminer
   - A notre connaissance la seule règle qui diffère entre notre implémentation et le jeu de société est que la navigatrice peut déplacer un joueur d'une case pour une action dans notre implémentation tandis que dans le vrai jeu elle peut déplacer le joueur de 3 cases pour une action.
Cela aurait ajouté plusieurs contraintes dans notre code car il aurait fallu gérer les cas où la navigatrice veut déplacer un joueur d'une seule ou de deux cases seulement tout en enlevant 1 point d'action. Par manque de temps nous n'avons pas implémenté cette feature.
- Un problème réside dans notre implémentation qui est le suivant : 
Si deux objets apparaissent au même endroit et que le premier n'a pas été ramassé alors un objet disparait (ce qui a peu de chance d'arriver lors d'une partie). Le changement vers une liste nécessite de nombreuses modifications dans le code.
## 4- Ce que nous n'avons pas réalisé nous même
* L'intégralité des images a été généré via Nano Banana 2 (Modèle de génération d'images de google)
* Les sons ont étés récupérés sur youtube ainsi que sur 101soundboaords (https://www.101soundboards.com/)
* Les personnalisations graphiques ne s'appliquaient pour la plupart pas jusqu'à que nous copions ce code sur la documentation d'Oracle (https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html#available)
  ~~~
  try {
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception _){}
  ~~~
  
# Read-Me
## 1- Description
Ce projet est une adaptation numérique du jeu de carte 'Le désert Interdit'. Le principe du jeu est le suivant : Vous êtes une équipe de joueurs ayant survécu à un crash
, vous vous retrouvez alors perdu dans le desert avec une quantité d'eau limitée. Vous devez survivre, trouver des indices et des pièces d'équipement pour réparer votre machine. Cependant il y aussi un danger permanent, la tempête. Vous découvrirez les différentes fonctionnalités des joueurs, des équipements et des zones en jouant, ou en regardant les règles.

## 2- Prérequis et Lancement

### Prérequis
* **Java Development Kit (JDK) :** Version 8 
* **Ressources :** Il faut s'assurer que les images (`desert.png`, `tempete.png`, etc.) sont situés dans le dossier images à la racine et les sons (`.wav`) sont situés dans la racine du projet.

### Lancement

Lancement automatique avec IntelliJ IDEA

