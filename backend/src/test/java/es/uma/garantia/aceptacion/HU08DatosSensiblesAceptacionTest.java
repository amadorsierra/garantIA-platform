package es.uma.garantia.aceptacion;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import es.uma.garantia.PruebaIntegracion;

/**
 * HU-08 — Advertencia de datos sensibles.
 * Pruebas de aceptación (T0) de la parte de backend: el análisis del texto.
 *
 * CA5 (no publicar hasta haber visto la advertencia) es comportamiento de la interfaz:
 * lo comprueban las pruebas del diálogo en el frontend (T7).
 */
@Tag("aceptacion")
@DisplayName("HU-08 — Advertencia de datos sensibles")
class HU08DatosSensiblesAceptacionTest extends PruebaIntegracion {

	private static final String AFECTADO = "afectado@garantia.test";
	private static final String AYUDANTE = "ayudante@garantia.test";

	@BeforeEach
	void crearUsuarios() throws Exception {
		registrar("marta", AFECTADO);
		registrarConRol("pablo", AYUDANTE, "USUARIO_AYUDANTE");
	}

	private ResultActions analizar(String texto, String email) throws Exception {
		String json = "{\"texto\": \"" + texto.replace("\"", "\\\"") + "\"}";
		return mvc.perform(post("/api/privacidad/analizar")
				.with(httpBasic(email, PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json));
	}

	// CA1. El sistema analiza el texto de la publicación antes de publicarla.

	@Test
	@DisplayName("CA1 · Quien puede publicar puede analizar el texto antes de publicar")
	void ca1_analisisDelTexto() throws Exception {
		analizar("Mi portátil no enciende desde ayer", AFECTADO)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hayDatosSensibles").isBoolean())
				.andExpect(jsonPath("$.detecciones").isArray());
	}

	@Test
	@DisplayName("CA1 · Quien no puede publicar casos no usa el análisis")
	void ca1_sinPermisoDePublicar() throws Exception {
		analizar("Texto cualquiera", AYUDANTE)
				.andExpect(status().isForbidden());
	}

	// CA2. El sistema analiza también los documentos adjuntos.

	@Test
	@Disabled("Pendiente de HU-03 (adjuntar documentos, Sprint 2): todavía no se pueden adjuntar archivos")
	@DisplayName("CA2 · Se analizan los documentos adjuntos")
	void ca2_documentosAdjuntos() {
	}

	// CA3. Si detecta posibles datos personales, muestra una advertencia.

	@Test
	@DisplayName("CA3 · Detecta DNI, teléfono, correo, dirección y nombre completo")
	void ca3_detectaLosTiposPrincipales() throws Exception {
		String texto = "Soy María García López, con DNI 12345678Z. Llamadme al 612 345 678 "
				+ "o escribid a maria.garcia@correo.es. Vivo en la Calle Larios 5, Málaga.";

		analizar(texto, AFECTADO)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hayDatosSensibles").value(true))
				.andExpect(jsonPath("$.detecciones[*].tipo",
						hasItems("NOMBRE_COMPLETO", "DNI", "TELEFONO", "EMAIL", "DIRECCION")));
	}

	@Test
	@DisplayName("CA3 · Detecta datos bancarios: IBAN y tarjeta")
	void ca3_detectaDatosBancarios() throws Exception {
		analizar("Devolvedme el dinero al ES91 2100 0418 4502 0005 1332, pagué con la 4111 1111 1111 1111", AFECTADO)
				.andExpect(jsonPath("$.detecciones[*].tipo", hasItems("IBAN", "TARJETA")));
	}

	// CA4. La advertencia indica qué datos se han detectado y dónde.

	@Test
	@DisplayName("CA4 · Cada detección indica el tipo, el fragmento y su posición en el texto")
	void ca4_queYDonde() throws Exception {
		// "Mi DNI es " ocupa las posiciones 0 a 9; el DNI empieza en la 10
		analizar("Mi DNI es 12345678Z y no lo encuentro", AFECTADO)
				.andExpect(jsonPath("$.detecciones", hasSize(1)))
				.andExpect(jsonPath("$.detecciones[0].tipo").value("DNI"))
				.andExpect(jsonPath("$.detecciones[0].fragmento").value("12345678Z"))
				.andExpect(jsonPath("$.detecciones[0].inicio").value(10))
				.andExpect(jsonPath("$.detecciones[0].fin").value(19))
				.andExpect(jsonPath("$.detecciones[0].descripcion").isString());
	}

	// CA6. Si no se detectan datos personales, no aparece ninguna advertencia.

	@Test
	@DisplayName("CA6 · Un texto sin datos personales no da ninguna detección")
	void ca6_sinDatosPersonales() throws Exception {
		analizar("La lavadora pierde agua desde el segundo mes. El servicio técnico vino dos veces.", AFECTADO)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hayDatosSensibles").value(false))
				.andExpect(jsonPath("$.detecciones", hasSize(0)));
	}

	@Test
	@DisplayName("CA6 · Un DNI con la letra mal no se considera un DNI")
	void ca6_dniConLetraIncorrecta() throws Exception {
		analizar("El número de pedido es 12345678A", AFECTADO)
				.andExpect(jsonPath("$.hayDatosSensibles").value(false));
	}
}
