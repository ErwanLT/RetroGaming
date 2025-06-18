import pygame
import random
import sys

# Initialisation de Pygame
pygame.init()

# Constantes du jeu
TAILLE_CASE = 20
LARGEUR = 600
HAUTEUR = 600
FPS = 10

# Couleurs
NOIR = (0, 0, 0)
VERT = (0, 255, 0)
ROUGE = (255, 0, 0)
BLANC = (255, 255, 255)

class Serpent:
    def __init__(self):
        self.segments = [(0, 0), (-20, 0), (-40, 0)]
        self.direction = "DROITE"
        self.derniere_direction = "DROITE"
        self.tete = self.segments[0]
    
    def deplacer(self):
        # Déplacer le corps
        for i in range(len(self.segments) - 1, 0, -1):
            self.segments[i] = self.segments[i-1][:]
        
        # Déplacer la tête
        x, y = self.segments[0]
        if self.direction == "DROITE":
            x += TAILLE_CASE
        elif self.direction == "GAUCHE":
            x -= TAILLE_CASE
        elif self.direction == "HAUT":
            y -= TAILLE_CASE
        elif self.direction == "BAS":
            y += TAILLE_CASE
        
        self.segments[0] = (x, y)
        self.tete = self.segments[0]
        self.derniere_direction = self.direction
    
    def grandir(self):
        # Ajouter un segment à la fin
        self.segments.append(self.segments[-1][:])
    
    def collision_mur(self):
        x, y = self.tete
        return x < 0 or x >= LARGEUR or y < 0 or y >= HAUTEUR
    
    def collision_soi_meme(self):
        return self.tete in self.segments[1:]
    
    def dessiner(self, surface):
        for i, segment in enumerate(self.segments):
            x, y = segment
            
            if i == 0:  # Tête du serpent
                # Corps de la tête (cercle vert plus grand)
                pygame.draw.circle(surface, VERT, (x + TAILLE_CASE//2, y + TAILLE_CASE//2), TAILLE_CASE//2)
                # Yeux (cercles noirs)
                if self.direction == "DROITE":
                    pygame.draw.circle(surface, NOIR, (x + TAILLE_CASE//2 + 3, y + TAILLE_CASE//2 - 3), 2)
                    pygame.draw.circle(surface, NOIR, (x + TAILLE_CASE//2 + 3, y + TAILLE_CASE//2 + 3), 2)
                elif self.direction == "GAUCHE":
                    pygame.draw.circle(surface, NOIR, (x + TAILLE_CASE//2 - 3, y + TAILLE_CASE//2 - 3), 2)
                    pygame.draw.circle(surface, NOIR, (x + TAILLE_CASE//2 - 3, y + TAILLE_CASE//2 + 3), 2)
                elif self.direction == "HAUT":
                    pygame.draw.circle(surface, NOIR, (x + TAILLE_CASE//2 - 3, y + TAILLE_CASE//2 - 3), 2)
                    pygame.draw.circle(surface, NOIR, (x + TAILLE_CASE//2 + 3, y + TAILLE_CASE//2 - 3), 2)
                elif self.direction == "BAS":
                    pygame.draw.circle(surface, NOIR, (x + TAILLE_CASE//2 - 3, y + TAILLE_CASE//2 + 3), 2)
                    pygame.draw.circle(surface, NOIR, (x + TAILLE_CASE//2 + 3, y + TAILLE_CASE//2 + 3), 2)
                # Langue (petit rectangle rouge)
                if self.direction == "DROITE":
                    pygame.draw.rect(surface, ROUGE, (x + TAILLE_CASE, y + TAILLE_CASE//2 - 1, 3, 2))
                elif self.direction == "GAUCHE":
                    pygame.draw.rect(surface, ROUGE, (x - 3, y + TAILLE_CASE//2 - 1, 3, 2))
                elif self.direction == "HAUT":
                    pygame.draw.rect(surface, ROUGE, (x + TAILLE_CASE//2 - 1, y - 3, 2, 3))
                elif self.direction == "BAS":
                    pygame.draw.rect(surface, ROUGE, (x + TAILLE_CASE//2 - 1, y + TAILLE_CASE, 2, 3))
            
            else:  # Corps du serpent
                # Corps principal (cercle vert)
                pygame.draw.circle(surface, VERT, (x + TAILLE_CASE//2, y + TAILLE_CASE//2), TAILLE_CASE//2 - 1)
                # Détail du corps (petit cercle plus foncé)
                pygame.draw.circle(surface, (0, 200, 0), (x + TAILLE_CASE//2, y + TAILLE_CASE//2), TAILLE_CASE//2 - 3)

class Nourriture:
    def __init__(self, serpent):
        self.position = self.generer_position(serpent)
        self.font = pygame.font.SysFont(None, TAILLE_CASE + 5)  # Police pour l'émoji
    
    def generer_position(self, serpent):
        while True:
            x = random.randint(0, (LARGEUR - TAILLE_CASE) // TAILLE_CASE) * TAILLE_CASE
            y = random.randint(0, (HAUTEUR - TAILLE_CASE) // TAILLE_CASE) * TAILLE_CASE
            if (x, y) not in serpent.segments:
                return (x, y)
    
    def dessiner(self, surface):
        x, y = self.position
        # Dessiner une pomme stylisée
        # Corps de la pomme (cercle rouge)
        pygame.draw.circle(surface, ROUGE, (x + TAILLE_CASE//2, y + TAILLE_CASE//2), TAILLE_CASE//2 - 2)
        # Tige de la pomme (rectangle marron)
        pygame.draw.rect(surface, (139, 69, 19), (x + TAILLE_CASE//2 - 1, y, 2, 4))
        # Feuille (petit rectangle vert)
        pygame.draw.rect(surface, VERT, (x + TAILLE_CASE//2 + 2, y + 2, 3, 2))

class Jeu:
    def __init__(self):
        self.ecran = pygame.display.set_mode((LARGEUR, HAUTEUR))
        pygame.display.set_caption("🐍 Snake Game")
        self.horloge = pygame.time.Clock()
        self.font = pygame.font.SysFont(None, 36)
        
        self.serpent = Serpent()
        self.nourriture = Nourriture(self.serpent)
        self.score = 0
        self.en_cours = True
        self.pause = False
    
    def gerer_evenements(self):
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                return False
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_UP and self.serpent.derniere_direction != "BAS":
                    self.serpent.direction = "HAUT"
                elif event.key == pygame.K_DOWN and self.serpent.derniere_direction != "HAUT":
                    self.serpent.direction = "BAS"
                elif event.key == pygame.K_LEFT and self.serpent.derniere_direction != "DROITE":
                    self.serpent.direction = "GAUCHE"
                elif event.key == pygame.K_RIGHT and self.serpent.derniere_direction != "GAUCHE":
                    self.serpent.direction = "DROITE"
                elif event.key == pygame.K_SPACE:
                    self.pause = not self.pause
        return True
    
    def mettre_a_jour(self):
        if self.pause:
            return
            
        self.serpent.deplacer()
        
        # Vérifier collision avec nourriture
        if self.serpent.tete == self.nourriture.position:
            self.score += 10
            self.serpent.grandir()
            self.nourriture = Nourriture(self.serpent)
        
        # Vérifier collisions
        if self.serpent.collision_mur() or self.serpent.collision_soi_meme():
            self.en_cours = False
    
    def afficher_score(self):
        texte = self.font.render(f"Score: {self.score}", True, BLANC)
        self.ecran.blit(texte, (10, 10))
    
    def afficher_game_over(self):
        texte = self.font.render("Game Over!", True, ROUGE)
        rect = texte.get_rect(center=(LARGEUR//2, HAUTEUR//2))
        self.ecran.blit(texte, rect)
    
    def afficher_pause(self):
        texte = self.font.render("PAUSE", True, BLANC)
        rect = texte.get_rect(center=(LARGEUR//2, HAUTEUR//2))
        self.ecran.blit(texte, rect)
    
    def dessiner(self):
        self.ecran.fill(NOIR)
        self.serpent.dessiner(self.ecran)
        self.nourriture.dessiner(self.ecran)
        self.afficher_score()
        
        if not self.en_cours:
            self.afficher_game_over()
        elif self.pause:
            self.afficher_pause()
        
        pygame.display.flip()
    
    def executer(self):
        while self.en_cours:
            if not self.gerer_evenements():
                break
            
            self.mettre_a_jour()
            self.dessiner()
            self.horloge.tick(FPS)
        
        # Attendre un peu avant de fermer
        pygame.time.wait(2000)
        pygame.quit()
        print(f"💀 Game Over - Score final: {self.score}")

if __name__ == "__main__":
    jeu = Jeu()
    jeu.executer()