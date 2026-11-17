// HU-08 T4 · Prototipo de detección de datos personales con reglas y validaciones.
// Sirve para medir la detección antes de implementarla en el backend (T6).
// Uso: node evaluar.mjs

const LETRAS_DNI = 'TRWAGMYFPDXBNJZSQVHLCKE'

const NOMBRES = [
  'María', 'Carmen', 'Ana', 'Laura', 'Lucía', 'Marta', 'Elena', 'Isabel', 'Paula', 'Sara', 'Cristina',
  'Pilar', 'Rosa', 'Raquel', 'Sofía', 'Julia', 'Andrea', 'Irene', 'Alba', 'Nuria', 'Silvia', 'Patricia',
  'Beatriz', 'Teresa', 'Mercedes', 'Rocío', 'Eva', 'Inés', 'Clara', 'Natalia', 'Lorena', 'Noelia',
  'Antonio', 'José', 'Manuel', 'Francisco', 'David', 'Juan', 'Javier', 'Daniel', 'Carlos', 'Jesús',
  'Alejandro', 'Miguel', 'Rafael', 'Pedro', 'Pablo', 'Ángel', 'Sergio', 'Fernando', 'Jorge', 'Luis',
  'Alberto', 'Álvaro', 'Adrián', 'Diego', 'Raúl', 'Iván', 'Rubén', 'Enrique', 'Óscar', 'Ramón', 'Andrés',
  'Vicente', 'Joaquín', 'Santiago', 'Víctor', 'Mario', 'Roberto', 'Jaime', 'Ignacio', 'Alfonso', 'Hugo',
  'Marcos', 'Tomás', 'Fabián', 'Amador', 'Gonzalo', 'Guillermo', 'Rodrigo', 'Emilio', 'Julio', 'Agustín'
]
const NOMBRES_SET = new Set(NOMBRES)
const MAYUS = 'A-ZÁÉÍÓÚÑÜ'
const MINUS = 'a-záéíóúñü'
const PALABRA_MAYUS = `[${MAYUS}][${MINUS}]+`

function soloDigitos(texto) {
  return texto.replace(/\D/g, '')
}

function dniValido(texto) {
  const limpio = texto.replace(/[\s-]/g, '').toUpperCase()
  const numero = Number(limpio.slice(0, 8))
  return LETRAS_DNI[numero % 23] === limpio[8]
}

function nieValido(texto) {
  const limpio = texto.replace(/[\s-]/g, '').toUpperCase()
  const prefijo = { X: '0', Y: '1', Z: '2' }[limpio[0]]
  const numero = Number(prefijo + limpio.slice(1, 8))
  return LETRAS_DNI[numero % 23] === limpio[8]
}

function ibanValido(texto) {
  const limpio = texto.replace(/\s/g, '').toUpperCase()
  if (limpio.length < 15 || limpio.length > 34) return false
  const reordenado = limpio.slice(4) + limpio.slice(0, 4)
  let resto = 0
  for (const caracter of reordenado) {
    const valor = parseInt(caracter, 36)
    const trozo = String(resto) + String(valor)
    resto = Number(trozo) % 97
  }
  return resto === 1
}

function luhnValido(texto) {
  const digitos = soloDigitos(texto)
  if (digitos.length < 13 || digitos.length > 19) return false
  let suma = 0
  let doble = false
  for (let i = digitos.length - 1; i >= 0; i--) {
    let d = Number(digitos[i])
    if (doble) {
      d = d * 2
      if (d > 9) d = d - 9
    }
    suma += d
    doble = !doble
  }
  return suma % 10 === 0
}

function nombreCompletoValido(fragmento) {
  // Un nombre completo necesita al menos una palabra que no sea un nombre de pila (el apellido)
  const palabras = fragmento.split(/\s+/).filter((p) => new RegExp(`^${PALABRA_MAYUS}$`).test(p))
  return palabras.some((p) => !NOMBRES_SET.has(p))
}

// Orden de las reglas: las de formato más largo primero, para que no se pisen
const REGLAS = [
  { tipo: 'EMAIL', patron: /[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}/g },
  { tipo: 'IBAN', patron: /\b[A-Z]{2}\d{2}(?:\s?[A-Z0-9]{4}){3,7}(?:\s?[A-Z0-9]{1,3})?\b/g, valida: ibanValido },
  { tipo: 'TARJETA', patron: /(?<![\d-])(?:\d[\s-]?){12,18}\d(?![\d-])/g, valida: luhnValido },
  { tipo: 'NIE', patron: /\b[XYZ][\s-]?\d{7}[\s-]?[A-Za-z]\b/g, valida: nieValido },
  { tipo: 'DNI', patron: /\b\d{8}[\s-]?[A-Za-z]\b/g, valida: dniValido },
  { tipo: 'TELEFONO', patron: /(?<![\w+])(?:(?:\+|00)34[\s.-]?)?[6789](?:[\s.-]?\d){8}(?!\w)/g },
  {
    tipo: 'DIRECCION',
    patron: new RegExp(
      `(?:\\b(?:[Cc]alle|[Aa]venida|[Aa]vda\\.?|[Pp]laza|[Pp]za\\.?|[Pp]aseo|[Cc]amino|[Cc]arretera|[Rr]onda|[Tt]ravesía|[Gg]lorieta)|C/)` +
      `\\s*(?:(?:de|del|de la|de los|de las)\\s+)?${PALABRA_MAYUS}(?:\\s+(?:(?:de|del|de la)\\s+)?${PALABRA_MAYUS}){0,3},?\\s*(?:n[º°o.]\\s*)?\\d{1,4}`,
      'g')
  },
  {
    tipo: 'NOMBRE_COMPLETO',
    patron: new RegExp(`\\b(?:${NOMBRES.join('|')})(?:\\s+(?:(?:de|del|de la)\\s+)?${PALABRA_MAYUS}){1,3}`, 'g'),
    valida: nombreCompletoValido
  }
]

export function detectar(texto) {
  const detecciones = []
  REGLAS.forEach((regla) => {
    for (const coincidencia of texto.matchAll(regla.patron)) {
      const fragmento = coincidencia[0].trim()
      const inicio = coincidencia.index
      const fin = inicio + fragmento.length
      const solapa = detecciones.some((d) => inicio < d.fin && fin > d.inicio)
      if (solapa) continue
      if (regla.valida && !regla.valida(fragmento)) continue
      detecciones.push({ tipo: regla.tipo, fragmento, inicio, fin })
    }
  })
  detecciones.sort((a, b) => a.inicio - b.inicio)
  return detecciones
}
