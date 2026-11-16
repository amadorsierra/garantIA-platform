package es.uma.garantia.admin;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Gestión de roles por el Administrador (HU-26 T5).
 *
 * GET /api/admin/usuarios               · Lista de cuentas con su rol (ROLES_GESTIONAR)
 * PUT /api/admin/usuarios/{id}/rol      · Cambia el rol: {"rol": "MODERADOR"} (ROLES_GESTIONAR)
 * GET /api/admin/auditoria-roles        · Bitácora de cambios, del más reciente al más antiguo (AUDITORIA_CONSULTAR)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private final GestionRolesService gestionRoles;

	public AdminController(GestionRolesService gestionRoles) {
		this.gestionRoles = gestionRoles;
	}

	@GetMapping("/usuarios")
	public List<UsuarioAdminVista> usuarios() {
		return gestionRoles.listarUsuarios();
	}

	@PutMapping("/usuarios/{id}/rol")
	public UsuarioAdminVista cambiarRol(@PathVariable Long id, @Valid @RequestBody CambioRolRequest datos,
			Authentication autenticacion) {
		return UsuarioAdminVista.de(gestionRoles.cambiarRol(id, datos.rol(), autenticacion.getName()));
	}

	@GetMapping("/auditoria-roles")
	public List<AuditoriaRolVista> auditoria() {
		return gestionRoles.consultarAuditoria();
	}
}
