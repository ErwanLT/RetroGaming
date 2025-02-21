# PacMan
<div align="center">
    <img width="401" alt="image" src="https://github.com/user-attachments/assets/3c7aac0f-f750-464f-9ba9-b5e35763a884" />
</div>

Une implémentation moderne du jeu classique Pac-Man en Java avec Swing, fidèle à l’original tout en ajoutant une touche personnelle grâce à l’utilisation d’emojis pour les fantômes et les fruits bonus.

## Description
Ce projet est une recréation du jeu Pac-Man, où le joueur contrôle Pac-Man pour manger tous les points et power pellets dans un labyrinthe tout en évitant ou poursuivant des fantômes. Les fonctionnalités incluent :
* Un labyrinthe inspiré de l’original avec tunnels latéraux et une cage centrale.
* Des fantômes avec un comportement intelligent (poursuite ou fuite).
* Des power pellets qui rendent les fantômes vulnérables temporairement.
* Des fruits bonus aléatoires représentés par des emojis.
* Un système de score et de vies affiché dans une barre d’information.

## Comment jouer
* Déplacement : Utilisez les flèches directionnelles (↑, ↓, ←, →) pour déplacer Pac-Man.
* Objectif : Mangez tous les points blancs (●) et power pellets (○) pour terminer le niveau.
* Fantômes :
  * Normalement (👻), ils vous poursuivent. Si vous les touchez, vous perdez une vie.
  * Après avoir mangé un power pellet, ils deviennent vulnérables (💀) pendant 6 secondes. Vous pouvez alors les manger pour 200 points chacun. Ils retournent à la cage centrale et réapparaissent après 3 secondes.
* Fruits bonus : Des emojis aléatoires (🍒, 🍓, 🍎, 🍌, 🍊) apparaissent occasionnellement, valant 50 points chacun.
* Fin de niveau : Le niveau se termine quand tous les points et power pellets sont mangés, puis il se réinitialise.
* Game Over : Vous perdez si vous n’avez plus de vies (initialement 3).

## Fonctionnalités principales
* Labyrinthe : Une grille 20x21 inspirée du Pac-Man original avec tunnels latéraux et une cage centrale pour les fantômes.
* Fantômes :
  * Deux fantômes avec un comportement semi-aléatoire (70% de poursuite intelligente, 30% aléatoire).
  * État vulnérable de 6 secondes après un power pellet, suivi d’un retour à la cage après être mangés.
* Power Pellets : Quatre gros points aux coins qui rendent les fantômes comestibles.
* Fruits : Apparaissent aléatoirement avec une chance de 5% par tick (toutes les 200ms après un délai initial).
* Interface : Barre d’information en haut affichant le score et les vies, labyrinthe en dessous.