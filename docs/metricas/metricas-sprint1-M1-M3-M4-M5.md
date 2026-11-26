# Práctica 5 — Métricas del Sprint 1

**M1, M3, M4 y M5** · Responsable: Amador Sierra González · Sprint 1 (9/11 - 22/11/2026) · GarantIA

## Fuentes de datos

- **ClickUp** (vistas M1, M3, M4 y M5 de la lista «Sprint 1»): estado, puntos de sprint, fecha límite, fecha de cierre, duración estimada y tiempo registrado de cada HU y subtarea.
- **GitHub** (historial de *merges* de la rama principal): fecha real en que se terminó cada tarea. El tablero se actualizó con retraso, así que la fecha de cierre de ClickUp es posterior a la real. Las fechas reales están también en un comentario de cada HU en ClickUp.
- Los resultados se documentan aquí porque el plan gratuito de ClickUp ha agotado los usos de campos personalizados y no permite añadir el resultado como texto en las vistas.

---

## M1 — % de HUs comprometidas entregadas (KPI)

**Cálculo:** HUs completadas al cierre / HUs comprometidas al inicio × 100.
**Umbral:** ≥ 85 % correcto · 70–85 % revisar estimaciones · < 70 % reducir alcance.

| HU | Nombre | SP | Comprometida al inicio | Estado al cierre | Entregada |
|---|---|---:|---|---|---|
| HU-01 | Creación de cuenta | 8 | Sí | hecho | Sí |
| HU-26 | Gestión de permisos según el rol | 5 | Sí | hecho | Sí |
| HU-16 | Consulta de casos publicados | 5 | Sí | hecho | Sí |
| HU-08 | Advertencia de datos sensibles | 8 | No (añadida el 17/11) | hecho | Sí |

> **Resultado: 3 de 3 HUs comprometidas entregadas = 100 %** (18 de 18 SP) → **correcto**.
> Además se entregó HU-08 (8 SP), que entró a mitad de sprint: en total, **4 HUs y 26 SP**.

- HU-08 no estaba comprometida al inicio, así que no entra en el denominador. Se informa aparte para no inflar el KPI.

---

## M3 — Desviación del esfuerzo estimado (KPI)

**Cálculo:** (horas registradas − horas estimadas) / horas estimadas × 100.
**Umbral:** −20 % a +20 % correcto · +20 % a +30 % comentar en la retrospectiva · > +30 % reestimar · < −20 % se puede aumentar la carga del siguiente sprint.

### Por historia de usuario

| HU | Subtareas | Estimado | Registrado | Desviación |
|---|---:|---:|---:|---:|
| HU-01 | 7 | 17h 30m | 12h 30m | −28,6 % |
| HU-26 | 7 | 13h 30m | 9h 45m | −27,8 % |
| HU-16 | 6 | 11h 30m | 8h | −30,4 % |
| HU-08 | 8 | 16h | 11h 15m | −29,7 % |
| **Sprint completo** | **28** | **58h 30m** | **41h 30m** | **−29,1 %** |

### Por tipo de tarea

| Tipo | Qué incluye | Estimado | Registrado | Desviación |
|---|---|---:|---:|---:|
| Desarrollo | Backend y frontend | 33h | 23h 15m | −29,5 % |
| Pruebas | T0 de cada HU y HU-26 T6 | 10h | 7h 15m | −27,5 % |
| Análisis y diseño | Documentos de análisis y diseño (T1–T3) | 11h | 7h 45m | −29,5 % |
| Investigación (IA) | HU-08 T2, T4 y T5 | 4h 30m | 3h 15m | −27,8 % |

> **Resultado: −29,1 % en el sprint** (41h 30m registradas frente a 58h 30m estimadas).
> Sin HU-08, las tres HUs comprometidas dan −28,8 % (30h 15m frente a 42h 30m).
> Está por debajo de −20 % → **se puede aumentar la carga del siguiente sprint**.

- La desviación es casi igual en todas las HUs (−28 % a −30 %) y en todos los tipos de tarea. No hay un tipo de tarea mal estimado: el equipo sobrestima de forma **sistemática**.
- **Acción:** en el Sprint 2, reducir las estimaciones de tareas parecidas un 25 % aproximadamente, o mantenerlas y comprometer más puntos de sprint.

