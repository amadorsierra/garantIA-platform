package es.uma.garantia.usuario;

import es.uma.garantia.seguridad.Rol;

/** Confirmación del registro (HU-01 CA5). Nunca incluye la contraseña. */
public record RegistroResponse(Long id, String nombreUsuario, String email, Rol rol, String mensaje) {

	public static final String MENSAJE_EXITO = "Cuenta creada correctamente";

	public static RegistroResponse de(Usuario usuario) {
		return new RegistroResponse(usuario.getId(), usuario.getNombreUsuario(), usuario.getEmail(),
				usuario.getRol(), MENSAJE_EXITO);
	}
}
