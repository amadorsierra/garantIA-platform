# Estrategia y alcance de la apertura de GarantIA

**Responsables:** Fabián González Serón y Javier Hernando Iranzo · Equipo A · Práctica P9 · 26 de noviembre de 2026

**Base del análisis:** estado del repositorio `garantIA-platform` en la rama `main` al cierre del Sprint 1 y backlog del proyecto en ClickUp (4 historias de usuario completadas y 25 pendientes).

**Situación de partida.** El repositorio ya es público en GitHub, pero no incluye ninguna licencia. Sin licencia, el código no puede reutilizarse legalmente, por lo que el proyecto no puede considerarse Open Source. Además, el repositorio contiene material que no debería ser público (apartado 2). La apertura consiste, por tanto, en regularizar y ordenar un repositorio que ya es accesible, y no en publicarlo por primera vez.

---

## 1. Objetivo de la apertura y beneficios esperados

**Objetivo.** Garantizar que GarantIA pueda seguir utilizándose y desarrollándose una vez finalizada la asignatura, y que su componente más reutilizable, el detector de datos personales en textos en español, pueda aprovecharse en otros proyectos.

Debe tenerse en cuenta que el valor diferencial previsto del producto reside en las funcionalidades de inteligencia artificial del backlog, ninguna de ellas implementada todavía:

- Generación de borradores de reclamación (HU-10 y HU-11).
- Búsqueda de casos similares (HU-05 y HU-18).
- Clasificación de casos (HU-17).
- Resúmenes de casos resueltos (HU-19).
- Detección de contenido inapropiado (HU-20).

En el estado actual, lo que se publicaría es un foro con gestión de roles y un detector basado en reglas. La comunicación del proyecto debe describir lo que está implementado y presentar el resto como planificado.

| Beneficio esperado | Justificación |
|---|---|
| Mejora del detector con textos reales | La evaluación de HU-08 (T5) reconoce que 66 textos redactados por el propio equipo son una muestra limitada. Para añadir un caso basta con una línea más en `casos-evaluacion.csv`, por lo que también pueden contribuir personas sin perfil técnico. |
| Continuidad tras la asignatura | Con licencia y documentación, el proyecto puede ser retomado por otros estudiantes (siguientes ediciones o trabajos de fin de grado) sin depender del equipo actual. |
| Revisión externa de la seguridad | El registro, la autenticación y el control de acceso por roles (HU-01 y HU-26) son las partes más sensibles del sistema. La revisión por terceros aumenta la probabilidad de detectar errores. |
| Valor como portafolio | Un repositorio ordenado, con integración continua y decisiones documentadas, refleja la forma de trabajo del equipo. |

No se persigue una comunidad amplia ni usuarios finales a corto plazo. Un foro de garantías en español tiene un público reducido y el producto aún no está completo (apartado 4).

---

## 2. Componentes que se publican y componentes excluidos

| Componente | Decisión | Justificación |
|---|---|---|
| `backend/` (Spring Boot 4, Java 21, Flyway) | Se publica | Constituye el producto. |
| `frontend/` (React 19, Vite) | Se publica | Constituye el producto. |
| Módulo `privacidad/`, prototipo `herramientas/prototipo-hu08/` y conjunto de evaluación `casos-evaluacion.csv` | Se publican y se destacan en el README | Es el componente con mayor potencial de reutilización. Valida DNI, NIE, IBAN y tarjetas mediante sus dígitos de control y dispone de un conjunto de evaluación etiquetado. |
| `.github/workflows/ci.yml` | Se publica | Permite a un colaborador externo saber que su contribución se comprueba de forma automática. |
| `docs/hu-*` (diseño de cada historia de usuario) | Se publica | Documenta el motivo de las decisiones y facilita la incorporación de nuevos colaboradores. |
| Datos de demostración (`V1000__datos_demo.sql`) | Se publican, indicando que son solo para desarrollo | Son necesarios para probar la aplicación en local. Solo se cargan con el perfil `dev`, y la contraseña común de las cuentas de prueba debe figurar como dato de demostración. |
| `docs/reuniones/` (ocho grabaciones y sus transcripciones, 74 MB) | Se excluye y se elimina también del historial | Contiene la voz y las conversaciones de los integrantes del equipo, que son datos personales. No aporta valor a un usuario externo y aumenta el tamaño de cada clonación. |
| `docs/metricas/` | Se excluye | Recoge la evaluación interna del equipo, con horas registradas por persona. Se incorporará a la entrega privada de la asignatura. |
| Espacio de ClickUp (listas «Sprint 1» y «Defectos») | No se publica | Es una herramienta de planificación interna a la que un externo no tiene acceso. Los defectos que sigan abiertos se trasladarán a GitHub Issues. |
| Funcionalidades de IA y adjuntos (HU-03) | No se anuncian | Aún no existen. Se incluyen en el roadmap como funcionalidades planificadas. |
| Futuras integraciones con proveedores de IA | Se publicará el código, nunca las claves de acceso | El proveedor y la clave se configurarán mediante variables de entorno, igual que la conexión a la base de datos. Deberá existir una implementación que funcione sin clave, para que cualquier persona pueda probar el sistema sin coste. |

