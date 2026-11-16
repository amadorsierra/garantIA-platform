// HU-16 T5 · Controles de paginación. "pagina" empieza en 0, como en la API.
export default function Paginacion({ pagina, totalPaginas, onCambiar }) {
  if (totalPaginas <= 1) {
    return null
  }

  const esPrimera = pagina === 0
  const esUltima = pagina >= totalPaginas - 1

  return (
    <nav aria-label="Paginación" className="paginacion">
      <button type="button" className="boton-secundario" disabled={esPrimera} onClick={() => onCambiar(pagina - 1)}>
        ← Anterior
      </button>
      <span aria-current="page">Página {pagina + 1} de {totalPaginas}</span>
      <button type="button" className="boton-secundario" disabled={esUltima} onClick={() => onCambiar(pagina + 1)}>
        Siguiente →
      </button>
    </nav>
  )
}
