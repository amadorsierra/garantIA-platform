package es.uma.garantia.privacidad;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnalisisRequest(
		@NotNull(message = "El texto es obligatorio")
		@Size(max = 20000, message = "El texto no puede tener más de 20 000 caracteres")
		String texto) {

	/** El texto puede llevar datos personales: no se muestra en los logs. */
	@Override
	public String toString() {
		return "AnalisisRequest[texto=" + (texto == null ? 0 : texto.length()) + " caracteres]";
	}
}
