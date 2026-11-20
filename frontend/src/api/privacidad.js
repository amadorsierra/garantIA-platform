import { peticion } from './cliente'

// HU-08 · POST /api/privacidad/analizar (contrato en docs/hu-08/T3-flujo-advertencia.md)
export function analizarTexto(texto) {
  return peticion('/privacidad/analizar', { metodo: 'POST', cuerpo: { texto } })
}
