import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { plantasAPI } from '../services/api'
import { Plus, Edit, Trash2, CheckCircle, XCircle } from 'lucide-react'

export default function Plantas() {
  const { t } = useTranslation()
  const [plantas, setPlantas] = useState([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [editingPlanta, setEditingPlanta] = useState(null)
  const [formData, setFormData] = useState({
    nombre: '',
    especie: '',
    ubicacion: '',
    descripcion: '',
    fechaSiembra: '',
    activa: true,
  })
  
  useEffect(() => {
    loadPlantas()
  }, [])
  
  const loadPlantas = async () => {
    try {
      const response = await plantasAPI.getAll(0, 100)
      setPlantas(response.data.content || [])
    } catch (error) {
      console.error('Error cargando plantas:', error)
    } finally {
      setLoading(false)
    }
  }
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      if (editingPlanta) {
        await plantasAPI.update(editingPlanta.id, formData)
      } else {
        await plantasAPI.create(formData)
      }
      setShowModal(false)
      resetForm()
      loadPlantas()
    } catch (error) {
      console.error('Error guardando planta:', error)
      alert('Error al guardar la planta')
    }
  }
  
  const handleEdit = (planta) => {
    setEditingPlanta(planta)
    setFormData({
      nombre: planta.nombre,
      especie: planta.especie || '',
      ubicacion: planta.ubicacion || '',
      descripcion: planta.descripcion || '',
      fechaSiembra: planta.fechaSiembra || '',
      activa: planta.activa,
    })
    setShowModal(true)
  }
  
  const handleDelete = async (id) => {
    if (!confirm(t('plants.deleteConfirm'))) return
    
    try {
      await plantasAPI.delete(id)
      loadPlantas()
    } catch (error) {
      console.error('Error eliminando planta:', error)
      alert('Error al eliminar la planta')
    }
  }
  
  const resetForm = () => {
    setEditingPlanta(null)
    setFormData({
      nombre: '',
      especie: '',
      ubicacion: '',
      descripcion: '',
      fechaSiembra: '',
      activa: true,
    })
  }
  
  const openCreateModal = () => {
    resetForm()
    setShowModal(true)
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
          <h2 className="text-2xl font-bold text-gray-900">{t('plants.title')}</h2>
          <p className="text-gray-600 mt-1">{t('plants.subtitle')}</p>
        </div>
        <button onClick={openCreateModal} className="btn-primary flex items-center gap-2">
          <Plus className="w-4 h-4" />
          {t('plants.newPlant')}
        </button>
      </div>
      
      <div className="card overflow-hidden p-0">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('plants.name')}</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('plants.species')}</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('plants.location')}</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('plants.plantingDate')}</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('common.actions')}</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">{t('plants.sensorCount')}</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">{t('common.actions')}</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {plantas.map((planta) => (
              <tr key={planta.id} className="hover:bg-gray-50">
                <td className="px-6 py-4">
                  <div className="font-medium text-gray-900">{planta.nombre}</div>
                </td>
                <td className="px-6 py-4 text-sm text-gray-600">{planta.especie || '-'}</td>
                <td className="px-6 py-4 text-sm text-gray-600">{planta.ubicacion || '-'}</td>
                <td className="px-6 py-4 text-sm text-gray-600">{planta.fechaSiembra || '-'}</td>
                <td className="px-6 py-4">
                  {planta.activa ? (
                    <span className="badge-success flex items-center gap-1 w-fit">
                      <CheckCircle className="w-3 h-3" />
                      {t('plants.active')}
                    </span>
                  ) : (
                    <span className="badge-warning flex items-center gap-1 w-fit">
                      <XCircle className="w-3 h-3" />
                      {t('plants.inactive')}
                    </span>
                  )}
                </td>
                <td className="px-6 py-4 text-sm text-gray-600">{planta.cantidadSensores || 0}</td>
                <td className="px-6 py-4 text-right space-x-2">
                  <button
                    onClick={() => handleEdit(planta)}
                    className="text-blue-600 hover:text-blue-800 inline-flex items-center gap-1"
                  >
                    <Edit className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleDelete(planta.id)}
                    className="text-red-600 hover:text-red-800 inline-flex items-center gap-1"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        
        {plantas.length === 0 && (
          <div className="text-center py-12 text-gray-500">
            {t('plants.noPlants')}
          </div>
        )}
      </div>
      
      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900">
                {editingPlanta ? t('plants.editPlant') : t('plants.newPlant')}
              </h3>
            </div>
            
            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="label">{t('plants.name')} *</label>
                <input
                  type="text"
                  required
                  className="input-field"
                  value={formData.nombre}
                  onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                />
              </div>
              
              <div>
                <label className="label">{t('plants.species')}</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.especie}
                  onChange={(e) => setFormData({ ...formData, especie: e.target.value })}
                />
              </div>
              
              <div>
                <label className="label">{t('plants.location')}</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.ubicacion}
                  onChange={(e) => setFormData({ ...formData, ubicacion: e.target.value })}
                />
              </div>
              
              <div>
                <label className="label">{t('plants.description')}</label>
                <textarea
                  rows="3"
                  className="input-field"
                  value={formData.descripcion}
                  onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
                />
              </div>
              
              <div>
                <label className="label">{t('plants.plantingDate')}</label>
                <input
                  type="date"
                  className="input-field"
                  value={formData.fechaSiembra}
                  onChange={(e) => setFormData({ ...formData, fechaSiembra: e.target.value })}
                />
              </div>
              
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="activa"
                  className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
                  checked={formData.activa}
                  onChange={(e) => setFormData({ ...formData, activa: e.target.checked })}
                />
                <label htmlFor="activa" className="ml-2 text-sm text-gray-700">
                  {t('plants.plantActive')}
                </label>
              </div>
              
              <div className="flex gap-3 pt-4">
                <button type="submit" className="btn-primary flex-1">
                  {editingPlanta ? t('common.update') : t('common.create')}
                </button>
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
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