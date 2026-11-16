# HU-16 T2 — Diseño de la interfaz de listado de casos

**Responsable:** Tomás Soler Gordo · **Revisor:** Adrián Barranquero Ponce

Pantallas de la lista y del detalle, a partir de la T1. Las implementa Tomás en la T4 y la paginación Javier en la T5.

## Lista de casos (`/casos`)

```
┌────────────────────────────────────────────────────────────────┐
│  GarantIA                                        pablo   Salir │
├────────────────────────────────────────────────────────────────┤
│  Casos publicados                                              │
│                                                                │
│  Buscar                                                        │
│  ┌─────────────────────────────────────────────┐ ┌──────────┐  │
│  │ portátil                                    │ │  Buscar  │  │
│  └─────────────────────────────────────────────┘ └──────────┘  │
│                                                                │
│  Estado          Categoría            Ordenar                  │
│  [ Todos    ▾]   [ Todas         ▾]   [ Más recientes ▾]       │
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Portátil que no enciende a los 8 meses      🟢 Abierto   │  │
│  │ Electrónica · marta · 5 nov 2026                         │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ Lavadora con fuga de agua                   🔵 Resuelto  │  │
│  │ Electrodomésticos · marta · 6 nov 2026                   │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                │
│        ← Anterior      Página 1 de 2      Siguiente →          │
└────────────────────────────────────────────────────────────────┘
```

### Comportamiento

| Acción | Resultado |
|---|---|
| Escribir y pulsar «Buscar» o Intro | Busca en títulos y descripciones (CA4). No se busca mientras se escribe, para no lanzar una petición por cada letra |
| Cambiar Estado, Categoría u Ordenar | Se aplica al momento (CA3) |
| Cualquier filtro o búsqueda nuevos | Se vuelve a la página 1 |
| Pulsar una tarjeta | Abre el detalle (CA5) |
| Filtros en la URL | Se guardan en la dirección (`/casos?estado=ABIERTO&q=portátil`), así se puede volver atrás o compartir el enlace sin perderlos |

Opciones de los desplegables:
- **Estado:** Todos, Abierto, Cerrado, Resuelto
- **Categoría:** Todas, y después las que devuelve `GET /api/casos/categorias`
- **Ordenar:** Más recientes (por defecto), Más antiguos

### Paginación (T5)

- 10 casos por página, como acordaron Jose y Javier tras el Scrum Meeting del 10/11.
- «Anterior» está desactivado en la primera página y «Siguiente» en la última.
- Si todo cabe en una página, la paginación no se muestra.

## Detalle de un caso (`/casos/:id`)

```
┌────────────────────────────────────────────────────────────────┐
│  ← Volver a la lista                                           │
│                                                                │
│  Portátil que no enciende a los 8 meses          🟢 Abierto    │
│  Electrónica · marta · 5 nov 2026                              │
│                                                                │
│  Compré un portátil hace 8 meses y de repente no enciende.     │
│  La tienda dice que es por un golpe, pero no se ha caído...    │
│                                                                │
│  Respuestas (1)                                                │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ pablo · 6 nov 2026                                       │  │
│  │ Durante los primeros años de garantía se presume que...  │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────┘
```

## Estados de la pantalla

Los mensajes son los de la T1. El aviso de error tiene siempre su botón «Reintentar», que repite la misma petición sin perder los filtros (CA7).

## Accesibilidad

- La lista es una lista real (`<ul>`) y cada tarjeta un enlace, para que se pueda usar con el teclado.
- Los avisos de «sin resultados» usan `role="status"` y los errores `role="alert"`.
- La paginación va dentro de `<nav aria-label="Paginación">` y la página actual lleva `aria-current="page"`.
