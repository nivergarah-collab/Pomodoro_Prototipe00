# Conexiones · PomoStudy

## GitHub
- Repositorio: https://github.com/nivergarah-collab/Pomodoro_Prototipe00 (público).
- Rama principal: `main`. Rama de trabajo conjunta: `feature/adaptar-al-espacio`.
- Acceso del agente en la nube: lectura y clonado funcionan. **Escritura rechazada el 2026-10-02 (403)**: la app de Claude en GitHub no está instalada para este repositorio. Solución: https://github.com/apps/claude/installations/select_target → elegir la cuenta `nivergarah-collab` → "Repository access" → agregar `Pomodoro_Prototipe00`; o reconectar GitHub en los ajustes de conectores de claude.ai.
- Subida desde el computador: la hace el usuario con `git`.

## Gemini / Firebase AI
- `metadata.json` declara la capacidad `SERVER_SIDE_GEMINI_API` y el proyecto depende de `firebase-ai`, pero el código no lo usa todavía.
- La clave, si se usa, va en `.env` (ignorado por git), nunca en el repositorio.

## Otros servicios
Ninguno por ahora.
