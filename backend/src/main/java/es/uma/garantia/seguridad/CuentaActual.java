package es.uma.garantia.seguridad;

import java.util.ArrayList;
import java.util.List;

import es.uma.garantia.usuario.Usuario;

/** Datos de la cuenta con la que se ha iniciado sesión. Nunca incluye la contraseña. */
public record CuentaActual(Long id, String nombreUsuario, String email, Rol rol, List<Permiso> permisos) {

	public static CuentaActual de(Usuario usuario) {
		List<Permiso> permisos = new ArrayList<>(usuario.getRol().getPermisos());
		return new CuentaActual(usuario.getId(), usuario.getNombreUsuario(), usuario.getEmail(),
				usuario.getRol(), permisos);
	}
}
