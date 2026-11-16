package es.uma.garantia.caso;

import java.time.LocalDateTime;

/** Caso tal como aparece en la lista (HU-16 CA2). */
public record CasoResumen(Long id, String titulo, String autor, LocalDateTime fechaPublicacion,
		String categoria, EstadoCaso estado) {

	public static CasoResumen de(Caso caso) {
		return new CasoResumen(caso.getId(), caso.getTitulo(), caso.getAutor().getNombreUsuario(),
				caso.getFechaPublicacion(), caso.getCategoria(), caso.getEstado());
	}
}
