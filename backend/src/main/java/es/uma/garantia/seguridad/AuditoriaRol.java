package es.uma.garantia.seguridad;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entrada de la bitácora de auditoría: un cambio de rol (HU-26 CA5).
 *
 * Guarda el correo del usuario y de quien hizo el cambio, y no claves ajenas,
 * para que el registro se conserve aunque una cuenta se borre más adelante.
 */
@Entity
@Table(name = "auditoria_roles")
public class AuditoriaRol {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "usuario_id", nullable = false)
	private Long usuarioId;

	@Column(name = "email_usuario", nullable = false)
	private String emailUsuario;

	@Enumerated(EnumType.STRING)
	@Column(name = "rol_anterior", nullable = false)
	private Rol rolAnterior;

	@Enumerated(EnumType.STRING)
	@Column(name = "rol_nuevo", nullable = false)
	private Rol rolNuevo;

	@Column(name = "realizado_por", nullable = false)
	private String realizadoPor;

	@Column(nullable = false)
	private LocalDateTime fecha;

	protected AuditoriaRol() {
		// Para JPA
	}

	public AuditoriaRol(Long usuarioId, String emailUsuario, Rol rolAnterior, Rol rolNuevo, String realizadoPor) {
		this.usuarioId = usuarioId;
		this.emailUsuario = emailUsuario;
		this.rolAnterior = rolAnterior;
		this.rolNuevo = rolNuevo;
		this.realizadoPor = realizadoPor;
		this.fecha = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public String getEmailUsuario() {
		return emailUsuario;
	}

	public Rol getRolAnterior() {
		return rolAnterior;
	}

	public Rol getRolNuevo() {
		return rolNuevo;
	}

	public String getRealizadoPor() {
		return realizadoPor;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}
}
