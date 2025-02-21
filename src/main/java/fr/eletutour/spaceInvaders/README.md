# Space Invaders
<div align="center">
  <img width="799" alt="image" src="https://github.com/user-attachments/assets/369921f3-7e56-45d4-975b-c2c7729f4c8d" />
</div>

Ce projet est une implémentation du jeu classique Space Invaders en Java utilisant la bibliothèque Swing pour l'interface graphique. Le jeu propose deux panels : un pour l'espace de jeu et un pour afficher le score. Le joueur contrôle un vaisseau pour affronter des vagues d'invaders, avec une difficulté croissante à travers différents niveaux.

## Commandes du jeu
* **Flèche Gauche** : Déplace le vaisseau à gauche
* **Flèche Droite** : Déplace le vaisseau à droite
* **Barre Espace** : Tire un projectile
* **R** (après Game Over) : Redémarre une nouvelle partie
* **Q** (après Game Over) : Quitte le jeu

## Fonctionnalités
* **Gameplay** :
  * Contrôlez un vaisseau pour abattre des invaders descendant progressivement.
  * Les invaders tirent aléatoirement sur le joueur.
  * Système de vies (3 au départ), perdues lorsqu'un tir ennemi touche le vaisseau.
* **Progression** :
  * Niveaux de difficulté croissante : plus d'invaders, vitesse accrue, tirs ennemis plus fréquents.
  * Passage au niveau suivant après avoir éliminé tous les invaders.
* **Interface** :
  * Panel de jeu (600x600 pixels) avec vaisseau, invaders, et projectiles.
  * Panel de score (200x600 pixels) affichant le score en temps réel.
  * Affichage des vies et du niveau dans le coin supérieur gauche.
* **Graphismes** :
  * Invaders avec design détaillé (corps, antennes, yeux, pattes).
  * Vaisseau joueur avec cockpit visuel.
  * Projectiles différenciés (blancs pour le joueur, jaunes pour les ennemis).
* Game Over :
  * Écran de fin avec options pour redémarrer (R) ou quitter (Q).