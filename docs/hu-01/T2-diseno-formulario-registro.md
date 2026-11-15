# HU-01 T2 — Diseño del formulario de registro

**Responsable:** Tomás Soler Gordo · **Revisor:** Javier Hernando Iranzo

Diseño de la pantalla de registro para que Adrián la implemente (T5). Los campos y mensajes salen de la T1.

## Boceto

```
┌──────────────────────────────────────────────┐
│  GarantIA                     Iniciar sesión │
├──────────────────────────────────────────────┤
│                                              │
│   Crear una cuenta                           │
│   Publica tus casos y recibe ayuda           │
│                                              │
│   Nombre de usuario                          │
│   ┌──────────────────────────────────────┐   │
│   │ lucia                                │   │
│   └──────────────────────────────────────┘   │
│   De 3 a 30 caracteres. Es tu nombre público │
│                                              │
│   Correo electrónico                         │
│   ┌──────────────────────────────────────┐   │
│   │ lucia@correo                         │   │  ← borde rojo
│   └──────────────────────────────────────┘   │
│   ⚠ Introduce un correo electrónico válido   │  ← texto rojo
│                                              │
│   Contraseña                                 │
│   ┌──────────────────────────────────────┐   │
│   │ ••••••••                             │   │
│   └──────────────────────────────────────┘   │
│   Mínimo 8 caracteres, con letras y números  │
│                                              │
│   ┌──────────────────────────────────────┐   │
│   │            Crear cuenta              │   │
│   └──────────────────────────────────────┘   │
│                                              │
│   ¿Ya tienes cuenta? Inicia sesión           │
└──────────────────────────────────────────────┘
```

## Comportamiento

| Momento | Qué pasa |
|---|---|
| Al escribir | Nada. No se marcan errores mientras el usuario escribe |
| Al salir de un campo | Se valida ese campo y, si está mal, se muestra su error debajo (CA2) |
| Al pulsar «Crear cuenta» | Se validan todos los campos. Si hay errores no se envía nada y el foco va al primer campo con error (CA4) |
| Mientras se envía | El botón se desactiva y pasa a decir «Creando cuenta…» para evitar dobles envíos |
| El servidor responde con errores | Cada error aparece debajo de su campo. El de correo duplicado sale debajo del correo (CA3) |
| Error de conexión | Aviso arriba del formulario: «No se ha podido conectar con el servidor. Inténtalo de nuevo». Los datos escritos se conservan |
| Registro correcto | Se sustituye el formulario por el mensaje «Cuenta creada correctamente» y un botón «Iniciar sesión» (CA5, CA6) |

## Accesibilidad

- Cada campo tiene su `<label>` visible, nunca solo un texto de ejemplo dentro del campo.
- El error de cada campo se enlaza con `aria-describedby` y el campo se marca con `aria-invalid="true"`.
- Los mensajes generales (éxito y error de conexión) usan `role="status"` o `role="alert"` para que los lectores de pantalla los anuncien.
- El color nunca es la única señal: los errores llevan el icono ⚠ y texto.

## Estilo

| Elemento | Valor |
|---|---|
| Ancho del formulario | 400 px como máximo, centrado |
| Color principal (botón) | `#1F3A5F` |
| Color de error | `#B42318` |
| Color de éxito | `#1E6B3A` |
| Separación entre campos | 16 px |
| Tipografía | La del sistema (`system-ui`) |
