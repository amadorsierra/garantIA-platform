import { useEffect, useState } from 'react'
import { Link, useLocation, useParams } from 'react-router'
import { obtenerCaso } from '../api/casos'
import AvisoError from '../componentes/AvisoError'
import EtiquetaEstado from '../componentes/EtiquetaEstado'
import { formatearFecha } from '../utilidades/formato'

// HU-16 T4 · Detalle de un caso con sus respuestas (CA5)
export default function CasoDetallePagina() {
  const { id } = useParams()
  const ubicacion = useLocation()
  const [intento, setIntento] = useState(0)
  const [resultado, setResultado] = useState({ clave: null, caso: null, error: null, reintentable: false })

  const claveActual = id + '#' + intento
  const cargando = resultado.clave !== claveActual

  // Vuelve a la lista con los mismos filtros con los que se salió de ella
  const desde = ubicacion.state && ubicacion.state.desde ? ubicacion.state.desde : ''
  const enlaceVolver = <Link to={'/casos' + desde}>← Volver a la lista</Link>

  useEffect(() => {
    let cancelado = false
    const clave = id + '#' + intento

    obtenerCaso(id)
      .then((caso) => {
        if (!cancelado) setResultado({ clave, caso, error: null, reintentable: false })
      })
      .catch((error) => {
        if (cancelado) return
        if (error.estado === 404 || error.estado === 403) {
          setResultado({ clave, caso: null, error: error.message, reintentable: false })
        } else {
          setResultado({ clave, caso: null, error: 'No se ha podido cargar el caso', reintentable: true })
        }
      })

    return () => {
      cancelado = true
    }
  }, [id, intento])

  if (cargando) {
    return <p>Cargando caso…</p>
  }

  if (resultado.error) {
    return (
      <section>
        {enlaceVolver}
        <AvisoError mensaje={resultado.error}
          onReintentar={resultado.reintentable ? () => setIntento(intento + 1) : null} />
      </section>
    )
  }

  const caso = resultado.caso
  return (
    <article className="detalle-caso">
      {enlaceVolver}

      <header className="tarjeta-caso-cabecera">
        <h1>{caso.titulo}</h1>
        <EtiquetaEstado estado={caso.estado} />
      </header>
      <p className="tarjeta-caso-datos">
        {caso.categoria} · {caso.autor} · {formatearFecha(caso.fechaPublicacion)}
      </p>
      <p className="descripcion-caso">{caso.descripcion}</p>

      <h2>Respuestas ({caso.respuestas.length})</h2>
      {caso.respuestas.length === 0 ? (
        <p>Todavía no hay respuestas para este caso</p>
      ) : (
        <ul className="lista-respuestas">
          {caso.respuestas.map((respuesta) => (
            <li key={respuesta.id} className="respuesta">
              <p className="tarjeta-caso-datos">{respuesta.autor} · {formatearFecha(respuesta.fecha)}</p>
              <p>{respuesta.contenido}</p>
            </li>
          ))}
        </ul>
      )}
    </article>
  )
}
