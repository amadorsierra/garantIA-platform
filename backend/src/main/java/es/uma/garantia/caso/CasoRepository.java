package es.uma.garantia.caso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CasoRepository extends JpaRepository<Caso, Long>, JpaSpecificationExecutor<Caso> {

	/** Categorías que tienen algún caso, para el filtro de la lista. */
	@Query("SELECT DISTINCT c.categoria FROM Caso c ORDER BY c.categoria")
	List<String> buscarCategorias();
}
