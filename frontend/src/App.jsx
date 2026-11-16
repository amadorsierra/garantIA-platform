import { Route, Routes } from 'react-router'
import Cabecera from './componentes/Cabecera'
import RutaPrivada from './componentes/RutaPrivada'
import EntrarPagina from './paginas/EntrarPagina'
import InicioPagina from './paginas/InicioPagina'
import RegistroPagina from './paginas/RegistroPagina'

export default function App() {
  return (
    <>
      <Cabecera />
      <main className="contenido">
        <Routes>
          <Route path="/" element={<RutaPrivada><InicioPagina /></RutaPrivada>} />
          <Route path="/registro" element={<RegistroPagina />} />
          <Route path="/entrar" element={<EntrarPagina />} />
        </Routes>
      </main>
    </>
  )
}
