package es.uma.garantia.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import es.uma.garantia.seguridad.AuditoriaRol;
import es.uma.garantia.seguridad.AuditoriaRolRepository;
import es.uma.garantia.seguridad.Rol;
import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

class GestionRolesServiceTest {

	private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
	private final AuditoriaRolRepository auditoria = mock(AuditoriaRolRepository.class);
	private final GestionRolesService servicio = new GestionRolesService(usuarios, auditoria);

	private Usuario lucia() {
		Usuario usuario = new Usuario("lucia", "lucia@correo.es", "hash");
		when(usuarios.findById(7L)).thenReturn(Optional.of(usuario));
		return usuario;
	}

	@Test
	void cambiaElRolYLoAnotaEnLaAuditoria() {
		Usuario usuario = lucia();

		servicio.cambiarRol(7L, Rol.MODERADOR, "Admin@Garantia.es");

		assertThat(usuario.getRol()).isEqualTo(Rol.MODERADOR);
		verify(usuarios).save(usuario);

		ArgumentCaptor<AuditoriaRol> entrada = ArgumentCaptor.forClass(AuditoriaRol.class);
		verify(auditoria).save(entrada.capture());
		assertThat(entrada.getValue().getEmailUsuario()).isEqualTo("lucia@correo.es");
		assertThat(entrada.getValue().getRolAnterior()).isEqualTo(Rol.USUARIO_AFECTADO);
		assertThat(entrada.getValue().getRolNuevo()).isEqualTo(Rol.MODERADOR);
		assertThat(entrada.getValue().getRealizadoPor()).isEqualTo("admin@garantia.es");
		assertThat(entrada.getValue().getFecha()).isNotNull();
	}

	@Test
	void siElRolNoCambiaNoSeAnotaNada() {
		lucia();

		servicio.cambiarRol(7L, Rol.USUARIO_AFECTADO, "admin@garantia.es");

		verify(auditoria, never()).save(any());
	}

	@Test
	void unAdministradorNoPuedeCambiarSuPropioRol() {
		Usuario admin = new Usuario("admin", "admin@garantia.es", "hash");
		admin.cambiarRol(Rol.ADMINISTRADOR);
		when(usuarios.findById(1L)).thenReturn(Optional.of(admin));

		assertThatThrownBy(() -> servicio.cambiarRol(1L, Rol.USUARIO_AFECTADO, "admin@garantia.es"))
				.isInstanceOf(CambioRolNoPermitidoException.class);

		assertThat(admin.getRol()).isEqualTo(Rol.ADMINISTRADOR);
		verify(auditoria, never()).save(any());
	}

	@Test
	void fallaSiElUsuarioNoExiste() {
		when(usuarios.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> servicio.cambiarRol(99L, Rol.MODERADOR, "admin@garantia.es"))
				.isInstanceOf(UsuarioNoEncontradoException.class);
	}
}
