# Licencia, documentación y colaboración de GarantIA

**Responsables:** Jose Ruiz Pareja y Carlos Rodríguez Martín · Equipo C · Práctica P9 · 27 de noviembre de 2026

**Base del análisis:** estado de `main` en `amadorsierra/garantIA-platform` tras el Sprint 1 y la estrategia del Equipo A (`docs/open-source/open-source-strategy.md`).

**Situación de partida:**

| Elemento | Estado actual |
|---|---|
| Licencia | **No hay.** El repositorio es público, pero el código no se puede reutilizar legalmente. |
| README | Solo contiene el título `# garantIA-platform`. |
| `CONTRIBUTING`, `CODE_OF_CONDUCT`, `SECURITY`, plantillas de issue y PR | No existen. `.github/` solo tiene `workflows/ci.yml`. |
| Protección de `main` | **Desactivada.** Se puede fusionar sin revisión ni CI en verde. |
| CI | Tres trabajos: Backend, Frontend y Aceptación. Las 30 pruebas de aceptación de HU-01, HU-08, HU-16 y HU-26 **ya pasan**, pero ese trabajo sigue marcado como `continue-on-error`. |
| Issues de GitHub | Ninguna abierta. Solo existen las etiquetas por defecto (`good first issue`, `help wanted`…). El backlog está en ClickUp. |
| Convenciones ya en uso | Ramas `tipo/HU-xx-Tx-descripcion`, Conventional Commits en español, un PR por tarea con revisor asignado y documentación de diseño por HU en `docs/hu-*`. |

Lo publicable es un foro con registro, roles, consulta de casos y un detector de datos personales por reglas (HU-08). Las funcionalidades de IA del backlog **no existen** y no se documentan como disponibles (decisión del Equipo A).

---

## 1. Licencia

**Decisión:**

| Parte | Licencia |
|---|---|
| Código: `backend/`, `frontend/` y `herramientas/prototipo-hu08/` | **Apache License 2.0** |
| Documentación (`docs/hu-*`) y conjunto de evaluación `backend/src/test/resources/privacidad/casos-evaluacion.csv` | **CC BY 4.0** |

**Justificación:**

- **Reutilizar el detector es el objetivo principal de la apertura** (Equipo A). Una licencia permisiva deja que otros proyectos, incluidos los cerrados, integren `privacidad/` sin obligarles a liberar su código. Con GPL o AGPL, muchos de esos usuarios potenciales no podrían usarlo.
- **Concesión expresa de patentes.** MIT no la incluye y Apache 2.0 sí. Un detector con validación de dígitos de control (DNI, NIE, IBAN, tarjetas) es justo el tipo de componente donde importa.
- **Encaja con las dependencias.** Spring Boot, Flyway y Testcontainers son Apache 2.0. React, Vite, Vitest y React Router son MIT. El driver de PostgreSQL es BSD-2. Todas son compatibles con Apache 2.0, como sale en la revisión de dependencias del Equipo B.
- **La documentación y los datos de evaluación no son código.** CC BY 4.0 es la licencia estándar para ellos: permite reutilizarlos citando la fuente, y así se reconoce a quien aporte casos al CSV.

**Descartadas:**

- **MIT:** no incluye concesión de patentes ni obliga a indicar los cambios.
- **AGPL-3.0:** solo compensaría si el modelo de sostenibilidad dependiera de que nadie ofreciera GarantIA como servicio cerrado. No es el objetivo del Equipo A, y frenaría la reutilización del detector.

**Requisito previo:** la conformidad de los ocho autores con la licencia. La recoge el Equipo D, y cada autor lo confirma con un comentario en el PR que añade `LICENSE`.

---

## 2. Contenido mínimo del README

El objetivo, fijado por el Equipo A, es que una persona externa arranque la aplicación en menos de 15 minutos solo con el README. Secciones, en este orden:

1. **Qué es GarantIA.** Un párrafo: foro donde consumidores con un problema de garantía publican su caso y otros usuarios les ayudan.
2. **Estado del proyecto y roadmap.** Debe incluir el aviso **"Versión inicial, no apta para producción"**. Tabla en tres bloques:
   - **Hecho:** HU-01, HU-26, HU-16 y HU-08, esta última solo sobre texto.
   - **Siguiente (`v0.1.0`):** publicar casos (HU-02), responder (HU-13) y confirmar que un caso está resuelto (HU-07).
   - **Planificado con IA:**
     - Borradores de reclamación (HU-10 y HU-11).
     - Búsqueda de casos similares (HU-05 y HU-18).
     - Clasificación de casos (HU-17).
     - Resúmenes de casos resueltos (HU-19).
     - Detección de contenido inapropiado (HU-20).

   Cada fila enlaza a su issue, para que quien quiera colaborar en la parte de IA vea dónde hacerlo. Los borradores de reclamación llevarán el aviso de que **no son asesoramiento jurídico**.
