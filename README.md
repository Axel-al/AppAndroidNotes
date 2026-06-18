# Notes

Application Android de prise de notes réalisée avec Jetpack Compose. Elle permet de créer, consulter, modifier, épingler et supprimer des notes stockées localement.

Projet réalisé par Axel ALABEATRIX, Thomas GERARD et Gabriel PLISSONIER.

## Technologies

- Kotlin et Jetpack Compose
- Architecture MVVM
- Room et KSP
- Dagger Hilt
- Navigation Compose
- `Flow` et `StateFlow`

## Exécution

Le projet requiert un JDK 17 et un SDK Android configuré. Utiliser exclusivement le Gradle Wrapper :

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

L’APK est généré dans :

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Documentation

- [Compte-rendu](documentation/COMPTE_RENDU.md)
- [Captures d’écran](documentation/captures)
