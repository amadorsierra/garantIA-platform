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
            <Link to="/casos">Casos</Link>
            {cuenta.permisos.includes('CASOS_PUBLICAR') && <Link to="/comprobar-texto">Comprobar texto</Link>}
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