3. **Arranque rápido** con los comandos reales:
   ```bash
   cd backend && docker compose up -d
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   cd ../frontend && npm install && npm run dev
   ```
   Cuentas de demostración `admin@`, `marta@`, `pablo@` y `moderador@garantia.local` con la contraseña `Demo1234`. Se advierte de que son datos solo de desarrollo: se cargan desde `V1000__datos_demo.sql` únicamente con el perfil `dev`.
4. **Requisitos:** Java 21, Node LTS (la misma que usa el CI) y Docker. Docker hace falta también para las pruebas, porque usan Testcontainers.
5. **Pruebas:** `./mvnw verify`, `./mvnw verify -Paceptacion`, `npm test` y `npm run lint`.
6. **Arquitectura:** los paquetes del backend (`seguridad`, `usuario`, `caso`, `privacidad`, `admin`), las rutas del frontend y un enlace a `docs/hu-*`.
7. **Detector de datos personales.** Qué tipos reconoce, cómo probarlo en `/comprobar-texto` y enlace a la evaluación (T5). Incluye un párrafo en inglés, para que lo encuentre gente de fuera (Equipo A).
8. **Contribuir:** enlaces a `CONTRIBUTING.md`, a las issues `good first issue` y a `CODE_OF_CONDUCT.md`.
9. **Licencia:** Apache 2.0 para el código y CC BY 4.0 para la documentación y los datos.

---

## 3. Reportar bugs y proponer funcionalidades

**Canal único: GitHub Issues.** ClickUp queda solo para la planificación interna de la asignatura, para no tener información duplicada. Se usan formularios en `.github/ISSUE_TEMPLATE/` y `blank_issues_enabled: false`. Las preguntas generales van a GitHub Discussions.

| Formulario | Campos obligatorios | Etiquetas |
|---|---|---|
| **Bug** | Pasos para reproducir, resultado esperado y obtenido, rol con el que se estaba conectado, componente (backend, frontend o detector) y versión o commit. | `bug`, `triage` |
| **Propuesta** | Problema que resuelve, rol del usuario que la necesita y criterios de aceptación en el formato de las HU del proyecto. | `enhancement`, `triage` |
| **El detector no reconoce un texto** | Texto **inventado**, tipo de dato que debería detectar y si es un falso positivo o un falso negativo. Es la vía para colaboradores sin perfil técnico (Equipo A). | `detector`, `good first issue` |

**Reglas:**

- **Nunca datos personales reales** en issues, ejemplos ni en el CSV. Los formularios lo recuerdan en un aviso. Un issue con datos reales se edita y el mantenedor borra la versión anterior.
- **Vulnerabilidades por vía privada.** Un fallo en el control de acceso (HU-26), el registro (HU-01) o el detector se notifica mediante *Private vulnerability reporting* de GitHub, no en un issue público. El procedimiento va en `SECURITY.md`.
- **Triage semanal.** Las issues con `triage` se revisan y priorizan cada semana. Una propuesta aceptada pasa a ser una HU del roadmap. La persona responsable del triage la fija el Equipo D.

---

## 4. Normas para Pull Requests

Se recogen en `CONTRIBUTING.md` y en `.github/pull_request_template.md`, y formalizan lo que el equipo ya hace.

1. **Ramas desde `main`** con el formato `tipo/descripcion` (`feat/`, `fix/`, `docs/`, `test/`). Los colaboradores externos trabajan desde un fork. No se usa rama `develop`.
2. **Conventional Commits en español**, con la HU o el componente como ámbito: `feat(HU-08): …`, `fix(privacidad): …`. Las referencias a ClickUp se sustituyen por `Closes #n`.
3. **Un PR por issue**, pequeño. Si supera unas 400 líneas, se divide, como se hizo con los parches del Sprint 1.
4. **Requisitos para fusionar:**
   - Los tres trabajos del CI en verde.
   - Pruebas para el código nuevo.
   - Ninguna clave ni credencial en el código. Esto pesa especialmente en las futuras integraciones de IA: el proveedor y la clave van en variables de entorno, y debe existir una implementación simulada que funcione sin clave, para que cualquiera pueda ejecutar las pruebas sin coste (Equipo A).
   - Una aprobación de un mantenedor distinto del autor. Quiénes son mantenedores lo decide el Equipo D.
5. **Protección de `main` en GitHub:** PR obligatorio, una aprobación y los checks *Backend*, *Frontend* y *Aceptación* como requeridos.
6. **El trabajo de aceptación pasa a ser obligatorio.** Se quita `continue-on-error` y se renombra a "Pruebas de aceptación", porque las 30 ya pasan. El propio `ci.yml` indica que debe hacerse al cerrar las historias del sprint. Una HU nueva sube sus pruebas T0 con `@Disabled("Pendiente de HU-xx")`, como ya se hace con la CA2 de HU-08, para no dejar `main` en rojo.
7. **Fusión con *squash*.**
8. **DCO** (`git commit -s`) en lugar de CLA. Con Apache 2.0 es suficiente y no frena al que aporta por primera vez.

---

## 5. Código de conducta

**Decisión:** adoptar **Contributor Covenant 2.1** en español, sin redactar uno propio, con tres adaptaciones:

