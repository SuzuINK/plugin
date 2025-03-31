# ReveilDesChasseurs

Un plugin Minecraft qui ajoute un système de donjons et de quêtes avec des mobs personnalisés.

## Fonctionnalités

- Système de donjons générés procéduralement
- Système de classes avec des capacités uniques
- Système de rangs avec des multiplicateurs de récompenses
- Mobs personnalisés avec MythicMobs
- Système de récompenses (XP et argent)
- Système de pièges et de spawners
- Génération de butin aléatoire

## Prérequis

- Spigot/Paper 1.19.4+
- MythicMobs 5.3.5+
- Java 17+

## Installation

1. Téléchargez la dernière version du plugin
2. Placez le fichier .jar dans le dossier `plugins` de votre serveur
3. Redémarrez votre serveur
4. Configurez le plugin dans le fichier `config.yml`

## Commandes

### Donjons
- `/dungeon join <nom>` - Rejoindre un donjon
- `/dungeon leave` - Quitter le donjon actuel
- `/dungeon list` - Afficher la liste des donjons disponibles
- `/dungeon info <nom>` - Afficher les informations d'un donjon

### Classes
- `/class` - Afficher votre classe actuelle
- `/class <classe>` - Changer de classe
  - Classes disponibles : guerrier, archer, mage, assassin

### Rangs
- `/rank` - Afficher votre rang actuel
- `/rank <rang>` - Changer de rang
  - Rangs disponibles : novice, apprenti, expert, maître, grand maître

### Quêtes
- `/quest accept <nom>` - Accepter une quête
- `/quest abandon <nom>` - Abandonner une quête
- `/quest complete <nom>` - Compléter une quête
- `/quest list` - Afficher la liste des quêtes disponibles
- `/quest info <nom>` - Afficher les informations d'une quête

## Permissions

- `reveildeschasseurs.dungeon` - Permet d'utiliser les commandes de donjon
- `reveildeschasseurs.class` - Permet d'utiliser les commandes de classe
- `reveildeschasseurs.rank` - Permet d'utiliser les commandes de rang
- `reveildeschasseurs.quest` - Permet d'utiliser les commandes de quête

## Configuration

Le fichier `config.yml` permet de configurer :
- Les classes et leurs statistiques
- Les rangs et leurs multiplicateurs
- Les paramètres des donjons
- Les récompenses de base
- Les multiplicateurs de difficulté
- Les statistiques des mobs
- Les messages du plugin

## Développement

Le projet utilise Maven pour la gestion des dépendances.

Pour compiler le plugin :
```bash
mvn clean package
```

Le fichier .jar sera généré dans le dossier `target`.

## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :
1. Fork le projet
2. Créer une branche pour votre fonctionnalité
3. Commit vos changements
4. Push sur votre fork
5. Créer une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Support

Si vous rencontrez des problèmes ou avez des questions :
1. Consultez la documentation
2. Ouvrez une issue sur GitHub
3. Contactez-nous sur Discord 