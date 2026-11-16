// Cliente de la API del backend.
// Añade las credenciales de la sesión (HTTP Basic) y convierte los errores en ErrorApi.

export const MENSAJE_SIN_CONEXION = 'No se ha podido conectar con el servidor. Inténtalo de nuevo'

// Las credenciales solo se guardan en memoria: al recargar la página hay que volver a entrar.
let credenciales = null

export class ErrorApi extends Error {
  constructor(estado, mensaje, errores) {
    super(mensaje)
    this.estado = estado
    this.errores = errores || {}
  }
}

// btoa solo admite caracteres latinos; así también funcionan contraseñas con ñ o tildes
function codificarBase64(texto) {
  const bytes = new TextEncoder().encode(texto)
  let binario = ''
  bytes.forEach((byte) => {
    binario += String.fromCharCode(byte)
  })
  return btoa(binario)
}

export function guardarCredenciales(email, password) {
  credenciales = codificarBase64(email + ':' + password)
}

export function borrarCredenciales() {
  credenciales = null
}

async function leerJson(respuesta) {
  const texto = await respuesta.text()
  if (!texto) {
    return null
  }
  try {
    return JSON.parse(texto)
  } catch {
    return null
  }
}

export async function peticion(ruta, opciones = {}) {
  const cabeceras = { Accept: 'application/json' }
  if (opciones.cuerpo !== undefined) {
    cabeceras['Content-Type'] = 'application/json'
  }
  if (credenciales) {
    cabeceras.Authorization = 'Basic ' + credenciales
  }

  let respuesta
  try {
    respuesta = await fetch('/api' + ruta, {
      method: opciones.metodo || 'GET',
      headers: cabeceras,
      body: opciones.cuerpo !== undefined ? JSON.stringify(opciones.cuerpo) : undefined
    })
  } catch {
    throw new ErrorApi(0, MENSAJE_SIN_CONEXION)
  }

  const datos = await leerJson(respuesta)
  if (!respuesta.ok) {
    const mensaje = datos && datos.mensaje ? datos.mensaje : 'Ha ocurrido un error inesperado'
    throw new ErrorApi(respuesta.status, mensaje, datos ? datos.errores : {})
  }
  return datos
}
