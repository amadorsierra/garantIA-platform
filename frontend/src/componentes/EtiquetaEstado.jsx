const TEXTOS = {
  ABIERTO: 'Abierto',
  CERRADO: 'Cerrado',
  RESUELTO: 'Resuelto'
}

// Etiqueta de color con el texto del estado (docs/hu-16/T1-presentacion-casos.md)
export default function EtiquetaEstado({ estado }) {
  return (
    <span className={'etiqueta-estado estado-' + estado.toLowerCase()}>
      {TEXTOS[estado] || estado}
    </span>
  )
}
