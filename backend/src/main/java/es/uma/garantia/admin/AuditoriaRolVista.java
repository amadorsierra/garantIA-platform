package es.uma.garantia.admin;

import java.time.LocalDateTime;

import es.uma.garantia.seguridad.AuditoriaRol;
import es.uma.garantia.seguridad.Rol;

/** Entrada de la bitácora de cambios de rol (HU-26 CA5). */
public record AuditoriaRolVista(Long id, String emailUsuario, Rol rolAnterior, Rol rolNuevo,
		String realizadoPor, LocalDateTime fecha) {

	public static AuditoriaRolVista de(AuditoriaRol entrada) {
		return new AuditoriaRolVista(entrada.getId(), entrada.getEmailUsuario(), entrada.getRolAnterior(),
				entrada.getRolNuevo(), entrada.getRealizadoPor(), entrada.getFecha());
	}
}
