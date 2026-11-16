import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import Paginacion from './Paginacion'

describe('Paginacion', () => {
  it('no se muestra si todo cabe en una página', () => {
    const { container } = render(<Paginacion pagina={0} totalPaginas={1} onCambiar={vi.fn()} />)
    expect(container).toBeEmptyDOMElement()
  })

  it('muestra la página actual empezando en 1', () => {
    render(<Paginacion pagina={1} totalPaginas={3} onCambiar={vi.fn()} />)
    expect(screen.getByText('Página 2 de 3')).toHaveAttribute('aria-current', 'page')
  })

  it('desactiva Anterior en la primera página y Siguiente en la última', () => {
    const { rerender } = render(<Paginacion pagina={0} totalPaginas={2} onCambiar={vi.fn()} />)
    expect(screen.getByRole('button', { name: '← Anterior' })).toBeDisabled()
    expect(screen.getByRole('button', { name: 'Siguiente →' })).toBeEnabled()

    rerender(<Paginacion pagina={1} totalPaginas={2} onCambiar={vi.fn()} />)
    expect(screen.getByRole('button', { name: 'Siguiente →' })).toBeDisabled()
  })

  it('avisa de la página elegida', () => {
    const onCambiar = vi.fn()
    render(<Paginacion pagina={1} totalPaginas={3} onCambiar={onCambiar} />)

    fireEvent.click(screen.getByRole('button', { name: 'Siguiente →' }))
    fireEvent.click(screen.getByRole('button', { name: '← Anterior' }))
    expect(onCambiar.mock.calls).toEqual([[2], [0]])
  })
})
