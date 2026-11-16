import { fireEvent, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Route, renderizar, simularApi } from '../pruebas/utilidades'
import CasoDetallePagina from './CasoDetallePagina'

const CASO = {
  id: 7,
  titulo: 'Portátil que no enciende',
  descripcion: 'Dejó de encender a los 8 meses',
  autor: 'marta',
  fechaPublicacion: '2026-11-01T10:00:00',
  categoria: 'Electrónica',
  estado: 'ABIERTO',
  respuestas: [
    { id: 1, autor: 'pablo', contenido: 'Reclama al vendedor', fecha: '2026-11-02T09:00:00' }
  ]
}

function mostrar() {
  renderizar(null, {
    ruta: '/casos/7',
    rutas: [<Route key="detalle" path="/casos/:id" element={<CasoDetallePagina />} />]
  })
}

describe('CasoDetallePagina', () => {
  it('muestra el caso completo y sus respuestas (CA5)', async () => {
    const fetch = simularApi(() => ({ estado: 200, cuerpo: CASO }))
    mostrar()

    expect(await screen.findByRole('heading', { name: 'Portátil que no enciende' })).toBeInTheDocument()
    expect(screen.getByText('Dejó de encender a los 8 meses')).toBeInTheDocument()
    expect(screen.getByText('Electrónica · marta · 1 nov 2026')).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Respuestas (1)' })).toBeInTheDocument()
    expect(screen.getByText('Reclama al vendedor')).toBeInTheDocument()
    expect(screen.getByText('pablo · 2 nov 2026')).toBeInTheDocument()
    expect(fetch.mock.calls[0][0]).toBe('/api/casos/7')
  })

  it('indica cuando todavía no hay respuestas', async () => {
    simularApi(() => ({ estado: 200, cuerpo: { ...CASO, respuestas: [] } }))
    mostrar()

    expect(await screen.findByText('Todavía no hay respuestas para este caso')).toBeInTheDocument()
  })

  it('si el caso no existe lo indica sin botón de reintentar', async () => {
    simularApi(() => ({ estado: 404, cuerpo: { mensaje: 'El caso no existe' } }))
    mostrar()

    expect(await screen.findByRole('alert')).toHaveTextContent('El caso no existe')
    expect(screen.queryByRole('button', { name: 'Reintentar' })).not.toBeInTheDocument()
    expect(screen.getByRole('link', { name: '← Volver a la lista' })).toHaveAttribute('href', '/casos')
  })

  it('si falla la carga permite reintentar (CA7)', async () => {
    let llamadas = 0
    simularApi(() => {
      llamadas++
      return llamadas === 1 ? { estado: 500, cuerpo: {} } : { estado: 200, cuerpo: CASO }
    })
    mostrar()

    expect(await screen.findByRole('alert')).toHaveTextContent('No se ha podido cargar el caso')
    fireEvent.click(screen.getByRole('button', { name: 'Reintentar' }))
    expect(await screen.findByRole('heading', { name: 'Portátil que no enciende' })).toBeInTheDocument()
  })
})
