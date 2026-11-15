package es.uma.garantia.usuario;

import java.time.LocalDateTime;

import es.uma.garantia.seguridad.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Cuenta de usuario (HU-01). Campos definidos en docs/hu-01/T1-datos-registro.md.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre_usuario", nullable = false, length = 30)
	private String nombreUsuario;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(name = "password_hash", nullable = false, length = 100)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private Rol rol;

	@Column(name = "fecha_registro", nullable = false)
	private LocalDateTime fechaRegistro;

	protected Usuario() {
		// Para JPA
	}

	/**
	 * Crea una cuenta nueva con el rol por defecto.
	 * Recibe la contraseña ya cifrada: esta clase nunca ve la contraseña en claro.
	 */
	public Usuario(String nombreUsuario, String email, String passwordHash) {
		this.nombreUsuario = nombreUsuario.trim();
		this.email = normalizarEmail(email);
		this.passwordHash = passwordHash;
		this.rol = Rol.POR_DEFECTO;
		this.fechaRegistro = LocalDateTime.now();
	}

	/** Los correos se guardan y se comparan siempre en minúsculas y sin espacios. */
	public static String normalizarEmail(String email) {
		return email.trim().toLowerCase();
	}

	public void cambiarRol(Rol nuevoRol) {
		this.rol = nuevoRol;
	}

	public Long getId() {
		return id;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Rol getRol() {
		return rol;
	}

	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}
}
