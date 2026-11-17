package es.uma.garantia.privacidad;

/**
 * Un dato personal encontrado en el texto (HU-08 CA4): qué es y dónde está.
 * "inicio" y "fin" son posiciones en el texto; "fin" no se incluye.
 */
public record Deteccion(TipoDatoSensible tipo, String descripcion, String fragmento, int inicio, int fin) {

	public Deteccion(TipoDatoSensible tipo, String fragmento, int inicio) {
		this(tipo, tipo.getDescripcion(), fragmento, inicio, inicio + fragmento.length());
	}

	public boolean solapaCon(Deteccion otra) {
		return inicio < otra.fin && fin > otra.inicio;
	}
}
