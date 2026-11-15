package es.uma.garantia.seguridad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

class UsuarioDetailsServiceTest {

	private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
	private final UsuarioDetailsService servicio = new UsuarioDetailsService(usuarios);

	private List<String> nombres(UserDetails detalles) {
		List<String> nombres = new ArrayList<>();
		for (GrantedAuthority autoridad : detalles.getAuthorities()) {
			nombres.add(autoridad.getAuthority());
		}
		return nombres;
	}

	@Test
	void cargaElUsuarioConSuRolYSusPermisos() {
		Usuario usuario = new Usuario("lucia", "lucia@correo.es", "hash");
		when(usuarios.findByEmail("lucia@correo.es")).thenReturn(Optional.of(usuario));

		UserDetails detalles = servicio.loadUserByUsername("lucia@correo.es");

		assertThat(detalles.getUsername()).isEqualTo("lucia@correo.es");
		assertThat(detalles.getPassword()).isEqualTo("hash");
		assertThat(nombres(detalles)).containsExactlyInAnyOrder(
				"ROLE_USUARIO_AFECTADO", "CASOS_LEER", "CASOS_PUBLICAR", "RESPUESTAS_PUBLICAR");
	}

	@Test
	void buscaElCorreoNormalizado() {
		Usuario usuario = new Usuario("lucia", "lucia@correo.es", "hash");
		when(usuarios.findByEmail("lucia@correo.es")).thenReturn(Optional.of(usuario));

		UserDetails detalles = servicio.loadUserByUsername("  LUCIA@Correo.es ");

		assertThat(detalles.getUsername()).isEqualTo("lucia@correo.es");
	}

	@Test
	void fallaSiElCorreoNoExiste() {
		when(usuarios.findByEmail("nadie@correo.es")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> servicio.loadUserByUsername("nadie@correo.es"))
				.isInstanceOf(UsernameNotFoundException.class);
	}
}
