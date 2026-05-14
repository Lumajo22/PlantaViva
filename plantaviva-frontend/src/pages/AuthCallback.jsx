import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useTranslation } from 'react-i18next'
import { Leaf } from 'lucide-react'

export default function AuthCallback() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const { t } = useTranslation()

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/auth/user', {
          credentials: 'include', // Importante para enviar cookies
        })

        if (response.ok) {
          const userData = await response.json()
          login(userData)
          navigate('/', { replace: true })
        } else {
          console.error('Error obteniendo usuario')
          navigate('/login', { replace: true })
        }
      } catch (error) {
        console.error('Error en callback:', error)
        navigate('/login', { replace: true })
      }
    }

    fetchUser()
  }, [login, navigate])

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-50 to-primary-100 flex items-center justify-center">
      <div className="text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl mb-4 animate-pulse">
          <Leaf className="w-10 h-10 text-white" />
        </div>
        <h2 className="text-xl font-semibold text-gray-900 mb-2">
          {t('login.loggingIn')}
        </h2>
        <p className="text-gray-600">
          {t('common.loading')}
        </p>
      </div>
    </div>
  )
}