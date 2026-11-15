package es.uma.garantia.comun;

import java.util.Map;

/**
 * Formato común de los errores de la API.
 * "errores" lleva el mensaje de cada campo que hay que corregir (vacío si no aplica).
 */
public record RespuestaError(String mensaje, Map<String, String> errores) {

	public static RespuestaError de(String mensaje) {
		return new RespuestaError(mensaje, Map.of());
	}
}
