import { useState } from 'react'
import { cerrarSesion, iniciarSesion } from '../api/auth'
import { ContextoAuth } from './contextoAuth'

export default function ProveedorAuth({ children }) {
  const [cuenta, setCuenta] = useState(null)

  async function entrar(email, password) {
    const datos = await iniciarSesion(email, password)
    setCuenta(datos)
    return datos
  }

  function salir() {
    cerrarSesion()
    setCuenta(null)
  }

  return (
    <ContextoAuth.Provider value={{ cuenta, entrar, salir }}>
      {children}
    </ContextoAuth.Provider>
  )
}
