A faire :
 - ajouter prof au git 
 - Utiliser factory là où c'est approprié
 - mettre les joueurs dans le plateau
 - ajouter un bouton se déplacer
- ajouter indice joueur actif dans le controller ainsi qu'un attribut joueurs initialiser lors de la construction grace au plateau
- affichage fleches sur les cases accesibles
- piles de sables sur le coté (sable max restant)
- Couleur random lors de ajout d'un joueur
- possibilité d'entrer un nom pour ajout joueur, sinon nom de base Joueur numéro size

A savoir :

-  Une tuile est considérée comme bloquée s’il y a 2 marqueurs
   Sable ou plus dessus
- une fois que deux indices sont reveles l'objet apparait à l'intersection, cependant après l'objet peut bouger si la tempete bouge la case
- 2 à 5 joueurs
- 6 roles spéciaux, chaque joueur a forcément un rôle spécial donc joueur est une classe abstraite
- si l'oeil doit se déplacer par exemple de deux cases vers une direction mais qu'il n'y a qu'une case dans cette direction alors l'oeil se déplace quand même mais d'une case
- si un seul joueur meurt de soif, tout le monde perd immédiatement la partie.

Défaite
Il y a 3 façons possibles de perdre :
1. Mourir de soif : si un joueur atteint le symbole de sa gourde.
2. Mourir ensablé : si vous devez ajouter un marqueur Sable
   sur une tuile et qu’il n’y en a plus. // à gérer par le max sable
3. Mourir emporté dans les airs : l’échelle Tempête de sable
   atteint le symbole
