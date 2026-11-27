# GarantIA: gobernanza y sostenibilidad

**Práctica 9 – Equipo D**

## 1. Modelo de gobernanza

Para GarantIA proponemos un modelo de gobernanza basado en un **Steering Committee**, formado inicialmente por tres miembros del equipo con conocimientos de backend, frontend y pruebas.

Hemos elegido este modelo porque permite repartir las responsabilidades y evita que todas las decisiones dependan de una sola persona. Además, encaja con nuestra forma actual de trabajar mediante issues, ramas y Pull Requests en GitHub.

El comité se encargará de decidir las prioridades, los cambios importantes y la publicación de nuevas versiones. Las decisiones quedarán registradas en GitHub. Al terminar la asignatura se revisará quién puede seguir manteniendo el proyecto y, posteriormente, se revisará la composición del comité cada tres meses. Los colaboradores externos que participen de forma habitual también podrán llegar a formar parte de él.

## 2. Revisión y aceptación de cambios

Cualquier colaborador podrá proponer cambios mediante una Pull Request, explicando qué ha modificado, con qué issue está relacionado el cambio y cómo lo ha probado.

Para aceptar una PR será necesario:

- Que la apruebe al menos una persona distinta de su autor.
- Que supere las comprobaciones de CI correspondientes (tests, linter y compilación).
- Que se resuelvan los comentarios pendientes que impidan su integración.
- Que se actualice la documentación si el cambio lo requiere.

La integración en `main` la realizará un mantenedor. En cambios relacionados con la autenticación, los roles y permisos, los datos personales o dependencias relevantes, pediremos dos revisiones de personas distintas del autor, ya que GarantIA puede gestionar información sensible.

Antes de abrir el proyecto a contribuciones externas, habrá que proteger la rama `main` y configurar las comprobaciones obligatorias junto con el equipo B. Las pruebas de aceptación exigidas deberán ser bloqueantes; las pendientes o desactivadas quedarán justificadas en una issue.

## 3. Resolución de desacuerdos

Si hay diferencias de opinión, primero intentaremos llegar a un acuerdo comentando las distintas opciones en la issue o Pull Request correspondiente. Tendremos en cuenta el esfuerzo necesario, la utilidad del cambio, la seguridad y su mantenimiento. Si hace falta, se podrá realizar una pequeña prueba para comparar las alternativas.

Si después de una semana no se llega a un acuerdo, el comité decidirá por mayoría, con la participación de al menos dos de sus miembros. En caso de empate, se mantendrá la solución actual hasta disponer de más información. Si existe un riesgo de seguridad o privacidad sin resolver, no se aceptará el cambio.

La decisión final y sus motivos quedarán registrados en GitHub.

## 4. Abandono de un desarrollador principal

Para evitar que la salida de un miembro paralice el proyecto, proponemos que haya al menos dos administradores del repositorio, con cuentas individuales y autenticación en dos pasos. También mantendremos actualizada la documentación sobre la instalación, configuración, pruebas y despliegue de GarantIA.

Si un desarrollador principal decide abandonar el proyecto, repartiremos sus tareas pendientes, revisaremos sus permisos y organizaremos un traspaso de conocimientos. Si la salida es inesperada, los demás mantenedores asumirán temporalmente las tareas más importantes.

Al finalizar la asignatura tendremos que comprobar quién está dispuesto a seguir colaborando. Si no hay suficientes personas, se reducirá el alcance del proyecto y se buscarán nuevos mantenedores. Si no encontramos un relevo, informaremos de la situación en el README y, si es necesario, archivaremos el repositorio.

## 5. Riesgos de continuidad

| Riesgo | Consecuencia | Mitigación |
|---|---|---|
| Dependencia de pocos desarrolladores | La salida de un miembro puede dificultar el mantenimiento de ciertas partes de GarantIA. | El comité repartirá el conocimiento mediante documentación y revisiones cruzadas. Habrá al menos dos administradores. |
| Falta de tiempo después de la asignatura | Se pueden acumular bugs e issues sin resolver. | Los mantenedores repartirán las tareas según su disponibilidad y darán prioridad a las correcciones y al mantenimiento. Si no hay capacidad, se comunicará la pausa. |
| Costes de alojamiento y servicios de IA | Mantener GarantIA en funcionamiento puede resultar demasiado caro. | Los responsables del despliegue establecerán un presupuesto, alertas y límites de consumo, además de documentar alternativas de instalación. |

## 6. Modelo de sostenibilidad

Proponemos un modelo comunitario de bajo coste, basado inicialmente en el trabajo voluntario y, si el proyecto consigue atraer colaboradores, en donaciones y patrocinios.

Nuestra prioridad será que cualquier persona pueda instalar GarantIA en su propio entorno, sin depender de que mantengamos un servidor público. Si decidimos mantener una demo, utilizaremos datos ficticios y limitaremos el consumo de recursos, especialmente el de los servicios de IA. Antes de contratar servicios, estimaremos los costes y acordaremos quién los asumirá. También procuraremos que sea posible sustituir el proveedor de IA.

Si el proyecto crece, podríamos ofrecer servicios de instalación, alojamiento o soporte técnico, siempre que tengamos capacidad para ello y sean compatibles con la licencia elegida. No dependeremos de esos ingresos para empezar.

Consideramos que este modelo es el más adecuado porque GarantIA nace como un proyecto universitario y todavía no cuenta con financiación estable. Si recibimos financiación, publicaremos un resumen de ingresos y gastos. Los patrocinadores no tendrán acceso especial a los datos de los usuarios ni prioridad automática en las decisiones.

## 7. Acciones pendientes

| Acción | Responsable | Cuándo |
|---|---|---|
| Elegir a los miembros del comité y a los administradores del repositorio. | Todo el equipo | Antes de publicar el proyecto. |
| Proteger `main` y configurar las revisiones y comprobaciones obligatorias. | Mantenedores y equipo B | Antes de aceptar contribuciones externas. |
| Acordar y publicar las normas de colaboración y toma de decisiones, de acuerdo con la licencia. | Equipos C y D | Antes del lanzamiento. |
| Documentar el proyecto y confirmar quién seguirá manteniéndolo. | Todo el equipo | Antes de terminar la asignatura. |
| Estimar los costes de los servicios y decidir si se mantendrá una demo pública. | Comité y responsables del despliegue | Antes de contratar servicios. |

Antes de publicar GarantIA, consideramos imprescindible contar con un equipo mínimo de mantenimiento, varios responsables con acceso al repositorio y unas normas claras para aceptar contribuciones. Estas propuestas deberán acordarse con el resto del equipo e incorporarse al plan común de lanzamiento.
