package es.uma.garantia.privacidad;

import java.util.List;

/**
 * Detecta datos personales en un texto.
 *
 * Es una interfaz, como recomienda la T2, para poder añadir otro detector más adelante
 * (por ejemplo, uno basado en Presidio) sin cambiar el resto de la aplicación.
 */
public interface DetectorDatosSensibles {

	/** Devuelve las detecciones ordenadas por su posición en el texto. */
	List<Deteccion> detectar(String texto);
}
