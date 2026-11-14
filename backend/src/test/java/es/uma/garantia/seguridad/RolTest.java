package es.uma.garantia.seguridad;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Comprueba que el modelo de roles coincide con la matriz de la T2. */
class RolTest {

	@Test
	void unaCuentaNuevaEsUsuarioAfectado() {
		assertThat(Rol.POR_DEFECTO).isEqualTo(Rol.USUARIO_AFECTADO);
	}

	@Test
	void todosLosRolesPuedenLeerCasos() {
		for (Rol rol : Rol.values()) {
			assertThat(rol.tienePermiso(Permiso.CASOS_LEER))
					.as("%s debe poder leer casos", rol)
					.isTrue();
		}
	}

	@Test
	void permisosDelUsuarioAfectado() {
		assertThat(Rol.USUARIO_AFECTADO.getPermisos()).containsExactlyInAnyOrder(
				Permiso.CASOS_LEER, Permiso.CASOS_PUBLICAR, Permiso.RESPUESTAS_PUBLICAR);
	}

	@Test
	void permisosDelUsuarioAyudante() {
		assertThat(Rol.USUARIO_AYUDANTE.getPermisos()).containsExactlyInAnyOrder(
				Permiso.CASOS_LEER, Permiso.RESPUESTAS_PUBLICAR);
	}

	@Test
	void permisosDelModerador() {
		assertThat(Rol.MODERADOR.getPermisos()).containsExactlyInAnyOrder(
				Permiso.CASOS_LEER, Permiso.CONTENIDO_MODERAR);
	}

	@Test
	void permisosDelAdministrador() {
		assertThat(Rol.ADMINISTRADOR.getPermisos()).containsExactlyInAnyOrder(
				Permiso.CASOS_LEER, Permiso.ROLES_GESTIONAR, Permiso.AUDITORIA_CONSULTAR);
	}

	@Test
	void soloElAdministradorGestionaRoles() {
		for (Rol rol : Rol.values()) {
			boolean esAdministrador = rol == Rol.ADMINISTRADOR;
			assertThat(rol.tienePermiso(Permiso.ROLES_GESTIONAR)).isEqualTo(esAdministrador);
		}
	}

	@Test
	void losPermisosNoSePuedenModificar() {
		assertThatThrownBy(() -> Rol.USUARIO_AYUDANTE.getPermisos().add(Permiso.ROLES_GESTIONAR))
				.isInstanceOf(UnsupportedOperationException.class);
	}
}
