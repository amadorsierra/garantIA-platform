package es.uma.garantia.admin;

public class UsuarioNoEncontradoException extends RuntimeException {

	public static final String MENSAJE = "El usuario no existe";

	public UsuarioNoEncontradoException() {
		super(MENSAJE);
	}
}
