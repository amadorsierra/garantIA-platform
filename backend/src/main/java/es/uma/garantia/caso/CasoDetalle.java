package es.uma.garantia.caso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Detalle completo de un caso con sus respuestas (HU-16 CA5). */
public record CasoDetalle(Long id, String titulo, String descripcion, String autor,
		LocalDateTime fechaPublicacion, String categoria, EstadoCaso estado, List<RespuestaVista> respuestas) {

	public record RespuestaVista(Long id, String autor, String contenido, LocalDateTime fecha) {
	}

	public static CasoDetalle de(Caso caso) {
		List<RespuestaVista> respuestas = new ArrayList<>();
		for (Respuesta respuesta : caso.getRespuestas()) {
			respuestas.add(new RespuestaVista(respuesta.getId(), respuesta.getAutor().getNombreUsuario(),
					respuesta.getContenido(), respuesta.getFecha()));
		}
		return new CasoDetalle(caso.getId(), caso.getTitulo(), caso.getDescripcion(),
				caso.getAutor().getNombreUsuario(), caso.getFechaPublicacion(), caso.getCategoria(),
				caso.getEstado(), respuestas);
	}
}
