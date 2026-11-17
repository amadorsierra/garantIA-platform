// HU-08 T4 · Mide el prototipo con el conjunto de evaluación compartido con el backend.
// Uso: node herramientas/prototipo-hu08/evaluar.mjs   (desde la raíz del repositorio)
import { readFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'
import { detectar } from './detector.mjs'

const raiz = join(dirname(fileURLToPath(import.meta.url)), '..', '..')
const csv = readFileSync(join(raiz, 'backend/src/test/resources/privacidad/casos-evaluacion.csv'), 'utf8')

const casos = []
csv.split('\n').forEach((linea) => {
  if (!linea.trim() || linea.startsWith('#') || linea === 'texto;tipos') return
  const [texto, tipos] = linea.split(';')
  casos.push({ texto, esperados: tipos ? tipos.split('|').filter(Boolean) : [] })
})

const porTipo = {}
function contar(tipo, campo) {
  if (!porTipo[tipo]) porTipo[tipo] = { vp: 0, fp: 0, fn: 0 }
  porTipo[tipo][campo]++
}

const fallos = []
casos.forEach(({ texto, esperados }) => {
  const encontrados = [...new Set(detectar(texto).map((d) => d.tipo))]
  esperados.forEach((tipo) => contar(tipo, encontrados.includes(tipo) ? 'vp' : 'fn'))
  encontrados.forEach((tipo) => {
    if (!esperados.includes(tipo)) contar(tipo, 'fp')
  })
  const falta = esperados.filter((t) => !encontrados.includes(t))
  const sobra = encontrados.filter((t) => !esperados.includes(t))
  if (falta.length || sobra.length) fallos.push({ texto, falta, sobra })
})

const total = { vp: 0, fp: 0, fn: 0 }
console.log('Tipo              VP  FP  FN  Precisión  Exhaustividad')
Object.keys(porTipo).sort().forEach((tipo) => {
  const { vp, fp, fn } = porTipo[tipo]
  total.vp += vp
  total.fp += fp
  total.fn += fn
  const precision = vp + fp ? vp / (vp + fp) : 1
  const exhaustividad = vp + fn ? vp / (vp + fn) : 1
  console.log(`${tipo.padEnd(16)} ${String(vp).padStart(3)} ${String(fp).padStart(3)} ${String(fn).padStart(3)}  ${(precision * 100).toFixed(0).padStart(8)}%  ${(exhaustividad * 100).toFixed(0).padStart(12)}%`)
})
const precisionTotal = total.vp / (total.vp + total.fp)
const exhaustividadTotal = total.vp / (total.vp + total.fn)
console.log(`${'TOTAL'.padEnd(16)} ${String(total.vp).padStart(3)} ${String(total.fp).padStart(3)} ${String(total.fn).padStart(3)}  ${(precisionTotal * 100).toFixed(0).padStart(8)}%  ${(exhaustividadTotal * 100).toFixed(0).padStart(12)}%`)
console.log(`\nTextos: ${casos.length} · Textos con algún fallo: ${fallos.length}`)
fallos.forEach(({ texto, falta, sobra }) => {
  console.log(`- ${texto}\n    no detectado: ${falta.join(', ') || '—'} · falso aviso: ${sobra.join(', ') || '—'}`)
})
