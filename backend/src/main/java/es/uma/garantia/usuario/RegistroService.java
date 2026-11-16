package es.uma.garantia.usuario;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta de cuentas nuevas (HU-01 T4). */
@Service
public class RegistroService {

	private final UsuarioRepository usuarios;
	private final PasswordEncoder passwordEncoder;

	public RegistroService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder) {
		this.usuarios = usuarios;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Usuario registrar(RegistroRequest datos) {
		String email = Usuario.normalizarEmail(datos.email());
		if (usuarios.existsByEmail(email)) {
			throw new EmailYaRegistradoException();
		}

		String passwordHash = passwordEncoder.encode(datos.password());
		return usuarios.save(new Usuario(datos.nombreUsuario(), email, passwordHash));
	}
}
