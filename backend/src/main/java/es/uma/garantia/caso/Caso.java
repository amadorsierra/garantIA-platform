package es.uma.garantia.caso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.uma.garantia.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * Caso publicado en el foro.
 *
 * En el Sprint 1 solo se consulta (HU-16); la publicación llega con HU-02 en el Sprint 2.
 */
@Entity
@Table(name = "casos")
public class Caso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String titulo;

	@Column(nullable = false)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "autor_id", nullable = false)
	private Usuario autor;

	@Column(name = "fecha_publicacion", nullable = false)
	private LocalDateTime fechaPublicacion;

	@Column(nullable = false, length = 50)
	private String categoria;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoCaso estado;

	@OneToMany(mappedBy = "caso")
	@OrderBy("fecha ASC, id ASC")
	private List<Respuesta> respuestas = new ArrayList<>();

	protected Caso() {
		// Para JPA
	}

	public Caso(String titulo, String descripcion, Usuario autor, String categoria, EstadoCaso estado,
			LocalDateTime fechaPublicacion) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.autor = autor;
		this.categoria = categoria;
		this.estado = estado;
		this.fechaPublicacion = fechaPublicacion;
	}

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Usuario getAutor() {
		return autor;
	}

	public LocalDateTime getFechaPublicacion() {
		return fechaPublicacion;
	}

	public String getCategoria() {
		return categoria;
	}

	public EstadoCaso getEstado() {
		return estado;
	}

	public List<Respuesta> getRespuestas() {
		return respuestas;
	}
}
