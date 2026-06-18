# Compte-rendu

L’application est structurée selon une architecture MVVM en séparant la persistance Room, le repository, l’injection de dépendances Hilt et la présentation Compose. Le DAO expose la liste des notes sous forme de `Flow`, triée avec les notes épinglées en premier, puis le ViewModel transforme ce flux en `StateFlow` consommé par l’interface. Les opérations d’écriture sont exécutées dans `viewModelScope` et les erreurs de suppression ou de sauvegarde sont affichées dans une barre de message.

Le même écran d’édition gère la création et la modification grâce à l’identifiant transmis par une route Navigation Compose typée. Le formulaire conserve sa saisie pendant une rotation, désactive l’enregistrement lorsque le titre est vide et préserve les propriétés de la note lors d’une mise à jour. Une couleur par défaut a été ajoutée à l’entité et utilisée comme fond des cartes. La méthode de recherche demandée est également présente dans le DAO et le repository.

La principale adaptation a concerné les versions récentes du squelette Android : AGP 9 utilise Kotlin intégré. La configuration a donc été conservée et complétée avec KSP 2.3.9, Room 2.8.4 et Hilt 2.59.2, plutôt que de reprendre les anciennes versions du sujet.

La validation comprend les tests unitaires, le lint, la génération des APK, deux tests Compose instrumentés et un parcours manuel complet : création, lecture, modification, suppression avec confirmation, épinglage, persistance après arrêt forcé, rotation et état vide.

## Captures

- [Liste des notes](captures/01-liste-notes.png)
- [Ajout d’une note](captures/02-edition-note.png)
- [Confirmation de suppression](captures/03-confirmation-suppression.png)
