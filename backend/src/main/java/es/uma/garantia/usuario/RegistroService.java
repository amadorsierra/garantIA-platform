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

		comprobarQueNoContieneDatosPersonales(datos.password(), datos.nombreUsuario(), email);

		String passwordHash = passwordEncoder.encode(datos.password());
		return usuarios.save(new Usuario(datos.nombreUsuario(), email, passwordHash));
	}

	/**
	 * Una contraseña que contiene el nombre de usuario o el correo es fácil de adivinar
	 * para quien conoce la cuenta (HU-01 T6).
	 */
	private void comprobarQueNoContieneDatosPersonales(String password, String nombreUsuario, String email) {
		String passwordMinusculas = password.toLowerCase();
		String parteLocalEmail = email.substring(0, email.indexOf('@'));

		boolean contieneNombre = passwordMinusculas.contains(nombreUsuario.trim().toLowerCase());
		boolean contieneEmail = parteLocalEmail.length() >= 3 && passwordMinusculas.contains(parteLocalEmail);
		if (contieneNombre || contieneEmail) {
			throw new ContrasenaDebilException();
		}
	}
}
