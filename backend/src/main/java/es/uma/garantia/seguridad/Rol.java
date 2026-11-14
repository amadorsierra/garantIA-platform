package es.uma.garantia.seguridad;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Roles del sistema y los permisos de cada uno (matriz de docs/hu-26/T2-matriz-permisos.md).
 *
 * Los permisos están en el código y no en la base de datos: así la matriz se revisa
 * en las pull requests y no puede cambiarse por error desde la aplicación.
 */
public enum Rol {

	USUARIO_AFECTADO(EnumSet.of(
			Permiso.CASOS_LEER,
			Permiso.CASOS_PUBLICAR,
			Permiso.RESPUESTAS_PUBLICAR)),

	USUARIO_AYUDANTE(EnumSet.of(
			Permiso.CASOS_LEER,
			Permiso.RESPUESTAS_PUBLICAR)),

	MODERADOR(EnumSet.of(
			Permiso.CASOS_LEER,
			Permiso.CONTENIDO_MODERAR)),

	ADMINISTRADOR(EnumSet.of(
			Permiso.CASOS_LEER,
			Permiso.ROLES_GESTIONAR,
			Permiso.AUDITORIA_CONSULTAR));

	/** Rol que recibe toda cuenta nueva al registrarse. */
	public static final Rol POR_DEFECTO = USUARIO_AFECTADO;

	private final Set<Permiso> permisos;

	Rol(Set<Permiso> permisos) {
		this.permisos = Collections.unmodifiableSet(permisos);
	}

	public Set<Permiso> getPermisos() {
		return permisos;
	}

	public boolean tienePermiso(Permiso permiso) {
		return permisos.contains(permiso);
	}
}
