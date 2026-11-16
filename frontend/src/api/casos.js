import { peticion } from './cliente'

// HU-16 · GET /api/casos con los filtros que tengan valor
export function buscarCasos(filtros) {
  const parametros = new URLSearchParams()
  Object.keys(filtros).forEach((clave) => {
    const valor = filtros[clave]
    if (valor !== undefined && valor !== null && valor !== '') {
      parametros.set(clave, valor)
    }
  })
  const consulta = parametros.toString()
  return peticion('/casos' + (consulta ? '?' + consulta : ''))
}

export function obtenerCaso(id) {
  return peticion('/casos/' + id)
}

export function obtenerCategorias() {
  return peticion('/casos/categorias')
}
