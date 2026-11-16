package es.uma.garantia.comun;

import java.util.List;

/**
 * Una página de resultados (HU-16 T5).
 * "pagina" empieza en 0; "tamano" es el número máximo de elementos por página.
 */
public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
}
