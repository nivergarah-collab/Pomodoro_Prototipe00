# Pruebas · PomoStudy

Estrategia según `05-pruebas-automatizadas.md`. Los comandos aún no fueron verificados.

## Niveles
| Nivel | Dónde | Quién lo corre |
|---|---|---|
| Estructura | `scripts/verificar-estructura.ps1` | Agente e integración continua |
| Unitarias (Kotlin puro) | `app/src/test/` | Agente e integración continua |
| Integración (Room en memoria, ViewModel con dobles) | `app/src/test/` con Robolectric | Integración continua y usuario |
| Instrumentadas | `app/src/androidTest/` | Usuario (requiere dispositivo) |

Prioridad de cobertura: temporizador (fases, bloques, pausa, saltar, cierre), meta diaria, estadísticas semanales y `StudyRepository`.

## Comandos (Windows, desde la raíz del proyecto)
- Estructura: `.\scripts\verificar-estructura.ps1`
- Una clase: `.\gradlew.bat testDebugUnitTest --tests "com.example.NombreDeLaClase" --console=plain -q`
- Suite unitaria completa: `.\gradlew.bat testDebugUnitTest --console=plain -q`
- Instrumentadas: `.\gradlew.bat connectedDebugAndroidTest`

El repositorio no trae `gradlew`: Android Studio lo genera al sincronizar con `gradle/wrapper/gradle-wrapper.properties`. Si no aparece, correr una vez `gradle wrapper` con Gradle 9.3.1.

## Debug.keystore
La compilación de depuración exige `debug.keystore` en la raíz (no se versiona). Para crearlo:
`keytool -genkeypair -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"`

## Lectura eficiente de resultados
Con `-q` Gradle solo imprime los fallos. Si hay fallos, leer únicamente las etiquetas `<failure` de los XML en `app/build/test-results/testDebugUnitTest/`, no el reporte HTML ni el registro completo.

## Ciclo rápido en la nube (agentes)
La nube no accede a Maven, Google ni Gradle, así que el agente no puede compilar esta app Android. Prueba solo lógica Kotlin pura si hay un compilador disponible, y deja la compilación y la suite completa a GitHub Actions.

## Integración continua
`.github/workflows/pruebas.yml` (flujo `Pruebas`): verificación de estructura y `testDebugUnitTest` en cada push a `main`, `feature/**` y `fix/**`, y en pull requests. Lo coloca el usuario.

## Pruebas existentes
`ExampleUnitTest` y `ExampleRobolectricTest` son de AI Studio y se reemplazarán por pruebas reales (ver `docs/estado.md`).

## Último resultado
Sin ejecutar.
