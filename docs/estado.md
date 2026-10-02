# Estado · PomoStudy

Última actualización: 2026-10-02

## Hecho
- Proyecto importado desde Google AI Studio al repositorio https://github.com/nivergarah-collab/Pomodoro_Prototipe00 (commits `b0a8244` y `05a36eb`).
- Carpeta registrada en el espacio `Android` (`Android/Pomodoro_Prototipe00`).
- Estructura estándar creada: README, CHANGELOG, `skills/00-iniciar.md`, `docs/` y `scripts/verificar-estructura.ps1`.
- Revisión inicial del proyecto (ver "Notas de la revisión").
- Rama de trabajo conjunta: `feature/adaptar-al-espacio`.

## Siguiente
1. **Usuario:** habilitar el acceso de la app de Claude en GitHub a este repositorio, para que el agente pueda subir la rama (ver "Preguntas pendientes").
2. **Usuario:** colocar en `.github/workflows/` el flujo `pruebas.yml` que entregará Claude (esa ruta es solo del usuario).
3. Reemplazar las pruebas de ejemplo por pruebas reales de la lógica del temporizador, extrayéndola del `MainViewModel` a una clase Kotlin pura.
4. Pruebas de la meta diaria y de `StudyRepository` con una base Room en memoria.

## Preguntas pendientes
- El push de `feature/adaptar-al-espacio` fue rechazado (403): la app de Claude en GitHub no tiene acceso a este repositorio. Hay que agregarlo en https://github.com/apps/claude/installations/select_target (Repository access) o reconectar GitHub en los ajustes de conectores de claude.ai.
- ¿Se usará la API de Gemini? `metadata.json` declara la capacidad y `build.gradle.kts` incluye `firebase-ai`, pero el código no la usa. Si no se usa, conviene quitarla (con confirmación).

## Pruebas
Sin ejecutar. Las dos clases de prueba que trae el proyecto son de ejemplo y casi no prueban código de la app.

## Notas de la revisión
- Tamaño: unas 5.500 líneas de Kotlin. Los archivos más grandes: `SessionConfigScreen.kt` (787), `WeeklyStudyChart.kt` (642), `DailyGoalComponents.kt` (558), `ActiveTimerScreen.kt` (537), `MainViewModel.kt` (512).
- La lógica del temporizador (fases, bloques, avance, cierre de sesión) vive dentro de `MainViewModel`, que hereda de `AndroidViewModel` y depende de Room y de vibración: difícil de probar tal como está.
- `ExampleUnitTest` repite cálculos propios en vez de llamar a código de la app (solo usa `StudyPreset`). `ExampleRobolectricTest` comprueba el nombre de la app.
- El proyecto no incluye `gradlew` ni `gradle-wrapper.jar`, solo `gradle-wrapper.properties` (Gradle 9.3.1). La integración continua usa `gradle/actions/setup-gradle` con esa versión.
- `app/build.gradle.kts` firma también la compilación de depuración con `debug.keystore`, que está en `.gitignore`: sin ese archivo `assembleDebug` y las pruebas pueden fallar. La integración continua lo genera con `keytool`.
- La firma de lanzamiento lee `KEYSTORE_PATH`, `STORE_PASSWORD` y `KEY_PASSWORD` del entorno; no hay secretos en los archivos. Revisión de credenciales: sin hallazgos (solo la contraseña estándar `android` del almacén de depuración).
- `google-services.json` no existe; el plugin está en modo `WARN` y `googleServices.missing.passthrough=true`, así que no bloquea la compilación.
- `allowBackup="true"` en el manifiesto: revisar si conviene, porque la base Room guarda datos de estudio por usuario.
