import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { lecturasAPI, sensoresAPI } from '../services/api'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import { Plus, AlertTriangle } from 'lucide-react'

export default function Lecturas() {
  const { t } = useTranslation()
  const [sensores, setSensores] = useState([])
  const [selectedSensor, setSelectedSensor] = useState('')
  const [lecturas, setLecturas] = useState([])
  const [loading, setLoading] = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [formData, setFormData] = useState({
    sensorId: '',
    valor: '',
    timestamp: new Date().toISOString().slice(0, 16),
  })
  
  useEffect(() => {
    loadSensores()
  }, [])
  
  useEffect(() => {
    if (selectedSensor) {
      loadLecturas(selectedSensor)
    }
  }, [selectedSensor])
  
  const loadSensores = async () => {
    try {
      const response = await sensoresAPI.getAll()
      setSensores(response.data || [])
      if (response.data?.length > 0) {
        setSelectedSensor(response.data[0].id)
      }
    } catch (error) {
      console.error('Error cargando sensores:', error)
    }
  }
  
  const loadLecturas = async (sensorId) => {
    setLoading(true)
    try {
      const response = await lecturasAPI.getBySensor(sensorId, 0, 50)
      const data = response.data.content || []
      setLecturas(data.reverse())
    } catch (error) {
      console.error('Error cargando lecturas:', error)
    } finally {
      setLoading(false)
    }
  }
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const data = {
        ...formData,
        sensorId: parseInt(formData.sensorId),
        valor: parseFloat(formData.valor),
      }
      await lecturasAPI.create(data)
      setShowModal(false)
      resetForm()
      if (selectedSensor) loadLecturas(selectedSensor)
    } catch (error) {
      console.error('Error guardando lectura:', error)
      alert('Error al registrar la lectura')
    }
  }
  
  const resetForm = () => {
    setFormData({
      sensorId: '',
      valor: '',
      timestamp: new Date().toISOString().slice(0, 16),
    })
  }
  
  const chartData = lecturas.map((l) => ({
    timestamp: new Date(l.timestamp).toLocaleTimeString('es', { 
      hour: '2-digit', 
      minute: '2-digit' 
    }),
    valor: l.valor,
    esAnomalia: l.esAnomalia,
  }))
  
  const selectedSensorData = sensores.find(s => s.id === parseInt(selectedSensor))
  
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">{t('readings.title')}</h2>
          <p className="text-gray-600 mt-1">{t('readings.subtitle')}</p>
        </div>
        <button onClick={() => setShowModal(true)} className="btn-primary flex items-center gap-2">
          <Plus className="w-4 h-4" />
          {t('readings.newReading')}
        </button>
      </div>
      
      <div className="card">
        <label className="label">{t('readings.selectSensor')}</label>
        <select
          className="input-field max-w-md"
          value={selectedSensor}
          onChange={(e) => setSelectedSensor(e.target.value)}
        >
          {sensores.map((sensor) => (
            <option key={sensor.id} value={sensor.id}>
              {sensor.plantaNombre} - {sensor.tipo} ({sensor.unidad})
            </option>
          ))}
        </select>
      </div>
      
      {selectedSensorData && (
        <div className="card">
          <div className="mb-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">{t('readings.chart')}</h3>
            <p className="text-sm text-gray-600">
              {t('readings.acceptableRange')}: {selectedSensorData.umbralMin ?? '-'} - {selectedSensorData.umbralMax ?? '-'} {selectedSensorData.unidad}
            </p>
          </div>
          
          {loading ? (
            <div className="flex items-center justify-center h-64">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
            </div>
          ) : lecturas.length === 0 ? (
            <div className="text-center py-12 text-gray-500">
              {t('readings.noReadings')}
            </div>
          ) : (
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="timestamp" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line 
                  type="monotone" 
                  dataKey="valor" 
                  stroke="#16a34a" 
                  strokeWidth={2}
                  dot={(props) => {
                    const { cx, cy, payload } = props
                    return (
                      <circle
                        cx={cx}
                        cy={cy}
                        r={payload.esAnomalia ? 6 : 4}
                        fill={payload.esAnomalia ? '#ef4444' : '#16a34a'}
                        stroke={payload.esAnomalia ? '#991b1b' : '#14532d'}
                        strokeWidth={2}
                      />
                    )
                  }}
                />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
      )}
      
      {lecturas.length > 0 && (
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">{t('readings.history')}</h3>
          <div className="space-y-2 max-h-96 overflow-y-auto">
            {lecturas.slice().reverse().map((lectura) => (
              <div
                key={lectura.id}
                className={`flex items-center justify-between p-3 rounded-lg ${
                  lectura.esAnomalia ? 'bg-red-50 border border-red-200' : 'bg-gray-50'
                }`}
              >
                <div className="flex items-center gap-3">
                  {lectura.esAnomalia && (
                    <AlertTriangle className="w-5 h-5 text-red-600" />
                  )}
                  <div>
                    <p className="font-medium text-gray-900">
                      {lectura.valor} {selectedSensorData?.unidad}
                    </p>
                    <p className="text-sm text-gray-600">
                      {new Date(lectura.timestamp).toLocaleString()}
                    </p>
                  </div>
                </div>
                {lectura.esAnomalia && (
                  <span className="badge-danger">{t('readings.anomalyDetected')}</span>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
      
      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl max-w-lg w-full">
            <div className="p-6 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900">{t('readings.newReading')}</h3>
            </div>
            
            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="label">{t('sensors.plant')} *</label>
                <select
                  required
                  className="input-field"
                  value={formData.sensorId}
                  onChange={(e) => setFormData({ ...formData, sensorId: e.target.value })}
                >
                  <option value="">{t('readings.selectSensor')}</option>
                  {sensores.map((sensor) => (
                    <option key={sensor.id} value={sensor.id}>
                      {sensor.plantaNombre} - {sensor.tipo}
                    </option>
                  ))}
                </select>
              </div>
              
              <div>
                <label className="label">{t('readings.value')} *</label>
                <input
                  type="number"
                  step="0.01"
                  required
                  className="input-field"
                  value={formData.valor}
                  onChange={(e) => setFormData({ ...formData, valor: e.target.value })}
                />
              </div>
              
              <div>
                <label className="label">{t('readings.timestamp')} *</label>
                <input
                  type="datetime-local"
                  required
                  className="input-field"
                  value={formData.timestamp}
                  onChange={(e) => setFormData({ ...formData, timestamp: e.target.value })}
                />
              </div>
              
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <p className="text-sm text-blue-800">
                  <strong>{t('readings.note')}</strong>
                </p>
              </div>
              
              <div className="flex gap-3 pt-4">
                <button type="submit" className="btn-primary flex-1">
                  {t('common.create')}
                </button>
                <button
                  type="button"
                  onClick={() => { setShowModal(false); resetForm() }}
                  className="btn-secondary flex-1"
                >
                  {t('common.cancel')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}