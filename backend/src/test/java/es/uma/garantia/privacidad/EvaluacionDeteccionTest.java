package es.uma.garantia.privacidad;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Mide el detector con el conjunto de evaluación de la T4 y exige los umbrales de la T5:
 * precisión ≥ 95 % y exhaustividad ≥ 85 %. Si un cambio en las reglas empeora la detección, falla.
 */
class EvaluacionDeteccionTest {

	private static final double PRECISION_MINIMA = 0.95;
	private static final double EXHAUSTIVIDAD_MINIMA = 0.85;

	private final DetectorPorReglas detector = new DetectorPorReglas();

	@Test
	void cumpleLosUmbralesAcordadosEnLaT5() throws IOException {
		int aciertos = 0;
		int falsosAvisos = 0;
		int noDetectados = 0;

		for (String linea : leerConjunto().split("\n")) {
			if (linea.isBlank() || linea.startsWith("#") || linea.equals("texto;tipos")) {
				continue;
			}
			String[] partes = linea.split(";", -1);
			Set<String> esperados = new HashSet<>();
			for (String tipo : partes[1].trim().split("\\|")) {
				if (!tipo.isBlank()) {
					esperados.add(tipo);
				}
			}
			Set<String> encontrados = new HashSet<>();
			for (Deteccion deteccion : detector.detectar(partes[0])) {
				encontrados.add(deteccion.tipo().name());
			}

			for (String tipo : esperados) {
				if (encontrados.contains(tipo)) {
					aciertos++;
				} else {
					noDetectados++;
				}
			}
			for (String tipo : encontrados) {
				if (!esperados.contains(tipo)) {
					falsosAvisos++;
				}
			}
		}

		double precision = aciertos / (double) (aciertos + falsosAvisos);
		double exhaustividad = aciertos / (double) (aciertos + noDetectados);
		assertThat(precision).as("precisión").isGreaterThanOrEqualTo(PRECISION_MINIMA);
		assertThat(exhaustividad).as("exhaustividad").isGreaterThanOrEqualTo(EXHAUSTIVIDAD_MINIMA);
	}

	private String leerConjunto() throws IOException {
		try (InputStream entrada = getClass().getResourceAsStream("/privacidad/casos-evaluacion.csv")) {
			assertThat(entrada).as("conjunto de evaluación").isNotNull();
			return new String(entrada.readAllBytes(), StandardCharsets.UTF_8).replace("\r", "");
		}
	}
}
