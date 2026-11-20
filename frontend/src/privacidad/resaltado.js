// Divide el texto en trozos normales y trozos detectados, para resaltar los datos personales.
// Las detecciones llegan ordenadas y sin solaparse (así las devuelve el backend).
export function dividirTexto(texto, detecciones) {
  const trozos = []
  let posicion = 0
  detecciones.forEach((deteccion) => {
    if (deteccion.inicio > posicion) {
      trozos.push({ texto: texto.slice(posicion, deteccion.inicio), deteccion: null })
    }
    trozos.push({ texto: texto.slice(deteccion.inicio, deteccion.fin), deteccion })
    posicion = deteccion.fin
  })
  if (posicion < texto.length) {
    trozos.push({ texto: texto.slice(posicion), deteccion: null })
  }
  return trozos
}
