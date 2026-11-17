package es.uma.garantia.privacidad;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import es.uma.garantia.PruebaIntegracion;
import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

/** Endpoint de análisis de privacidad (HU-08 T6). */
class PrivacidadControllerTest extends PruebaIntegracion {

	private static final String EMAIL = "marta@correo.es";

	@Autowired
	private UsuarioRepository usuarios;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void crearUsuario() {
		usuarios.saveAndFlush(new Usuario("marta", EMAIL, passwordEncoder.encode(PASSWORD)));
	}

	private ResultActions enviar(String json) throws Exception {
		return mvc.perform(post("/api/privacidad/analizar")
				.with(httpBasic(EMAIL, PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json));
	}

	@Test
	void devuelveLasDeteccionesConSuDescripcion() throws Exception {
		enviar("{\"texto\": \"Llamadme al 612 345 678\"}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.hayDatosSensibles").value(true))
				.andExpect(jsonPath("$.detecciones", hasSize(1)))
				.andExpect(jsonPath("$.detecciones[0].tipo").value("TELEFONO"))
				.andExpect(jsonPath("$.detecciones[0].descripcion").value("Número de teléfono"))
				.andExpect(jsonPath("$.detecciones[0].fragmento").value("612 345 678"));
	}

	@Test
	void sinTextoDevuelve400() throws Exception {
		enviar("{}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.texto").value("El texto es obligatorio"));
	}

	@Test
	void unTextoDemasiadoLargoDevuelve400() throws Exception {
		String largo = "a".repeat(20001);
		enviar("{\"texto\": \"" + largo + "\"}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.texto").exists());
	}

	@Test
	void sinSesionDevuelve401() throws Exception {
		mvc.perform(post("/api/privacidad/analizar")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"texto\": \"Hola\"}"))
				.andExpect(status().isUnauthorized());
	}
}
