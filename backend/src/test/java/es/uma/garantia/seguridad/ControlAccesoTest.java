package es.uma.garantia.seguridad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.uma.garantia.PruebaIntegracion;
import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

/** Control de acceso a los endpoints (HU-26 T4). */
class ControlAccesoTest extends PruebaIntegracion {

	@Autowired
	private UsuarioRepository usuarios;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void crearUsuario() {
		usuarios.saveAndFlush(new Usuario("lucia", "lucia@correo.es", passwordEncoder.encode(PASSWORD)));
	}

	@Test
	void sinSesionDevuelve401ConMensajeYSinVentanaDelNavegador() throws Exception {
		mvc.perform(get("/api/auth/yo"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().doesNotExist("WWW-Authenticate"))
				.andExpect(jsonPath("$.mensaje").value(RespuestasSeguridad.MENSAJE_SIN_SESION));
	}

	@Test
	void conCredencialesCorrectasDevuelveLaCuenta() throws Exception {
		mvc.perform(get("/api/auth/yo").with(httpBasic("lucia@correo.es", PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("lucia@correo.es"))
				.andExpect(jsonPath("$.rol").value("USUARIO_AFECTADO"))
				.andExpect(jsonPath("$.permisos", hasItem("CASOS_LEER")))
				.andExpect(jsonPath("$.passwordHash").doesNotExist());
	}

	@Test
	void elCorreoNoDistingueMayusculasAlEntrar() throws Exception {
		mvc.perform(get("/api/auth/yo").with(httpBasic("LUCIA@correo.es", PASSWORD)))
				.andExpect(status().isOk());
	}

	@Test
	void conContrasenaIncorrectaDevuelve401() throws Exception {
		mvc.perform(get("/api/auth/yo").with(httpBasic("lucia@correo.es", "Incorrecta99")))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value(RespuestasSeguridad.MENSAJE_SIN_SESION));
	}

	@Test
	void sinElPermisoNecesarioDevuelve403ConMensaje() throws Exception {
		mvc.perform(get("/api/admin/usuarios").with(httpBasic("lucia@correo.es", PASSWORD)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.mensaje").value(RespuestasSeguridad.MENSAJE_ACCESO_DENEGADO));
	}

	@Test
	void elRegistroEsPublico() throws Exception {
		int estado = mvc.perform(post("/api/auth/registro")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andReturn().getResponse().getStatus();

		assertThat(estado).isNotIn(401, 403);
	}

	@Test
	void lasRutasNoListadasExigenSesion() throws Exception {
		mvc.perform(get("/api/ruta-que-no-existe"))
				.andExpect(status().isUnauthorized());
	}
}
