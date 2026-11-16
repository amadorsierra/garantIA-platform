package es.uma.garantia.caso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uma.garantia.comun.Pagina;

/**
 * Consulta de casos publicados (HU-16 T3).
 * Los métodos son transaccionales para poder leer el autor y las respuestas al montar la respuesta.
 */
@Service
@Transactional(readOnly = true)
public class CasoService {

	private final CasoRepository casos;

	public CasoService(CasoRepository casos) {
		this.casos = casos;
	}

	/** Casos por página acordados en el Scrum Meeting del 10/11. */
	public static final int TAMANO_POR_DEFECTO = 10;
	/** Límite para que nadie pida la base de datos entera de una vez. */
	public static final int TAMANO_MAXIMO = 50;

	/**
	 * Busca los casos de una página (HU-16 T5).
	 * Los valores fuera de rango se corrigen: página negativa → 0, tamaño entre 1 y 50.
	 */
	public Pagina<CasoResumen> buscar(FiltroCasos filtro, int pagina, int tamano) {
		int paginaValida = Math.max(pagina, 0);
		int tamanoValido = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO);

		PageRequest peticion = PageRequest.of(paginaValida, tamanoValido, filtro.comoOrden());
		Page<Caso> resultado = casos.findAll(filtro.comoEspecificacion(), peticion);

		List<CasoResumen> contenido = new ArrayList<>();
		for (Caso caso : resultado.getContent()) {
			contenido.add(CasoResumen.de(caso));
		}
		return new Pagina<>(contenido, paginaValida, tamanoValido, resultado.getTotalElements(),
				resultado.getTotalPages());
	}

	public CasoDetalle detalle(Long id) {
		Caso caso = casos.findById(id).orElseThrow(CasoNoEncontradoException::new);
		return CasoDetalle.de(caso);
	}

	public List<String> categorias() {
		return casos.buscarCategorias();
	}
}
