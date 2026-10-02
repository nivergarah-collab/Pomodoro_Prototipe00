# PomoStudy

Aplicación Android para sesiones de estudio con la técnica Pomodoro: bloques personalizables con tema y subtema, temporizador con alertas, perfiles de usuario, meta diaria y estadísticas semanales. Nació en Google AI Studio y desde ahora se trabaja con las skills del espacio `Android`.

## Estado
Prototipo funcional importado desde AI Studio (commit `05a36eb`). Se adaptó a la forma de trabajo del espacio: estructura estándar, skills y estrategia de pruebas. Las pruebas que trae son de ejemplo y la lógica del temporizador aún no está cubierta. Detalle en `docs/estado.md`.

## Cómo ejecutarlo
1. Abrir la carpeta en Android Studio con JDK 17 o superior y sincronizar Gradle (el proyecto usa Gradle 9.3.1 y AGP 9.1.1).
2. Para compilar en depuración hace falta un `debug.keystore` en la raíz del proyecto (está en `.gitignore`). Si falta, se genera con `keytool` (ver `docs/pruebas.md`).
3. Ejecutar el módulo `app` en un dispositivo o emulador con Android 7.0 (API 24) o superior.

Los requisitos salen de `app/build.gradle.kts` y `gradle/libs.versions.toml`; la ejecución no fue verificada en este espacio.

## Estructura
- `app/src/main/java/com/example/`: código Kotlin con Jetpack Compose.
  - `MainActivity.kt`: actividad única.
  - `ui/MainViewModel.kt`: estado de la sesión, temporizador, usuarios y ajustes.
  - `ui/session/`, `ui/stats/`, `ui/user/`, `ui/components/`, `ui/theme/`: pantallas, diálogos y componentes.
  - `data/`: base de datos Room (`AppDatabase`, `dao/`, `model/`) y `repository/StudyRepository.kt`.
  - `util/NotificationAlertManager.kt`: sonido y vibración de las alertas.
- `app/src/test/`: pruebas unitarias en JVM (incluye Robolectric).
- `app/src/androidTest/`: pruebas instrumentadas.
- `skills/`: constructor y reglas específicas del proyecto.
- `docs/`: estado, decisiones, pruebas y conexiones.
- `scripts/`: verificaciones automáticas.
- `metadata.json` y `.env.example`: archivos de AI Studio; se conservan por ahora.

## Decisiones importantes
- Se mantiene Kotlin + Compose + Room, como salió de AI Studio.
- La clave de Gemini nunca se versiona: va en `.env` (ignorado por git) o como secreto.
- El repositorio es público: no se suben claves, almacenes de claves ni `google-services.json`.

Detalle en `docs/decisiones.md`.

## Documentación relacionada
- `docs/estado.md`
- `docs/decisiones.md`
- `docs/pruebas.md`
- `docs/conexiones.md`
- `skills/00-iniciar.md`
- `CHANGELOG.md`