---

## M4 — Cumplimiento de fechas (KPI)

**Cálculo:** HUs cerradas en su fecha límite o antes / HUs con fecha límite en el sprint × 100, más el retraso medio de las HUs retrasadas.
**Umbral:** ≥ 90 % correcto · 75–90 % analizar en la retrospectiva · < 75 % replanificar.

| HU | Fecha límite | Terminada (último merge) | Cierre en ClickUp | Margen | A tiempo |
|---|---|---|---|---|---|
| HU-08 | 22/11 12:00 | 20/11 19:37 (PR #31) | 21/11 14:16 | 1,7 días antes | Sí |
| HU-01 | 22/11 13:00 | 16/11 16:43 (PR #20) | 21/11 14:51 | 5,8 días antes | Sí |
| HU-26 | 22/11 13:00 | 16/11 18:43 (PR #22) | 21/11 14:50 | 5,8 días antes | Sí |
| HU-16 | 22/11 13:00 | 16/11 18:41 (PR #23) | 21/11 14:51 | 5,8 días antes | Sí |

> **Resultado: 4 de 4 HUs a tiempo = 100 %**, retraso medio 0 días → **correcto**.
> Las HUs críticas (HU-08 y HU-26) también se entregaron antes de su fecha.

### Origen de los datos

- **Fecha límite:** campo «Fecha límite» de ClickUp. La de HU-08 se fijó el 21/11, al incorporarla al sprint, con el mismo criterio que las demás HUs.
- **Terminada:** fecha del merge del último PR de cada HU en la rama principal (GitHub).
- **Cierre en ClickUp:** fecha automática al pasar la HU a «hecho». Se incluye solo como referencia.

### Fechas de las subtareas

- Las tres T0 (pruebas de aceptación de HU-01, HU-16 y HU-26) tenían fecha límite el **10/11** y se integraron el **12/11** (PR #4, #6 y #7): **2 días de retraso**. No afectó a la entrega de las HUs, pero retrasó el inicio de las tareas que dependían de ellas (ver M5).
- El resto de subtareas no tenía fecha límite propia.
- El tablero se actualizó tarde: en ClickUp todo aparece cerrado el 21/11, aunque el trabajo terminó el 16/11 (HU-01, HU-16 y HU-26) y el 20/11 (HU-08). Para la métrica se usa la fecha real del merge.

---

## M5 — Tiempo de ciclo y tareas bloqueadas

**Cálculo:** días medios desde que una tarea empieza hasta que se cierra, y número de tareas bloqueadas o en revisión más de 2 días.
**Umbral:** con 2 o más tareas bloqueadas a la vez se revisan las dependencias y se redistribuye el trabajo.

### Tiempo de ciclo por HU (primer commit de su primera tarea → último merge)

| HU | Inicio | Fin | Tiempo de ciclo |
|---|---|---|---:|
| HU-01 | 12/11 12:02 | 16/11 16:43 | 4,2 días |
| HU-26 | 12/11 12:00 | 16/11 18:43 | 4,3 días |
| HU-16 | 12/11 12:05 | 16/11 18:41 | 4,3 días |
| HU-08 | 17/11 11:57 | 20/11 19:37 | 3,3 días |
| **Media** | | | **4,0 días** |

### Tareas y bloqueos

| Indicador | Valor |
|---|---|
| Tareas en estado «bloqueado» | 0 |
| Tareas en revisión más de 2 días | 0 |

> **Resultado: 4,0 días de ciclo medio por HU, 0 tareas bloqueadas y 0 tareas en revisión más de 2 días** → no se ha alcanzado el umbral de acción.

---

## Resumen y acciones para el Sprint 2

| Métrica | Resultado | Umbral | Acción |
|---|---|---|---|
| M1 HUs entregadas (KPI) | 100 % (3/3) + HU-08 | Correcto | Ninguna |
| M3 Desviación de esfuerzo (KPI) | −29,1 % | < −20 % | Aumentar la carga o reducir estimaciones ~25 % |
| M4 Cumplimiento de fechas (KPI) | 100 %, retraso 0 días | Correcto | Entregar las T0 en su fecha |
| M5 Ciclo y bloqueos | 4,0 días/HU, 0 bloqueadas | Sin acción | Ninguna |
