# HU-16 T1 — Cómo se mostrarán los casos publicados en el foro

**Responsable:** Tomás Soler Gordo · **Revisor:** Javier Hernando Iranzo

Define qué información se ve de cada caso en la lista y en el detalle, y cómo se presenta. Es la base del diseño de la interfaz (T2), del endpoint (T3) y de la visualización (T4).

## En la lista (HU-16 CA2)

Cada caso aparece como una tarjeta con:

| Dato | Formato | Ejemplo |
|---|---|---|
| Título | Enlace al detalle, en negrita | **Portátil que no enciende a los 8 meses** |
| Estado | Etiqueta de color | 🟢 Abierto · ⚪ Cerrado · 🔵 Resuelto |
| Categoría | Texto secundario | Electrónica |
| Autor | Nombre de usuario | marta |
| Fecha de publicación | Fecha corta en español | 5 nov 2026 |

La lista no muestra la descripción: así caben más casos en pantalla y el usuario entra al detalle si le interesa. Tampoco hay etiquetas libres en el Sprint 1: la «categoría/etiquetas» de CA2 se cubre con una sola categoría por caso.

## En el detalle (HU-16 CA5)

- Título, estado, categoría, autor y fecha, como en la lista
- **Descripción completa**, respetando los saltos de línea
- **Respuestas**, de la más antigua a la más reciente (orden de conversación), cada una con su autor y su fecha
- Si no hay respuestas: «Todavía no hay respuestas para este caso»
- Un enlace «← Volver a la lista» que conserva los filtros aplicados

## Estados y colores

| Estado | Significado | Color |
|---|---|---|
| `ABIERTO` | Busca ayuda | Verde `#1E6B3A` |
| `CERRADO` | Su autor lo cerró sin resolverlo | Gris `#5A6472` |
| `RESUELTO` | El problema se resolvió (HU-07) | Azul `#1F3A5F` |

La etiqueta siempre lleva el texto del estado, no solo el color.

## Situaciones especiales

| Situación | Qué se muestra |
|---|---|
| Cargando | «Cargando casos…» |
| No hay casos que coincidan (CA6) | «No hay casos que coincidan con tu búsqueda» y un botón «Quitar filtros» |
| Error al cargar (CA7) | «No se han podido cargar los casos» y un botón «Reintentar» |
| Sin permiso (403) | El mensaje de acceso denegado que devuelve la API |
| El caso no existe (404) | «El caso no existe» y el enlace para volver a la lista |
