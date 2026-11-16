import { useState } from 'react'
import { Link, useNavigate } from 'react-router'
import { useAuth } from '../auth/contextoAuth'

// Inicio de sesión con la cuenta creada (HU-01 CA6)
export default function EntrarPagina() {
  const { entrar } = useAuth()
  const navegar = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [enviando, setEnviando] = useState(false)

  async function enviar(evento) {
    evento.preventDefault()
    setError('')
    if (email.trim() === '' || password === '') {
      setError('Introduce tu correo electrónico y tu contraseña')
      return
    }

    setEnviando(true)
    try {
      await entrar(email.trim(), password)
      navegar('/')
    } catch (excepcion) {
      if (excepcion.estado === 401) {
        setError('El correo o la contraseña no son correctos')
      } else {
        setError(excepcion.message)
      }
    } finally {
      setEnviando(false)
    }
  }

  return (
    <section className="tarjeta">
      <h1>Iniciar sesión</h1>

      {error && <p role="alert" className="aviso-error">{error}</p>}

      <form onSubmit={enviar} noValidate>
        <div className="campo">
          <label htmlFor="email-entrar">Correo electrónico</label>
          <input id="email-entrar" type="email" value={email} autoComplete="email"
            onChange={(evento) => setEmail(evento.target.value)} />
        </div>
        <div className="campo">
          <label htmlFor="password-entrar">Contraseña</label>
          <input id="password-entrar" type="password" value={password} autoComplete="current-password"
            onChange={(evento) => setPassword(evento.target.value)} />
        </div>
        <button type="submit" className="boton" disabled={enviando}>
          {enviando ? 'Entrando…' : 'Entrar'}
        </button>
      </form>

      <p className="pie-formulario">
        ¿No tienes cuenta? <Link to="/registro">Crea una</Link>
      </p>
    </section>
  )
}
