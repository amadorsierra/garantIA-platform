package es.uma.garantia.aceptacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import es.uma.garantia.PruebaIntegracion;

/**
 * HU-01 — Creación de cuenta.
 * Pruebas de aceptación (T0): un escenario o más por criterio de aceptación.
 */
@Tag("aceptacion")
@DisplayName("HU-01 — Creación de cuenta")
class HU01CreacionCuentaAceptacionTest extends PruebaIntegracion {

	private ResultActions registrarCon(String nombreUsuario, String email, String password) throws Exception {
		String json = """
				{"nombreUsuario": "%s", "email": "%s", "password": "%s"}
				""".formatted(nombreUsuario, email, password);

		return mvc.perform(post("/api/auth/registro")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json));
	}

	// CA1. El usuario puede registrarse con nombre de usuario, correo y contraseña.

	@Test
	@DisplayName("CA1 · Registro con nombre de usuario, correo y contraseña")
	void ca1_registroCorrecto() throws Exception {
		registrarCon("lucia", "lucia@correo.es", PASSWORD)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nombreUsuario").value("lucia"))
				.andExpect(jsonPath("$.email").value("lucia@correo.es"))
				.andExpect(jsonPath("$.password").doesNotExist());

		assertThat(contarUsuarios()).isEqualTo(1);
	}

	// CA2. Se comprueban los campos obligatorios y el formato del correo.

	@Test
	@DisplayName("CA2 · Los campos obligatorios no pueden estar vacíos")
	void ca2_camposObligatorios() throws Exception {
		registrarCon("", "", "")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.nombreUsuario").exists())
				.andExpect(jsonPath("$.errores.email").exists())
				.andExpect(jsonPath("$.errores.password").exists());
	}

	@Test
	@DisplayName("CA2 · El correo debe tener un formato válido")
	void ca2_formatoCorreo() throws Exception {
		registrarCon("lucia", "esto-no-es-un-correo", PASSWORD)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.email").exists());
	}

	// CA3. No se permite registrar un correo ya registrado.

	@Test
	@DisplayName("CA3 · No se puede crear una cuenta con un correo ya registrado")
	void ca3_correoDuplicado() throws Exception {
		registrarCon("lucia", "lucia@correo.es", PASSWORD).andExpect(status().isCreated());

		registrarCon("otra", "lucia@correo.es", PASSWORD)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.errores.email").exists());

		assertThat(contarUsuarios()).isEqualTo(1);
	}

	@Test
	@DisplayName("CA3 · El correo duplicado se detecta aunque cambien las mayúsculas")
	void ca3_correoDuplicadoMayusculas() throws Exception {
		registrarCon("lucia", "lucia@correo.es", PASSWORD).andExpect(status().isCreated());

		registrarCon("otra", "LUCIA@Correo.es", PASSWORD)
				.andExpect(status().isConflict());
	}

	// CA4. Si algún dato no es válido, se indica qué corregir sin crear la cuenta.

	@Test
	@DisplayName("CA4 · Con datos no válidos se indica qué corregir y no se crea la cuenta")
	void ca4_datosNoValidosNoCreanCuenta() throws Exception {
		registrarCon("lucia", "lucia@correo.es", "123")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").exists())
				.andExpect(jsonPath("$.errores.password").isString());

		assertThat(contarUsuarios()).isZero();
	}

	// CA5. Cuando el registro se completa, el sistema lo confirma.

	@Test
	@DisplayName("CA5 · El sistema confirma la creación de la cuenta")
	void ca5_confirmacion() throws Exception {
		registrarCon("lucia", "lucia@correo.es", PASSWORD)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.mensaje").value("Cuenta creada correctamente"));
	}

	// CA6. El usuario puede acceder con la cuenta creada y usar publicación y participación.

	@Test
	@DisplayName("CA6 · Se puede entrar con la cuenta creada y tiene permisos de publicación y participación")
	void ca6_accesoConCuentaCreada() throws Exception {
		registrarCon("lucia", "lucia@correo.es", PASSWORD).andExpect(status().isCreated());

		mvc.perform(get("/api/auth/yo").with(httpBasic("lucia@correo.es", PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombreUsuario").value("lucia"))
				.andExpect(jsonPath("$.permisos", hasItems("CASOS_PUBLICAR", "RESPUESTAS_PUBLICAR")));
	}

	@Test
	@DisplayName("CA6 · Con una contraseña incorrecta no se puede entrar")
	void ca6_passwordIncorrecta() throws Exception {
		registrarCon("lucia", "lucia@correo.es", PASSWORD).andExpect(status().isCreated());

		mvc.perform(get("/api/auth/yo").with(httpBasic("lucia@correo.es", "Incorrecta99")))
				.andExpect(status().isUnauthorized());
	}
}
