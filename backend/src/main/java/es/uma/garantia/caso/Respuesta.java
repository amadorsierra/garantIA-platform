package es.uma.garantia.caso;

import java.time.LocalDateTime;

import es.uma.garantia.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Respuesta o comentario a un caso. */
@Entity
@Table(name = "respuestas")
public class Respuesta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "caso_id", nullable = false)
	private Caso caso;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "autor_id", nullable = false)
	private Usuario autor;

	@Column(nullable = false)
	private String contenido;

	@Column(nullable = false)
	private LocalDateTime fecha;

	protected Respuesta() {
		// Para JPA
	}

	public Respuesta(Caso caso, Usuario autor, String contenido, LocalDateTime fecha) {
		this.caso = caso;
		this.autor = autor;
		this.contenido = contenido;
		this.fecha = fecha;
	}

	public Long getId() {
		return id;
	}

	public Caso getCaso() {
		return caso;
	}

	public Usuario getAutor() {
		return autor;
	}

	public String getContenido() {
		return contenido;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}
}
