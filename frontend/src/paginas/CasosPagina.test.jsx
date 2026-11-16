import { fireEvent, screen, within } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Route, renderizar, simularApi } from '../pruebas/utilidades'
import CasosPagina from './CasosPagina'

const CASOS = [
  { id: 1, titulo: 'Móvil con la pantalla rota', autor: 'marta', fechaPublicacion: '2026-11-05T10:00:00', categoria: 'Electrónica', estado: 'ABIERTO' },
  { id: 2, titulo: 'Lavadora con fuga de agua', autor: 'pablo', fechaPublicacion: '2026-11-03T10:00:00', categoria: 'Electrodomésticos', estado: 'RESUELTO' }
]

// API falsa: categorías fijas y, para la lista, lo que devuelva respuestaLista(url)
function api(respuestaLista) {
  return simularApi((url) => {
    if (url === '/api/casos/categorias') {
      return { estado: 200, cuerpo: ['Electrodomésticos', 'Electrónica'] }
    }
    return respuestaLista(url)
  })
}

function peticionesDeLista(fetch) {
  const urls = []
  fetch.mock.calls.forEach(([url]) => {
    if (url !== '/api/casos/categorias') urls.push(url)
  })
  return urls
}

function mostrar(ruta = '/casos') {
  renderizar(null, {
    ruta,
    rutas: [<Route key="casos" path="/casos" element={<CasosPagina />} />]
  })
}

describe('CasosPagina', () => {
  it('muestra título, estado, categoría, autor y fecha de cada caso (CA2)', async () => {
    api(() => ({ estado: 200, cuerpo: CASOS }))
    mostrar()

    const elementos = await screen.findAllByRole('listitem')
    expect(elementos).toHaveLength(2)
    const primero = within(elementos[0])
    expect(primero.getByText('Móvil con la pantalla rota')).toBeInTheDocument()
    expect(primero.getByText('Abierto')).toBeInTheDocument()
    expect(primero.getByText('Electrónica · marta · 5 nov 2026')).toBeInTheDocument()
    expect(primero.getByRole('link')).toHaveAttribute('href', '/casos/1')
  })

  it('aplica los filtros de estado, categoría y orden (CA3)', async () => {
    const fetch = api(() => ({ estado: 200, cuerpo: CASOS }))
    mostrar()
    await screen.findAllByRole('listitem')
    await screen.findByRole('option', { name: 'Electrónica' })

    fireEvent.change(screen.getByLabelText('Estado'), { target: { value: 'ABIERTO' } })
    await screen.findAllByRole('listitem')
    fireEvent.change(screen.getByLabelText('Categoría'), { target: { value: 'Electrónica' } })
    await screen.findAllByRole('listitem')
    fireEvent.change(screen.getByLabelText('Ordenar'), { target: { value: 'antiguos' } })
    await screen.findAllByRole('listitem')

    const ultima = peticionesDeLista(fetch).pop()
    const parametros = new URL(ultima, 'http://localhost').searchParams
    expect(parametros.get('estado')).toBe('ABIERTO')
    expect(parametros.get('categoria')).toBe('Electrónica')
    expect(parametros.get('orden')).toBe('antiguos')
  })

  it('busca por texto libre al pulsar Buscar (CA4)', async () => {
    const fetch = api(() => ({ estado: 200, cuerpo: CASOS }))
    mostrar()
    await screen.findAllByRole('listitem')

    fireEvent.change(screen.getByLabelText('Buscar'), { target: { value: ' lavadora ' } })
    fireEvent.click(screen.getByRole('button', { name: 'Buscar' }))
    await screen.findAllByRole('listitem')

    const ultima = peticionesDeLista(fetch).pop()
    expect(new URL(ultima, 'http://localhost').searchParams.get('q')).toBe('lavadora')
  })

  it('carga los filtros que vienen en la dirección', async () => {
    const fetch = api(() => ({ estado: 200, cuerpo: CASOS }))
    mostrar('/casos?estado=CERRADO&q=nevera')
    await screen.findAllByRole('listitem')

    expect(screen.getByLabelText('Buscar')).toHaveValue('nevera')
    expect(screen.getByLabelText('Estado')).toHaveValue('CERRADO')
    expect(peticionesDeLista(fetch)[0]).toContain('estado=CERRADO')
  })

  it('si no hay resultados lo indica y permite quitar los filtros (CA6)', async () => {
    const fetch = api(() => ({ estado: 200, cuerpo: [] }))
    mostrar('/casos?q=bicicleta')

    expect(await screen.findByRole('status')).toHaveTextContent('No hay casos que coincidan con tu búsqueda')

    fireEvent.click(screen.getByRole('button', { name: 'Quitar filtros' }))
    await screen.findByRole('status')
    expect(peticionesDeLista(fetch).pop()).toBe('/api/casos')
    expect(screen.getByLabelText('Buscar')).toHaveValue('')
  })

  it('si falla la carga avisa y permite reintentar (CA7)', async () => {
    let llamadas = 0
    api(() => {
      llamadas++
      return llamadas === 1 ? { estado: 500, cuerpo: { mensaje: 'Error interno' } } : { estado: 200, cuerpo: CASOS }
    })
    mostrar()

    expect(await screen.findByRole('alert')).toHaveTextContent('No se han podido cargar los casos')

    fireEvent.click(screen.getByRole('button', { name: 'Reintentar' }))
    expect(await screen.findAllByRole('listitem')).toHaveLength(2)
  })

  it('sin permiso muestra el mensaje de acceso denegado', async () => {
    api(() => ({ estado: 403, cuerpo: { mensaje: 'Acceso denegado: no tienes permiso para esta acción' } }))
    mostrar()

    expect(await screen.findByRole('alert')).toHaveTextContent('Acceso denegado: no tienes permiso para esta acción')
  })
})
