import { useRef, useState } from 'react'
import { Link } from 'react-router'
import { registrar } from '../api/auth'
import Campo from '../componentes/Campo'
import { validarCampo, validarRegistro } from '../validacion/registro'

const CAMPOS = ['nombreUsuario', 'email', 'password']

// HU-01 T5 · Formulario de registro (diseño en docs/hu-01/T2-diseno-formulario-registro.md)
export default function RegistroPagina() {
  const [datos, setDatos] = useState({ nombreUsuario: '', email: '', password: '' })
  const [errores, setErrores] = useState({})
  const [errorGeneral, setErrorGeneral] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [mensajeExito, setMensajeExito] = useState('')

  const refNombreUsuario = useRef(null)
  const refEmail = useRef(null)
  const refPassword = useRef(null)

  function cambiar(evento) {
    const { name, value } = evento.target
    setDatos({ ...datos, [name]: value })
  }

  // Al salir de un campo se valida solo ese campo
  function salirDeCampo(evento) {
    const { name, value } = evento.target
    setErrores({ ...errores, [name]: validarCampo(name, value) })
  }

  function enfocarPrimerError(nuevosErrores) {
    const refs = { nombreUsuario: refNombreUsuario, email: refEmail, password: refPassword }
    const primero = CAMPOS.find((campo) => nuevosErrores[campo])
    if (primero && refs[primero].current) {
      refs[primero].current.focus()
    }
  }

  async function enviar(evento) {
    evento.preventDefault()
    setErrorGeneral('')

    const nuevosErrores = validarRegistro(datos)
    setErrores(nuevosErrores)
    if (Object.keys(nuevosErrores).length > 0) {
      enfocarPrimerError(nuevosErrores)
      return
    }

    setEnviando(true)
    try {
      const respuesta = await registrar({
        nombreUsuario: datos.nombreUsuario.trim(),
        email: datos.email.trim(),
        password: datos.password
      })
      setMensajeExito(respuesta.mensaje)
    } catch (error) {
      const erroresServidor = error.errores || {}
      if (Object.keys(erroresServidor).length > 0) {
        setErrores(erroresServidor)
        enfocarPrimerError(erroresServidor)
      } else {
        setErrorGeneral(error.message)
      }
    } finally {
      setEnviando(false)
    }
  }

  if (mensajeExito) {
    return (
      <section className="tarjeta">
        <p role="status" className="aviso-exito">{mensajeExito}</p>
        <Link className="boton" to="/entrar">Iniciar sesión</Link>
      </section>
    )
  }

  return (
    <section className="tarjeta">
      <h1>Crear una cuenta</h1>
      <p className="subtitulo">Publica tus casos y recibe ayuda</p>

      {errorGeneral && <p role="alert" className="aviso-error">{errorGeneral}</p>}

      <form onSubmit={enviar} noValidate>
        <Campo
          id="nombreUsuario"
          etiqueta="Nombre de usuario"
          valor={datos.nombreUsuario}
          error={errores.nombreUsuario}
          ayuda="De 3 a 30 caracteres. Es tu nombre público"
          onChange={cambiar}
          onBlur={salirDeCampo}
          autoComplete="username"
          refCampo={refNombreUsuario}
        />
        <Campo
          id="email"
          etiqueta="Correo electrónico"
          tipo="email"
          valor={datos.email}
          error={errores.email}
          onChange={cambiar}
          onBlur={salirDeCampo}
          autoComplete="email"
          refCampo={refEmail}
        />
        <Campo
          id="password"
          etiqueta="Contraseña"
          tipo="password"
          valor={datos.password}
          error={errores.password}
          ayuda="Mínimo 8 caracteres, con letras y números"
          onChange={cambiar}
          onBlur={salirDeCampo}
          autoComplete="new-password"
          refCampo={refPassword}
        />

        <button type="submit" className="boton" disabled={enviando}>
          {enviando ? 'Creando cuenta…' : 'Crear cuenta'}
        </button>
      </form>

      <p className="pie-formulario">
        ¿Ya tienes cuenta? <Link to="/entrar">Inicia sesión</Link>
      </p>
    </section>
  )
}
