package es.uma.garantia.privacidad;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ValidadoresControlTest {

	@Test
	void dni() {
		assertThat(ValidadoresControl.dniValido("12345678Z")).isTrue();
		assertThat(ValidadoresControl.dniValido("12345678-z")).isTrue();
		assertThat(ValidadoresControl.dniValido("87654321X")).isTrue();
		assertThat(ValidadoresControl.dniValido("12345678A")).isFalse();
		assertThat(ValidadoresControl.dniValido("1234567Z")).isFalse();
	}

	@Test
	void nie() {
		assertThat(ValidadoresControl.nieValido("X1234567L")).isTrue();
		assertThat(ValidadoresControl.nieValido("Y-1234567-X")).isTrue();
		assertThat(ValidadoresControl.nieValido("X1234567A")).isFalse();
	}

	@Test
	void iban() {
		assertThat(ValidadoresControl.ibanValido("ES91 2100 0418 4502 0005 1332")).isTrue();
		assertThat(ValidadoresControl.ibanValido("ES7921000813610123456789")).isTrue();
		assertThat(ValidadoresControl.ibanValido("ES12 3456 7890 1234 5678 9012")).isFalse();
		assertThat(ValidadoresControl.ibanValido("ES91")).isFalse();
	}

	@Test
	void tarjeta() {
		assertThat(ValidadoresControl.luhnValido("4111 1111 1111 1111")).isTrue();
		assertThat(ValidadoresControl.luhnValido("5500-0000-0000-0004")).isTrue();
		assertThat(ValidadoresControl.luhnValido("4111111111111112")).isFalse();
		assertThat(ValidadoresControl.luhnValido("1234")).isFalse();
	}
}
