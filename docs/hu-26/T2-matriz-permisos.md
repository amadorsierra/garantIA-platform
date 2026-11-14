# HU-26 T2 — Matriz de permisos y accesos

**Responsable:** Carlos Rodríguez Martín · **Revisor:** Adrián Barranquero Ponce

Parte de los permisos identificados en la T1 y fija qué rol puede usar cada endpoint de la API. Es la referencia del control de acceso (T4) y de la verificación (T6).

## Matriz rol × permiso

| Permiso | Usuario Afectado | Usuario Ayudante | Moderador | Administrador |
|---|:---:|:---:|:---:|:---:|
| `CASOS_LEER` | ✅ | ✅ | ✅ | ✅ |
| `CASOS_PUBLICAR` | ✅ | ❌ | ❌ | ❌ |
| `RESPUESTAS_PUBLICAR` | ✅ | ✅ | ❌ | ❌ |
| `CONTENIDO_MODERAR` | ❌ | ❌ | ✅ | ❌ |
| `ROLES_GESTIONAR` | ❌ | ❌ | ❌ | ✅ |
| `AUDITORIA_CONSULTAR` | ❌ | ❌ | ❌ | ✅ |

## Matriz endpoint × acceso (Sprint 1)

| Método y ruta | Historia | Acceso | Sin sesión | Con sesión, sin permiso |
|---|---|---|---|---|
| `POST /api/auth/registro` | HU-01 | Público | — | — |
| `GET /api/auth/yo` | HU-01 / HU-26 | Cualquier usuario con sesión | 401 | — |
| `GET /api/casos` | HU-16 | `CASOS_LEER` | 401 | 403 |
| `GET /api/casos/{id}` | HU-16 | `CASOS_LEER` | 401 | 403 |
| `PUT /api/admin/usuarios/{id}/rol` | HU-26 | `ROLES_GESTIONAR` | 401 | 403 |
| `GET /api/admin/auditoria-roles` | HU-26 | `AUDITORIA_CONSULTAR` | 401 | 403 |
| Cualquier otra ruta | — | Cualquier usuario con sesión | 401 | — |

## Respuestas de error

Para que el usuario vea un mensaje y no una página en blanco (acordado en el Scrum Meeting del 10/11), los dos casos devuelven JSON:

- **401 · sin sesión o credenciales incorrectas**
  `{"mensaje": "Debes iniciar sesión para acceder"}`
- **403 · sesión iniciada sin permiso**
  `{"mensaje": "Acceso denegado: no tienes permiso para esta acción"}`

## Reglas

1. **Se deniega por defecto.** Toda ruta nueva exige sesión, aunque nadie se acuerde de añadirla a la matriz.
2. **Se comprueba por permiso, no por rol.** Si mañana el Moderador necesita leer la auditoría, se cambia la matriz rol × permiso y ningún endpoint.
3. **Los permisos se leen en cada petición.** No se guardan en la sesión, así que un cambio de rol se aplica de inmediato (HU-26 CA4).
