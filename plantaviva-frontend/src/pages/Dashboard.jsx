import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { plantasAPI, sensoresAPI, alertasAPI } from '../services/api'
import { Leaf, Gauge, Bell, TrendingUp } from 'lucide-react'

export default function Dashboard() {
  const { t } = useTranslation()
  const [stats, setStats] = useState({
    totalPlantas: 0,
    plantasActivas: 0,
    totalSensores: 0,
    alertasPendientes: 0,
  })
  
  const [loading, setLoading] = useState(true)
  
  useEffect(() => {
    loadStats()
  }, [])
  
  const loadStats = async () => {
    try {
      const [plantasRes, sensoresRes, alertasRes] = await Promise.all([
        plantasAPI.getAll(0, 1000),
        sensoresAPI.getAll(),
        alertasAPI.countPendientes(),
      ])
      
      const plantas = plantasRes.data.content || []
      const sensores = sensoresRes.data || []
      
      setStats({
        totalPlantas: plantas.length,
        plantasActivas: plantas.filter(p => p.activa).length,
        totalSensores: sensores.length,
        alertasPendientes: alertasRes.data,
      })
    } catch (error) {
      console.error('Error cargando estadísticas:', error)
    } finally {
      setLoading(false)
    }
  }
  
  const statCards = [
    {
      title: t('dashboard.activePlants'),
      value: stats.plantasActivas,
      total: stats.totalPlantas,
      icon: Leaf,
      color: 'bg-green-500',
      bgLight: 'bg-green-50',
      textColor: 'text-green-700',
    },
    {
      title: t('dashboard.sensors'),
      value: stats.totalSensores,
      icon: Gauge,
      color: 'bg-blue-500',
      bgLight: 'bg-blue-50',
      textColor: 'text-blue-700',
    },
    {
      title: t('dashboard.pendingAlerts'),
      value: stats.alertasPendientes,
      icon: Bell,
      color: 'bg-red-500',
      bgLight: 'bg-red-50',
      textColor: 'text-red-700',
    },
  ]
  
  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    )
  }
  
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-gray-900">{t('dashboard.title')}</h2>
        <p className="text-gray-600 mt-1">{t('dashboard.subtitle')}</p>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {statCards.map((stat) => (
          <div key={stat.title} className="card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                <p className="text-3xl font-bold text-gray-900 mt-2">
                  {stat.value}
                  {stat.total !== undefined && (
                    <span className="text-lg text-gray-400 ml-2">/ {stat.total}</span>
                  )}
                </p>
              </div>
              <div className={`${stat.bgLight} p-4 rounded-xl`}>
                <stat.icon className={`w-8 h-8 ${stat.textColor}`} />
              </div>
            </div>
          </div>
        ))}
      </div>
      
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          {t('dashboard.welcome')}
        </h3>
        <div className="space-y-3 text-gray-700">
          <p>{t('dashboard.description')}</p>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6">
            <div className="flex gap-3">
              <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center flex-shrink-0">
                <Leaf className="w-4 h-4 text-primary-600" />
              </div>
              <div>
                <h4 className="font-medium text-gray-900">{t('dashboard.plantManagement')}</h4>
                <p className="text-sm text-gray-600">{t('dashboard.plantManagementDesc')}</p>
              </div>
            </div>
            <div className="flex gap-3">
              <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center flex-shrink-0">
                <Gauge className="w-4 h-4 text-blue-600" />
              </div>
              <div>
                <h4 className="font-medium text-gray-900">{t('dashboard.iotSensors')}</h4>
                <p className="text-sm text-gray-600">{t('dashboard.iotSensorsDesc')}</p>
              </div>
            </div>
            <div className="flex gap-3">
              <div className="w-8 h-8 rounded-full bg-purple-100 flex items-center justify-center flex-shrink-0">
                <TrendingUp className="w-4 h-4 text-purple-600" />
              </div>
              <div>
                <h4 className="font-medium text-gray-900">{t('dashboard.aiAnomalies')}</h4>
                <p className="text-sm text-gray-600">{t('dashboard.aiAnomaliesDesc')}</p>
              </div>
            </div>
            <div className="flex gap-3">
              <div className="w-8 h-8 rounded-full bg-red-100 flex items-center justify-center flex-shrink-0">
                <Bell className="w-4 h-4 text-red-600" />
              </div>
              <div>
                <h4 className="font-medium text-gray-900">{t('dashboard.smartAlerts')}</h4>
                <p className="text-sm text-gray-600">{t('dashboard.smartAlertsDesc')}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}