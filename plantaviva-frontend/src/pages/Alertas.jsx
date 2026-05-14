import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { alertasAPI } from '../services/api'
import { AlertTriangle, CheckCircle, Trash2 } from 'lucide-react'

export default function Alertas() {
  const { t } = useTranslation()
  const [alertas, setAlertas] = useState([])
  const [filtro, setFiltro] = useState('pendientes')
  const [loading, setLoading] = useState(true)
  
  useEffect(() => {
    loadAlertas()
  }, [filtro])
  
  const loadAlertas = async () => {
    setLoading(true)
    try {
      const response = filtro === 'pendientes'
        ? await alertasAPI.getPendientes()
        : await alertasAPI.getAll()
      setAlertas(response.data || [])
    } catch (error) {
      console.error('Error cargando alertas:', error)
    } finally {
      setLoading(false)
    }
  }
  
  const handleMarcarRevisada = async (id) => {
    try {
      await alertasAPI.marcarRevisada(id)
      loadAlertas()
    } catch (error) {
      console.error('Error marcando alerta como revisada:', error)
    }
  }
  
  const handleEliminar = async (id) => {
    if (!confirm(t('alerts.deleteConfirm'))) return
    
    try {
      await alertasAPI.delete(id)
      loadAlertas()
    } catch (error) {
      console.error('Error eliminando alerta:', error)
    }
  }
  
  const alertasPendientes = alertas.filter(a => !a.revisada)
  const alertasRevisadas = alertas.filter(a => a.revisada)
  
  const AlertaCard = ({ alerta }) => (
    <div className={`card ${alerta.revisada ? 'bg-gray-50' : 'border-l-4 border-l-red-500'}`}>
      <div className="flex items-start gap-4">
        <div className={`p-3 rounded-xl ${alerta.revisada ? 'bg-gray-200' : 'bg-red-100'}`}>
          {alerta.revisada ? (
            <CheckCircle className="w-6 h-6 text-gray-600" />
          ) : (
            <AlertTriangle className="w-6 h-6 text-red-600" />
          )}
        </div>
        
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-4 mb-2">
            <div>
              <h3 className="font-semibold text-gray-900">{alerta.plantaNombre}</h3>
              <p className="text-sm text-gray-600">
                {new Date(alerta.createdAt).toLocaleString()}
              </p>
            </div>
            {alerta.revisada ? (
              <span className="badge-success flex-shrink-0">{t('alerts.reviewed')}</span>
            ) : (
              <span className="badge-danger flex-shrink-0">{t('alerts.notReviewed')}</span>
            )}
          </div>
          
          <p className="text-gray-700 mb-3">{alerta.mensaje}</p>
          
          <div className="flex items-center gap-2 text-sm text-gray-600">
            <span>{t('alerts.readingId')}: #{alerta.lecturaId}</span>
            <span>•</span>
            <span className="font-medium">{alerta.lecturaValor}</span>
          </div>
        </div>
        
        <div className="flex flex-col gap-2">
          {!alerta.revisada && (
            <button
              onClick={() => handleMarcarRevisada(alerta.id)}
              className="btn-primary text-sm"
              title={t('alerts.markReviewed')}
            >
              <CheckCircle className="w-4 h-4" />
            </button>
          )}
          <button
            onClick={() => handleEliminar(alerta.id)}
            className="btn-danger text-sm"
            title={t('common.delete')}
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  )
  
  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    )
  }
  
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">{t('alerts.title')}</h2>
          <p className="text-gray-600 mt-1">{t('alerts.subtitle')}</p>
        </div>
        
        <div className="flex gap-2">
          <button
            onClick={() => setFiltro('pendientes')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filtro === 'pendientes'
                ? 'bg-red-600 text-white'
                : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
            }`}
          >
            {t('alerts.pending')} ({alertasPendientes.length})
          </button>
          <button
            onClick={() => setFiltro('todas')}
            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
              filtro === 'todas'
                ? 'bg-primary-600 text-white'
                : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
            }`}
          >
            {t('alerts.all')} ({alertas.length})
          </button>
        </div>
      </div>
      
      {filtro === 'pendientes' && alertasPendientes.length === 0 && (
        <div className="card text-center py-12">
          <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            {t('alerts.allGood')}
          </h3>
          <p className="text-gray-600">
            {t('alerts.allGoodDesc')}
          </p>
        </div>
      )}
      
      {filtro === 'todas' && alertas.length === 0 && (
        <div className="card text-center py-12 text-gray-500">
          {t('alerts.noAlerts')}
        </div>
      )}
      
      {filtro === 'pendientes' && alertasPendientes.length > 0 && (
        <div className="space-y-4">
          <div className="flex items-center gap-2 text-red-600 font-medium">
            <AlertTriangle className="w-5 h-5" />
            {alertasPendientes.length} {alertasPendientes.length !== 1 ? t('alerts.pending').toLowerCase() : t('alerts.notReviewed').toLowerCase()}
          </div>
          {alertasPendientes.map((alerta) => (
            <AlertaCard key={alerta.id} alerta={alerta} />
          ))}
        </div>
      )}
      
      {filtro === 'todas' && alertas.length > 0 && (
        <div className="space-y-6">
          {alertasPendientes.length > 0 && (
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-3 flex items-center gap-2">
                <AlertTriangle className="w-5 h-5 text-red-600" />
                {t('alerts.pending')} ({alertasPendientes.length})
              </h3>
              <div className="space-y-4">
                {alertasPendientes.map((alerta) => (
                  <AlertaCard key={alerta.id} alerta={alerta} />
                ))}
              </div>
            </div>
          )}
          
          {alertasRevisadas.length > 0 && (
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-3 flex items-center gap-2">
                <CheckCircle className="w-5 h-5 text-green-600" />
                {t('alerts.reviewed')} ({alertasRevisadas.length})
              </h3>
              <div className="space-y-4">
                {alertasRevisadas.map((alerta) => (
                  <AlertaCard key={alerta.id} alerta={alerta} />
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}