package es.uma.garantia.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.uma.garantia.seguridad.AuditoriaRol;
import es.uma.garantia.seguridad.AuditoriaRolRepository;
import es.uma.garantia.seguridad.Rol;
import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

/**
 * Asignación y comprobación de roles (HU-26 T5).
 *
 * Además de la regla por ruta de SecurityConfig, cada método comprueba el permiso con
 * @PreAuthorize: si mañana otro endpoint llama a este servicio, sigue protegido.
 */
@Service
public class GestionRolesService {

	private final UsuarioRepository usuarios;
	private final AuditoriaRolRepository auditoria;

	public GestionRolesService(UsuarioRepository usuarios, AuditoriaRolRepository auditoria) {
		this.usuarios = usuarios;
		this.auditoria = auditoria;
	}

	/**
	 * Cambia el rol de una cuenta y lo anota en la bitácora (CA1, CA5).
	 * Se guarda al momento, así que la siguiente petición de ese usuario ya usa el rol nuevo (CA4).
	 */
	@Transactional
	@PreAuthorize("hasAuthority('ROLES_GESTIONAR')")
	public Usuario cambiarRol(Long usuarioId, Rol nuevoRol, String emailAdministrador) {
		Usuario usuario = usuarios.findById(usuarioId).orElseThrow(UsuarioNoEncontradoException::new);

		if (usuario.getEmail().equals(Usuario.normalizarEmail(emailAdministrador))) {
			throw new CambioRolNoPermitidoException();
		}

		Rol rolAnterior = usuario.getRol();
		if (rolAnterior == nuevoRol) {
			return usuario;
		}

		usuario.cambiarRol(nuevoRol);
		usuarios.save(usuario);
		auditoria.save(new AuditoriaRol(usuario.getId(), usuario.getEmail(), rolAnterior, nuevoRol,
				Usuario.normalizarEmail(emailAdministrador)));
		return usuario;
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('ROLES_GESTIONAR')")
	public List<UsuarioAdminVista> listarUsuarios() {
		List<UsuarioAdminVista> resultado = new ArrayList<>();
		for (Usuario usuario : usuarios.findAll(Sort.by("email"))) {
			resultado.add(UsuarioAdminVista.de(usuario));
		}
		return resultado;
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('AUDITORIA_CONSULTAR')")
	public List<AuditoriaRolVista> consultarAuditoria() {
		List<AuditoriaRolVista> resultado = new ArrayList<>();
		for (AuditoriaRol entrada : auditoria.findAllByOrderByFechaDescIdDesc()) {
			resultado.add(AuditoriaRolVista.de(entrada));
		}
		return resultado;
	}
}
