import { fireEvent, screen, waitFor } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { MENSAJES } from '../validacion/registro'
import { respuestaJson, renderizar, simularFetch } from '../pruebas/utilidades'
import RegistroPagina from './RegistroPagina'

function rellenar(nombreUsuario, email, password) {
  fireEvent.change(screen.getByLabelText('Nombre de usuario'), { target: { value: nombreUsuario } })
  fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: email } })
  fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: password } })
}

function pulsarCrear() {
  fireEvent.click(screen.getByRole('button', { name: 'Crear cuenta' }))
}

describe('RegistroPagina', () => {
  it('muestra los tres campos con su etiqueta', () => {
    renderizar(<RegistroPagina />)
    expect(screen.getByLabelText('Nombre de usuario')).toBeInTheDocument()
    expect(screen.getByLabelText('Correo electrónico')).toBeInTheDocument()
    expect(screen.getByLabelText('Contraseña')).toHaveAttribute('type', 'password')
  })

  it('valida un campo al salir de él', () => {
    renderizar(<RegistroPagina />)
    const email = screen.getByLabelText('Correo electrónico')
    fireEvent.change(email, { target: { value: 'lucia@correo' } })
    fireEvent.blur(email)

    expect(screen.getByText(/Introduce un correo electrónico válido/)).toBeInTheDocument()
    expect(email).toHaveAttribute('aria-invalid', 'true')
  })

  it('con datos no válidos no envía nada y enfoca el primer error (CA4)', () => {
    const fetch = simularFetch()
    renderizar(<RegistroPagina />)
    rellenar('', 'lucia@correo.es', '123')
    pulsarCrear()

    expect(fetch).not.toHaveBeenCalled()
    expect(screen.getByText(new RegExp(MENSAJES.nombreObligatorio))).toBeInTheDocument()
    expect(screen.getByText(new RegExp(MENSAJES.passwordNoValida))).toBeInTheDocument()
    expect(screen.getByLabelText('Nombre de usuario')).toHaveFocus()
  })

  it('envía los datos y muestra la confirmación (CA1, CA5)', async () => {
    const fetch = simularFetch(respuestaJson(201, { id: 1, mensaje: 'Cuenta creada correctamente' }))
    renderizar(<RegistroPagina />)
    rellenar(' lucia ', 'lucia@correo.es', 'Clave1234')
    pulsarCrear()

    expect(await screen.findByRole('status')).toHaveTextContent('Cuenta creada correctamente')
    expect(screen.getByRole('link', { name: 'Iniciar sesión' })).toBeInTheDocument()

    const [url, opciones] = fetch.mock.calls[0]
    expect(url).toBe('/api/auth/registro')
    expect(opciones.method).toBe('POST')
    expect(JSON.parse(opciones.body)).toEqual({
      nombreUsuario: 'lucia', email: 'lucia@correo.es', password: 'Clave1234'
    })
  })

  it('desactiva el botón mientras se envía', async () => {
    simularFetch(respuestaJson(201, { mensaje: 'Cuenta creada correctamente' }))
    renderizar(<RegistroPagina />)
    rellenar('lucia', 'lucia@correo.es', 'Clave1234')
    pulsarCrear()

    expect(screen.getByRole('button', { name: 'Creando cuenta…' })).toBeDisabled()
    await screen.findByRole('status')
  })

  it('muestra el error del servidor debajo del correo si ya está registrado (CA3)', async () => {
    simularFetch(respuestaJson(409, {
      mensaje: 'Ya existe una cuenta con ese correo electrónico',
      errores: { email: 'Ya existe una cuenta con ese correo electrónico' }
    }))
    renderizar(<RegistroPagina />)
    rellenar('lucia', 'lucia@correo.es', 'Clave1234')
    pulsarCrear()

    expect(await screen.findByText(/Ya existe una cuenta con ese correo electrónico/)).toBeInTheDocument()
    await waitFor(() => expect(screen.getByLabelText('Correo electrónico')).toHaveFocus())
  })

  it('si no hay conexión avisa y conserva los datos escritos', async () => {
    const fetch = simularFetch()
    fetch.mockRejectedValueOnce(new TypeError('Failed to fetch'))
    renderizar(<RegistroPagina />)
    rellenar('lucia', 'lucia@correo.es', 'Clave1234')
    pulsarCrear()

    expect(await screen.findByRole('alert')).toHaveTextContent('No se ha podido conectar con el servidor')
    expect(screen.getByLabelText('Nombre de usuario')).toHaveValue('lucia')
  })
})
