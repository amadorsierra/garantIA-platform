package es.uma.garantia.comun;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import es.uma.garantia.caso.CasoNoEncontradoException;
import es.uma.garantia.usuario.EmailYaRegistradoException;

/** Convierte las excepciones en respuestas JSON con el formato de RespuestaError. */
@RestControllerAdvice
public class ManejadorErrores {

	public static final String MENSAJE_DATOS_NO_VALIDOS = "Hay datos que corregir";

	/** 400 · Datos no válidos: un mensaje por campo (HU-01 CA4). */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public RespuestaError datosNoValidos(MethodArgumentNotValidException excepcion) {
		Map<String, String> errores = new LinkedHashMap<>();
		for (FieldError error : excepcion.getBindingResult().getFieldErrors()) {
			// Si un campo está vacío, el mensaje "es obligatorio" es el más claro y tiene prioridad
			if ("NotBlank".equals(error.getCode())) {
				errores.put(error.getField(), error.getDefaultMessage());
			} else {
				errores.putIfAbsent(error.getField(), error.getDefaultMessage());
			}
		}
		return new RespuestaError(MENSAJE_DATOS_NO_VALIDOS, errores);
	}

	/** 400 · El cuerpo de la petición no es un JSON válido. */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public RespuestaError cuerpoIlegible() {
		return RespuestaError.de("La petición no tiene un formato válido");
	}

	/** 409 · Correo ya registrado (HU-01 CA3). El error va en el campo email para el formulario. */
	@ExceptionHandler(EmailYaRegistradoException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public RespuestaError emailYaRegistrado(EmailYaRegistradoException excepcion) {
		return new RespuestaError(excepcion.getMessage(), Map.of("email", excepcion.getMessage()));
	}

	/** 400 · Un parámetro tiene un valor no válido, por ejemplo un estado que no existe. */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public RespuestaError parametroNoValido(MethodArgumentTypeMismatchException excepcion) {
		return RespuestaError.de("El valor de '" + excepcion.getName() + "' no es válido");
	}

	/** 404 · El caso pedido no existe (HU-16 CA7). */
	@ExceptionHandler(CasoNoEncontradoException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public RespuestaError casoNoEncontrado(CasoNoEncontradoException excepcion) {
		return RespuestaError.de(excepcion.getMessage());
	}
}
