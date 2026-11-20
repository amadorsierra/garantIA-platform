import { useState } from 'react'
import { analizarTexto } from '../api/privacidad'

// HU-08 T7 · Lógica del flujo de docs/hu-08/T3-flujo-advertencia.md, lista para el formulario de HU-02.
//
// Uso: const comprobacion = useComprobacionPrivacidad()
//      comprobacion.comprobar(texto, publicar)   ← al pulsar «Publicar»
//
// - Sin datos personales: llama a publicar() directamente y no muestra ningún aviso (CA6).
// - Con datos personales: guarda el resultado para mostrar el diálogo y NO publica
//   hasta que el usuario elige «Publicar de todos modos» (CA5).
export default function useComprobacionPrivacidad() {
  const [estado, setEstado] = useState('inactivo') // inactivo | analizando | advertencia | error
  const [analisis, setAnalisis] = useState(null) // { texto, detecciones, publicar }
  const [error, setError] = useState('')

  async function comprobar(texto, publicar) {
    setEstado('analizando')
    setError('')
    try {
      const resultado = await analizarTexto(texto)
      if (resultado.hayDatosSensibles) {
        setAnalisis({ texto, detecciones: resultado.detecciones, publicar })
        setEstado('advertencia')
      } else {
        setAnalisis(null)
        setEstado('inactivo')
        publicar()
      }
    } catch (excepcion) {
      setError(excepcion.estado === 0 ? excepcion.message : 'No se ha podido comprobar si tu texto tiene datos personales')
      setEstado('error')
    }
  }

  // Cierra el aviso sin publicar; quien llama decide qué marcar en el formulario
  function revisar() {
    const detecciones = analisis ? analisis.detecciones : []
    setAnalisis(null)
    setEstado('inactivo')
    return detecciones
  }

  function publicarIgualmente() {
    const publicar = analisis.publicar
    setAnalisis(null)
    setEstado('inactivo')
    publicar()
  }

  function cancelar() {
    setAnalisis(null)
    setEstado('inactivo')
  }

  return { estado, analisis, error, comprobar, revisar, publicarIgualmente, cancelar }
}
