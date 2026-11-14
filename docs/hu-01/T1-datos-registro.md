# HU-01 T1 — Datos necesarios para registrar usuarios

**Responsable:** Adrián Barranquero Ponce · **Revisor:** Carlos Rodríguez Martín

Define qué datos se piden al registrarse, sus reglas y los mensajes de error. Lo usan el diseño del formulario (T2), el modelo de datos (T3), el endpoint (T4) y el formulario web (T5).

## Datos que introduce el usuario

| Campo | Obligatorio | Reglas | Por qué |
|---|:---:|---|---|
| `nombreUsuario` | Sí | De 3 a 30 caracteres. Solo letras sin tilde, números, punto, guion y guion bajo | Es el nombre público con el que firma en el foro (HU-01 CA1) |
| `email` | Sí | Formato de correo válido, 254 caracteres como máximo. Se guarda en minúsculas y sin espacios. No puede estar registrado ya | Sirve para iniciar sesión y es único por cuenta (CA2, CA3) |
| `password` | Sí | De 8 a 64 caracteres, con al menos una letra y un número | Seguridad mínima. El máximo de 64 se debe a que BCrypt solo usa los primeros 72 bytes |

## Datos que genera el sistema

| Dato | Valor |
|---|---|
| `id` | Lo asigna la base de datos |
| `rol` | `USUARIO_AFECTADO`, el rol por defecto (ver HU-26 T1) |
| `fechaRegistro` | Fecha y hora del alta |

## Datos que no se piden

Ni nombre real, ni teléfono, ni DNI, ni dirección. Ninguna funcionalidad del Sprint 1 los necesita, y la plataforma trata de proteger datos personales (HU-08): lo que no se pide no puede filtrarse.

## Mensajes de error (CA4)

| Situación | Campo | Mensaje |
|---|---|---|
| Nombre de usuario vacío | `nombreUsuario` | El nombre de usuario es obligatorio |
| Nombre con longitud o caracteres no válidos | `nombreUsuario` | Usa de 3 a 30 caracteres: letras, números, punto, guion o guion bajo |
| Correo vacío | `email` | El correo electrónico es obligatorio |
| Correo con formato no válido | `email` | Introduce un correo electrónico válido |
| Correo ya registrado | `email` | Ya existe una cuenta con ese correo electrónico |
| Contraseña vacía | `password` | La contraseña es obligatoria |
| Contraseña que no cumple las reglas | `password` | La contraseña debe tener entre 8 y 64 caracteres, con al menos una letra y un número |

Mensaje de éxito (CA5): **Cuenta creada correctamente**.

## Decisiones

1. **El correo es único y el nombre de usuario no.** CA3 solo exige que el correo sea único. Si el foro necesitara distinguir autores con el mismo nombre, se añadiría en otro sprint.
2. **El correo se compara sin distinguir mayúsculas.** `Lucia@Correo.es` y `lucia@correo.es` son la misma cuenta.
3. **La contraseña nunca se devuelve ni se registra en los logs.** Solo se guarda su hash (HU-01 T6).
