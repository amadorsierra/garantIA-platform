package es.uma.garantia.usuario;

/** Se intenta registrar un correo que ya tiene cuenta (HU-01 CA3). */
public class EmailYaRegistradoException extends RuntimeException {

	public static final String MENSAJE = "Ya existe una cuenta con ese correo electrónico";

	public EmailYaRegistradoException() {
		super(MENSAJE);
	}
}
