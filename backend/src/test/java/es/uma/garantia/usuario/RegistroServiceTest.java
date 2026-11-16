package es.uma.garantia.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.uma.garantia.seguridad.Rol;

class RegistroServiceTest {

	private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
	private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
	private final RegistroService servicio = new RegistroService(usuarios, passwordEncoder);

	@BeforeEach
	void configurarMocks() {
		when(passwordEncoder.encode("Clave1234")).thenReturn("hash-de-Clave1234");
		when(usuarios.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
	}

	@Test
	void guardaLaCuentaConLaContrasenaCifradaYElRolPorDefecto() {
		Usuario usuario = servicio.registrar(new RegistroRequest("lucia", "lucia@correo.es", "Clave1234"));

		assertThat(usuario.getNombreUsuario()).isEqualTo("lucia");
		assertThat(usuario.getPasswordHash()).isEqualTo("hash-de-Clave1234");
		assertThat(usuario.getRol()).isEqualTo(Rol.USUARIO_AFECTADO);
	}

	@Test
	void normalizaElCorreoAntesDeComprobarSiExiste() {
		servicio.registrar(new RegistroRequest("lucia", " Lucia@Correo.ES ", "Clave1234"));

		verify(usuarios).existsByEmail("lucia@correo.es");
	}

	@Test
	void rechazaUnCorreoYaRegistradoSinGuardarNada() {
		when(usuarios.existsByEmail("lucia@correo.es")).thenReturn(true);

		assertThatThrownBy(() -> servicio.registrar(new RegistroRequest("otra", "lucia@correo.es", "Clave1234")))
				.isInstanceOf(EmailYaRegistradoException.class)
				.hasMessage("Ya existe una cuenta con ese correo electrónico");

		verify(usuarios, never()).save(any());
	}

	@Test
	void rechazaUnaContrasenaQueContieneElNombreDeUsuario() {
		assertThatThrownBy(() -> servicio.registrar(new RegistroRequest("lucia", "x@correo.es", "Lucia2026")))
				.isInstanceOf(ContrasenaDebilException.class);

		verify(usuarios, never()).save(any());
	}
}
