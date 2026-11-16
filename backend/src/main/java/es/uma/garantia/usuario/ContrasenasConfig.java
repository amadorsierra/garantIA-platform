package es.uma.garantia.usuario;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Almacenamiento seguro de contraseñas (HU-01 T6).
 *
 * - Se guardan con BCrypt: un hash lento, con sal aleatoria distinta en cada contraseña.
 * - Coste 12 (2^12 iteraciones): unos 250 ms por hash, suficiente para frenar ataques
 *   de fuerza bruta sin que el registro o el inicio de sesión se noten lentos.
 * - Los hashes antiguos de coste 10 (datos de demostración) siguen funcionando,
 *   porque el coste va guardado dentro de cada hash.
 */
@Configuration
public class ContrasenasConfig {

	public static final int COSTE_BCRYPT = 12;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(COSTE_BCRYPT);
	}
}
