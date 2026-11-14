package es.uma.garantia.seguridad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRolRepository extends JpaRepository<AuditoriaRol, Long> {

	/** Bitácora completa, de la entrada más reciente a la más antigua. */
	List<AuditoriaRol> findAllByOrderByFechaDescIdDesc();
}
