import { afterEach, describe, expect, it } from 'vitest'
import { respuestaJson, simularFetch } from '../pruebas/utilidades'
import { ErrorApi, MENSAJE_SIN_CONEXION, borrarCredenciales, guardarCredenciales, peticion } from './cliente'

// HU-26 T6 · Verificación de accesos desde el lado del frontend
describe('cliente de la API', () => {
  afterEach(() => {
    borrarCredenciales()
  })

  it('sin sesión no envía ninguna credencial', async () => {
    const fetch = simularFetch(respuestaJson(200, []))
    await peticion('/casos')

    expect(fetch.mock.calls[0][1].headers.Authorization).toBeUndefined()
  })

  it('con sesión envía las credenciales en cada petición', async () => {
    const fetch = simularFetch(respuestaJson(200, []))
    guardarCredenciales('lucia@correo.es', 'Clave1234')
    await peticion('/casos')

    expect(fetch.mock.calls[0][1].headers.Authorization).toBe('Basic ' + btoa('lucia@correo.es:Clave1234'))
  })

  it('codifica bien las contraseñas con ñ o tildes', async () => {
    const fetch = simularFetch(respuestaJson(200, []))
    guardarCredenciales('nuria@correo.es', 'Contraseña1')
    await peticion('/casos')

    const codificado = fetch.mock.calls[0][1].headers.Authorization.replace('Basic ', '')
    const bytes = Uint8Array.from(atob(codificado), (letra) => letra.charCodeAt(0))
    expect(new TextDecoder().decode(bytes)).toBe('nuria@correo.es:Contraseña1')
  })

  it('un 403 llega como ErrorApi con el mensaje de acceso denegado', async () => {
    simularFetch(respuestaJson(403, { mensaje: 'Acceso denegado: no tienes permiso para esta acción' }))

    const error = await peticion('/admin/usuarios').catch((excepcion) => excepcion)
    expect(error).toBeInstanceOf(ErrorApi)
    expect(error.estado).toBe(403)
    expect(error.message).toBe('Acceso denegado: no tienes permiso para esta acción')
  })

  it('los errores de validación traen el error de cada campo', async () => {
    simularFetch(respuestaJson(400, { mensaje: 'Hay datos que corregir', errores: { email: 'Introduce un correo electrónico válido' } }))

    const error = await peticion('/auth/registro', { metodo: 'POST', cuerpo: {} }).catch((excepcion) => excepcion)
    expect(error.errores).toEqual({ email: 'Introduce un correo electrónico válido' })
  })

  it('sin conexión con el servidor da un mensaje claro', async () => {
    const fetch = simularFetch()
    fetch.mockRejectedValueOnce(new TypeError('Failed to fetch'))

    const error = await peticion('/casos').catch((excepcion) => excepcion)
    expect(error.estado).toBe(0)
    expect(error.message).toBe(MENSAJE_SIN_CONEXION)
  })

  it('al salir se dejan de enviar las credenciales', async () => {
    const fetch = simularFetch(respuestaJson(200, []))
    guardarCredenciales('lucia@correo.es', 'Clave1234')
    borrarCredenciales()
    await peticion('/casos')

    expect(fetch.mock.calls[0][1].headers.Authorization).toBeUndefined()
  })
})
