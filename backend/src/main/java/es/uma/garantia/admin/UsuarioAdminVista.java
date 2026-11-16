package es.uma.garantia.admin;

import es.uma.garantia.seguridad.Rol;
import es.uma.garantia.usuario.Usuario;

/** Cuenta tal como la ve el administrador. */
public record UsuarioAdminVista(Long id, String nombreUsuario, String email, Rol rol) {

	public static UsuarioAdminVista de(Usuario usuario) {
		return new UsuarioAdminVista(usuario.getId(), usuario.getNombreUsuario(), usuario.getEmail(), usuario.getRol());
	}
}
