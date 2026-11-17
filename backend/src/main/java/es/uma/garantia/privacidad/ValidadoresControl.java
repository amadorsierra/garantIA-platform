package es.uma.garantia.privacidad;

/**
 * Comprobación de los dígitos de control (docs/hu-08/T1-tipos-datos-personales.md).
 * Evitan que un número de pedido o de serie con la misma forma se tome por un dato personal.
 */
public final class ValidadoresControl {

	private static final String LETRAS_DNI = "TRWAGMYFPDXBNJZSQVHLCKE";

	private ValidadoresControl() {
	}

	/** 8 cifras y la letra que corresponde al resto de dividirlas entre 23. */
	public static boolean dniValido(String texto) {
		String limpio = limpiar(texto);
		if (!limpio.matches("\\d{8}[A-Z]")) {
			return false;
		}
		int numero = Integer.parseInt(limpio.substring(0, 8));
		return LETRAS_DNI.charAt(numero % 23) == limpio.charAt(8);
	}

	/** X, Y o Z (que valen 0, 1 y 2), 7 cifras y la letra de control, calculada como en el DNI. */
	public static boolean nieValido(String texto) {
		String limpio = limpiar(texto);
		if (!limpio.matches("[XYZ]\\d{7}[A-Z]")) {
			return false;
		}
		String prefijo = String.valueOf("XYZ".indexOf(limpio.charAt(0)));
		int numero = Integer.parseInt(prefijo + limpio.substring(1, 8));
		return LETRAS_DNI.charAt(numero % 23) == limpio.charAt(8);
	}

	/** Algoritmo del módulo 97: se mueven los 4 primeros caracteres al final y el resultado debe dar resto 1. */
	public static boolean ibanValido(String texto) {
		String limpio = limpiar(texto);
		if (limpio.length() < 15 || limpio.length() > 34) {
			return false;
		}
		String reordenado = limpio.substring(4) + limpio.substring(0, 4);
		int resto = 0;
		for (char caracter : reordenado.toCharArray()) {
			int valor = Character.digit(caracter, 36);
			if (valor < 0) {
				return false;
			}
			resto = Integer.parseInt(resto + String.valueOf(valor)) % 97;
		}
		return resto == 1;
	}

	/** Algoritmo de Luhn de las tarjetas: de 13 a 19 cifras. */
	public static boolean luhnValido(String texto) {
		String digitos = texto.replaceAll("\\D", "");
		if (digitos.length() < 13 || digitos.length() > 19) {
			return false;
		}
		int suma = 0;
		boolean doble = false;
		for (int i = digitos.length() - 1; i >= 0; i--) {
			int digito = digitos.charAt(i) - '0';
			if (doble) {
				digito = digito * 2;
				if (digito > 9) {
					digito = digito - 9;
				}
			}
			suma += digito;
			doble = !doble;
		}
		return suma % 10 == 0;
	}

	private static String limpiar(String texto) {
		return texto.replaceAll("[\\s-]", "").toUpperCase();
	}
}
