import { render } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router'
import { vi } from 'vitest'
import ProveedorAuth from '../auth/ProveedorAuth'

// Respuesta falsa del backend para simular fetch
export function respuestaJson(estado, cuerpo) {
  return new Response(cuerpo === undefined ? '' : JSON.stringify(cuerpo), {
    status: estado,
    headers: { 'Content-Type': 'application/json' }
  })
}

// Sustituye fetch por un mock que devuelve las respuestas indicadas, en orden
export function simularFetch(...respuestas) {
  const mock = vi.fn()
  respuestas.forEach((respuesta) => {
    mock.mockResolvedValueOnce(respuesta)
  })
  vi.stubGlobal('fetch', mock)
  return mock
}

// Renderiza la aplicación (o una parte) con router y sesión, empezando en la ruta indicada
export function renderizar(elemento, { ruta = '/', rutas = null } = {}) {
  return render(
    <MemoryRouter initialEntries={[ruta]}>
      <ProveedorAuth>
        {rutas ? <Routes>{rutas}</Routes> : elemento}
      </ProveedorAuth>
    </MemoryRouter>
  )
}

export { Route }
