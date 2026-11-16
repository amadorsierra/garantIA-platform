import { borrarCredenciales, guardarCredenciales, peticion } from './cliente'

// HU-01 · POST /api/auth/registro
export function registrar(datos) {
  return peticion('/auth/registro', { metodo: 'POST', cuerpo: datos })
}

// Comprueba el correo y la contraseña pidiendo la cuenta actual (GET /api/auth/yo)
export async function iniciarSesion(email, password) {
  guardarCredenciales(email, password)
  try {
    return await peticion('/auth/yo')
  } catch (error) {
    borrarCredenciales()
    throw error
  }
}

export function cerrarSesion() {
  borrarCredenciales()
}
