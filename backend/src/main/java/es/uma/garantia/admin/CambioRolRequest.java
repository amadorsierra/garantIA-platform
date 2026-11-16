package es.uma.garantia.admin;

import es.uma.garantia.seguridad.Rol;
import jakarta.validation.constraints.NotNull;

public record CambioRolRequest(@NotNull(message = "Indica el nuevo rol") Rol rol) {
}
