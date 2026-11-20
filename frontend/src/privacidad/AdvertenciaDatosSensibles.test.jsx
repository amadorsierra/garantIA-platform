import { fireEvent, render, screen, within } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import AdvertenciaDatosSensibles from './AdvertenciaDatosSensibles'

const TEXTO = 'Soy María García López, llamadme al 612 345 678'
const DETECCIONES = [
  { tipo: 'NOMBRE_COMPLETO', descripcion: 'Nombre y apellidos', fragmento: 'María García López', inicio: 4, fin: 22 },
  { tipo: 'TELEFONO', descripcion: 'Número de teléfono', fragmento: '612 345 678', inicio: 36, fin: 47 }
]

function mostrar(manejadores = {}) {
  const props = { onRevisar: vi.fn(), onPublicar: vi.fn(), onCancelar: vi.fn(), ...manejadores }
  render(<AdvertenciaDatosSensibles texto={TEXTO} detecciones={DETECCIONES} {...props} />)
  return props
}

describe('AdvertenciaDatosSensibles', () => {
  it('es un diálogo de alerta con su título (CA3)', () => {
    mostrar()
    expect(screen.getByRole('alertdialog', { name: 'Tu publicación puede contener datos personales' })).toBeInTheDocument()
  })

  it('indica qué datos se han detectado y los resalta en el texto (CA4)', () => {
    mostrar()
    const lista = screen.getByRole('list')
    expect(within(lista).getByText('Nombre y apellidos:')).toBeInTheDocument()
    expect(within(lista).getByText(/«612 345 678»/)).toBeInTheDocument()

    const resaltados = document.querySelectorAll('mark')
    expect(Array.from(resaltados).map((marca) => marca.textContent)).toEqual(['María García López', '612 345 678'])
  })

  it('pone el foco en «Revisar el texto» al abrirse', () => {
    mostrar()
    expect(screen.getByRole('button', { name: 'Revisar el texto' })).toHaveFocus()
  })

  it('cada botón avisa de la elección del usuario', () => {
    const props = mostrar()
    fireEvent.click(screen.getByRole('button', { name: 'Revisar el texto' }))
    fireEvent.click(screen.getByRole('button', { name: 'Publicar de todos modos' }))
    fireEvent.click(screen.getByRole('button', { name: 'Cancelar' }))

    expect(props.onRevisar).toHaveBeenCalledTimes(1)
    expect(props.onPublicar).toHaveBeenCalledTimes(1)
    expect(props.onCancelar).toHaveBeenCalledTimes(1)
  })

  it('Escape equivale a revisar el texto', () => {
    const props = mostrar()
    fireEvent.keyDown(screen.getByRole('alertdialog'), { key: 'Escape' })
    expect(props.onRevisar).toHaveBeenCalledTimes(1)
    expect(props.onPublicar).not.toHaveBeenCalled()
  })

  it('avisa de que la detección no es perfecta', () => {
    mostrar()
    expect(screen.getByText(/revisa tú también que no haya otros datos personales/)).toBeInTheDocument()
  })
})
