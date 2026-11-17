package es.uma.garantia.privacidad;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

class DetectorPorReglasTest {

	private final DetectorPorReglas detector = new DetectorPorReglas();

	private List<TipoDatoSensible> tipos(String texto) {
		List<TipoDatoSensible> tipos = new ArrayList<>();
		for (Deteccion deteccion : detector.detectar(texto)) {
			tipos.add(deteccion.tipo());
		}
		return tipos;
	}

	@ParameterizedTest(name = "{1}: {0}")
	@CsvSource(delimiter = '|', textBlock = """
			Mi DNI es 12345678Z                  | DNI
			Con NIE X1234567L                    | NIE
			Llamadme al 612 345 678              | TELEFONO
			Mi móvil es el +34 612345678         | TELEFONO
			Escribid a lucia.martin@gmail.com    | EMAIL
			Al ES91 2100 0418 4502 0005 1332     | IBAN
			Con la tarjeta 4111 1111 1111 1111   | TARJETA
			Vivo en la Calle Larios 5            | DIRECCION
			Enviadlo a Avda. de Andalucía 12     | DIRECCION
			Me llamo María García López          | NOMBRE_COMPLETO
			Soy Carmen de la Torre               | NOMBRE_COMPLETO
			Me atendió Álvaro Torres             | NOMBRE_COMPLETO
			""")
	void detectaCadaTipo(String texto, TipoDatoSensible tipo) {
		assertThat(tipos(texto)).containsExactly(tipo);
	}

	@ParameterizedTest(name = "sin datos: {0}")
	@CsvSource(delimiter = '|', textBlock = """
			El número de pedido es 12345678A
			Número de serie 4111111111111112
			El pedido 1234567890 lleva tres semanas
			Llevo 612 días esperando
			María me dijo que le pasó lo mismo
			Juan Carlos de la tienda me atendió
			La calle estaba cortada
			Mi iPhone 13 Pro se apaga solo
			""")
	void noDaFalsosAvisos(String texto) {
		assertThat(detector.detectar(texto)).isEmpty();
	}

	@Test
	void indicaElFragmentoYSuPosicion() {
		Deteccion deteccion = detector.detectar("Mi DNI es 12345678Z y no lo encuentro").get(0);

		assertThat(deteccion.fragmento()).isEqualTo("12345678Z");
		assertThat(deteccion.inicio()).isEqualTo(10);
		assertThat(deteccion.fin()).isEqualTo(19);
		assertThat(deteccion.descripcion()).isEqualTo("DNI");
	}

	@Test
	void devuelveLasDeteccionesEnOrdenDeAparicion() {
		List<TipoDatoSensible> encontrados = tipos(
				"Soy María García López, DNI 12345678Z, teléfono 612 345 678, correo maria@correo.es");

		assertThat(encontrados).containsExactly(TipoDatoSensible.NOMBRE_COMPLETO, TipoDatoSensible.DNI,
				TipoDatoSensible.TELEFONO, TipoDatoSensible.EMAIL);
	}

	@Test
	void lasCifrasDeUnIbanNoSeCuentanTambienComoTarjeta() {
		assertThat(tipos("Reembolso al ES91 2100 0418 4502 0005 1332")).containsExactly(TipoDatoSensible.IBAN);
	}

	@Test
	void unTextoVacioNoTieneDetecciones() {
		assertThat(detector.detectar("")).isEmpty();
	}
}
