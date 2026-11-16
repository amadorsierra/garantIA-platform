package es.uma.garantia.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * POST /api/auth/registro · Crea una cuenta (HU-01 T4). Es público.
 *
 * Respuestas:
 * - 201 con la cuenta y el mensaje "Cuenta creada correctamente"
 * - 400 si algún dato no es válido, con el error de cada campo
 * - 409 si el correo ya tiene cuenta
 */
@RestController
@RequestMapping("/api/auth")
public class RegistroController {

	private final RegistroService registroService;

	public RegistroController(RegistroService registroService) {
		this.registroService = registroService;
	}

	@PostMapping("/registro")
	@ResponseStatus(HttpStatus.CREATED)
	public RegistroResponse registrar(@Valid @RequestBody RegistroRequest datos) {
		Usuario usuario = registroService.registrar(datos);
		return RegistroResponse.de(usuario);
	}
}
