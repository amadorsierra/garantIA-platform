// Aviso de error con botón para reintentar la carga (HU-16 CA7)
export default function AvisoError({ mensaje, onReintentar }) {
  return (
    <div role="alert" className="aviso-error">
      <p>{mensaje}</p>
      {onReintentar && (
        <button type="button" className="boton-secundario" onClick={onReintentar}>Reintentar</button>
      )}
    </div>
  )
}
