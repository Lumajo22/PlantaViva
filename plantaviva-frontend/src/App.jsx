import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'
import AuthCallback from './pages/AuthCallback'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Plantas from './pages/Plantas'
import Sensores from './pages/Sensores'
import Lecturas from './pages/Lecturas'
import Alertas from './pages/Alertas'

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Ruta pública: Login */}
          <Route path="/login" element={<Login />} />
          
          {/* Callback después de autenticar con Google */}
          <Route path="/auth/callback" element={<AuthCallback />} />
          
          {/* Rutas protegidas: Requieren autenticación */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route index element={<Dashboard />} />
            <Route path="plantas" element={<Plantas />} />
            <Route path="sensores" element={<Sensores />} />
            <Route path="lecturas" element={<Lecturas />} />
            <Route path="alertas" element={<Alertas />} />
          </Route>

          {/* Ruta por defecto: redirigir a login */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App