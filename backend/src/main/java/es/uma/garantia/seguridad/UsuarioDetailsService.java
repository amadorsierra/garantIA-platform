package es.uma.garantia.seguridad;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.uma.garantia.usuario.Usuario;
import es.uma.garantia.usuario.UsuarioRepository;

/**
 * Carga el usuario que inicia sesión (por correo) con sus permisos.
 *
 * Se consulta la base de datos en cada petición, así que un cambio de rol
 * se aplica en la siguiente petición sin volver a iniciar sesión (HU-26 CA4).
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

	private final UsuarioRepository usuarios;

	public UsuarioDetailsService(UsuarioRepository usuarios) {
		this.usuarios = usuarios;
	}

	@Override
	public UserDetails loadUserByUsername(String email) {
		Usuario usuario = usuarios.findByEmail(Usuario.normalizarEmail(email))
				.orElseThrow(() -> new UsernameNotFoundException("No existe ninguna cuenta con ese correo"));

		return User.withUsername(usuario.getEmail())
				.password(usuario.getPasswordHash())
				.authorities(autoridadesDe(usuario.getRol()))
				.build();
	}

	/** El rol (ROLE_X) y cada uno de sus permisos (CASOS_LEER, ...) son autoridades de Spring Security. */
	static List<GrantedAuthority> autoridadesDe(Rol rol) {
		List<GrantedAuthority> autoridades = new ArrayList<>();
		autoridades.add(new SimpleGrantedAuthority("ROLE_" + rol.name()));
		for (Permiso permiso : rol.getPermisos()) {
			autoridades.add(new SimpleGrantedAuthority(permiso.name()));
		}
		return autoridades;
	}
}
