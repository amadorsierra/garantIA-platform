# Prototipo de detección de datos personales (HU-08 T4)

**Responsable:** Fabián González Serón · **Revisor:** José Ruíz Pareja

Prototipo rápido, en JavaScript, de las reglas recomendadas en la T2. Sirve para medir cuánto detectan antes de implementarlas en el backend (T6). No forma parte de la aplicación.

## Qué contiene

| Archivo | Qué es |
|---|---|
| `detector.mjs` | Las reglas: un patrón por tipo de dato de la T1 y la validación de su control (letra del DNI y NIE, módulo 97 del IBAN, Luhn de las tarjetas). Si dos reglas encuentran el mismo trozo de texto, gana la primera |
| `evaluar.mjs` | Pasa el detector por el conjunto de evaluación y saca, por tipo, los aciertos, los falsos avisos y los datos no detectados |
| `backend/src/test/resources/privacidad/casos-evaluacion.csv` | El conjunto de evaluación: 66 textos de casos de garantías etiquetados a mano. Lo comparten el prototipo y las pruebas del backend |

## Cómo ejecutarlo

Desde la raíz del repositorio, con Node.js:

```bash
node herramientas/prototipo-hu08/evaluar.mjs
```

## Conjunto de evaluación

- 34 textos con algún dato personal, al menos dos por tipo, y algunos que combinan varios.
- 27 textos sin datos personales pero con trampas: números de pedido, de serie y de factura con la misma forma que un DNI, una tarjeta o un IBAN, precios, fechas, modelos de producto, tiendas y nombres de pila sueltos.
- 5 textos con datos personales que las reglas no pueden detectar, puestos a propósito (nombre en minúsculas, teléfono escrito con letras…), para que la medida no salga mejor de lo que es.

Para añadir un caso basta con una línea más en el CSV. Los resultados y su análisis están en la T5 (`docs/hu-08/T5-evaluacion-deteccion.md`).
