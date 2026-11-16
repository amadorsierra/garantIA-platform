import { Link } from 'react-router'
import { useAuth } from '../auth/contextoAuth'

export default function Cabecera() {
  const { cuenta, salir } = useAuth()

  return (
    <header className="cabecera">
      <Link to="/" className="marca">GarantIA</Link>
      <nav>
        {cuenta ? (
          <>
            <span className="usuario-actual">{cuenta.nombreUsuario}</span>
            <button type="button" className="boton-enlace" onClick={salir}>Salir</button>
          </>
        ) : (
          <>
            <Link to="/entrar">Iniciar sesión</Link>
            <Link to="/registro">Crear cuenta</Link>
          </>
        )}
      </nav>
    </header>
  )
}
