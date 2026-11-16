package es.uma.garantia.caso;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.uma.garantia.PruebaIntegracion;
import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;
import jakarta.persistence.EntityManager;

/** Endpoint de consulta de casos (HU-16 T3). */
class CasoConsultaTest extends PruebaIntegracion {

	private static final String EMAIL = "marta@correo.es";

	@Autowired
	private UsuarioRepository usuarios;

	@Autowired
	private CasoRepository casos;

	@Autowired
	private RespuestaRepository respuestas;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EntityManager entityManager;

	private Usuario marta;

	@BeforeEach
	void prepararDatos() {
		marta = usuarios.save(new Usuario("marta", EMAIL, passwordEncoder.encode(PASSWORD)));
	}

	private Caso crearCaso(String titulo, String descripcion, String categoria, EstadoCaso estado, int dia) {
		return casos.saveAndFlush(new Caso(titulo, descripcion, marta, categoria, estado,
				LocalDateTime.of(2026, 11, dia, 10, 0)));
	}

	@Test
	void listaVaciaSiNoHayCasos() throws Exception {
		mvc.perform(get("/api/casos").with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..id").isEmpty());
	}

	@Test
	void listaConLosCamposDeCadaCaso() throws Exception {
		crearCaso("Portátil roto", "No enciende", "Electrónica", EstadoCaso.ABIERTO, 1);

		mvc.perform(get("/api/casos").with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(jsonPath("$..titulo", contains("Portátil roto")))
				.andExpect(jsonPath("$..autor", contains("marta")))
				.andExpect(jsonPath("$..categoria", contains("Electrónica")))
				.andExpect(jsonPath("$..estado", contains("ABIERTO")))
				.andExpect(jsonPath("$..fechaPublicacion", contains(startsWith("2026-11-01T10:00"))))
				// La lista no incluye la descripción completa
				.andExpect(jsonPath("$..descripcion").isEmpty());
	}

	@Test
	void combinaFiltroBusquedaYOrden() throws Exception {
		crearCaso("Portátil roto", "No enciende", "Electrónica", EstadoCaso.ABIERTO, 1);
		crearCaso("Portátil lento", "Va muy lento", "Electrónica", EstadoCaso.ABIERTO, 2);
		crearCaso("Portátil sin batería", "La batería no carga", "Electrónica", EstadoCaso.CERRADO, 3);
		crearCaso("Nevera ruidosa", "Hace ruido", "Electrodomésticos", EstadoCaso.ABIERTO, 4);

		mvc.perform(get("/api/casos")
				.param("q", "portátil")
				.param("estado", "ABIERTO")
				.param("categoria", "electrónica")
				.param("orden", "antiguos")
				.with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(jsonPath("$..titulo", contains("Portátil roto", "Portátil lento")));
	}

	@Test
	void unEstadoQueNoExisteDevuelve400() throws Exception {
		mvc.perform(get("/api/casos").param("estado", "INVENTADO").with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value("El valor de 'estado' no es válido"));
	}

	@Test
	void detalleConLasRespuestasEnOrdenCronologico() throws Exception {
		Caso caso = crearCaso("Portátil roto", "No enciende", "Electrónica", EstadoCaso.ABIERTO, 1);
		respuestas.save(new Respuesta(caso, marta, "Segunda", LocalDateTime.of(2026, 11, 3, 10, 0)));
		respuestas.saveAndFlush(new Respuesta(caso, marta, "Primera", LocalDateTime.of(2026, 11, 2, 10, 0)));
		// Se vacía la caché de JPA para que el detalle se lea de la base de datos
		entityManager.clear();

		mvc.perform(get("/api/casos/{id}", caso.getId()).with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.descripcion").value("No enciende"))
				.andExpect(jsonPath("$.respuestas", hasSize(2)))
				.andExpect(jsonPath("$.respuestas[*].contenido", contains("Primera", "Segunda")));
	}

	@Test
	void detalleDeUnCasoQueNoExisteDevuelve404() throws Exception {
		mvc.perform(get("/api/casos/{id}", 123456).with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensaje").value("El caso no existe"));
	}

	@Test
	void categoriasSinRepetirYOrdenadas() throws Exception {
		crearCaso("A", "a", "Electrónica", EstadoCaso.ABIERTO, 1);
		crearCaso("B", "b", "Electrodomésticos", EstadoCaso.ABIERTO, 2);
		crearCaso("C", "c", "Electrónica", EstadoCaso.ABIERTO, 3);

		mvc.perform(get("/api/casos/categorias").with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(jsonPath("$", contains("Electrodomésticos", "Electrónica")));
	}
}
