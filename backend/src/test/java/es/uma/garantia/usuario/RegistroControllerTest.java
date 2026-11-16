package es.uma.garantia.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import es.uma.garantia.PruebaIntegracion;

/** Endpoint de registro (HU-01 T4). */
class RegistroControllerTest extends PruebaIntegracion {

	private ResultActions enviar(String json) throws Exception {
		return mvc.perform(post("/api/auth/registro")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json));
	}

	@Test
	void devuelve201ConLaCuentaYElMensajeDeConfirmacion() throws Exception {
		enviar("""
				{"nombreUsuario": "lucia", "email": "Lucia@Correo.es", "password": "Clave1234"}
				""")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.email").value("lucia@correo.es"))
				.andExpect(jsonPath("$.rol").value("USUARIO_AFECTADO"))
				.andExpect(jsonPath("$.mensaje").value("Cuenta creada correctamente"))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.passwordHash").doesNotExist());
	}

	@Test
	void guardaLaContrasenaCifradaYNuncaEnClaro() throws Exception {
		enviar("""
				{"nombreUsuario": "lucia", "email": "lucia@correo.es", "password": "Clave1234"}
				""")
				.andExpect(status().isCreated());

		String hash = jdbc.queryForObject("SELECT password_hash FROM usuarios WHERE email = 'lucia@correo.es'", String.class);
		assertThat(hash).isNotEqualTo("Clave1234").startsWith("$2");
	}

	@Test
	void conCamposVaciosDevuelveElMensajeObligatorioDeCadaCampo() throws Exception {
		enviar("""
				{"nombreUsuario": "", "email": "", "password": ""}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value("Hay datos que corregir"))
				.andExpect(jsonPath("$.errores.nombreUsuario").value("El nombre de usuario es obligatorio"))
				.andExpect(jsonPath("$.errores.email").value("El correo electrónico es obligatorio"))
				.andExpect(jsonPath("$.errores.password").value("La contraseña es obligatoria"));
	}

	@Test
	void sinCamposEnElJsonTambienLosMarcaComoObligatorios() throws Exception {
		enviar("{}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.email").value("El correo electrónico es obligatorio"));
	}

	@Test
	void rechazaUnCorreoSinDominioCompleto() throws Exception {
		enviar("""
				{"nombreUsuario": "lucia", "email": "lucia@correo", "password": "Clave1234"}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.email").value("Introduce un correo electrónico válido"));
	}

	@Test
	void rechazaUnNombreDeUsuarioConEspacios() throws Exception {
		enviar("""
				{"nombreUsuario": "lucia gomez", "email": "lucia@correo.es", "password": "Clave1234"}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.nombreUsuario").exists());
	}

	@Test
	void rechazaUnaContrasenaDemasiadoCorta() throws Exception {
		enviar("""
				{"nombreUsuario": "lucia", "email": "lucia@correo.es", "password": "Cl4ve"}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.password").exists());
	}

	@Test
	void conUnCorreoYaRegistradoDevuelve409ConElErrorEnElCampoEmail() throws Exception {
		String json = """
				{"nombreUsuario": "lucia", "email": "lucia@correo.es", "password": "Clave1234"}
				""";
		enviar(json).andExpect(status().isCreated());

		enviar(json)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.errores.email").value("Ya existe una cuenta con ese correo electrónico"));
	}

	@Test
	void conUnJsonMalFormadoDevuelve400() throws Exception {
		enviar("{esto no es json")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").exists());
	}
}
