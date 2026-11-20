import { useRef, useState } from 'react'
import AvisoError from '../componentes/AvisoError'
import AdvertenciaDatosSensibles from '../privacidad/AdvertenciaDatosSensibles'
import useComprobacionPrivacidad from '../privacidad/useComprobacionPrivacidad'

// HU-08 T7 · Página para probar el aviso de datos personales mientras no existe
// el formulario de publicación (HU-02). Simula la publicación al final del flujo.
export default function ComprobarTextoPagina() {
  const [texto, setTexto] = useState('')
  const [mensaje, setMensaje] = useState('')
  const refTexto = useRef(null)
  const comprobacion = useComprobacionPrivacidad()

  function publicar() {
    setMensaje('Tu caso se publicaría ahora.')
  }

  function enviar(evento) {
    evento.preventDefault()
    setMensaje('')
    if (texto.trim() === '') {
      setMensaje('Escribe el texto que quieres comprobar.')
      return
    }
    comprobacion.comprobar(texto, publicar)
  }

  // Vuelve al texto y selecciona el primer dato detectado para que el usuario lo cambie
  function revisar() {
    const detecciones = comprobacion.revisar()
    const campo = refTexto.current
    campo.focus()
    if (detecciones.length > 0) {
      campo.setSelectionRange(detecciones[0].inicio, detecciones[0].fin)
    }
  }

  function cancelar() {
    comprobacion.cancelar()
    setMensaje('No se ha publicado nada. Tu texto sigue aquí.')
  }

  return (
    <section className="tarjeta tarjeta-ancha">
      <h1>Comprobar datos personales</h1>
      <p className="subtitulo">Antes de publicar un caso, comprobamos que no incluya datos personales.</p>

      <form onSubmit={enviar}>
        <div className="campo">
          <label htmlFor="texto-caso">Texto del caso</label>
          <textarea id="texto-caso" ref={refTexto} rows="8" value={texto}
            onChange={(evento) => setTexto(evento.target.value)} />
        </div>
        <button type="submit" className="boton" disabled={comprobacion.estado === 'analizando'}>
          {comprobacion.estado === 'analizando' ? 'Comprobando…' : 'Publicar'}
        </button>
      </form>

      {mensaje && <p role="status" className="aviso-exito">{mensaje}</p>}

      {comprobacion.estado === 'error' && (
        <AvisoError mensaje={comprobacion.error} onReintentar={() => comprobacion.comprobar(texto, publicar)} />
      )}

      {comprobacion.estado === 'advertencia' && (
        <AdvertenciaDatosSensibles
          texto={comprobacion.analisis.texto}
          detecciones={comprobacion.analisis.detecciones}
          onRevisar={revisar}
          onPublicar={comprobacion.publicarIgualmente}
          onCancelar={cancelar}
        />
      )}
    </section>
  )
}
