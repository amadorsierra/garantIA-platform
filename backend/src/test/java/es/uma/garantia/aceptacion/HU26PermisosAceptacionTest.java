package es.uma.garantia.aceptacion;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import es.uma.garantia.PruebaIntegracion;

/**
 * HU-26 — Gestión de permisos según el rol.
 * Pruebas de aceptación (T0): un escenario o más por criterio de aceptación.
 */
@Tag("aceptacion")
@DisplayName("HU-26 — Gestión de permisos según el rol")
class HU26PermisosAceptacionTest extends PruebaIntegracion {

	private static final String ADMIN = "admin@garantia.test";
	private static final String AFECTADO = "afectado@garantia.test";
	private static final String AYUDANTE = "ayudante@garantia.test";
	private static final String MODERADOR = "moderador@garantia.test";

	@BeforeEach
	void crearUsuarios() throws Exception {
		registrarConRol("admin", ADMIN, "ADMINISTRADOR");
		registrar("afectado", AFECTADO);
		registrarConRol("ayudante", AYUDANTE, "USUARIO_AYUDANTE");
		registrarConRol("moderador", MODERADOR, "MODERADOR");
	}

	private String cuerpoRol(String rol) {
		return """
				{"rol": "%s"}
				""".formatted(rol);
	}

	// CA1. Solo el Administrador del sistema puede asignar, modificar o revocar roles.

	@Test
	@DisplayName("CA1 · El administrador puede cambiar el rol de una cuenta")
	void ca1_administradorCambiaRol() throws Exception {
		mvc.perform(put("/api/admin/usuarios/{id}/rol", idDe(AFECTADO))
				.with(httpBasic(ADMIN, PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content(cuerpoRol("USUARIO_AYUDANTE")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.rol").value("USUARIO_AYUDANTE"));
	}

	@Test
	@DisplayName("CA1 · Un usuario que no es administrador no puede cambiar roles")
	void ca1_noAdministradorNoCambiaRol() throws Exception {
		mvc.perform(put("/api/admin/usuarios/{id}/rol", idDe(AYUDANTE))
				.with(httpBasic(MODERADOR, PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content(cuerpoRol("ADMINISTRADOR")))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("CA1 · Sin iniciar sesión no se pueden cambiar roles")
	void ca1_anonimoNoCambiaRol() throws Exception {
		mvc.perform(put("/api/admin/usuarios/{id}/rol", idDe(AFECTADO))
				.contentType(MediaType.APPLICATION_JSON)
				.content(cuerpoRol("ADMINISTRADOR")))
				.andExpect(status().isUnauthorized());
	}

	// CA2. Cada rol tiene definidos sus permisos específicos.

	@Test
	@DisplayName("CA2 · Cada rol tiene sus permisos definidos")
	void ca2_permisosPorRol() throws Exception {
		mvc.perform(get("/api/auth/yo").with(httpBasic(AFECTADO, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.rol").value("USUARIO_AFECTADO"))
				.andExpect(jsonPath("$.permisos", hasItems("CASOS_LEER", "CASOS_PUBLICAR")))
				.andExpect(jsonPath("$.permisos", not(hasItem("ROLES_GESTIONAR"))));

		mvc.perform(get("/api/auth/yo").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(jsonPath("$.permisos", hasItems("CASOS_LEER", "RESPUESTAS_PUBLICAR")))
				.andExpect(jsonPath("$.permisos", not(hasItem("CASOS_PUBLICAR"))));

		mvc.perform(get("/api/auth/yo").with(httpBasic(MODERADOR, PASSWORD)))
				.andExpect(jsonPath("$.permisos", hasItems("CASOS_LEER", "CONTENIDO_MODERAR")))
				.andExpect(jsonPath("$.permisos", not(hasItem("ROLES_GESTIONAR"))));

		mvc.perform(get("/api/auth/yo").with(httpBasic(ADMIN, PASSWORD)))
				.andExpect(jsonPath("$.permisos", hasItems("ROLES_GESTIONAR", "AUDITORIA_CONSULTAR")));
	}

	// CA3. Se restringe el acceso a funcionalidades y APIs sin el rol necesario.

	@Test
	@DisplayName("CA3 · Sin el rol necesario se devuelve un mensaje de acceso denegado")
	void ca3_accesoDenegadoConMensaje() throws Exception {
		mvc.perform(get("/api/admin/auditoria-roles").with(httpBasic(AYUDANTE, PASSWORD)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.mensaje").value("Acceso denegado: no tienes permiso para esta acción"));
	}

	@Test
	@DisplayName("CA3 · Sin iniciar sesión no se accede a la API protegida")
	void ca3_anonimoNoAccede() throws Exception {
		mvc.perform(get("/api/casos"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").exists());
	}

	// CA4. Cualquier cambio de rol se aplica de forma inmediata.

	@Test
	@DisplayName("CA4 · El cambio de rol se aplica en la siguiente petición, sin volver a entrar")
	void ca4_cambioInmediato() throws Exception {
		mvc.perform(put("/api/admin/usuarios/{id}/rol", idDe(AFECTADO))
				.with(httpBasic(ADMIN, PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content(cuerpoRol("MODERADOR")))
				.andExpect(status().isOk());

		mvc.perform(get("/api/auth/yo").with(httpBasic(AFECTADO, PASSWORD)))
				.andExpect(jsonPath("$.rol").value("MODERADOR"))
				.andExpect(jsonPath("$.permisos", hasItem("CONTENIDO_MODERAR")));
	}

	// CA5. Se registra en la bitácora de auditoría cada modificación de rol.

	@Test
	@DisplayName("CA5 · Cada cambio de rol queda registrado en la auditoría")
	void ca5_cambioRegistradoEnAuditoria() throws Exception {
		mvc.perform(put("/api/admin/usuarios/{id}/rol", idDe(AFECTADO))
				.with(httpBasic(ADMIN, PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content(cuerpoRol("USUARIO_AYUDANTE")))
				.andExpect(status().isOk());

		mvc.perform(get("/api/admin/auditoria-roles").with(httpBasic(ADMIN, PASSWORD)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].emailUsuario").value(AFECTADO))
				.andExpect(jsonPath("$[0].rolAnterior").value("USUARIO_AFECTADO"))
				.andExpect(jsonPath("$[0].rolNuevo").value("USUARIO_AYUDANTE"))
				.andExpect(jsonPath("$[0].realizadoPor").value(ADMIN))
				.andExpect(jsonPath("$[0].fecha").exists());
	}
}
