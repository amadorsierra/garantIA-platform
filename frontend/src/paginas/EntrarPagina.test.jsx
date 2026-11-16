import { fireEvent, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Route, respuestaJson, renderizar, simularFetch } from '../pruebas/utilidades'
import EntrarPagina from './EntrarPagina'

function rutas() {
  return [
    <Route key="entrar" path="/entrar" element={<EntrarPagina />} />,
    <Route key="inicio" path="/" element={<p>Página de inicio</p>} />
  ]
}

function entrarCon(email, password) {
  fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: email } })
  fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: password } })
  fireEvent.click(screen.getByRole('button', { name: 'Entrar' }))
}

describe('EntrarPagina', () => {
  it('entra con la cuenta creada y envía las credenciales (CA6)', async () => {
    const fetch = simularFetch(respuestaJson(200, { nombreUsuario: 'lucia', permisos: [] }))
    renderizar(null, { ruta: '/entrar', rutas: rutas() })
    entrarCon('lucia@correo.es', 'Clave1234')

    expect(await screen.findByText('Página de inicio')).toBeInTheDocument()
    const [url, opciones] = fetch.mock.calls[0]
    expect(url).toBe('/api/auth/yo')
    expect(opciones.headers.Authorization).toBe('Basic ' + btoa('lucia@correo.es:Clave1234'))
  })

  it('con credenciales incorrectas muestra un aviso', async () => {
    simularFetch(respuestaJson(401, { mensaje: 'Debes iniciar sesión para acceder' }))
    renderizar(null, { ruta: '/entrar', rutas: rutas() })
    entrarCon('lucia@correo.es', 'mala')

    expect(await screen.findByRole('alert')).toHaveTextContent('El correo o la contraseña no son correctos')
  })

  it('pide rellenar los dos campos', () => {
    const fetch = simularFetch()
    renderizar(null, { ruta: '/entrar', rutas: rutas() })
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }))

    expect(screen.getByRole('alert')).toHaveTextContent('Introduce tu correo electrónico y tu contraseña')
    expect(fetch).not.toHaveBeenCalled()
  })
})
