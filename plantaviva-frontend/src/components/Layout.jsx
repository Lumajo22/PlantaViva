import { Outlet, Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useTranslation } from 'react-i18next'
import { Home, Leaf, Gauge, Activity, Bell, LogOut, User, Globe } from 'lucide-react'

export default function Layout() {
  const location = useLocation()
  const { user, logout } = useAuth()
  const { t, i18n } = useTranslation()
  
  const isActive = (path) => {
    if (path === '/') return location.pathname === '/'
    return location.pathname.startsWith(path)
  }
  
  const changeLanguage = (lang) => {
    i18n.changeLanguage(lang)
    localStorage.setItem('language', lang)
  }
  
  const navItems = [
    { path: '/', label: t('nav.dashboard'), icon: Home },
    { path: '/plantas', label: t('nav.plants'), icon: Leaf },
    { path: '/sensores', label: t('nav.sensors'), icon: Gauge },
    { path: '/lecturas', label: t('nav.readings'), icon: Activity },
    { path: '/alertas', label: t('nav.alerts'), icon: Bell },
  ]
  
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            {/* Logo */}
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-700 rounded-xl flex items-center justify-center">
                <Leaf className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-xl font-bold text-gray-900">PlantaViva</h1>
                <p className="text-xs text-gray-500">{t('login.subtitle')}</p>
              </div>
            </div>

            {/* Usuario, Idioma y Logout */}
            <div className="flex items-center gap-4">
              {/* Selector de idioma */}
              <div className="flex items-center gap-1 bg-gray-100 rounded-lg p-1">
                <button
                  onClick={() => changeLanguage('es')}
                  className={`px-3 py-1 rounded text-sm font-medium transition-colors ${
                    i18n.language === 'es'
                      ? 'bg-white text-primary-600 shadow-sm'
                      : 'text-gray-600 hover:text-gray-900'
                  }`}
                >
                  🇪🇸 ES
                </button>
                <button
                  onClick={() => changeLanguage('en')}
                  className={`px-3 py-1 rounded text-sm font-medium transition-colors ${
                    i18n.language === 'en'
                      ? 'bg-white text-primary-600 shadow-sm'
                      : 'text-gray-600 hover:text-gray-900'
                  }`}
                >
                  🇺🇸 EN
                </button>
              </div>

              {user && (
                <>
                  <div className="flex items-center gap-2 text-sm">
                    <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
                      <User className="w-4 h-4 text-primary-600" />
                    </div>
                    <div className="hidden md:block">
                      <p className="font-medium text-gray-900">{user.name}</p>
                      <p className="text-xs text-gray-500">{user.email}</p>
                    </div>
                  </div>
                  <button
                    onClick={logout}
                    className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-gray-700 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                    title={t('common.logout')}
                  >
                    <LogOut className="w-4 h-4" />
                    <span className="hidden md:inline">{t('common.logout')}</span>
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      </header>
      
      {/* Navigation */}
      <nav className="bg-white border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex gap-1">
            {navItems.map(({ path, label, icon: Icon }) => (
              <Link
                key={path}
                to={path}
                className={`
                  flex items-center gap-2 px-4 py-3 text-sm font-medium border-b-2 transition-colors
                  ${isActive(path)
                    ? 'border-primary-600 text-primary-700'
                    : 'border-transparent text-gray-600 hover:text-gray-900 hover:border-gray-300'
                  }
                `}
              >
                <Icon className="w-4 h-4" />
                {label}
              </Link>
            ))}
          </div>
        </div>
      </nav>
      
      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
    </div>
  )
}