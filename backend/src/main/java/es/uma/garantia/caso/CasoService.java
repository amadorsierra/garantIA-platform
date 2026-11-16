package es.uma.garantia.caso;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	public List<CasoResumen> buscar(FiltroCasos filtro) {
		List<CasoResumen> resultado = new ArrayList<>();
		for (Caso caso : casos.findAll(filtro.comoEspecificacion(), filtro.comoOrden())) {
			resultado.add(CasoResumen.de(caso));
		}
		return resultado;
	}

	public CasoDetalle detalle(Long id) {
		Caso caso = casos.findById(id).orElseThrow(CasoNoEncontradoException::new);
		return CasoDetalle.de(caso);
	}

	public List<String> categorias() {
		return casos.buscarCategorias();
	}
}
