package es.uma.garantia.caso;

public class CasoNoEncontradoException extends RuntimeException {

	public static final String MENSAJE = "El caso no existe";

	public CasoNoEncontradoException() {
		super(MENSAJE);
	}
}
