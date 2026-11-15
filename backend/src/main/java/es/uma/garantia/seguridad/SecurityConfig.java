package es.uma.garantia.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Control de acceso a los endpoints (HU-26 T4).
 * Las reglas son las de la matriz de docs/hu-26/T2-matriz-permisos.md.
 *
 * - Autenticación HTTP Basic con el correo y la contraseña, sin sesión en el servidor.
 * - Se comprueba por permiso, no por rol.
 * - Lo que no aparece en la lista exige haber iniciado sesión (denegar por defecto).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain filtroSeguridad(HttpSecurity http, RespuestasSeguridad respuestas) throws Exception {
		http
				// API sin sesión ni cookies: no hace falta protección CSRF
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.httpBasic(basic -> basic.authenticationEntryPoint(respuestas.sinSesion()))
				.exceptionHandling(errores -> errores
						.authenticationEntryPoint(respuestas.sinSesion())
						.accessDeniedHandler(respuestas.accesoDenegado()))
				.authorizeHttpRequests(peticiones -> peticiones
						.requestMatchers(HttpMethod.POST, "/api/auth/registro").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers("/api/casos/**").hasAuthority(Permiso.CASOS_LEER.name())
						.requestMatchers("/api/admin/auditoria-roles").hasAuthority(Permiso.AUDITORIA_CONSULTAR.name())
						.requestMatchers("/api/admin/**").hasAuthority(Permiso.ROLES_GESTIONAR.name())
						.anyRequest().authenticated());

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
