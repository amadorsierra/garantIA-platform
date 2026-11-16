// Campo de formulario con su etiqueta, una ayuda opcional y su mensaje de error accesible
export default function Campo({ id, etiqueta, tipo = 'text', valor, error, ayuda, onChange, onBlur, autoComplete, refCampo }) {
  const idAyuda = id + '-ayuda'
  const idError = id + '-error'

  let descritoPor
  if (error) {
    descritoPor = idError
  } else if (ayuda) {
    descritoPor = idAyuda
  }

  return (
    <div className="campo">
      <label htmlFor={id}>{etiqueta}</label>
      <input
        id={id}
        name={id}
        type={tipo}
        value={valor}
        onChange={onChange}
        onBlur={onBlur}
        autoComplete={autoComplete}
        aria-invalid={error ? 'true' : 'false'}
        aria-describedby={descritoPor}
        ref={refCampo}
      />
      {error ? (
        <p id={idError} className="campo-error">⚠ {error}</p>
      ) : (
        ayuda && <p id={idAyuda} className="campo-ayuda">{ayuda}</p>
      )}
    </div>
  )
}
