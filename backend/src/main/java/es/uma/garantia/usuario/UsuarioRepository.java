package es.uma.garantia.usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	/** El correo debe venir normalizado (ver Usuario.normalizarEmail). */
	Optional<Usuario> findByEmail(String email);

	/** El correo debe venir normalizado (ver Usuario.normalizarEmail). */
	boolean existsByEmail(String email);
}
