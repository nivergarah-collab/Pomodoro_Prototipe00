# Decisiones · PomoStudy

Más reciente primero.

## 2026-10-02
- **Se adopta el proyecto en el espacio `Android`:** estructura estándar de las skills maestras, constructor propio (`skills/00-iniciar.md`) y estrategia de pruebas. Se trabaja en la rama `feature/adaptar-al-espacio`, aparte de `main`.
- **Tecnología:** se mantiene lo que generó AI Studio (Kotlin, Compose, Room, Gradle 9.3.1). No se reescribe nada en esta etapa.
- **Repositorio público:** se prohíbe versionar `.env`, almacenes de claves, `google-services.json` y claves de Gemini o Firebase. La firma de lanzamiento queda por variables de entorno o secretos de GitHub.
- **Pruebas primero en la lógica del temporizador:** es el núcleo de la app y hoy está atrapada en el `MainViewModel`. Se extraerá a clases Kotlin puras antes de añadir funcionalidades.
- **Integración continua:** el flujo `Pruebas` genera el `debug.keystore` con `keytool` y usa Gradle 9.3.1 sin `gradlew`. Lo coloca el usuario, porque `.github/` es de solo lectura para el agente.
- **Sin APK descargable por ahora:** no se ha pedido; se puede añadir un flujo `APK` igual al de `ff1`.
