const FORMATO_FECHA = new Intl.DateTimeFormat('es-ES', { day: 'numeric', month: 'short', year: 'numeric' })

// "2026-11-05T10:00:00" -> "5 nov 2026"
export function formatearFecha(textoFecha) {
  return FORMATO_FECHA.format(new Date(textoFecha)).replace('.', '')
}
