package es.uma.garantia;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base de las pruebas de integración y de aceptación.
 *
 * Arranca la aplicación completa contra un PostgreSQL de Testcontainers y deshace
 * los cambios en la base de datos al terminar cada prueba (@Transactional), así
 * que cada prueba empieza con la base de datos vacía.
 *
 * Las pruebas de aceptación solo usan la API HTTP y SQL, no clases de producción,
 * para poder escribirse antes de implementar (tareas T0).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public abstract class PruebaIntegracion {

	protected static final String PASSWORD = "Clave1234";

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected JdbcTemplate jdbc;

	/** Registra una cuenta nueva a través de la API (rol por defecto: Usuario Afectado). */
	protected void registrar(String nombreUsuario, String email) throws Exception {
		String json = """
				{"nombreUsuario": "%s", "email": "%s", "password": "%s"}
				""".formatted(nombreUsuario, email, PASSWORD);

		mvc.perform(post("/api/auth/registro")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				.andExpect(status().isCreated());
	}

	/** Registra una cuenta y le pone directamente el rol indicado en la base de datos. */
	protected void registrarConRol(String nombreUsuario, String email, String rol) throws Exception {
		registrar(nombreUsuario, email);
		jdbc.update("UPDATE usuarios SET rol = ? WHERE email = ?", rol, email);
	}

	protected Long idDe(String email) {
		return jdbc.queryForObject("SELECT id FROM usuarios WHERE email = ?", Long.class, email);
	}

	protected int contarUsuarios() {
		return jdbc.queryForObject("SELECT COUNT(*) FROM usuarios", Integer.class);
	}
}
