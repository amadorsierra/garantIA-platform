# HU-26 T1 — Permisos de cada rol

**Responsable:** José Ruíz Pareja · **Revisor:** Carlos Rodríguez Martín

Identifica qué puede hacer cada rol del sistema. Es la entrada de la matriz de permisos (T2) y del modelo de roles (T3).

## Roles

| Rol | Quién es | Rol por defecto |
|---|---|---|
| `USUARIO_AFECTADO` | Persona con un problema de garantía que publica su caso y recibe ayuda | Sí: toda cuenta nueva empieza con este rol |
| `USUARIO_AYUDANTE` | Persona que consulta casos y aporta su experiencia respondiendo | No |
| `MODERADOR` | Revisa contenido y aplica sanciones (el Moderador Humano de las historias) | No |
| `ADMINISTRADOR` | Gestiona las cuentas y sus roles | No |

El Moderador IA no es una persona con cuenta: es un proceso del sistema y no tiene rol de usuario.

## Permisos

Los permisos son acciones concretas. Un rol es un conjunto de permisos.

| Permiso | Qué permite | Historias que lo usan |
|---|---|---|
| `CASOS_LEER` | Consultar la lista de casos publicados y su detalle | HU-16 |
| `CASOS_PUBLICAR` | Publicar un caso propio | HU-02 (Sprint 2) |
| `RESPUESTAS_PUBLICAR` | Responder a un caso | HU-13 |
| `CONTENIDO_MODERAR` | Revisar contenido y decidir sanciones | HU-22, HU-23 |
| `ROLES_GESTIONAR` | Asignar, modificar o revocar roles | HU-26 CA1 |
| `AUDITORIA_CONSULTAR` | Consultar la bitácora de cambios de rol | HU-26 CA5 |

## Permisos de cada rol

- **Usuario Afectado:** `CASOS_LEER`, `CASOS_PUBLICAR`, `RESPUESTAS_PUBLICAR`. Publica sus casos y participa en los de otros (HU-01 CA6).
- **Usuario Ayudante:** `CASOS_LEER`, `RESPUESTAS_PUBLICAR`. Consulta y responde, pero no publica casos propios.
- **Moderador:** `CASOS_LEER`, `CONTENIDO_MODERAR`.
- **Administrador:** `CASOS_LEER`, `ROLES_GESTIONAR`, `AUDITORIA_CONSULTAR`.

## Decisiones

1. **Todos los roles pueden leer casos.** HU-16 CA1 habla de «Usuario Ayudante (o con permisos de lectura)». Lo que se exige es haber iniciado sesión: sin sesión no se ve nada.
2. **El administrador no modera.** Separar las dos funciones evita que una sola cuenta concentre todo el poder. Si una persona necesita ambas cosas, tendrá dos cuentas.
3. **Una cuenta tiene un solo rol.** Ninguna historia del backlog pide combinar roles, y así el modelo es más sencillo.
