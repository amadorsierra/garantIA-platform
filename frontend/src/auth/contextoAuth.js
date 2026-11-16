import { createContext, useContext } from 'react'

export const ContextoAuth = createContext(null)

// Devuelve { cuenta, entrar, salir }. cuenta es null si no se ha iniciado sesión.
export function useAuth() {
  return useContext(ContextoAuth)
}