- **Contacto:** un correo del proyecto, no personal, que atienden dos personas que fija el Equipo D. Así, una denuncia contra una de ellas puede llegar a la otra.
- **Datos personales:** se prohíbe expresamente publicar o pedir datos personales de terceros (*doxxing*), facturas o tickets reales en issues, PR, el CSV de evaluación o el foro de demostración. Es coherente con la HU-08.
- **Marcas y empresas:** no se permiten acusaciones contra comercios o fabricantes concretos en los ejemplos ni en los datos de prueba. Los casos de demostración son ficticios.

Ante un incumplimiento, se aplica la escalera del propio Covenant: corrección, advertencia, expulsión temporal y expulsión permanente.

---

## 6. Tres *good first issues*

| # | Issue | Por qué es adecuada | Archivos |
|---|---|---|---|
| 1 | **Añadir 10 textos nuevos al conjunto de evaluación del detector.** Deben ser inventados y etiquetados, de tipos con pocos ejemplos o con falsos positivos frecuentes. | No requiere programar: una línea por caso. Mejora la limitación que reconoce la T5 de HU-08, con solo 66 textos del equipo, y ejercita el flujo completo de fork, PR y CI, porque `EvaluacionDeteccionTest` la valida. | `backend/src/test/resources/privacidad/casos-evaluacion.csv` |
| 2 | **Detectar matrículas de vehículo españolas** (`0000 BBB`) como nuevo `TipoDatoSensible`. | Copia un patrón que ya existe (valor del enum, regla y pruebas) y da a conocer el componente más reutilizable. | `privacidad/TipoDatoSensible.java`, `DetectorPorReglas.java`, `DetectorPorReglasTest.java` y, opcionalmente, el CSV |
| 3 | **Añadir un `.mailmap`** que unifique los 15 nombres de autor del historial, que corresponden a 8 personas (p. ej. "Jose Ruiz" y "José Ruiz Pareja", "TomasSolerG" y "Tomás Soler Gordo"). | No requiere conocer el código. Hace fiable el indicador de concentración de contribuciones del plan común y facilita recoger la conformidad de los autores con la licencia. | `.mailmap` |

Cada una se publica con `good first issue` y `help wanted`, los criterios de aceptación y un mantenedor asignado como mentor.

**Sobre HU-04 (valoración de respuestas) y HU-15 (seguimiento de temas), que propone el Equipo A.** No las marcamos como *good first issue*. Cada una necesita una migración de Flyway, un endpoint, permisos en `SecurityConfig` y una interfaz en el frontend. Es demasiado para una primera contribución. Se etiquetan como `help wanted` para una segunda contribución.

---

## Riesgos

| Riesgo | Mitigación |
|---|---|
| El repositorio sigue público sin licencia mientras se decide | `LICENSE` es la primera acción, en cuanto los ocho autores den su conformidad. |
| Alguien sube datos personales reales en un issue o en el CSV | Aviso en los formularios, regla del código de conducta y revisión del CSV en cada PR. |
| La puesta en marcha falla para un externo (Docker, Testcontainers o versiones) y abandona | Probar el README con alguien ajeno al equipo antes del anuncio (criterio del Equipo A). |
| Fallos del CI ajenos al código hacen pensar que `main` está roto | Ya ha pasado: el último run de `main` falló al descargar Maven (`wget: Failed to fetch …apache-maven-3.10.0-bin.zip`). Se relanza el run y se puede usar caché de Maven en el CI (Equipo B). |
| Quien llega por el nombre "GarantIA" espera IA que todavía no existe y se va decepcionado | El README y la descripción del repositorio separan claramente lo hecho de lo planificado. La IA solo aparece en el roadmap. |
| Un colaborador envía a un proveedor de IA externo textos con datos personales de usuarios | Norma en `CONTRIBUTING.md`: todo texto pasa por el detector de HU-08 antes de salir hacia la IA, y los PR que lo incumplan no se aceptan. |

## Acciones pendientes

| Acción | Momento |
|---|---|
| PR con `LICENSE` (Apache 2.0), `NOTICE` y la licencia de `docs/` y del CSV, con la conformidad de los ocho autores (Equipo D) | Antes de publicar |
| PR con `README.md`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SECURITY.md` y las plantillas de issue y PR | Antes de publicar |
| Activar la protección de `main`, el *Private vulnerability reporting* y Discussions. Quitar `continue-on-error` del trabajo de aceptación | Antes de publicar |
| Crear las etiquetas `triage` y `detector` y abrir las tres *good first issues* | Primera publicación |

## Coordinación con otros equipos

- **Equipo A:** elegimos la licencia del código y del conjunto de evaluación, el README con su párrafo en inglés y las *good first issues*, como nos pide en sus acciones. Hemos cambiado sus candidatas HU-04 y HU-15 por tareas más pequeñas, con la justificación del apartado 6.
- **Equipo B:** la eliminación de `docs/reuniones/` y `docs/metricas/` del historial debe hacerse antes de añadir la licencia, para no licenciar ese material. También la caché de Maven en el CI.
- **Equipo D:** mantenedores, revisores, contacto del código de conducta y responsable del triage. Recoge la conformidad de los autores con la licencia.