**Nota sobre la exclusión.** Las métricas y las reuniones se entregaron en este repositorio como parte de prácticas anteriores. Antes de eliminarlas del historial, deben guardarse en la entrega privada de la asignatura y acordarse con el Scrum Master. Hasta entonces, es una decisión del plan y no una acción ya realizada.

**Decisión sobre el detector.** De momento se mantiene dentro del repositorio principal. Separarlo en una librería independiente solo se justifica si surge demanda externa. Hacerlo antes duplicaría el esfuerzo de mantenimiento en un equipo que dejará de trabajar en el proyecto al terminar el curso.

---

## 3. Usuarios y colaboradores potenciales

| Perfil | Uso previsto | Probabilidad | Requisitos |
|---|---|---|---|
| Estudiantes de próximas ediciones o de trabajos de fin de grado | Retomar el desarrollo y completar el backlog | Alta | Puesta en marcha sencilla en local y un backlog público y ordenado |
| Desarrolladores que procesan textos de usuarios en español (foros, atención al cliente, formularios) | Reutilizar el detector de datos personales | Media | Visibilidad del detector en el README, con un resumen en inglés, y una interfaz documentada |
| Colaboradores sin perfil técnico | Aportar textos que el detector no reconoce | Media | Una plantilla de issue específica para este tipo de aportación |
| Asociaciones de consumidores u oficinas municipales de consumo | Desplegar el foro para su público | Baja | Producto completo, con moderación y preparado para producción |
| Consumidores con un problema de garantía | Utilizar el foro | Solo a través de un despliegue, no del repositorio | No aplica |

**Idioma.** El código, los identificadores y la documentación se mantienen en español, porque el dominio (garantías en España, DNI, NIE) y el público objetivo lo son. Solo se añadirá en el README un párrafo en inglés que describa el detector, para facilitar que se encuentre.

---

## 4. Madurez del proyecto

El proyecto se encuentra en una **fase inicial de desarrollo** (versión `0.0.1-SNAPSHOT`, sin releases). Sirve como base técnica para otros desarrolladores, pero no está preparado para usuarios finales ni para un despliegue en producción.

**Implementado en el Sprint 1 (26 puntos entregados):**

- HU-01 Creación de cuenta, con almacenamiento seguro de contraseñas.
- HU-26 Gestión de permisos según el rol, con registro de los cambios de rol.
- HU-16 Consulta de casos publicados: listado, detalle, filtros y paginación.
- HU-08 Advertencia de datos sensibles, solo sobre texto.
- Pruebas unitarias y de aceptación, integración continua en backend y frontend, y migraciones con Flyway.

**Pendiente que bloquea la apertura:**

| HU | Situación actual | Consecuencia para la apertura |
|---|---|---|
| HU-02 Publicación de casos | No existe ningún endpoint para crear casos. | Sin ella no se puede evaluar el foro: solo se consultan los casos de demostración. Es la condición principal para anunciar el proyecto. |
| HU-13 Responder consultas y HU-07 Confirmación de caso resuelto | Las respuestas y el estado `RESUELTO` existen en la base de datos, pero solo se cargan desde los datos de demostración. | El foro no permite la interacción entre usuarios ni completar el ciclo de vida de un caso. |
| HU-14, HU-27 y HU-22 a HU-24 (moderación) | El rol `MODERADOR` y el permiso `CONTENIDO_MODERAR` están definidos, pero no tienen funcionalidad. | Un despliegue público quedaría expuesto a abuso y spam. |
| HU-09 Modificación de datos personales | Sin implementar (prioridad urgente). | Los usuarios no pueden rectificar sus datos, lo que impide un uso real. |
| HU-03 Adjuntar documentos | Sin implementar; la CA2 de HU-08 queda pendiente de ella. | El detector debe presentarse como limitado a texto. |

Las funcionalidades de IA (apartado 1) y el resto del backlog (HU-04, HU-06, HU-12, HU-15, HU-21, HU-25, HU-28 y HU-29) no bloquean la apertura: figurarán en el roadmap y algunas pueden servir como primeras tareas para colaboradores externos. Además, falta configuración para producción: solo existen la configuración base y el perfil de desarrollo, y `compose.yaml` únicamente levanta la base de datos.

En lugar de un único criterio de «proyecto preparado», se establecen tres umbrales:

| Umbral | Condición | Qué permite |
|---|---|---|
| Regularización | Limpieza del repositorio, licencia y README | Puede abordarse de inmediato, ya que el repositorio es público |
| Primera versión (`v0.1.0`) y anuncio | HU-02, HU-13 y HU-07 integradas en `main` | Que un desarrollador evalúe un foro funcional de principio a fin |
| Despliegue recomendado | Además, HU-09, HU-14, HU-27, HU-28 y moderación | Que una organización lo ofrezca a usuarios reales. Hasta entonces, el README indicará que no es apto para producción. |

