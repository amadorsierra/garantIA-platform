import { Navigate, Route, Routes } from 'react-router'
import Cabecera from './componentes/Cabecera'
import RutaPrivada from './componentes/RutaPrivada'
import CasoDetallePagina from './paginas/CasoDetallePagina'
import CasosPagina from './paginas/CasosPagina'
import EntrarPagina from './paginas/EntrarPagina'
import RegistroPagina from './paginas/RegistroPagina'

export default function App() {
  return (
    <>
      <Cabecera />
      <main className="contenido">
        <Routes>
          <Route path="/" element={<Navigate to="/casos" replace />} />
          <Route path="/registro" element={<RegistroPagina />} />
          <Route path="/entrar" element={<EntrarPagina />} />
          <Route path="/casos" element={<RutaPrivada><CasosPagina /></RutaPrivada>} />
          <Route path="/casos/:id" element={<RutaPrivada><CasoDetallePagina /></RutaPrivada>} />
        </Routes>
      </main>
    </>
  )
}
