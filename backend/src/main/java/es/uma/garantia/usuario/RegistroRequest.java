package es.uma.garantia.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario de registro. Reglas y mensajes de docs/hu-01/T1-datos-registro.md.
 */
public record RegistroRequest(

		@NotBlank(message = "El nombre de usuario es obligatorio")
		@Size(min = 3, max = 30, message = RegistroRequest.MENSAJE_NOMBRE)
		@Pattern(regexp = "^[A-Za-z0-9._-]*$", message = RegistroRequest.MENSAJE_NOMBRE)
		String nombreUsuario,

		@NotBlank(message = "El correo electrónico es obligatorio")
		@Email(regexp = ".+@.+\\..+", message = "Introduce un correo electrónico válido")
		@Size(max = 254, message = "Introduce un correo electrónico válido")
		String email,

		@NotBlank(message = "La contraseña es obligatoria")
		@Size(min = 8, max = 64, message = RegistroRequest.MENSAJE_PASSWORD)
		String password) {

	static final String MENSAJE_NOMBRE = "Usa de 3 a 30 caracteres: letras, números, punto, guion o guion bajo";
	static final String MENSAJE_PASSWORD = "La contraseña debe tener entre 8 y 64 caracteres, con al menos una letra y un número";

	/** Evita que la contraseña aparezca en los logs si alguien imprime la petición. */
	@Override
	public String toString() {
		return "RegistroRequest[nombreUsuario=" + nombreUsuario + ", email=" + email + ", password=****]";
	}
}
