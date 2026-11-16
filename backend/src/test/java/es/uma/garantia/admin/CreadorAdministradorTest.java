package es.uma.garantia.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.uma.garantia.seguridad.Rol;
import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

class CreadorAdministradorTest {

	private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
	private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

	@Test
	void sinConfiguracionNoCreaNada() {
		new CreadorAdministrador(usuarios, passwordEncoder, "", "").run(null);

		verify(usuarios, never()).save(any());
	}

	@Test
	void creaElAdministradorSiNoExiste() {
		when(passwordEncoder.encode("Secreta123")).thenReturn("hash");

		new CreadorAdministrador(usuarios, passwordEncoder, "Admin@Garantia.es", "Secreta123").run(null);

		ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarios).save(guardado.capture());
		assertThat(guardado.getValue().getEmail()).isEqualTo("admin@garantia.es");
		assertThat(guardado.getValue().getRol()).isEqualTo(Rol.ADMINISTRADOR);
		assertThat(guardado.getValue().getPasswordHash()).isEqualTo("hash");
	}

	@Test
	void siYaExisteNoLoVuelveACrear() {
		when(usuarios.existsByEmail("admin@garantia.es")).thenReturn(true);

		new CreadorAdministrador(usuarios, passwordEncoder, "admin@garantia.es", "Secreta123").run(null);

		verify(usuarios, never()).save(any());
	}
}
