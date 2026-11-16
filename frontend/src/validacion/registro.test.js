import { describe, expect, it } from 'vitest'
import { MENSAJES, validarCampo, validarRegistro } from './registro'

describe('validarCampo', () => {
  it('exige los tres campos', () => {
    expect(validarCampo('nombreUsuario', '  ')).toBe(MENSAJES.nombreObligatorio)
    expect(validarCampo('email', '')).toBe(MENSAJES.emailObligatorio)
    expect(validarCampo('password', '')).toBe(MENSAJES.passwordObligatoria)
  })

  it('valida el nombre de usuario', () => {
    expect(validarCampo('nombreUsuario', 'lu')).toBe(MENSAJES.nombreNoValido)
    expect(validarCampo('nombreUsuario', 'lucia gomez')).toBe(MENSAJES.nombreNoValido)
    expect(validarCampo('nombreUsuario', 'a'.repeat(31))).toBe(MENSAJES.nombreNoValido)
    expect(validarCampo('nombreUsuario', 'lucia_g.92')).toBe('')
  })

  it('valida el formato del correo', () => {
    expect(validarCampo('email', 'lucia')).toBe(MENSAJES.emailNoValido)
    expect(validarCampo('email', 'lucia@correo')).toBe(MENSAJES.emailNoValido)
    expect(validarCampo('email', 'lucia@correo.es')).toBe('')
  })

  it('valida la contraseña: longitud, letras y números', () => {
    expect(validarCampo('password', 'Cl4ve')).toBe(MENSAJES.passwordNoValida)
    expect(validarCampo('password', 'solamenteletras')).toBe(MENSAJES.passwordNoValida)
    expect(validarCampo('password', '1234567890')).toBe(MENSAJES.passwordNoValida)
    expect(validarCampo('password', 'a1' + 'x'.repeat(63))).toBe(MENSAJES.passwordNoValida)
    expect(validarCampo('password', 'Clave1234')).toBe('')
  })
})

describe('validarRegistro', () => {
  it('devuelve solo los campos con error', () => {
    const errores = validarRegistro({ nombreUsuario: 'lucia', email: 'mal', password: '' })
    expect(errores).toEqual({
      email: MENSAJES.emailNoValido,
      password: MENSAJES.passwordObligatoria
    })
  })

  it('devuelve un objeto vacío si todo es válido', () => {
    expect(validarRegistro({ nombreUsuario: 'lucia', email: 'lucia@correo.es', password: 'Clave1234' })).toEqual({})
  })
})
