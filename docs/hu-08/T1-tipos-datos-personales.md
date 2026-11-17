# HU-08 T1 — Tipos de datos personales que deben detectarse

**Responsable:** Adrián Barranquero Ponce · **Revisor:** Fabián González Serón

Lista de los datos que el sistema debe detectar antes de publicar un caso, con un ejemplo de cada uno y cómo distinguirlos de un número cualquiera. Es la entrada de la investigación (T2), del prototipo (T4) y de la implementación (T6).

## Datos que se detectan

| Tipo | Ejemplo | Cómo se reconoce | Riesgo si se publica |
|---|---|---|---|
| `DNI` | 12345678Z | 8 cifras y una letra. **La letra debe ser la correcta** (resto de dividir entre 23) | Alto: identifica a la persona y permite suplantaciones |
| `NIE` | X1234567L | X, Y o Z, 7 cifras y la letra de control | Alto |
| `TELEFONO` | 612 345 678 · +34 912 345 678 | 9 cifras que empiezan por 6, 7, 8 o 9, con o sin prefijo +34 y separadores | Medio: llamadas y spam |
| `EMAIL` | maria.garcia@correo.es | Formato de correo | Medio |
| `IBAN` | ES91 2100 0418 4502 0005 1332 | 2 letras de país y 22 cifras en España. **Los dígitos de control deben cuadrar** (módulo 97) | Alto: fraude bancario |
| `TARJETA` | 4111 1111 1111 1111 | 13 a 19 cifras. **Debe pasar el algoritmo de Luhn** | Muy alto |
| `DIRECCION` | Calle Larios 5 · Avda. de Andalucía, 12 | Tipo de vía (calle, avenida, plaza, paseo…) seguido de un nombre y un número | Alto: localiza a la persona |
| `NOMBRE_COMPLETO` | María García López | Un nombre propio frecuente seguido de uno o dos apellidos en mayúscula | Medio: identifica a la persona junto con el resto del caso |

Los cinco que cita la HU-08 CA3 (nombre completo, DNI, teléfono, correo y dirección) son obligatorios. IBAN y tarjeta se añaden porque los casos de garantías hablan a menudo de reembolsos y pagos.

## Datos que no se detectan en el Sprint 1

| Dato | Motivo |
|---|---|
| Fotos de personas, DNI escaneados | Necesitan análisis de imágenes; dependen de los adjuntos (HU-03) |
| Matrículas de coche | Pocos casos de garantías las mencionan |
| Fecha de nacimiento | Se confunde con las fechas de compra, que son normales en un caso |
| Nombres de tiendas y marcas | No son datos personales: es lo que el foro necesita para ayudar |

## Reglas para evitar falsos avisos

1. **Se comprueban los dígitos de control** del DNI, NIE, IBAN y tarjeta. Así un número de pedido o de serie con la misma forma no salta.
2. **Un teléfono necesita exactamente 9 cifras** que empiecen por 6, 7, 8 o 9. Un número de pedido de 10 cifras no es un teléfono.
3. **Un nombre suelto no es un nombre completo.** «María me dijo» no salta; «María García López» sí.
4. **Los nombres de marca no cuentan** aunque vayan en mayúscula.

## Qué se muestra al usuario

Por cada dato detectado: su tipo en lenguaje claro (por ejemplo, «Número de teléfono»), el fragmento exacto y dónde está en el texto (HU-08 CA4).
