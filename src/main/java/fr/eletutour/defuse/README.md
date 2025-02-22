# Bomb Defusal Game
<div align="center">
  <img width="597" alt="image" src="https://github.com/user-attachments/assets/ee850438-c3a5-4895-8a9b-9b5a1916ff4c" />
</div>

## Description
Le *Bomb Defusal Game* est un jeu de désamorçage de bombe écrit en Java. Le joueur dispose de 60 secondes pour couper 3 fils corrects parmi 15 fils (5 bleus, 5 rouges, 5 jaunes) disposés dans une grille 5x3. Chaque ligne de la grille contient un fil bleu, un rouge et un jaune, dans cet ordre. Si le joueur coupe un fil correct, une LED verte s’allume ; sinon, une LED rouge s’allume. Le jeu se termine par une victoire si les 3 fils corrects sont coupés, ou par une défaite (explosion) si 3 fils incorrects sont coupés ou si le temps est écoulé. Un bouton "Indice" permet de voir les couleurs des fils corrects (sans préciser lesquels), et une pop-up à la fin du jeu indique les fils corrects avec leur position dans la grille.

## Fonctionnalités
- **Compteur à rebours** : Un timer de 60 secondes est affiché en haut de l’interface. S’il atteint zéro, la bombe explose.
- **Fils et LEDs** : Il y a 15 fils disposés en une grille 5x3 (Bleu, Rouge, Jaune par ligne). Couper un fil correct allume une LED verte, et un fil incorrect allume une LED rouge. 3 fils corrects doivent être coupés pour gagner, mais couper 3 fils incorrects entraîne une défaite.
- **Bouton Indice** : Permet de voir les couleurs des fils corrects (par exemple, "Bleu, Rouge" si des fils bleus et rouges sont corrects) sans indiquer leur position exacte.
- **Game Over** : À la fin (victoire ou défaite), une pop-up indique les fils corrects avec leur position dans la grille (par exemple, "Fil Bleu (Ligne 1, Colonne 1)").
- **Log au démarrage** : Les fils corrects sont logués dans la console (`System.out`) au démarrage du jeu, avec leur couleur et position visuelle dans la grille.

## Structure de l’Interface
- **Haut** : Compteur à rebours (format "00:XX", jaune sur fond noir).
- **Milieu** : 6 LEDs (3 vertes, 3 rouges, grises lorsqu’inactives).
- **Bas** : Grille 5x3 de fils, avec chaque ligne contenant un fil bleu (colonne 1), rouge (colonne 2), et jaune (colonne 3).
- **Bas de l’IHM** : Bouton "Indice" et zone de texte pour afficher les indices.
