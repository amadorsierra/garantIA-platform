package es.uma.garantia.caso;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.uma.garantia.comun.Pagina;

/**
 * Consulta de casos publicados (HU-16 T3). Requiere el permiso CASOS_LEER (ver SecurityConfig).
 *
 * GET /api/casos?estado=ABIERTO&categoria=Electrónica&q=texto&orden=recientes|antiguos&pagina=0&tamano=10
 * GET /api/casos/{id}
 * GET /api/casos/categorias
 */
@RestController
@RequestMapping("/api/casos")
public class CasoController {

	private final CasoService casoService;

	public CasoController(CasoService casoService) {
		this.casoService = casoService;
	}

	@GetMapping
	public Pagina<CasoResumen> listar(
			@RequestParam(required = false) EstadoCaso estado,
			@RequestParam(required = false) String categoria,
			@RequestParam(name = "q", required = false) String texto,
			@RequestParam(defaultValue = "recientes") String orden,
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "" + CasoService.TAMANO_POR_DEFECTO) int tamano) {
		return casoService.buscar(new FiltroCasos(estado, categoria, texto, orden), pagina, tamano);
	}

	@GetMapping("/{id}")
	public CasoDetalle detalle(@PathVariable Long id) {
		return casoService.detalle(id);
	}

	@GetMapping("/categorias")
	public List<String> categorias() {
		return casoService.categorias();
	}
}
