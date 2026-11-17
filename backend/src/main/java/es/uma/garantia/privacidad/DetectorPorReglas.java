package es.uma.garantia.privacidad;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * Detección de datos personales con reglas y validaciones (HU-08 T6).
 *
 * Es el prototipo de la T4 (herramientas/prototipo-hu08/detector.mjs) pasado a Java,
 * con las mismas reglas que se evaluaron en la T5. Si se cambia una regla aquí,
 * conviene cambiarla también en el prototipo y volver a medir.
 */
@Component
public class DetectorPorReglas implements DetectorDatosSensibles {

	/** Nombres de pila frecuentes: un nombre completo empieza por uno de ellos. */
	static final Set<String> NOMBRES = Set.of(
			"María", "Carmen", "Ana", "Laura", "Lucía", "Marta", "Elena", "Isabel", "Paula", "Sara", "Cristina",
			"Pilar", "Rosa", "Raquel", "Sofía", "Julia", "Andrea", "Irene", "Alba", "Nuria", "Silvia", "Patricia",
			"Beatriz", "Teresa", "Mercedes", "Rocío", "Eva", "Inés", "Clara", "Natalia", "Lorena", "Noelia",
			"Antonio", "José", "Manuel", "Francisco", "David", "Juan", "Javier", "Daniel", "Carlos", "Jesús",
			"Alejandro", "Miguel", "Rafael", "Pedro", "Pablo", "Ángel", "Sergio", "Fernando", "Jorge", "Luis",
			"Alberto", "Álvaro", "Adrián", "Diego", "Raúl", "Iván", "Rubén", "Enrique", "Óscar", "Ramón", "Andrés",
			"Vicente", "Joaquín", "Santiago", "Víctor", "Mario", "Roberto", "Jaime", "Ignacio", "Alfonso", "Hugo",
			"Marcos", "Tomás", "Fabián", "Amador", "Gonzalo", "Guillermo", "Rodrigo", "Emilio", "Julio", "Agustín");

	private static final String PALABRA_MAYUS = "[A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+";
	private static final String PARTICULA = "(?:(?:de la|de los|de las|del|de)\\s+)?";

	/** Una regla: el patrón que busca y, si existe, la comprobación que debe pasar lo encontrado. */
	private record Regla(TipoDatoSensible tipo, Pattern patron, Predicate<String> validacion) {
	}

	/** El orden importa: las de formato más largo van primero para que las demás no se las coman. */
	private final List<Regla> reglas = List.of(
			new Regla(TipoDatoSensible.EMAIL,
					Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"),
					fragmento -> true),
			new Regla(TipoDatoSensible.IBAN,
					Pattern.compile("\\b[A-Z]{2}\\d{2}(?:\\s?[A-Z0-9]{4}){3,7}(?:\\s?[A-Z0-9]{1,3})?\\b"),
					ValidadoresControl::ibanValido),
			new Regla(TipoDatoSensible.TARJETA,
					Pattern.compile("(?<![\\d-])(?:\\d[\\s-]?){12,18}\\d(?![\\d-])"),
					ValidadoresControl::luhnValido),
			new Regla(TipoDatoSensible.NIE,
					Pattern.compile("\\b[XYZ][\\s-]?\\d{7}[\\s-]?[A-Za-z]\\b"),
					ValidadoresControl::nieValido),
			new Regla(TipoDatoSensible.DNI,
					Pattern.compile("\\b\\d{8}[\\s-]?[A-Za-z]\\b"),
					ValidadoresControl::dniValido),
			new Regla(TipoDatoSensible.TELEFONO,
					// 9 cifras que empiezan por 6, 7, 8 o 9, con prefijo +34 o 0034 opcional
					Pattern.compile("(?<![A-Za-z0-9_+])(?:(?:\\+|00)34[\\s.-]?)?[6789](?:[\\s.-]?\\d){8}(?![A-Za-z0-9_])"),
					fragmento -> true),
			new Regla(TipoDatoSensible.DIRECCION,
					// Tipo de vía + nombre en mayúscula + número
					Pattern.compile("(?:\\b(?:[Cc]alle|[Aa]venida|[Aa]vda\\.?|[Pp]laza|[Pp]za\\.?|[Pp]aseo|[Cc]amino"
							+ "|[Cc]arretera|[Rr]onda|[Tt]ravesía|[Gg]lorieta)|C/)\\s*" + PARTICULA + PALABRA_MAYUS
							+ "(?:\\s+" + PARTICULA + PALABRA_MAYUS + "){0,3},?\\s*(?:n[º°o.]\\s*)?\\d{1,4}"),
					fragmento -> true),
			new Regla(TipoDatoSensible.NOMBRE_COMPLETO,
					// Nombre de pila frecuente + hasta tres palabras en mayúscula (apellidos).
					// No se usa \\b delante: en Java (y en JavaScript) solo reconoce letras sin tilde,
					// y no encontraría nombres como «Álvaro» u «Óscar».
					Pattern.compile("(?<![\\p{L}\\d])(?:" + String.join("|", NOMBRES) + ")(?:\\s+" + PARTICULA + PALABRA_MAYUS + "){1,3}"),
					DetectorPorReglas::tieneApellido));

	@Override
	public List<Deteccion> detectar(String texto) {
		List<Deteccion> detecciones = new ArrayList<>();
		for (Regla regla : reglas) {
			Matcher coincidencia = regla.patron().matcher(texto);
			while (coincidencia.find()) {
				Deteccion candidata = new Deteccion(regla.tipo(), coincidencia.group().trim(), coincidencia.start());
				if (!solapaConAlguna(candidata, detecciones) && regla.validacion().test(candidata.fragmento())) {
					detecciones.add(candidata);
				}
			}
		}
		detecciones.sort(Comparator.comparingInt(Deteccion::inicio));
		return detecciones;
	}

	private static boolean solapaConAlguna(Deteccion candidata, List<Deteccion> detecciones) {
		for (Deteccion deteccion : detecciones) {
			if (candidata.solapaCon(deteccion)) {
				return true;
			}
		}
		return false;
	}

	/** «Juan Carlos» son dos nombres de pila, no un nombre completo: hace falta al menos un apellido. */
	static boolean tieneApellido(String fragmento) {
		for (String palabra : fragmento.split("\\s+")) {
			boolean esPalabraEnMayuscula = palabra.matches(PALABRA_MAYUS);
			if (esPalabraEnMayuscula && !NOMBRES.contains(palabra)) {
				return true;
			}
		}
		return false;
	}
}
