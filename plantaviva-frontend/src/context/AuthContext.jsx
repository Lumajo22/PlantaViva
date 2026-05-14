import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // Verificar si hay usuario en sessionStorage al cargar
    const savedUser = sessionStorage.getItem('user')
    if (savedUser) {
      setUser(JSON.parse(savedUser))
    }
    setLoading(false)
  }, [])

  const login = (userData) => {
    setUser(userData)
    sessionStorage.setItem('user', JSON.stringify(userData))
  }

  const logout = async () => {
    setUser(null)
    sessionStorage.removeItem('user')
    
    try {
      // Cerrar sesión del backend
      await fetch('http://localhost:8080/logout', {
        method: 'POST',
        credentials: 'include'
      })
      
      // Abrir logout de Google en iframe oculto para limpiar sesión
      const iframe = document.createElement('iframe')
      iframe.style.display = 'none'
      iframe.src = 'https://accounts.google.com/Logout'
      document.body.appendChild(iframe)
      
      // Esperar un momento y redirigir al login
      setTimeout(() => {
        document.body.removeChild(iframe)
        window.location.href = '/login'
      }, 1000)
    } catch (error) {
      console.error('Error en logout:', error)
      window.location.href = '/login'
    }
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider')
  }
  return context
}