---

## 5. Prioridades para el éxito de la apertura

**1. Limpieza del repositorio y licencia, antes que cualquier otra acción.**
Deben eliminarse `docs/reuniones/` y `docs/metricas/` del historial completo, no solo de la rama principal, y debe incorporarse una licencia. Mientras el repositorio siga siendo público en su estado actual, las grabaciones pueden descargarse y el código no puede reutilizarse legalmente.

*Criterio de cumplimiento:* el historial no contiene ninguna referencia a `docs/reuniones/` y existe un fichero `LICENSE` aprobado por los ocho autores.

**2. Puesta en marcha autónoma por parte de una persona externa en menos de 15 minutos.**
El README debe incluir:
- Los pasos de arranque: base de datos con Docker Compose, backend con el perfil `dev` y frontend con `npm run dev`.
- Las cuentas de demostración.
- Una tabla con lo que está implementado y lo que no (apartado 4).

*Criterio de cumplimiento:* una persona ajena al equipo arranca la aplicación siguiendo únicamente el README.

**3. Traslado del trabajo pendiente a GitHub.**
- Migrar a GitHub Issues las 25 historias de usuario pendientes del backlog de ClickUp y los defectos abiertos, conservando la prioridad que ya tiene asignada cada una.
- Organizarlas por áreas mediante etiquetas (`foro`, `seguridad`, `ia`, `operacion`).
- En los commits nuevos, sustituir los identificadores de ClickUp por referencias a issues de GitHub.

*Criterio de cumplimiento:* el roadmap público refleja los tres umbrales e incluye al menos tres issues marcadas como `good first issue`. Su selección corresponde al Equipo C. Se proponen como candidatas HU-04 (valoración de respuestas) y HU-15 (seguimiento de temas), por su baja prioridad y su escaso acoplamiento con el resto del sistema.

---

## 6. Riesgos

| Riesgo | Probabilidad | Impacto | Mitigación |
|---|---|---|---|
| Las grabaciones y transcripciones permanecen en el historial público | Se está produciendo | Alto | Reescritura del historial (Equipo B) y aviso a quien disponga de copias del repositorio |
| Falta de acuerdo de todos los autores sobre la licencia (ocho personas, catorce identidades de commit) | Media | Alto | Obtener la conformidad expresa de los ocho autores antes de añadir la licencia. Al ser los únicos titulares del código, su acuerdo es suficiente. |
| Despliegue en producción sin mecanismos de moderación | Baja | Alto | Advertencia explícita en el README y ausencia de instrucciones de despliegue hasta alcanzar el tercer umbral |
| Ausencia de colaboradores externos | Alta | Medio | Se asume este riesgo. El objetivo mínimo es que el proyecto pueda retomarse. El conjunto de evaluación del detector ofrece la vía de contribución más accesible. |
| Duplicidad de información entre ClickUp y GitHub | Alta | Medio | GitHub será la referencia para todo lo público. ClickUp quedará limitado a la planificación interna de la asignatura. |
| Expectativas de una plataforma con IA que el código no satisface | Alta | Medio | Ni el README ni la descripción del repositorio mencionarán funcionalidades de IA como disponibles. Solo figurarán en el roadmap. |
| Interpretación de los borradores de reclamación (HU-10) como asesoramiento jurídico | Media, una vez implementados | Alto | Advertencia en la interfaz y en el README. Antes de implementarlos, deberá decidirse si un borrador generado puede publicarse en el foro. |
| Envío de datos personales de usuarios a un proveedor de IA externo | Media, una vez implementado | Alto | Aplicar el detector de HU-08 antes de enviar cualquier texto y permitir la configuración del proveedor, incluida una opción local |

---

## 7. Acciones pendientes

| Acción | Responsable | Momento |
|---|---|---|
| Reescribir el historial sin `docs/reuniones/` ni `docs/metricas/` y trasladar las métricas a la entrega privada | Equipo B | Antes de publicar |
| Elegir una licencia que permita reutilizar el detector y definir la licencia aplicable al conjunto de evaluación, que tiene carácter de datos y no de código | Equipo C | Antes de publicar |
| Recabar la conformidad de los ocho autores con la licencia elegida | Equipo D | Antes de publicar |
| Redactar el README con instrucciones de arranque, estado del proyecto, advertencia de uso en producción y descripción del detector en inglés | Equipo C, con el Equipo A | Antes de publicar |
| Migrar a GitHub Issues las historias pendientes y los defectos abiertos, con etiquetas | Equipo A | Primera publicación |
| Priorizar en el Sprint 2 el ciclo básico del foro (HU-02, HU-13 y HU-07) antes de las funcionalidades de IA (decisión del Product Owner) | Equipo A | Planificación del Sprint 2 |
| Publicar la versión `v0.1.0` y anunciar el proyecto una vez integradas HU-02, HU-13 y HU-07 | Todo el equipo | Primera publicación |
