import { useAuth } from '../auth/contextoAuth'

// Página tras iniciar sesión. La lista de casos llega con HU-16.
export default function InicioPagina() {
  const { cuenta } = useAuth()

  return (
    <section className="tarjeta">
      <h1>Hola, {cuenta.nombreUsuario}</h1>
      <p>Has iniciado sesión correctamente.</p>
    </section>
  )
}
