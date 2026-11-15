package es.uma.garantia.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import es.uma.garantia.PruebaIntegracion;
import es.uma.garantia.seguridad.Rol;

/** Pruebas del modelo de usuarios contra PostgreSQL (HU-01 T3). */
class UsuarioRepositoryTest extends PruebaIntegracion {

	@Autowired
	private UsuarioRepository usuarios;

	@Test
	void guardaUnUsuarioConElRolPorDefecto() {
		Usuario guardado = usuarios.saveAndFlush(new Usuario("lucia", "lucia@correo.es", "hash"));

		assertThat(guardado.getId()).isNotNull();
		assertThat(guardado.getRol()).isEqualTo(Rol.USUARIO_AFECTADO);
		assertThat(guardado.getFechaRegistro()).isNotNull();
	}

	@Test
	void guardaElCorreoEnMinusculasYSinEspacios() {
		usuarios.saveAndFlush(new Usuario("lucia", "  Lucia@Correo.ES ", "hash"));

		assertThat(usuarios.findByEmail("lucia@correo.es")).isPresent();
		assertThat(usuarios.existsByEmail("lucia@correo.es")).isTrue();
	}

	@Test
	void noPermiteDosCuentasConElMismoCorreo() {
		usuarios.saveAndFlush(new Usuario("lucia", "lucia@correo.es", "hash"));

		assertThatThrownBy(() -> usuarios.saveAndFlush(new Usuario("otra", "LUCIA@correo.es", "hash")))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void guardaElCambioDeRol() {
		Usuario usuario = usuarios.saveAndFlush(new Usuario("lucia", "lucia@correo.es", "hash"));

		usuario.cambiarRol(Rol.MODERADOR);
		usuarios.saveAndFlush(usuario);

		assertThat(usuarios.findByEmail("lucia@correo.es").get().getRol()).isEqualTo(Rol.MODERADOR);
	}
}
