package es.uma.garantia.seguridad;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.uma.garantia.usuario.UsuarioRepository;

/**
 * GET /api/auth/yo · Comprueba las credenciales y devuelve la cuenta con su rol y permisos.
 * El frontend lo usa para iniciar sesión y para saber qué puede mostrar.
 */
@RestController
@RequestMapping("/api/auth")
public class CuentaController {

	private final UsuarioRepository usuarios;

	public CuentaController(UsuarioRepository usuarios) {
		this.usuarios = usuarios;
	}

	@GetMapping("/yo")
	public CuentaActual yo(Authentication autenticacion) {
		return usuarios.findByEmail(autenticacion.getName())
				.map(CuentaActual::de)
				.orElseThrow();
	}
}
