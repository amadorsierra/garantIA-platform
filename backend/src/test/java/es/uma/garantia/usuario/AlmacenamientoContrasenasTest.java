package es.uma.garantia.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import es.uma.garantia.PruebaIntegracion;

/** Almacenamiento seguro de contraseñas, de punta a punta (HU-01 T6). */
class AlmacenamientoContrasenasTest extends PruebaIntegracion {

	private ResultActions registrarCon(String nombreUsuario, String email, String password) throws Exception {
		return mvc.perform(post("/api/auth/registro")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"nombreUsuario": "%s", "email": "%s", "password": "%s"}
						""".formatted(nombreUsuario, email, password)));
	}

	@Test
	void laContrasenaSeGuardaConBCryptYNoApareceEnNingunaColumna() throws Exception {
		registrarCon("lucia", "lucia@correo.es", "Clave1234").andExpect(status().isCreated());

		Map<String, Object> fila = jdbc.queryForMap("SELECT * FROM usuarios WHERE email = 'lucia@correo.es'");
		assertThat((String) fila.get("password_hash")).startsWith("$2a$12$");
		for (Object valor : fila.values()) {
			assertThat(String.valueOf(valor)).doesNotContain("Clave1234");
		}
	}

	@Test
	void seEntraConLaContrasenaGuardada() throws Exception {
		registrarCon("lucia", "lucia@correo.es", "Clave1234").andExpect(status().isCreated());

		mvc.perform(get("/api/auth/yo").with(httpBasic("lucia@correo.es", "Clave1234")))
				.andExpect(status().isOk());
	}

	@Test
	void rechazaContrasenasSinLetrasOSinNumeros() throws Exception {
		for (String password : List.of("12345678", "solamenteletras")) {
			registrarCon("lucia", "lucia@correo.es", password)
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.errores.password").exists());
		}
		assertThat(contarUsuarios()).isZero();
	}

	@Test
	void rechazaUnaContrasenaQueContieneElNombreDeUsuario() throws Exception {
		registrarCon("lucia", "otra@correo.es", "Lucia2026")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.password")
						.value("La contraseña no puede contener tu nombre de usuario ni tu correo"));
	}

	@Test
	void rechazaUnaContrasenaQueContieneElCorreo() throws Exception {
		registrarCon("pepe", "martagomez@correo.es", "martagomez1")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.password").exists());
	}

	@Test
	void unaContrasenaDe64CaracteresEsValidaYUnaDe65No() throws Exception {
		String de64 = "a1" + "x".repeat(62);
		registrarCon("lucia", "lucia@correo.es", de64).andExpect(status().isCreated());

		String de65 = "a1" + "x".repeat(63);
		registrarCon("marta", "marta@correo.es", de65).andExpect(status().isBadRequest());
	}
}
