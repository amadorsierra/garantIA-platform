package es.uma.garantia.usuario;

/** La contraseña contiene el nombre de usuario o el correo (HU-01 T6). */
public class ContrasenaDebilException extends RuntimeException {

	public static final String MENSAJE = "La contraseña no puede contener tu nombre de usuario ni tu correo";

	public ContrasenaDebilException() {
		super(MENSAJE);
	}
}
