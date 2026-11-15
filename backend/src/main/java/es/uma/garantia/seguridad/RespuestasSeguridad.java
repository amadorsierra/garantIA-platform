package es.uma.garantia.seguridad;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Respuestas en JSON para los errores de seguridad, para que el frontend muestre
 * un mensaje y no una página en blanco (acordado en el Scrum Meeting del 10/11).
 *
 * El 401 no envía la cabecera WWW-Authenticate a propósito: así el navegador
 * no abre su ventana de usuario y contraseña.
 */
@Component
public class RespuestasSeguridad {

	public static final String MENSAJE_SIN_SESION = "Debes iniciar sesión para acceder";
	public static final String MENSAJE_ACCESO_DENEGADO = "Acceso denegado: no tienes permiso para esta acción";

	public AuthenticationEntryPoint sinSesion() {
		return (request, response, excepcion) ->
				escribir(response, HttpStatus.UNAUTHORIZED, MENSAJE_SIN_SESION);
	}

	public AccessDeniedHandler accesoDenegado() {
		return (request, response, excepcion) ->
				escribir(response, HttpStatus.FORBIDDEN, MENSAJE_ACCESO_DENEGADO);
	}

	private void escribir(HttpServletResponse response, HttpStatus estado, String mensaje) throws IOException {
		response.setStatus(estado.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"mensaje\": \"" + mensaje + "\"}");
	}
}
