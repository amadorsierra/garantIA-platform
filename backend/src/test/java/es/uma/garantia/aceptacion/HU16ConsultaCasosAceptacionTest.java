package es.uma.garantia.aceptacion;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import es.uma.garantia.PruebaIntegracion;

/**
 * HU-16 — Consulta de casos publicados.
 * Pruebas de aceptación (T0), escritas por Jose y Tomás.
 *
 * Las comprobaciones sobre la lista usan "$..campo" para que valgan tanto si la API
 * devuelve una lista como si la devuelve dentro de una página (paginación, T5).
 */
@Tag("aceptacion")
@DisplayName("HU-16 — Consulta de casos publicados")
class HU16ConsultaCasosAceptacionTest extends PruebaIntegracion {

	private static final String AYUDANTE = "ayudante@garantia.test";
	private static final String AUTORA = "autora@garantia.test";

	private Long autoraId;

	@BeforeEach
	void prepararDatos() throws Exception {
		registrarConRol("ayudante", AYUDANTE, "USUARIO_AYUDANTE");
		registrar("marta", AUTORA);
		autoraId = idDe(AUTORA);
	}

	private Long insertarCaso(String titulo, String descripcion, String categoria, String estado, LocalDateTime fecha) {
		return jdbc.queryForObject("""
				INSERT INTO casos (titulo, descripcion, autor_id, fecha_publicacion, categoria, estado)
				VALUES (?, ?, ?, ?, ?, ?)
				RETURNING id
				""", Long.class, titulo, descripcion, autoraId, Timestamp.valueOf(fecha), categoria, estado);
	}

	private void insertarRespuesta(Long casoId, String contenido) {
		jdbc.update("""
				INSERT INTO respuestas (caso_id, autor_id, contenido, fecha)
				VALUES (?, ?, ?, ?)
				""", casoId, autoraId, contenido, Timestamp.valueOf(LocalDateTime.now()));
	}

	private void insertarTresCasos() {
		insertarCaso("Portátil que no enciende", "Dejó de encender a los 8 meses",
				"Electrónica", "ABIERTO", LocalDateTime.of(2026, 11, 1, 10, 0));
		insertarCaso("Lavadora con fuga de agua", "Pierde agua en cada lavado",
				"Electrodomésticos", "CERRADO", LocalDateTime.of(2026, 11, 3, 10, 0));
		insertarCaso("Móvil con la pantalla rota", "La tienda no acepta la garantía",
				"Electrónica", "ABIERTO", LocalDateTime.of(2026, 11, 5, 10, 0));
	}

	// CA1. Solo los usuarios autenticados con permiso de lectura acceden a la lista.

