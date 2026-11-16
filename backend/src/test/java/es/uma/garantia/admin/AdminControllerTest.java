package es.uma.garantia.admin;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import es.uma.garantia.PruebaIntegracion;
import es.uma.garantia.seguridad.Rol;
import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

/** Endpoints de gestión de roles (HU-26 T5). */
class AdminControllerTest extends PruebaIntegracion {

	private static final String ADMIN = "admin@garantia.es";

	@Autowired
	private UsuarioRepository usuarios;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Usuario admin;
	private Usuario lucia;

	@BeforeEach
	void crearCuentas() {
		admin = new Usuario("admin", ADMIN, passwordEncoder.encode(PASSWORD));
		admin.cambiarRol(Rol.ADMINISTRADOR);
		admin = usuarios.save(admin);
		lucia = usuarios.saveAndFlush(new Usuario("lucia", "lucia@correo.es", passwordEncoder.encode(PASSWORD)));
	}

	private ResultActions cambiarRol(Long id, String cuerpo) throws Exception {
		return mvc.perform(put("/api/admin/usuarios/{id}/rol", id)
				.with(httpBasic(ADMIN, PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content(cuerpo));
	}

	@Test
	void elAdministradorVeTodasLasCuentasConSuRol() throws Exception {
		mvc.perform(get("/api/admin/usuarios").with(httpBasic(ADMIN, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].email").value(ADMIN))
				.andExpect(jsonPath("$[1].rol").value("USUARIO_AFECTADO"));
	}

	@Test
	void cambiaElRolYQuedaEnLaAuditoria() throws Exception {
		cambiarRol(lucia.getId(), "{\"rol\": \"USUARIO_AYUDANTE\"}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.rol").value("USUARIO_AYUDANTE"));

		mvc.perform(get("/api/admin/auditoria-roles").with(httpBasic(ADMIN, PASSWORD)))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].rolNuevo").value("USUARIO_AYUDANTE"));
	}

	@Test
	void sinRolDevuelve400ConElError() throws Exception {
		cambiarRol(lucia.getId(), "{}")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.rol").value("Indica el nuevo rol"));
	}

	@Test
	void unRolQueNoExisteDevuelve400() throws Exception {
		cambiarRol(lucia.getId(), "{\"rol\": \"SUPERUSUARIO\"}")
				.andExpect(status().isBadRequest());
	}

	@Test
	void unaCuentaQueNoExisteDevuelve404() throws Exception {
		cambiarRol(987654L, "{\"rol\": \"MODERADOR\"}")
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.mensaje").value("El usuario no existe"));
	}

	@Test
	void elAdministradorNoPuedeCambiarSuPropioRol() throws Exception {
		cambiarRol(admin.getId(), "{\"rol\": \"USUARIO_AFECTADO\"}")
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensaje").value("No puedes cambiar tu propio rol"));
	}

	@Test
	void quienNoEsAdministradorNoVeLaListaDeCuentas() throws Exception {
		mvc.perform(get("/api/admin/usuarios").with(httpBasic("lucia@correo.es", PASSWORD)))
				.andExpect(status().isForbidden());
	}
}
