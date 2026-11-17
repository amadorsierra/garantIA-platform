package es.uma.garantia.seguridad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import es.uma.garantia.PruebaIntegracion;
import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

/**
 * HU-26 T6 · Verifica que nadie accede a funciones no autorizadas.
 *
 * Recorre la matriz de docs/hu-26/T2-matriz-permisos.md: cada rol contra cada endpoint protegido.
 * Si alguien cambia SecurityConfig o la matriz de Rol y abre un acceso por error, esta prueba falla.
 */
class VerificacionAccesosTest extends PruebaIntegracion {

	@Autowired
	private UsuarioRepository usuarios;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/** Cuenta sobre la que se prueba el cambio de rol. */
	private Long idObjetivo;

	/** Un endpoint protegido y el permiso que exige. */
	private enum Endpoint {
		LISTA_CASOS(Permiso.CASOS_LEER),
		CATEGORIAS(Permiso.CASOS_LEER),
		ANALIZAR_PRIVACIDAD(Permiso.CASOS_PUBLICAR),
		LISTA_USUARIOS(Permiso.ROLES_GESTIONAR),
		CAMBIAR_ROL(Permiso.ROLES_GESTIONAR),
		AUDITORIA(Permiso.AUDITORIA_CONSULTAR);

		final Permiso permiso;

		Endpoint(Permiso permiso) {
			this.permiso = permiso;
		}
	}

	@BeforeEach
	void crearCuentas() {
		for (Rol rol : Rol.values()) {
			Usuario usuario = new Usuario(rol.name().toLowerCase().replace("_", "-"), correoDe(rol),
					passwordEncoder.encode(PASSWORD));
			usuario.cambiarRol(rol);
			usuarios.save(usuario);
		}
		idObjetivo = usuarios.saveAndFlush(new Usuario("objetivo", "objetivo@garantia.test",
				passwordEncoder.encode(PASSWORD))).getId();
	}

	private static String correoDe(Rol rol) {
		return rol.name().toLowerCase() + "@garantia.test";
	}

	private MockHttpServletRequestBuilder peticion(Endpoint endpoint) {
		return switch (endpoint) {
			case LISTA_CASOS -> get("/api/casos");
			case CATEGORIAS -> get("/api/casos/categorias");
			case ANALIZAR_PRIVACIDAD -> post("/api/privacidad/analizar")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"texto\": \"Hola\"}");
			case LISTA_USUARIOS -> get("/api/admin/usuarios");
			case CAMBIAR_ROL -> put("/api/admin/usuarios/{id}/rol", idObjetivo)
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"rol\": \"USUARIO_AYUDANTE\"}");
			case AUDITORIA -> get("/api/admin/auditoria-roles");
		};
	}

	/** Todas las combinaciones rol × endpoint. */
	static List<Arguments> combinaciones() {
		List<Arguments> combinaciones = new ArrayList<>();
		for (Rol rol : Rol.values()) {
			for (Endpoint endpoint : Endpoint.values()) {
				combinaciones.add(Arguments.of(rol, endpoint));
			}
		}
		return combinaciones;
	}

	@ParameterizedTest(name = "{0} en {1}")
	@MethodSource("combinaciones")
	void cadaRolSoloAccedeALoQueLePermiteLaMatriz(Rol rol, Endpoint endpoint) throws Exception {
		int estado = mvc.perform(peticion(endpoint).with(httpBasic(correoDe(rol), PASSWORD)))
				.andReturn().getResponse().getStatus();

		if (rol.tienePermiso(endpoint.permiso)) {
			assertThat(estado).as("%s debería poder usar %s", rol, endpoint).isNotIn(401, 403);
		} else {
			assertThat(estado).as("%s no debería poder usar %s", rol, endpoint).isEqualTo(403);
		}
	}

	@ParameterizedTest(name = "sin sesión en {0}")
	@MethodSource("endpoints")
	void sinSesionNingunEndpointProtegidoResponde(Endpoint endpoint) throws Exception {
		mvc.perform(peticion(endpoint))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensaje").value(RespuestasSeguridad.MENSAJE_SIN_SESION));
	}

	static Endpoint[] endpoints() {
		return Endpoint.values();
	}

	@Test
	void unaCuentaQueNoExisteNoEntra() throws Exception {
		mvc.perform(get("/api/casos").with(httpBasic("nadie@garantia.test", PASSWORD)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void alQuitarleElRolDeAdministradorPierdeElAccesoEnLaSiguientePeticion() throws Exception {
		String admin = correoDe(Rol.ADMINISTRADOR);
		mvc.perform(get("/api/admin/usuarios").with(httpBasic(admin, PASSWORD)))
				.andExpect(status().isOk());

		jdbc.update("UPDATE usuarios SET rol = 'USUARIO_AFECTADO' WHERE email = ?", admin);
		// La prueba corre en una sola transacción: sin esto, JPA devolvería la cuenta que ya
		// tiene en memoria (con el rol antiguo) en lugar de leerla otra vez de la base de datos.
		// En la aplicación real cada petición tiene su propia transacción y lee el rol actual.
		entityManager.clear();

		mvc.perform(get("/api/admin/usuarios").with(httpBasic(admin, PASSWORD)))
				.andExpect(status().isForbidden());
	}

	@Test
	void unUsuarioNoPuedeDarseAsiMismoElRolDeAdministrador() throws Exception {
		Long idPropio = idDe(correoDe(Rol.USUARIO_AFECTADO));

		mvc.perform(put("/api/admin/usuarios/{id}/rol", idPropio)
				.with(httpBasic(correoDe(Rol.USUARIO_AFECTADO), PASSWORD))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"rol\": \"ADMINISTRADOR\"}"))
				.andExpect(status().isForbidden());

		String rol = jdbc.queryForObject("SELECT rol FROM usuarios WHERE id = ?", String.class, idPropio);
		assertThat(rol).isEqualTo("USUARIO_AFECTADO");
	}

	@Test
	void laRespuestaDeLaCuentaNuncaIncluyeLaContrasena() throws Exception {
		mvc.perform(get("/api/auth/yo").with(httpBasic(correoDe(Rol.USUARIO_AFECTADO), PASSWORD)))
				.andExpect(jsonPath("$.password").doesNotExist())
				.andExpect(jsonPath("$.passwordHash").doesNotExist());

		mvc.perform(get("/api/admin/usuarios").with(httpBasic(correoDe(Rol.ADMINISTRADOR), PASSWORD)))
				.andExpect(jsonPath("$[*].passwordHash").isEmpty());
	}
}
