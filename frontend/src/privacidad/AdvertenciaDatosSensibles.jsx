import { useEffect, useRef } from 'react'
import { dividirTexto } from './resaltado'

// HU-08 T7 · Diálogo de advertencia de datos personales (contenido en docs/hu-08/T3-flujo-advertencia.md)
export default function AdvertenciaDatosSensibles({ texto, detecciones, onRevisar, onPublicar, onCancelar }) {
  const refRevisar = useRef(null)

  // Al abrirse, el foco va a la opción recomendada
  useEffect(() => {
    refRevisar.current.focus()
  }, [])

  // Escape equivale a «Revisar el texto»
  function teclaPulsada(evento) {
    if (evento.key === 'Escape') {
      onRevisar()
    }
  }

  return (
    <div className="fondo-dialogo">
      <div
        role="alertdialog"
        aria-modal="true"
        aria-labelledby="advertencia-titulo"
        aria-describedby="advertencia-explicacion"
        className="dialogo"
        onKeyDown={teclaPulsada}
      >
        <h2 id="advertencia-titulo">Tu publicación puede contener datos personales</h2>
        <p id="advertencia-explicacion">
          Cualquier persona podrá leer tu caso. Te recomendamos quitar estos datos antes de publicar.
        </p>

        <ul className="lista-detecciones">
          {detecciones.map((deteccion) => (
            <li key={deteccion.inicio}>
              <strong>{deteccion.descripcion}:</strong> «{deteccion.fragmento}»
            </li>
          ))}
        </ul>

        <p className="texto-revisado" aria-label="Tu texto con los datos detectados resaltados">
          {dividirTexto(texto, detecciones).map((trozo, indice) =>
            trozo.deteccion ? <mark key={indice}>{trozo.texto}</mark> : <span key={indice}>{trozo.texto}</span>
          )}
        </p>

        <p className="nota-deteccion">
          La detección automática no es perfecta: revisa tú también que no haya otros datos personales.
        </p>

        <div className="botones-dialogo">
          <button type="button" className="boton" ref={refRevisar} onClick={onRevisar}>Revisar el texto</button>
          <button type="button" className="boton-secundario" onClick={onPublicar}>Publicar de todos modos</button>
          <button type="button" className="boton-enlace" onClick={onCancelar}>Cancelar</button>
        </div>
      </div>
    </div>
  )
}
