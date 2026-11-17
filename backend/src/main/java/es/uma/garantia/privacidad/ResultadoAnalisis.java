package es.uma.garantia.privacidad;

import java.util.List;

/** Respuesta de POST /api/privacidad/analizar (contrato en docs/hu-08/T3-flujo-advertencia.md). */
public record ResultadoAnalisis(boolean hayDatosSensibles, List<Deteccion> detecciones) {

	public static ResultadoAnalisis de(List<Deteccion> detecciones) {
		return new ResultadoAnalisis(!detecciones.isEmpty(), detecciones);
	}
}
