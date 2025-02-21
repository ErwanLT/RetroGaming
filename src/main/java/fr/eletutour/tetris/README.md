# Tetris
<div align="center">
    <img width="448" alt="image" src="https://github.com/user-attachments/assets/c57296ff-af2d-4877-8400-d4cb62ad82e5" />
</div>

Ce projet est une implémentation du jeu classique Tetris développée en Java avec l'interface graphique Swing. Il inclut les fonctionnalités de base du Tetris ainsi que des améliorations comme un système de score, des niveaux de difficulté croissante, et une interface visuelle améliorée.

## Fonctionnalités
* **Pièces classiques** : Les 7 formes traditionnelles de Tetris (I, J, L, O, S, T, Z) avec leurs couleurs distinctives.
* **Contrôles** :
  * Flèche gauche : Déplacer la pièce à gauche
  * Flèche droite : Déplacer la pièce à droite
  * Flèche bas : Accélérer la descente
  * Flèche haut : Faire pivoter la pièce
  * Espace : Démarrer le jeu depuis le menu initial
* **Système de score** :
  * 40 points × niveau pour 1 ligne
  * 100 points × niveau pour 2 lignes
  * 300 points × niveau pour 3 lignes
  * 1200 points × niveau pour 4 lignes
* **Niveaux** : La vitesse augmente tous les 10 lignes effacées (niveau max limité par une vitesse minimale de 100ms).
* **Interface** :
  * Plateau de jeu avec fond gris foncé et pièces colorées conservant leur couleur une fois posées.
  * Panneau latéral (gris clair) affichant le score, le niveau, le nombre de lignes effacées, et la prochaine pièce.
  * Écran de démarrage avec instruction "Press SPACE to start".
  * Message "Game Over" à la fin de la partie.