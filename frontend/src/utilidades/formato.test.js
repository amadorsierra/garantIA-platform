import { describe, expect, it } from 'vitest'
import { formatearFecha } from './formato'

describe('formatearFecha', () => {
  it('muestra la fecha corta en español', () => {
    expect(formatearFecha('2026-11-05T10:00:00')).toBe('5 nov 2026')
  })
})
