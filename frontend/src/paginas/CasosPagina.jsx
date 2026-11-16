import { useEffect, useState } from 'react'
import { Link, useLocation, useSearchParams } from 'react-router'
import { buscarCasos, obtenerCategorias } from '../api/casos'
import AvisoError from '../componentes/AvisoError'
import EtiquetaEstado from '../componentes/EtiquetaEstado'
import { formatearFecha } from '../utilidades/formato'

const MENSAJE_ERROR_CARGA = 'No se han podido cargar los casos'

// El 403 trae su propio mensaje de acceso denegado; el resto de errores, uno genérico
function mensajeDeError(error) {
  return error.estado === 403 ? error.message : MENSAJE_ERROR_CARGA
}

function leerFiltros(parametros) {
  return {
    q: parametros.get('q') || '',
    estado: parametros.get('estado') || '',
    categoria: parametros.get('categoria') || '',
    orden: parametros.get('orden') || ''
  }
}

// HU-16 T4 · Lista de casos publicados (diseño en docs/hu-16/T2-diseno-listado-casos.md)
export default function CasosPagina() {
  const [parametros, setParametros] = useSearchParams()
  const ubicacion = useLocation()
  const filtros = leerFiltros(parametros)

  const [textoBusqueda, setTextoBusqueda] = useState(filtros.q)
  const [categorias, setCategorias] = useState([])
  const [intento, setIntento] = useState(0)
  // Guarda para qué petición es el resultado: si no coincide con la actual, se está cargando
  const [resultado, setResultado] = useState({ clave: null, casos: [], error: null })

  const claveActual = parametros.toString() + '#' + intento
  const cargando = resultado.clave !== claveActual

  useEffect(() => {
    obtenerCategorias()
      .then(setCategorias)
      .catch(() => setCategorias([]))
  }, [])

  useEffect(() => {
    let cancelado = false
    const clave = parametros.toString() + '#' + intento

    buscarCasos(leerFiltros(parametros))
      .then((casos) => {
        if (!cancelado) setResultado({ clave, casos, error: null })
      })
      .catch((error) => {
        if (!cancelado) setResultado({ clave, casos: [], error: mensajeDeError(error) })
      })

    return () => {
      cancelado = true
    }
  }, [parametros, intento])

  // Cambia un filtro manteniendo los demás
  function cambiarFiltro(nombre, valor) {
    const nuevos = new URLSearchParams(parametros)
    if (valor) {
      nuevos.set(nombre, valor)
    } else {
      nuevos.delete(nombre)
    }
    setParametros(nuevos)
  }

  function buscar(evento) {
    evento.preventDefault()
    cambiarFiltro('q', textoBusqueda.trim())
  }

  function quitarFiltros() {
    setTextoBusqueda('')
    setParametros(new URLSearchParams())
  }

  return (
    <section>
      <h1>Casos publicados</h1>

      <form className="busqueda" onSubmit={buscar} role="search">
        <div className="campo campo-busqueda">
          <label htmlFor="busqueda">Buscar</label>
          <input id="busqueda" type="search" value={textoBusqueda}
            onChange={(evento) => setTextoBusqueda(evento.target.value)} />
        </div>
        <button type="submit" className="boton boton-buscar">Buscar</button>
      </form>

      <div className="filtros">
        <div className="campo">
          <label htmlFor="filtro-estado">Estado</label>
          <select id="filtro-estado" value={filtros.estado}
            onChange={(evento) => cambiarFiltro('estado', evento.target.value)}>
            <option value="">Todos</option>
            <option value="ABIERTO">Abierto</option>
            <option value="CERRADO">Cerrado</option>
            <option value="RESUELTO">Resuelto</option>
          </select>
        </div>
        <div className="campo">
          <label htmlFor="filtro-categoria">Categoría</label>
          <select id="filtro-categoria" value={filtros.categoria}
            onChange={(evento) => cambiarFiltro('categoria', evento.target.value)}>
            <option value="">Todas</option>
            {categorias.map((categoria) => (
              <option key={categoria} value={categoria}>{categoria}</option>
            ))}
          </select>
        </div>
        <div className="campo">
          <label htmlFor="filtro-orden">Ordenar</label>
          <select id="filtro-orden" value={filtros.orden}
            onChange={(evento) => cambiarFiltro('orden', evento.target.value)}>
            <option value="">Más recientes</option>
            <option value="antiguos">Más antiguos</option>
          </select>
        </div>
      </div>

      {cargando && <p>Cargando casos…</p>}

      {!cargando && resultado.error && (
        <AvisoError mensaje={resultado.error} onReintentar={() => setIntento(intento + 1)} />
      )}

      {!cargando && !resultado.error && resultado.casos.length === 0 && (
        <div role="status" className="sin-resultados">
          <p>No hay casos que coincidan con tu búsqueda</p>
          <button type="button" className="boton-secundario" onClick={quitarFiltros}>Quitar filtros</button>
        </div>
      )}

      {!cargando && !resultado.error && resultado.casos.length > 0 && (
        <ul className="lista-casos">
          {resultado.casos.map((caso) => (
            <li key={caso.id}>
              <Link to={'/casos/' + caso.id} state={{ desde: ubicacion.search }} className="tarjeta-caso">
                <span className="tarjeta-caso-cabecera">
                  <strong>{caso.titulo}</strong>
                  <EtiquetaEstado estado={caso.estado} />
                </span>
                <span className="tarjeta-caso-datos">
                  {caso.categoria} · {caso.autor} · {formatearFecha(caso.fechaPublicacion)}
                </span>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}
