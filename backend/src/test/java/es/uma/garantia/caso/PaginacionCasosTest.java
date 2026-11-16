package es.uma.garantia.caso;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
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

/** Paginación de la lista de casos (HU-16 T5). */
class PaginacionCasosTest extends PruebaIntegracion {

	private static final String EMAIL = "marta@correo.es";

	@Autowired
	private UsuarioRepository usuarios;

	@Autowired
	private CasoRepository casos;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/** Crea los casos "Caso 1" (el más antiguo) a "Caso 23" (el más reciente). */
	@BeforeEach
	void crear23Casos() {
		Usuario marta = usuarios.save(new Usuario("marta", EMAIL, passwordEncoder.encode(PASSWORD)));
		for (int i = 1; i <= 23; i++) {
			casos.save(new Caso("Caso " + i, "Descripción " + i, marta, "Electrónica", EstadoCaso.ABIERTO,
					LocalDateTime.of(2026, 11, 1, 0, 0).plusHours(i)));
		}
		casos.flush();
	}

	@Test
	void porDefectoDevuelveLaPrimeraPaginaCon10Casos() throws Exception {
		mvc.perform(get("/api/casos").with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.contenido", hasSize(10)))
				.andExpect(jsonPath("$.contenido[0].titulo").value("Caso 23"))
				.andExpect(jsonPath("$.pagina").value(0))
				.andExpect(jsonPath("$.tamano").value(10))
				.andExpect(jsonPath("$.totalElementos").value(23))
				.andExpect(jsonPath("$.totalPaginas").value(3));
	}

	@Test
	void laUltimaPaginaTraeLosQueSobran() throws Exception {
		mvc.perform(get("/api/casos").param("pagina", "2").with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(jsonPath("$.contenido[*].titulo", contains("Caso 3", "Caso 2", "Caso 1")));
	}

	@Test
	void unaPaginaQueNoExisteLlegaVacia() throws Exception {
		mvc.perform(get("/api/casos").param("pagina", "9").with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(jsonPath("$.contenido", hasSize(0)))
				.andExpect(jsonPath("$.totalElementos").value(23));
	}

	@Test
	void corrigeLosValoresFueraDeRango() throws Exception {
		mvc.perform(get("/api/casos").param("pagina", "-3").param("tamano", "500").with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(jsonPath("$.pagina").value(0))
				.andExpect(jsonPath("$.tamano").value(50))
				.andExpect(jsonPath("$.contenido", hasSize(23)));

		mvc.perform(get("/api/casos").param("tamano", "0").with(httpBasic(EMAIL, PASSWORD)))
				.andExpect(jsonPath("$.tamano").value(1));
	}

	@Test
	void laPaginacionRespetaLosFiltros() throws Exception {
		mvc.perform(get("/api/casos").param("q", "Caso 1").with(httpBasic(EMAIL, PASSWORD)))
				// "Caso 1" y del "Caso 10" al "Caso 19"
				.andExpect(jsonPath("$.totalElementos").value(11))
				.andExpect(jsonPath("$.totalPaginas").value(2));
	}
}
