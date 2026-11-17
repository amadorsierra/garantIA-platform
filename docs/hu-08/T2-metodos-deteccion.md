# HU-08 T2 — Métodos de detección de información sensible

**Responsable:** Javier Hernando Iranzo · **Revisor:** Carlos Rodríguez Martín

Compara las formas de detectar los datos de la T1 y recomienda una para el Sprint 1. **Recomendación: reglas con validación de los dígitos de control**, dentro de una interfaz que permita añadir un modelo de lenguaje más adelante si los nombres y las direcciones se escapan demasiado.

## Métodos estudiados

### 1. Reglas: expresiones regulares y validaciones

Un patrón por tipo de dato (por ejemplo, 8 cifras y una letra) y, cuando existe, la comprobación matemática de su control: letra del DNI y NIE, módulo 97 del IBAN, algoritmo de Luhn de las tarjetas.

- **Bien:** muy preciso con los datos que tienen formato fijo y control (DNI, NIE, IBAN, tarjeta, correo, teléfono). Es rápido y no depende de servicios externos. Se prueba en el CI como cualquier otro código.
- **Mal:** los nombres y las direcciones no tienen un formato fijo. Con reglas se detectan solo los casos típicos («María García López», «Calle Larios 5»).

### 2. Reconocimiento de entidades con modelos de lenguaje (NER)

Librerías como spaCy (con modelos en español) o Microsoft Presidio, que combina modelos NER con reglas, reconocen nombres de persona, lugares y organizaciones por el contexto de la frase.

- **Bien:** detectan nombres y direcciones que no siguen un patrón.
- **Mal:** son de Python, así que habría que montar un servicio aparte junto al backend en Java. Dan falsos positivos con marcas y tiendas, que en este foro aparecen en todos los casos, y su resultado es menos predecible en las pruebas.

### 3. API de un modelo de IA generativa

Enviar el texto a un modelo (OpenAI, Claude u otros) y pedirle que marque los datos personales.

- **Bien:** es el que mejor entiende el contexto.
- **Mal:** **manda a un tercero justo los datos personales que se quieren proteger**. Además cuesta dinero por cada análisis, necesita una clave, añade latencia y sus respuestas pueden variar entre una ejecución y otra, lo que complica las pruebas automáticas.

### 4. Análisis de documentos e imágenes

Para las facturas y fotos adjuntas (HU-08 CA2) habría que extraer el texto: directamente de los PDF con texto, o con reconocimiento óptico (OCR, por ejemplo Tesseract) en las fotos. Después se aplica el método elegido sobre ese texto. Depende de los adjuntos (HU-03), que son del Sprint 2.

## Comparación

| Criterio | Reglas | NER (spaCy, Presidio) | API de IA |
|---|---|---|---|
| DNI, NIE, IBAN, tarjeta, correo, teléfono | Muy bien (con validación) | Bien (Presidio usa reglas para estos) | Bien |
| Nombres y direcciones | Solo los casos típicos | Bien | Muy bien |
| Los datos salen del servidor | No | No | **Sí** |
| Coste | Ninguno | Ninguno; necesita más memoria | Por cada análisis |
| Encaje con Spring Boot y el CI | Directo | Servicio aparte en Python | Llamada externa, difícil de probar |
| Resultado repetible en las pruebas | Sí | Casi siempre | No siempre |

## Recomendación

1. **Sprint 1:** reglas con validación. Cubren con mucha precisión los datos de más riesgo (DNI, IBAN, tarjeta) sin enviar nada fuera y sin coste.
2. **Diseño abierto:** la detección se programa detrás de una interfaz, para poder añadir otro detector sin tocar el resto.
3. **Decisión con datos:** el prototipo (T4) y la evaluación (T5) miden cuántos nombres y direcciones se escapan. Si se escapan demasiados, se propone para el Sprint 2 un servicio con Presidio, que se ejecuta en nuestro servidor y no envía los datos a nadie.
4. **La API de IA generativa queda descartada** mientras suponga enviar datos personales a terceros.
