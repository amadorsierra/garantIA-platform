import { Navigate } from 'react-router'
import { useAuth } from '../auth/contextoAuth'

// Si no se ha iniciado sesión, manda a la página de entrar
export default function RutaPrivada({ children }) {
  const { cuenta } = useAuth()
  if (!cuenta) {
    return <Navigate to="/entrar" replace />
  }
  return children
}
