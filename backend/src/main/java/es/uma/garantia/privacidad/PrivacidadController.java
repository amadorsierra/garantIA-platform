package es.uma.garantia.privacidad;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * POST /api/privacidad/analizar · Analiza un texto antes de publicarlo (HU-08 T6).
 * Requiere el permiso CASOS_PUBLICAR (ver SecurityConfig). El texto no se guarda.
 */
@RestController
@RequestMapping("/api/privacidad")
public class PrivacidadController {

	private final DetectorDatosSensibles detector;

	public PrivacidadController(DetectorDatosSensibles detector) {
		this.detector = detector;
	}

	@PostMapping("/analizar")
	public ResultadoAnalisis analizar(@Valid @RequestBody AnalisisRequest peticion) {
		return ResultadoAnalisis.de(detector.detectar(peticion.texto()));
	}
}
