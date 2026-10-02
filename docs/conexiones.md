# Conexiones · PomoStudy

## GitHub
- Repositorio: https://github.com/nivergarah-collab/Pomodoro_Prototipe00 (público).
- Rama principal: `main`. Rama de trabajo conjunta: `feature/adaptar-al-espacio`.
- Acceso del agente en la nube: lectura, clonado y escritura habilitados. El 2026-10-02 el primer push dio 403 y funcionó tras agregar el repositorio en la instalación de la app de Claude en GitHub. Si vuelve a fallar: https://github.com/apps/claude/installations/select_target → "Repository access", o reconectar GitHub en los conectores de claude.ai.
- Subida desde el computador: la hace el usuario con `git`.

## Gemini / Firebase AI
- `metadata.json` declara la capacidad `SERVER_SIDE_GEMINI_API` y el proyecto depende de `firebase-ai`, pero el código no lo usa todavía.
- La clave, si se usa, va en `.env` (ignorado por git), nunca en el repositorio.

## Otros servicios
Ninguno por ahora.
