# HU-26 T6 — Verificación de que los usuarios no acceden a funciones no autorizadas

**Responsable:** Javier Hernando Iranzo · **Revisor:** Adrián Barranquero Ponce

La verificación está automatizada en `backend/src/test/java/es/uma/garantia/seguridad/VerificacionAccesosTest.java` y se ejecuta en cada pull request.

## Qué se comprueba

| Comprobación | Cómo |
|---|---|
| Cada rol solo accede a lo que le permite la matriz | Los 4 roles contra los 5 endpoints protegidos (20 combinaciones). Si el rol tiene el permiso, no puede recibir 401 ni 403. Si no lo tiene, debe recibir 403 |
| Sin sesión no se accede a nada protegido | Los 5 endpoints sin credenciales devuelven 401 con mensaje |
| Una cuenta inexistente no entra | 401 |
| Quitar un rol retira el acceso al momento (HU-26 CA4) | Un administrador degradado recibe 403 en la siguiente petición |
| Nadie se da a sí mismo el rol de administrador | Un Usuario Afectado que lo intenta recibe 403 y su rol no cambia |
| La contraseña no sale nunca en las respuestas | Ni en `/api/auth/yo` ni en la lista de usuarios del administrador |

En el frontend, `src/api/cliente.test.js` comprueba que un 403 llega a la pantalla con su mensaje de acceso denegado y que sin sesión no se envía ninguna credencial.

## Endpoints cubiertos

`GET /api/casos` · `GET /api/casos/categorias` · `GET /api/admin/usuarios` · `PUT /api/admin/usuarios/{id}/rol` · `GET /api/admin/auditoria-roles`

## Al añadir un endpoint nuevo

1. Añadirlo a la matriz de `docs/hu-26/T2-matriz-permisos.md`.
2. Añadir su regla en `SecurityConfig`.
3. Añadirlo al enum `Endpoint` de `VerificacionAccesosTest` con el permiso que exige. La prueba lo cruzará sola con todos los roles.
