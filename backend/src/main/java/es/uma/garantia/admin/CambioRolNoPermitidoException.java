package es.uma.garantia.admin;

/**
 * Un administrador intenta cambiar su propio rol.
 * Se impide para que el sistema no se quede sin ningún administrador por error.
 */
public class CambioRolNoPermitidoException extends RuntimeException {

	public static final String MENSAJE = "No puedes cambiar tu propio rol";

	public CambioRolNoPermitidoException() {
		super(MENSAJE);
	}
}
