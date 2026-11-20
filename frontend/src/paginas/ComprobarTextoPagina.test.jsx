import { fireEvent, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { renderizar, simularApi } from '../pruebas/utilidades'
import ComprobarTextoPagina from './ComprobarTextoPagina'

const CON_DATOS = {
  hayDatosSensibles: true,
  detecciones: [
    { tipo: 'DNI', descripcion: 'DNI', fragmento: '12345678Z', inicio: 10, fin: 19 }
  ]
}
const SIN_DATOS = { hayDatosSensibles: false, detecciones: [] }

function escribirYPublicar(texto) {
  fireEvent.change(screen.getByLabelText('Texto del caso'), { target: { value: texto } })
  fireEvent.click(screen.getByRole('button', { name: 'Publicar' }))
}

describe('ComprobarTextoPagina', () => {
  it('analiza el texto antes de publicar (CA1)', async () => {
    const fetch = simularApi(() => ({ estado: 200, cuerpo: SIN_DATOS }))
    renderizar(<ComprobarTextoPagina />)
    escribirYPublicar('Mi portátil no enciende')

    await screen.findByRole('status')
    const [url, opciones] = fetch.mock.calls[0]
    expect(url).toBe('/api/privacidad/analizar')
    expect(JSON.parse(opciones.body)).toEqual({ texto: 'Mi portátil no enciende' })
  })

  it('sin datos personales publica sin mostrar ningún aviso (CA6)', async () => {
    simularApi(() => ({ estado: 200, cuerpo: SIN_DATOS }))
    renderizar(<ComprobarTextoPagina />)
    escribirYPublicar('Mi portátil no enciende')

    expect(await screen.findByRole('status')).toHaveTextContent('Tu caso se publicaría ahora.')
    expect(screen.queryByRole('alertdialog')).not.toBeInTheDocument()
  })

  it('con datos personales muestra el aviso y no publica hasta que el usuario elige (CA3, CA5)', async () => {
    simularApi(() => ({ estado: 200, cuerpo: CON_DATOS }))
    renderizar(<ComprobarTextoPagina />)
    escribirYPublicar('Mi DNI es 12345678Z')

    expect(await screen.findByRole('alertdialog')).toBeInTheDocument()
    expect(screen.queryByText('Tu caso se publicaría ahora.')).not.toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: 'Publicar de todos modos' }))
    expect(screen.queryByRole('alertdialog')).not.toBeInTheDocument()
    expect(screen.getByRole('status')).toHaveTextContent('Tu caso se publicaría ahora.')
  })

  it('al revisar vuelve al texto con el dato detectado seleccionado', async () => {
    simularApi(() => ({ estado: 200, cuerpo: CON_DATOS }))
    renderizar(<ComprobarTextoPagina />)
    escribirYPublicar('Mi DNI es 12345678Z')

    fireEvent.click(await screen.findByRole('button', { name: 'Revisar el texto' }))

    const campo = screen.getByLabelText('Texto del caso')
    expect(screen.queryByRole('alertdialog')).not.toBeInTheDocument()
    expect(campo).toHaveFocus()
    expect(campo.selectionStart).toBe(10)
    expect(campo.selectionEnd).toBe(19)
    expect(screen.queryByText('Tu caso se publicaría ahora.')).not.toBeInTheDocument()
  })

  it('al cancelar no publica y conserva el texto', async () => {
    simularApi(() => ({ estado: 200, cuerpo: CON_DATOS }))
    renderizar(<ComprobarTextoPagina />)
    escribirYPublicar('Mi DNI es 12345678Z')

    fireEvent.click(await screen.findByRole('button', { name: 'Cancelar' }))

    expect(screen.getByRole('status')).toHaveTextContent('No se ha publicado nada')
    expect(screen.getByLabelText('Texto del caso')).toHaveValue('Mi DNI es 12345678Z')
  })

  it('si falla el análisis no publica y permite reintentar', async () => {
    let llamadas = 0
    simularApi(() => {
      llamadas++
      return llamadas === 1 ? { estado: 500, cuerpo: {} } : { estado: 200, cuerpo: SIN_DATOS }
    })
    renderizar(<ComprobarTextoPagina />)
    escribirYPublicar('Mi portátil no enciende')

    expect(await screen.findByRole('alert')).toHaveTextContent('No se ha podido comprobar')
    expect(screen.queryByText('Tu caso se publicaría ahora.')).not.toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: 'Reintentar' }))
    expect(await screen.findByRole('status')).toHaveTextContent('Tu caso se publicaría ahora.')
  })
})
