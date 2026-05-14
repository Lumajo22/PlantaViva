import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { sensoresAPI, plantasAPI } from '../services/api'
import { Plus, Edit, Trash2, Thermometer, Droplets, Sun } from 'lucide-react'

const TIPO_ICONS = {
  TEMPERATURA: Thermometer,
  HUMEDAD: Droplets,
  LUZ: Sun,
}

export default function Sensores() {
  const { t } = useTranslation()
  const [sensores, setSensores] = useState([])
  const [plantas, setPlantas] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingSensor, setEditingSensor] = useState(null)
  const [formData, setFormData] = useState({
    plantaId: '',
    tipo: 'TEMPERATURA',
    unidad: '°C',
    umbralMin: '',
    umbralMax: '',
    activo: true,
  })
  
  useEffect(() => {
    loadData()
  }, [])
  
  const loadData = async () => {
    try {
      const [sensoresRes, plantasRes] = await Promise.all([
        sensoresAPI.getAll(),
        plantasAPI.getAll(0, 1000),
      ])
      setSensores(sensoresRes.data || [])
      setPlantas(plantasRes.data.content || [])
    } catch (error) {
      console.error('Error cargando datos:', error)
    } finally {
      setLoading(false)
    }
  }
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const data = {
        ...formData,
        umbralMin: formData.umbralMin ? parseFloat(formData.umbralMin) : null,
        umbralMax: formData.umbralMax ? parseFloat(formData.umbralMax) : null,
        plantaId: parseInt(formData.plantaId),
      }
      
      if (editingSensor) {
        await sensoresAPI.update(editingSensor.id, data)
      } else {
        await sensoresAPI.create(data)
      }
      setShowModal(false)
      resetForm()
      loadData()
    } catch (error) {
      console.error('Error guardando sensor:', error)
      alert('Error al guardar el sensor')
    }
  }
  
  const handleEdit = (sensor) => {
    setEditingSensor(sensor)
    setFormData({
      plantaId: sensor.plantaId,
      tipo: sensor.tipo,
      unidad: sensor.unidad,
      umbralMin: sensor.umbralMin || '',
      umbralMax: sensor.umbralMax || '',
      activo: sensor.activo,
    })
    setShowModal(true)
  }
  
  const handleDelete = async (id) => {
    if (!confirm(t('sensors.deleteConfirm'))) return
    
    try {
      await sensoresAPI.delete(id)
      loadData()
    } catch (error) {
      console.error('Error eliminando sensor:', error)
      alert('Error al eliminar el sensor')
    }
  }
  
  const resetForm = () => {
    setEditingSensor(null)
    setFormData({
      plantaId: '',
      tipo: 'TEMPERATURA',
      unidad: '°C',
      umbralMin: '',
      umbralMax: '',
      activo: true,
    })
  }
  
  const handleTipoChange = (tipo) => {
    const unidades = {
      TEMPERATURA: '°C',
      HUMEDAD: '%',
      LUZ: 'lux',
    }
    setFormData({ ...formData, tipo, unidad: unidades[tipo] })
  }
  
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
          <h2 className="text-2xl font-bold text-gray-900">{t('sensors.title')}</h2>
          <p className="text-gray-600 mt-1">{t('sensors.subtitle')}</p>
        </div>
        <button onClick={() => setShowModal(true)} className="btn-primary flex items-center gap-2">
          <Plus className="w-4 h-4" />
          {t('sensors.newSensor')}
        </button>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {sensores.map((sensor) => {
          const Icon = TIPO_ICONS[sensor.tipo] || Thermometer
          return (
            <div key={sensor.id} className="card">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 bg-blue-50 rounded-xl flex items-center justify-center">
                    <Icon className="w-6 h-6 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">{sensor.tipo}</h3>
                    <p className="text-sm text-gray-600">{sensor.plantaNombre}</p>
                  </div>
                </div>
                {sensor.activo ? (
                  <span className="badge-success">{t('sensors.active')}</span>
                ) : (
                  <span className="badge-warning">{t('sensors.inactive')}</span>
                )}
              </div>
              
              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">{t('sensors.unit')}:</span>
                  <span className="font-medium">{sensor.unidad}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">{t('sensors.range')}:</span>
                  <span className="font-medium">
                    {sensor.umbralMin ?? '-'} - {sensor.umbralMax ?? '-'}
                  </span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">{t('sensors.readingCount')}:</span>
                  <span className="font-medium">{sensor.cantidadLecturas || 0}</span>
                </div>
              </div>
              
              <div className="flex gap-2 pt-4 border-t border-gray-200">
                <button
                  onClick={() => handleEdit(sensor)}
                  className="btn-secondary flex-1 text-sm"
                >
                  <Edit className="w-4 h-4 inline mr-1" />
                  {t('common.edit')}
                </button>
                <button
                  onClick={() => handleDelete(sensor.id)}
                  className="btn-danger flex-1 text-sm"
                >
                  <Trash2 className="w-4 h-4 inline mr-1" />
                  {t('common.delete')}
                </button>
              </div>
            </div>
          )
        })}
      </div>
      
      {sensores.length === 0 && (
        <div className="card text-center py-12 text-gray-500">
          {t('sensors.noSensors')}
        </div>
      )}
      
      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl max-w-lg w-full">
            <div className="p-6 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900">
                {editingSensor ? t('sensors.editSensor') : t('sensors.newSensor')}
              </h3>
            </div>
            
            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="label">{t('sensors.plant')} *</label>
                <select
                  required
                  className="input-field"
                  value={formData.plantaId}
                  onChange={(e) => setFormData({ ...formData, plantaId: e.target.value })}
                >
                  <option value="">{t('sensors.selectPlant')}</option>
                  {plantas.map((planta) => (
                    <option key={planta.id} value={planta.id}>
                      {planta.nombre}
                    </option>
                  ))}
                </select>
              </div>
              
              <div>
                <label className="label">{t('sensors.type')} *</label>
                <select
                  required
                  className="input-field"
                  value={formData.tipo}
                  onChange={(e) => handleTipoChange(e.target.value)}
                >
                  <option value="TEMPERATURA">{t('sensors.temperature')}</option>
                  <option value="HUMEDAD">{t('sensors.humidity')}</option>
                  <option value="LUZ">{t('sensors.light')}</option>
                </select>
              </div>
              
              <div>
                <label className="label">{t('sensors.unit')} *</label>
                <input
                  type="text"
                  required
                  className="input-field"
                  value={formData.unidad}
                  onChange={(e) => setFormData({ ...formData, unidad: e.target.value })}
                />
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="label">{t('sensors.minThreshold')}</label>
                  <input
                    type="number"
                    step="0.01"
                    className="input-field"
                    value={formData.umbralMin}
                    onChange={(e) => setFormData({ ...formData, umbralMin: e.target.value })}
                  />
                </div>
                <div>
                  <label className="label">{t('sensors.maxThreshold')}</label>
                  <input
                    type="number"
                    step="0.01"
                    className="input-field"
                    value={formData.umbralMax}
                    onChange={(e) => setFormData({ ...formData, umbralMax: e.target.value })}
                  />
                </div>
              </div>
              
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="activo"
                  className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
                  checked={formData.activo}
                  onChange={(e) => setFormData({ ...formData, activo: e.target.checked })}
                />
                <label htmlFor="activo" className="ml-2 text-sm text-gray-700">
                  {t('sensors.sensorActive')}
                </label>
              </div>
              
              <div className="flex gap-3 pt-4">
                <button type="submit" className="btn-primary flex-1">
                  {editingSensor ? t('common.update') : t('common.create')}
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