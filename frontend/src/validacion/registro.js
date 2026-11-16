// Reglas del formulario de registro (docs/hu-01/T1-datos-registro.md).
// Son las mismas que comprueba el backend; aquí sirven para avisar antes de enviar.

export const MENSAJES = {
  nombreObligatorio: 'El nombre de usuario es obligatorio',
  nombreNoValido: 'Usa de 3 a 30 caracteres: letras, números, punto, guion o guion bajo',
  emailObligatorio: 'El correo electrónico es obligatorio',
  emailNoValido: 'Introduce un correo electrónico válido',
  passwordObligatoria: 'La contraseña es obligatoria',
  passwordNoValida: 'La contraseña debe tener entre 8 y 64 caracteres, con al menos una letra y un número'
}

const PATRON_NOMBRE = /^[A-Za-z0-9._-]{3,30}$/
const PATRON_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

// Devuelve el mensaje de error del campo, o '' si es válido
export function validarCampo(campo, valor) {
  const texto = valor.trim()

  if (campo === 'nombreUsuario') {
    if (texto === '') return MENSAJES.nombreObligatorio
    if (!PATRON_NOMBRE.test(texto)) return MENSAJES.nombreNoValido
  }

  if (campo === 'email') {
    if (texto === '') return MENSAJES.emailObligatorio
    if (texto.length > 254 || !PATRON_EMAIL.test(texto)) return MENSAJES.emailNoValido
  }

  if (campo === 'password') {
    if (valor === '') return MENSAJES.passwordObligatoria
    const longitudCorrecta = valor.length >= 8 && valor.length <= 64
    const tieneLetra = /[A-Za-z]/.test(valor)
    const tieneNumero = /[0-9]/.test(valor)
    if (!longitudCorrecta || !tieneLetra || !tieneNumero) return MENSAJES.passwordNoValida
  }

  return ''
}

// Devuelve un objeto { campo: mensaje } solo con los campos que tienen error
export function validarRegistro(datos) {
  const errores = {}
  const campos = ['nombreUsuario', 'email', 'password']
  campos.forEach((campo) => {
    const error = validarCampo(campo, datos[campo])
    if (error) {
      errores[campo] = error
    }
  })
  return errores
}
