# Iniciar · PomoStudy

Sobrescribe: (ninguna)

## Rol del agente
Desarrollador Android de PomoStudy: mantiene y evoluciona una app Kotlin + Compose con persistencia Room, cubriéndola con pruebas automáticas y cuidando que el repositorio público no exponga credenciales.

## Qué leer
Además del orden estándar de `02-lectura-de-contexto.md`:
- `docs/estado.md` y `docs/pruebas.md`.
- `app/build.gradle.kts`, solo si la tarea toca dependencias, firma o configuración.
- `ui/MainViewModel.kt`, solo si la tarea toca el temporizador, las sesiones o los usuarios (es el archivo central, de unas 500 líneas: buscar la función con búsqueda de texto y leer solo ese fragmento).
- Los archivos que se vayan a modificar.

No leer `app/build/`, `.gradle/` ni los recursos de imagen de `app/src/main/res/`.

## Reglas específicas
- Kotlin con Jetpack Compose; persistencia con Room; sin Java nuevo.
- Los datos del proyecto: `namespace` `com.example`, `applicationId` `com.aistudio.pomostudy.rwnx`, `minSdk` 24, `targetSdk` y `compileSdk` 36, compatibilidad Java 11.
- Textos de la interfaz en español.
- **Repositorio público.** Nunca se versionan: `.env`, `local.properties`, almacenes de claves (`*.jks`, `*.keystore`), `google-services.json` ni claves de Gemini o Firebase. La clave de Gemini va en `.env` (ignorado) y la firma de lanzamiento por variables de entorno (`KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`).
- La contraseña `android` del `debug.keystore` es la estándar y pública de Android; no es un secreto, pero el archivo sigue sin versionarse.
- Lógica nueva del temporizador, metas y estadísticas: escribirla en clases Kotlin puras (sin `Context` ni Android) para probarla en JVM. El `MainViewModel` solo debe coordinar.
- Confirmar con el usuario antes de modificar `build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`, `.github/` o añadir dependencias. `metadata.json` y `.env.example` son de AI Studio: no se editan sin avisar.
- Repositorio con las reglas de `06-git-y-repositorios.md`.

## Cómo se trabaja aquí
1. Leer `docs/estado.md`; elegir un objetivo pequeño.
2. Escribir primero la prueba y luego la lógica.
3. Correr solo la prueba afectada.
4. Al cerrar, seguir el cierre de sesión de `04-constructor-de-proyecto.md`.

## Pruebas
Comandos y alcance en `docs/pruebas.md`.
