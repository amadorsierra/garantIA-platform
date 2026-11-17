# HU-08 T5 — Evaluación de falsos positivos y datos no detectados

**Responsables:** José Ruíz Pareja con Fabián González Serón

Resultado de pasar el prototipo (T4) por los 66 textos del conjunto de evaluación. **Las reglas no dan ningún falso aviso y detectan el 89 % de los datos personales**, por encima de los umbrales fijados. Se recomienda implementar la detección con reglas (T6).

## Umbrales acordados

Se fijan antes de medir, como pide el riesgo «Detección insuficiente de datos sensibles» de la memoria:

| Medida | Qué significa | Umbral |
|---|---|---|
| Precisión | De los avisos que da, cuántos son de verdad datos personales. Si es baja, el aviso molesta y los usuarios lo ignoran | ≥ 95 % |
| Exhaustividad | De los datos personales que hay, cuántos detecta. Si es baja, se publican datos sin avisar | ≥ 85 % |

## Resultados

Ejecución de `node herramientas/prototipo-hu08/evaluar.mjs`. VP = detectado correctamente; FP = falso aviso; FN = dato no detectado.

| Tipo | VP | FP | FN | Precisión | Exhaustividad |
|---|---:|---:|---:|---:|---:|
| DNI | 4 | 0 | 0 | 100 % | 100 % |
| NIE | 2 | 0 | 0 | 100 % | 100 % |
| IBAN | 4 | 0 | 0 | 100 % | 100 % |
| TARJETA | 2 | 0 | 0 | 100 % | 100 % |
| DIRECCION | 8 | 0 | 1 | 100 % | 89 % |
| TELEFONO | 7 | 0 | 1 | 100 % | 88 % |
| NOMBRE_COMPLETO | 10 | 0 | 2 | 100 % | 83 % |
| EMAIL | 3 | 0 | 1 | 100 % | 75 % |
| **Total** | **40** | **0** | **5** | **100 %** | **89 %** |

## Falsos positivos

Ninguno en los 27 textos sin datos personales. Lo que lo evita:

- **Los dígitos de control:** el pedido `12345678A`, el número de serie `4111111111111112` y el código de factura `ES12 3456 7890 1234 5678 9012` tienen la forma de un DNI, una tarjeta y un IBAN, pero no cuadran.
- **La longitud exacta del teléfono:** `1234567890` (10 cifras) y «612 días» no saltan.
- **La exigencia de apellido:** «María me dijo» y «Juan Carlos de la tienda» no se toman por nombres completos.
- **La exigencia de número en las direcciones:** «La calle estaba cortada» no salta.

## Datos no detectados

| Texto | Qué falla | ¿Se puede arreglar con reglas? |
|---|---|---|
| Soy maria garcia lopez… | Nombre en minúsculas | No sin disparar los falsos avisos |
| Vivo en la calle Mayor de mi pueblo | Dirección sin número | No: se confundiría con «la calle estaba cortada» |
| seis uno dos tres cuatro… | Teléfono escrito con letras | Sí, pero es un caso muy raro |
| lucia arroba gmail punto com | Correo disfrazado | Sí, pero es un caso muy raro |
| Me atendió Íñigo Urrutia… | Nombre de pila que no está en la lista | Sí: ampliar la lista de nombres |

Los cinco son casos en los que el usuario escribe de forma poco habitual o el nombre no es frecuente. Los datos de más riesgo (DNI, NIE, IBAN, tarjeta) se detectan todos.

## Límites de esta evaluación

- **66 textos son pocos**, y los ha escrito el propio equipo, así que las cifras reales serán algo peores. Cuando el foro tenga casos de verdad, hay que añadir al CSV los que fallen.
- **Solo se evalúa texto.** Los adjuntos (CA2) quedan para cuando exista HU-03.

## Decisión

1. **Se implementa la T6 con reglas**, portando el prototipo a Java tal cual.
2. **La prueba de la T6 lee este mismo CSV** y falla si la precisión baja del 95 % o la exhaustividad del 85 %. Así ningún cambio en las reglas empeora la detección sin que se note.
3. **El diálogo (T7) avisa de que la detección no es perfecta**: «Revisa tú también que no haya otros datos personales».
4. **Para el Sprint 2:** ampliar la lista de nombres y, si con casos reales la exhaustividad de nombres y direcciones baja del 80 %, estudiar Presidio, como propone la T2.
