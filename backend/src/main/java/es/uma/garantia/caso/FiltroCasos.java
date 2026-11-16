package es.uma.garantia.caso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

/**
 * Filtros, búsqueda y orden de la lista de casos (HU-16 CA3 y CA4).
 * Todos los campos son opcionales: si vienen vacíos no filtran.
 */
public record FiltroCasos(EstadoCaso estado, String categoria, String texto, String orden) {

	public static final String ORDEN_ANTIGUOS = "antiguos";

	/** Condición de búsqueda para la base de datos. */
	public Specification<Caso> comoEspecificacion() {
		return (caso, consulta, cb) -> {
			List<Predicate> condiciones = new ArrayList<>();

			if (estado != null) {
				condiciones.add(cb.equal(caso.get("estado"), estado));
			}
			if (tieneValor(categoria)) {
				condiciones.add(cb.equal(cb.lower(caso.get("categoria")), categoria.trim().toLowerCase()));
			}
			if (tieneValor(texto)) {
				String patron = "%" + texto.trim().toLowerCase() + "%";
				condiciones.add(cb.or(
						cb.like(cb.lower(caso.get("titulo")), patron),
						cb.like(cb.lower(caso.get("descripcion")), patron)));
			}

			return cb.and(condiciones.toArray(new Predicate[0]));
		};
	}

	/** Por defecto, del más reciente al más antiguo. */
	public Sort comoOrden() {
		Sort.Direction direccion = ORDEN_ANTIGUOS.equals(orden) ? Sort.Direction.ASC : Sort.Direction.DESC;
		return Sort.by(direccion, "fechaPublicacion").and(Sort.by(direccion, "id"));
	}

	private static boolean tieneValor(String valor) {
		return valor != null && !valor.isBlank();
	}
}