	@Test
	@DisplayName("CA1 · Sin iniciar sesión no se puede consultar la lista")
	void ca1_anonimoNoAccede() throws Exception {
		mvc.perform(get("/api/casos"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("CA1 · Un Usuario Ayudante puede consultar la lista")
	void ca1_ayudanteAccede() throws Exception {
		mvc.perform(get("/api/casos").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(status().isOk());
	}

	// CA2. La lista muestra título, autor, fecha, categoría y estado.

	@Test
	@DisplayName("CA2 · Cada caso de la lista muestra título, autor, fecha, categoría y estado")
	void ca2_camposDeLaLista() throws Exception {
		insertarCaso("Portátil que no enciende", "Dejó de encender a los 8 meses",
				"Electrónica", "ABIERTO", LocalDateTime.of(2026, 11, 1, 10, 0));

		mvc.perform(get("/api/casos").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..titulo", contains("Portátil que no enciende")))
				.andExpect(jsonPath("$..autor", contains("marta")))
				.andExpect(jsonPath("$..fechaPublicacion", hasSize(1)))
				.andExpect(jsonPath("$..categoria", contains("Electrónica")))
				.andExpect(jsonPath("$..estado", contains("ABIERTO")));
	}

	// CA3. Se puede filtrar y ordenar por fecha, estado o categoría.

	@Test
	@DisplayName("CA3 · Por defecto se ordena del caso más reciente al más antiguo")
	void ca3_ordenPorDefecto() throws Exception {
		insertarTresCasos();

		mvc.perform(get("/api/casos").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(jsonPath("$..titulo", contains(
						"Móvil con la pantalla rota", "Lavadora con fuga de agua", "Portátil que no enciende")));
	}

	@Test
	@DisplayName("CA3 · Se puede ordenar del más antiguo al más reciente")
	void ca3_ordenAntiguos() throws Exception {
		insertarTresCasos();

		mvc.perform(get("/api/casos").param("orden", "antiguos").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(jsonPath("$..titulo", contains(
						"Portátil que no enciende", "Lavadora con fuga de agua", "Móvil con la pantalla rota")));
	}

	@Test
	@DisplayName("CA3 · Se puede filtrar por estado")
	void ca3_filtroEstado() throws Exception {
		insertarTresCasos();

		mvc.perform(get("/api/casos").param("estado", "CERRADO").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(jsonPath("$..titulo", contains("Lavadora con fuga de agua")));
	}

	@Test
	@DisplayName("CA3 · Se puede filtrar por categoría")
	void ca3_filtroCategoria() throws Exception {
		insertarTresCasos();

		mvc.perform(get("/api/casos").param("categoria", "Electrónica").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(jsonPath("$..titulo", containsInAnyOrder(
						"Portátil que no enciende", "Móvil con la pantalla rota")));
	}

	// CA4. Búsqueda por texto libre en títulos o descripciones.

	@Test
	@DisplayName("CA4 · Se busca texto libre en el título, sin distinguir mayúsculas")
	void ca4_busquedaEnTitulo() throws Exception {
		insertarTresCasos();

		mvc.perform(get("/api/casos").param("q", "LAVADORA").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(jsonPath("$..titulo", contains("Lavadora con fuga de agua")));
	}

	@Test
	@DisplayName("CA4 · Se busca texto libre en la descripción")
	void ca4_busquedaEnDescripcion() throws Exception {
		insertarTresCasos();

		mvc.perform(get("/api/casos").param("q", "garantía").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(jsonPath("$..titulo", contains("Móvil con la pantalla rota")));
	}

	// CA5. Al seleccionar un caso se ve el detalle completo y sus respuestas.

	@Test
	@DisplayName("CA5 · El detalle muestra la publicación completa y sus respuestas")
	void ca5_detalleConRespuestas() throws Exception {
		Long casoId = insertarCaso("Portátil que no enciende", "Dejó de encender a los 8 meses",
				"Electrónica", "ABIERTO", LocalDateTime.of(2026, 11, 1, 10, 0));
		insertarRespuesta(casoId, "Reclama al vendedor: la garantía legal es de 3 años");

		mvc.perform(get("/api/casos/{id}", casoId).with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.titulo").value("Portátil que no enciende"))
				.andExpect(jsonPath("$.descripcion").value("Dejó de encender a los 8 meses"))
				.andExpect(jsonPath("$.autor").value("marta"))
				.andExpect(jsonPath("$.respuestas", hasSize(1)))
				.andExpect(jsonPath("$.respuestas[0].contenido")
						.value("Reclama al vendedor: la garantía legal es de 3 años"))
				.andExpect(jsonPath("$.respuestas[0].autor").value("marta"));
	}

	// CA6. Si no hay resultados, la lista llega vacía (el aviso lo muestra el frontend).

	@Test
	@DisplayName("CA6 · Si ningún caso coincide, la lista llega vacía")
	void ca6_sinResultados() throws Exception {
		insertarTresCasos();

		mvc.perform(get("/api/casos").param("q", "bicicleta").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..titulo").isEmpty());
	}

	// CA7. Si hay un error al cargar el detalle, se informa al usuario.

	@Test
	@DisplayName("CA7 · Un caso que no existe devuelve un mensaje de error")
	void ca7_casoInexistente() throws Exception {
		mvc.perform(get("/api/casos/{id}", 999999).with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensaje").value("El caso no existe"));
	}

	// Paginación (acordada en el Scrum Meeting del 10/11: 10 casos por página).

	@Test
	@DisplayName("Paginación · Se muestran 10 casos por página")
	void paginacion() throws Exception {
		for (int i = 1; i <= 12; i++) {
			insertarCaso("Caso " + i, "Descripción " + i, "Electrónica", "ABIERTO",
					LocalDateTime.of(2026, 11, 1, 10, 0).plusHours(i));
		}

		mvc.perform(get("/api/casos").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(jsonPath("$.contenido", hasSize(10)))
				.andExpect(jsonPath("$.totalElementos").value(12))
				.andExpect(jsonPath("$.totalPaginas").value(2));

		mvc.perform(get("/api/casos").param("pagina", "1").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(jsonPath("$.contenido", hasSize(2)))
				.andExpect(jsonPath("$.pagina").value(1));
	}
}
