package es.uma.garantia.seguridad;

/**
 * Acciones concretas que se pueden permitir o denegar.
 * Ver docs/hu-26/T1-permisos-por-rol.md.
 */
public enum Permiso {
	CASOS_LEER,
	CASOS_PUBLICAR,
	RESPUESTAS_PUBLICAR,
	CONTENIDO_MODERAR,
	ROLES_GESTIONAR,
	AUDITORIA_CONSULTAR
}
