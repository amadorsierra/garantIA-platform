package es.uma.garantia.usuario;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class ContrasenasConfigTest {

	private final PasswordEncoder encoder = new ContrasenasConfig().passwordEncoder();

	@Test
	void usaBCryptConCoste12() {
		assertThat(encoder.encode("Clave1234")).startsWith("$2a$12$");
	}

	@Test
	void laMismaContrasenaDaHashesDistintosPorLaSal() {
		assertThat(encoder.encode("Clave1234")).isNotEqualTo(encoder.encode("Clave1234"));
	}

	@Test
	void compruebaLaContrasenaCorrectaYRechazaLaIncorrecta() {
		String hash = encoder.encode("Clave1234");

		assertThat(encoder.matches("Clave1234", hash)).isTrue();
		assertThat(encoder.matches("clave1234", hash)).isFalse();
	}

	@Test
	void sigueAceptandoHashesAntiguosDeCoste10() {
		// Hash de "Demo1234" con coste 10, el de los datos de demostración
		String hashCoste10 = "$2a$10$lCKUHWei2edGhLhdfhv8XO/Wvb5VP1YQ/SlZ32hARSaGE85m1W7ji";

		assertThat(encoder.matches("Demo1234", hashCoste10)).isTrue();
	}
}
