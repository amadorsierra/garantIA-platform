package es.uma.garantia.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.uma.garantia.seguridad.Rol;
import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

/**
 * Crea la primera cuenta de Administrador al arrancar, si se configura con las variables
 * GARANTIA_ADMIN_EMAIL y GARANTIA_ADMIN_PASSWORD. Sin ellas no hace nada.
 *
 * Sin esto no habría forma de tener un administrador: solo un administrador puede asignar roles (CA1).
 */
@Component
public class CreadorAdministrador implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(CreadorAdministrador.class);

	private final UsuarioRepository usuarios;
	private final PasswordEncoder passwordEncoder;
	private final String email;
	private final String password;

	public CreadorAdministrador(UsuarioRepository usuarios, PasswordEncoder passwordEncoder,
			@Value("${garantia.admin.email:}") String email,
			@Value("${garantia.admin.password:}") String password) {
		this.usuarios = usuarios;
		this.passwordEncoder = passwordEncoder;
		this.email = email;
		this.password = password;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments argumentos) {
		if (email.isBlank() || password.isBlank()) {
			return;
		}
		String emailNormalizado = Usuario.normalizarEmail(email);
		if (usuarios.existsByEmail(emailNormalizado)) {
			return;
		}

		Usuario administrador = new Usuario("admin", emailNormalizado, passwordEncoder.encode(password));
		administrador.cambiarRol(Rol.ADMINISTRADOR);
		usuarios.save(administrador);
		log.info("Creada la cuenta de administrador {}", emailNormalizado);
	}
}
