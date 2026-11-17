package es.uma.garantia.privacidad;

/** Datos personales que se detectan (docs/hu-08/T1-tipos-datos-personales.md). */
public enum TipoDatoSensible {

	DNI("DNI"),
	NIE("NIE"),
	TELEFONO("Número de teléfono"),
	EMAIL("Correo electrónico"),
	IBAN("Cuenta bancaria (IBAN)"),
	TARJETA("Número de tarjeta"),
	DIRECCION("Dirección postal"),
	NOMBRE_COMPLETO("Nombre y apellidos");

	/** Nombre en lenguaje claro para mostrar al usuario. */
	private final String descripcion;

	TipoDatoSensible(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
