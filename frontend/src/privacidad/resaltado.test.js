import { describe, expect, it } from 'vitest'
import { dividirTexto } from './resaltado'

describe('dividirTexto', () => {
  it('separa los datos detectados del resto del texto', () => {
    const deteccion = { inicio: 10, fin: 19 }
    expect(dividirTexto('Mi DNI es 12345678Z y no lo encuentro', [deteccion])).toEqual([
      { texto: 'Mi DNI es ', deteccion: null },
      { texto: '12345678Z', deteccion },
      { texto: ' y no lo encuentro', deteccion: null }
    ])
  })

  it('funciona con datos al principio y al final', () => {
    const trozos = dividirTexto('612345678 y 612345679', [{ inicio: 0, fin: 9 }, { inicio: 12, fin: 21 }])
    expect(trozos.map((trozo) => trozo.texto)).toEqual(['612345678', ' y ', '612345679'])
  })

  it('sin detecciones devuelve el texto entero', () => {
    expect(dividirTexto('Hola', [])).toEqual([{ texto: 'Hola', deteccion: null }])
  })
})
